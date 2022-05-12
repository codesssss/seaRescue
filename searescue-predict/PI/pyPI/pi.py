# pyPI: Potential Intensity Calculations in Python
# -----------------------------------------------------------------------------------


# import required packages
import numpy as np
import numba as nb
from . import constants
from . import utilities


# define the function to calculate CAPE
#定义计算CAPE的函数
@nb.njit()
def cape(TP, RP, PP, T, R, P, ascent_flag=0, ptop=50, miss_handle=1):
    #     function [CAPED,TOB,LNB,IFLAG]= cape(TP,RP,PP,T,R,P,ascent_flag=0,ptop=50,miss_handle=1)
    #
    #       This function calculates the CAPE of a parcel given parcel pressure PP (hPa),
    #       temperature TP (K) and mixing ratio RP (gram/gram) and given a sounding
    #       of temperature (T in K) and mixing ratio (R in gram/gram) as a function
    #       of pressure (P in hPa). CAPED is the calculated value of CAPE following
    #       Emanuel 1994 (E94) Equation 6.3.6 and TOB is the temperature at the
    #       level of neutral buoyancy ("LNB") for the displaced parcel. IFLAG is a flag
    #       integer. If IFLAG = 1, routine is successful; if it is 0, routine did
    #       not run owing to improper sounding (e.g. no water vapor at parcel level).
    #       IFLAG=2 indicates that the routine did not converge, IFLAG=3 indicates that
    #       the input profile had missing values.
    #
    # 这个函数计算一个包裹的CAPE给定包裹压力PP (hPa)，温度TP (K)和混合比RP(克/克)，并给出一个探测温度(T, K)和混合比(R，克/克)作为压力的函数(P,
    # hPa)。CAPED为CAPE的计算值，根据Emanuel 1994 (E94)式6.3.6,TOB为位移包裹的中性浮力水平温度(LNB)。ifflag为标志整数。If If = 1, routine is
    # successful;如果为0，则由于探测不当(例如包裹层没有水汽)，程序没有运行。IFLAG=2表示例程没有收敛，IFLAG=3表示输入剖面有缺失值。
    #
    #  INPUT:   TP,RP,PP: floating point numbers of Parcel pressure (hPa),
    #             temperature (K), and mixing ratio (gram/gram)
    #
    #           T,R,P: One-dimensional arrays
    #             containing environmental pressure (hPa), temperature (K),
    #             and mixing ratio (gram/gram) profiles. The arrays MUST be
    #             arranged so that the lowest index corresponds
    #             to the lowest model level, with increasing index
    #             corresponding to decreasing pressure.
    #
    #           ascent_flag: Adjustable constant fraction for buoyancy of displaced
    #             parcels, where 0=Reversible ascent;  1=Pseudo-adiabatic ascent
    #
    #           ptop: Pressure below which sounding is ignored (hPa)
    #
    #           miss_handle: Flag that determines how missing (NaN) values are handled.
    #             If = 0 (BE02 default), NaN values in profile are ignored and PI is still calcuated
    #             If = 1 (pyPI default), given NaN values PI will be set to missing (with IFLAG=3)
    #             NOTE: If any missing values are between the lowest valid level and ptop
    #             then PI will automatically be set to missing (with IFLAG=3)
    #
    #
    #  OUTPUT:  CAPED (J/kg) is Convective Available Potential Energy of an air parcel
    #             consistent with its parcel and environmental properties.
    #
    #           TOB is the Temperature (K) at the level of neutral bouyancy
    #             for the displaced air parcel
    #
    #           LNB is the pressure level of neutral bouyancy (hPa) for the
    #             displaced air parcel
    #
    #           IFLAG is a flag where the value of 1 means OK; a value of 0
    #             indicates an improper sounding or parcel; a value of 2
    #             means that the routine failed to converge
    #

    #
    #   ***  Handle missing values   ***
    #

    # find if any values are missing in the temperature or mixing ratio array
    valid_i = ~np.isnan(T)
    first_valid = np.where(valid_i)[0][0]
    # Are there missing values? If so, assess according to flag
    if (np.sum(valid_i) != len(P)):
        # if not allowed, set IFLAG=3 and return missing CAPE
        if (miss_handle != 0):
            CAPED = np.nan
            TOB = np.nan
            LNB = np.nan
            IFLAG = 3
            # Return the unsuitable values
            return (CAPED, TOB, LNB, IFLAG)
        else:
            # if allowed, but there are missing values between the lowest existing level
            # and ptop, then set IFLAG=3 and return missing CAPE
            if np.sum(np.isnan(T[first_valid:len(P)]) > 0):
                CAPED = np.nan
                TOB = np.nan
                LNB = np.nan
                IFLAG = 3
                # Return the unsuitable values
                return (CAPED, TOB, LNB, IFLAG)
            else:
                first_lvl = first_valid
    else:
        first_lvl = 0

    # Populate new environmental profiles removing values above ptop and
    # find new number, N, of profile levels with which to calculate CAPE
    N = np.argmin(np.abs(P - ptop))

    P = P[first_lvl:N]
    T = T[first_lvl:N]
    R = R[first_lvl:N]
    nlvl = len(P)
    TVRDIF = np.zeros((nlvl,))

    #
    #   ***  Run checks   ***
    #

    # CHECK: Is the input parcel suitable? If not, return missing CAPE
    if ((RP < 1e-6) or (TP < 200)):
        CAPED = 0
        TOB = np.nan
        LNB = np.nan
        IFLAG = 0
        # Return the unsuitable values
        return (CAPED, TOB, LNB, IFLAG)

    #
    #  ***  Define various parcel quantities, including reversible   ***
    #  ***                       entropy, S                          ***
    #                         
    TPC = utilities.T_ktoC(TP)  # Parcel temperature in Celsius
    ESP = utilities.es_cc(TPC)  # Parcel's saturated vapor pressure
    EVP = utilities.ev(RP, PP)  # Parcel's partial vapor pressure
    RH = EVP / ESP  # Parcel's relative humidity
    RH = min([RH, 1.0])  # ensure that the relatively humidity does not exceed 1.0
    # calculate reversible total specific entropy per unit mass of dry air (E94, EQN. 4.5.9)
    S = utilities.entropy_S(TP, RP, PP)

    #
    #   ***  Estimate lifted condensation level pressure, PLCL   ***
    #     Based on E94 "calcsound.f" code at http://texmex.mit.edu/pub/emanuel/BOOK/
    #     see also https://psl.noaa.gov/data/composites/day/calculation.html
    #
    #   NOTE: Modern PLCL calculations are made following the exact expressions of Romps (2017),
    #   see https://journals.ametsoc.org/doi/pdf/10.1175/JAS-D-17-0102.1
    #   and Python PLCL code at http://romps.berkeley.edu/papers/pubdata/2016/lcl/lcl.py
    #
    PLCL = utilities.e_pLCL(TP, RH, PP)

    # Initial default values before loop
    CAPED = 0
    TOB = T[0]
    IFLAG = 1
    # Values to help loop
    NCMAX = 0
    jmin = int(1e6)

    #
    #   ***  Begin updraft loop   ***
    #

    # loop over each level in the profile
    for j in range(nlvl):

        # jmin is the index of the lowest pressure level evaluated in the loop
        jmin = int(min([jmin, j]))

        #
        #   *** Calculate Parcel quantities BELOW lifted condensation level   ***
        #
        if (P[j] >= PLCL):
            # Parcel temperature at this pressure
            TG = TP * (P[j] / PP) ** (constants.RD / constants.CPD)
            # Parcel Mixing ratio
            RG = RP
            # Parcel and Environmental Density Temperatures at this pressure (E94, EQN. 4.3.1 and 6.3.7)
            TLVR = utilities.Trho(TG, RG, RG)
            TVENV = utilities.Trho(T[j], R[j], R[j])
            # Bouyancy of the parcel in the environment (Proxy of E94, EQN. 6.1.5)
            TVRDIF[j,] = TLVR - TVENV

        #
        #   *** Calculate Parcel quantities ABOVE lifted condensation level   ***
        # 
        else:

            # Initial default values before loop
            TGNEW = T[j]
            TJC = utilities.T_ktoC(T[j])
            ES = utilities.es_cc(TJC)
            RG = utilities.rv(ES, P[j])

            #
            #   ***  Iteratively calculate lifted parcel temperature and mixing   ***
            #   ***                ratio for reversible ascent                    ***
            #

            # set loop counter and initial condition
            NC = 0
            TG = 0

            # loop until loop converges or bails out
            while ((np.abs(TGNEW - TG)) > 0.001):

                # Parcel temperature and mixing ratio during this iteration
                TG = TGNEW
                TC = utilities.T_ktoC(TG)
                ENEW = utilities.es_cc(TC)
                RG = utilities.rv(ENEW, P[j])

                # increase iteration count in the loop
                NC += 1

                #
                #   ***  Calculate estimates of the rates of change of the entropy    ***
                #   ***           with temperature at constant pressure               ***
                #

                ALV = utilities.Lv(TC)
                # calculate the rate of change of entropy with temperature, s_ell
                SL = (constants.CPD + RP * constants.CL + ALV * ALV * RG / (constants.RV * TG * TG)) / TG
                EM = utilities.ev(RG, P[j])
                # calculate the saturated entropy, s_k, noting r_T=RP and
                # the last term vanishes with saturation, i.e. RH=1
                SG = (constants.CPD + RP * constants.CL) * np.log(TG) - constants.RD * np.log(P[j] - EM) + ALV * RG / TG
                # convergence speed (AP, step in entropy fraction) varies as a function of 
                # number of iterations
                if (NC < 3):
                    # converge slowly with a smaller step
                    AP = 0.3
                else:
                    # speed the process with a larger step when nearing convergence
                    AP = 1.0
                # find the new temperature in the iteration
                TGNEW = TG + AP * (S - SG) / SL

                #
                #   ***   If the routine does not converge, set IFLAG=2 and bail out   ***
                #
                if (NC > 500) or (ENEW > (P[j] - 1)):
                    CAPED = 0
                    TOB = T[0]
                    LNB = P[0]
                    IFLAG = 2
                    # Return the uncoverged values
                    return (CAPED, TOB, LNB, IFLAG)

                # store the number of iterations
                NCMAX = NC

            #
            #   *** Calculate buoyancy   ***
            #
            # Parcel total mixing ratio: either reversible (ascent_flag=0) or pseudo-adiabatic (ascent_flag=1)
            RMEAN = ascent_flag * RG + (1 - ascent_flag) * RP
            # Parcel and Environmental Density Temperatures at this pressure (E94, EQN. 4.3.1 and 6.3.7)
            TLVR = utilities.Trho(TG, RMEAN, RG)
            TENV = utilities.Trho(T[j], R[j], R[j])
            # Bouyancy of the parcel in the environment (Proxy of E94, EQN. 6.1.5)
            TVRDIF[j,] = TLVR - TENV

    #
    #  ***  Begin loop to find Positive areas (PA) and Negative areas (NA) ***
    #                  ***  and CAPE from reversible ascent ***
    NA = 0.0
    PA = 0.0

    #
    #   ***  Find maximum level of positive buoyancy, INB    ***
    #
    INB = 0
    for j in range(nlvl - 1, jmin, -1):
        if (TVRDIF[j] > 0):
            INB = max([INB, j])

    # CHECK: Is the LNB higher than the surface? If not, return zero CAPE  
    if (INB == 0):
        CAPED = 0
        TOB = T[0]
        LNB = P[INB]
        #         TOB=np.nan
        LNB = 0
        # Return the unconverged values
        return (CAPED, TOB, LNB, IFLAG)

    # if check is passed, continue with the CAPE calculation
    else:

        #
        #   ***  Find positive and negative areas and CAPE  ***
        #                  via E94, EQN. 6.3.6)
        #
        for j in range(jmin + 1, INB + 1, 1):
            PFAC = constants.RD * (TVRDIF[j] + TVRDIF[j - 1]) * (P[j - 1] - P[j]) / (P[j] + P[j - 1])
            PA = PA + max([PFAC, 0.0])
            NA = NA - min([PFAC, 0.0])

        #
        #   ***   Find area between parcel pressure and first level above it ***
        #
        PMA = (PP + P[jmin])
        PFAC = constants.RD * (PP - P[jmin]) / PMA
        PA = PA + PFAC * max([TVRDIF[jmin], 0.0])
        NA = NA - PFAC * min([TVRDIF[jmin], 0.0])

        #
        #   ***   Find residual positive area above INB and TO  ***
        #         and finalize estimate of LNB and its temperature
        #
        PAT = 0.0
        TOB = T[INB]
        LNB = P[INB]
        if (INB < nlvl - 1):
            PINB = (P[INB + 1] * TVRDIF[INB] - P[INB] * TVRDIF[INB + 1]) / (TVRDIF[INB] - TVRDIF[INB + 1])
            LNB = PINB
            PAT = constants.RD * TVRDIF[INB] * (P[INB] - PINB) / (P[INB] + PINB)
            TOB = (T[INB] * (PINB - P[INB + 1]) + T[INB + 1] * (P[INB] - PINB)) / (P[INB] - P[INB + 1])

        #
        #   ***   Find CAPE  ***
        #
        CAPED = PA + PAT - NA
        CAPED = max([CAPED, 0.0])
        # set the flag to OK if procedure reached this point
        IFLAG = 1
        # Return the calculated outputs to the above program level 
        return (CAPED, TOB, LNB, IFLAG)


# 定义计算PI的函数
@nb.njit()
def pi(SSTC, MSL, P, TC, R, CKCD=0.9, ascent_flag=0, diss_flag=1, V_reduc=0.8, ptop=50, miss_handle=1):
    #     function [VMAX,PMIN,IFL,TO,OTL] = pi(SSTC,MSL,P,TC,R,CKCD=0.9,ascent_flag=0,diss_flag=1,V_reduc=0.8,ptop=50,miss_handle=0)
    #
    #   ***    This function calculates the maximum wind speed         ***
    #   ***             and mimimum central pressure                   ***
    #   ***    achievable in tropical cyclones, given a sounding       ***
    #   ***             and a sea surface temperature.                 ***
    #   这个函数计算热带气旋中可达到的最大风速和最小中心气压，给出一个探测值和海面温度。
    #   Thermodynamic and dynamic technical backgrounds (and calculations) are found in Bister
    #   and Emanuel (2002; BE02) and Emanuel's "Atmospheric Convection" (E94; 1994; ISBN: 978-0195066302)
    #
    #   热力学和动力学技术背景(和计算)可以在Bister和Emanuel (2002;BE02)和Emanuel的“大气对流”(E94;1994;ISBN: 978 - 0195066302)
    #  INPUT:   SSTC: Sea surface temperature (C)
    #           SSTC:海面温度(C)
    #           MSL: Mean Sea level pressure (hPa)
    #           MSL:平均海平面气压(hPa)
    #           P, TC, R:一维数组
    #           含压(hPa)、温度(C)、混合比(g/kg)。阵列的排列必须使最低的索引对应于最低的模型级别，指数的增加对应于压力的减少。
    #           温度至少是对流层顶的温度，最好是平流层低层的温度，但是边界层以上的混合比并不重要。缺失的混合比例可以用零代替
    #
    #           CKCD: C_k与C_D的比值(无单位数)，即焓和动量通量交换系数的比值(例如见Bister和Emanuel 1998, EQN)。17 - 18)。
    #           更多关于CK/CD的讨论见于Emanuel(2003)。默认值为0.9，如Wing等人(2015)


    #           ascent_flag:可调常数分数(无单位分数)
    #           其中0=可逆上升(默认值)，1=伪绝热上升
    #
    #           diss_flag:可调开关整数(flag integer;0或1)
    #             表示耗散加热是允许的(默认值)还是不允许的0。参见Bister和Emanuel(1998)中包含的耗散加热。

    #           v_reduce:可调常数分数(无单位分数)
    #             减少梯度风到10米风见Emanuel(2000)和Powell(1980)。默认是0.8
    #
    #           ptop: 测深低于时忽略的压力(百帕)
    #
    #           miss_handle:决定在CAPE计算中如何处理缺失值(NaN)的标志
    #             如果= 0 (BE02默认值)，配置文件中的NaN值被忽略，PI仍然被计算
    #             如果 = 1，给定NaN值PI将被设为missing (If =3)
    #             注意:如果任何缺失值在最低有效级别和ptop之间，那么PI将自动设置为缺失(If =3)
    #
    #  OUTPUT:
    #           VMAX为最大地面风速(m/s)
    #             减少通过v_reducc反射表面阻力
    #
    #           PMIN为最小中心压力(hPa)
    #

    #           IFL是一个标志:
    #           值为1表示OK;
    #           值为0表示不收敛;
    #           值为2表示CAPE例程未能收敛;
    #           值3表示CAPE例程由于输入中缺少数据而失败;
    #
    #           TO为出流温度，K
    #

    #           OTL为出水温度水平(hPa)，定义为发现的中性浮力温度水平
    #           即浮力实际上是一个在海平面压力下饱和的气团的同等条件
    #

    # convert units
    SSTK = utilities.T_Ctok(SSTC)  # SST in kelvin
    T = utilities.T_Ctok(TC)  # Temperature profile in kelvin
    R = R * 0.001  # Mixing ratio profile in g/g

    # 检查1:sst是否超过5C?如果没有，设置IFL=0并返回缺失的PI
    if (SSTC <= 5.0):
        VMAX = np.nan
        PMIN = np.nan
        IFL = 0
        TO = np.nan
        OTL = np.nan
        return (VMAX, PMIN, IFL, TO, OTL)

    # 检查2:温度曲线是否超过100K?如果没有，设置IFL=0并返回缺失的PI
    # if (np.min(T) <= 100):#返回列表中最小的
    #     VMAX = np.nan
    #     PMIN = np.nan
    #     IFL = 0
    #     TO = np.nan
    #     OTL = np.nan
    #     return (VMAX, PMIN, IFL, TO, OTL)

    # 设置缺失混合比例为零g/g，遵循Kerry的BE02算法
    R[np.isnan(R)] = 0.


    # 饱和水汽压
    # 来自克劳修斯-克拉珀龙关系/奥古斯特-罗氏-马格纳斯公式
    ES0 = utilities.es_cc(SSTC)

    # 定义包裹的起吊水平(第一个压力水平)
    NK = 0


    TP = T[NK]
    RP = R[NK]
    PP = P[NK]
    result = cape(TP, RP, PP, T, R, P, ascent_flag, ptop, miss_handle)
    CAPEA = result[0]
    IFLAG = result[3]
    # 如果CAPE函数触发了一个标志，将输出IFL设置为它
    if (IFLAG != 1):
        IFL = int(IFLAG)

    #
    #   ***开始迭代寻找最小压力***
    #

    # 设置循环计数器和初始条件
    NP = 0  # loop counter
    PM = 970.0
    PMOLD = PM  # initial condition from minimum pressure
    PNEW = 0.0  # initial condition from minimum pressure
    IFL = int(1)  # Default flag for CAPE calculation

    # 循环直到收敛或保释
    while (np.abs(PNEW - PMOLD) > 0.5):

        #
        #   ***找到CAPE在最大风速半径***
        #
        TP = T[NK]
        PP = min([PM, 1000.0])
        # find the mixing ratio with the average of the lowest level pressure and MSL
        RP = constants.EPS * R[NK] * MSL / (PP * (constants.EPS + R[NK]) - R[NK] * MSL)
        result = cape(TP, RP, PP, T, R, P, ascent_flag, ptop, miss_handle)
        CAPEM = result[0]
        IFLAG = result[3]
        # if the CAPE function tripped a different flag, set the output IFL to it
        if (IFLAG != 1):
            IFL = int(IFLAG)

        #
        #  ***  Find saturation CAPE at radius of maximum winds    ***
        #  *** Note that TO and OTL are found with this assumption ***
        #
        TP = SSTK
        PP = min([PM, 1000.0])
        RP = utilities.rv(ES0, PP)
        result = cape(TP, RP, PP, T, R, P, ascent_flag, ptop, miss_handle)
        CAPEMS, TOMS, LNBS, IFLAG = result
        # if the CAPE function tripped a flag, set the output IFL to it
        if (IFLAG != 1):
            IFL = int(IFLAG)
        # Store the outflow temperature and level of neutral bouyancy at the outflow level (OTL)
        TO = TOMS
        OTL = LNBS
        # Calculate the proxy for TC efficiency (BE02, EQN. 1-3)
        RAT = SSTK / TO
        # If dissipative heating is "off", TC efficiency proxy is set to 1.0 (BE02, pg. 3)
        if (diss_flag == 0):
            RAT = 1.0

        #
        #  ***  Initial estimate of pressure at the radius of maximum winds  ***
        #
        RS0 = RP
        # Lowest level and Sea-surface Density Temperature (E94, EQN. 4.3.1 and 6.3.7)
        TV0 = utilities.Trho(T[NK], R[NK], R[NK])
        TVSST = utilities.Trho(SSTK, RS0, RS0)
        # Average Surface Density Temperature, e.g. 1/2*[Tv(Tsfc)+Tv(sst)]
        TVAV = 0.5 * (TV0 + TVSST)
        # Converge toward CAPE*-CAPEM (BE02, EQN 3-4)
        CAT = (CAPEM - CAPEA) + 0.5 * CKCD * RAT * (CAPEMS - CAPEM)
        CAT = max([CAT, 0.0])
        # Iterate on pressure
        PNEW = MSL * np.exp(-CAT / (constants.RD * TVAV))

        #
        #   ***  Test for convergence (setup for possible next while iteration)  ***
        #
        # store the previous step's pressure       
        PMOLD = PM
        # store the current step's pressure
        PM = PNEW
        # increase iteration count in the loop
        NP += 1

        #
        #   ***   If the routine does not converge, set IFL=0 and return missing PI   ***
        #
        if (NP > 200) or (PM < 400):
            VMAX = np.nan
            PMIN = np.nan
            IFL = 0
            TO = np.nan
            OTL = np.nan
            return (VMAX, PMIN, IFL, TO, OTL)

    # Once converged, set potential intensity at the radius of maximum winds
    CATFAC = 0.5 * (1. + 1 / constants.b)
    CAT = (CAPEM - CAPEA) + CKCD * RAT * CATFAC * (CAPEMS - CAPEM)
    CAT = max([CAT, 0.0])

    # Calculate the minimum pressure at the eye of the storm
    # BE02 EQN. 4
    PMIN = MSL * np.exp(-CAT / (constants.RD * TVAV))

    # 计算最大风速半径下的潜在强度
    # BE02 EQN。3、减少某些部分(默认20%)，以解释减少
    # 10米风速的梯度风速(Emanuel 2000, Powell 1980)
    FAC = max([0.0, (CAPEMS - CAPEM)])
    VMAX = V_reduc * np.sqrt(CKCD * RAT * FAC)

    # 返回计算出的输出到上面的程序级别
    return (VMAX, PMIN, IFL, TO, OTL)
