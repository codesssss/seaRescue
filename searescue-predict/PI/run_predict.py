
import xarray as xr
import pickle
from fastapi import FastAPI
from fastapi.responses import StreamingResponse

import matplotlib
import matplotlib.pyplot as plt
# load in pyPI modules
from pyPI import pi
from pyPI.utilities import *

# define the sample data locations
datdir = './data/'
_FN = datdir + 'sample_data.nc'
_mdrF = datdir + 'mdr.pk1'


def run_sample_dataset(fn, dim='p', CKCD=0.9):
    """ This function calculates PI over the sample dataset using xarray """

    # 打开文件
    ds = xr.open_dataset(fn)
    # calculate PI over the whole data set using the xarray universal function
    print(ds['sst'], ds['msl'], ds['p'], ds['t'], ds['q'])
    result = xr.apply_ufunc(
        pi,
        ds['sst'], ds['msl'], ds['p'], ds['t'], ds['q'],
        kwargs=dict(CKCD=CKCD, ascent_flag=0, diss_flag=1, ptop=50, miss_handle=1),
        input_core_dims=[
            [], [], ['p', ], ['p', ], ['p', ],
        ],
        output_core_dims=[
            [], [], [], [], []
        ],
        vectorize=True
    )

    # store the result in an xarray data structure储存结果
    vmax, pmin, ifl, t0, otl = result
    out_ds = xr.Dataset({
        'vmax': vmax,
        'pmin': pmin,
        'ifl': ifl,
        't0': t0,
        'otl': otl,
        # merge the state data into the same data structure
        'sst': ds.sst,
        't': ds.t,
        'q': ds.q,
        'msl': ds.msl,
        'lsm': ds.lsm,
    })

    # add names and units to the structure
    out_ds.vmax.attrs['standard_name'], out_ds.vmax.attrs['units'] = 'Maximum Potential Intensity', 'm/s'
    out_ds.pmin.attrs['standard_name'], out_ds.pmin.attrs['units'] = 'Minimum Central Pressure', 'hPa'
    out_ds.ifl.attrs['standard_name'] = 'pyPI Flag'
    out_ds.t0.attrs['standard_name'], out_ds.t0.attrs['units'] = 'Outflow Temperature', 'K'
    out_ds.otl.attrs['standard_name'], out_ds.otl.attrs['units'] = 'Outflow Temperature Level', 'hPa'

    # return the output from pi.py as an xarray data structure
    return out_ds


def run_sample_analyses(ds, _mdrF, CKCD=0.9):
    """ This function performs PI analyses over the sample dataset using xarray """

    # load the basins dictionary
    basins = pickle.load(open(_mdrF, "rb"))

    # calculate PI analyses over the whole data set using the xarray universal function
    efficiency = xr.apply_ufunc(
        pi_effiency,
        ds['sst'] + 273.15, ds['t0'],
        input_core_dims=[
            [], [],
        ],
        output_core_dims=[
            [],
        ],
        vectorize=True
    )

    diseq = xr.apply_ufunc(
        pi_diseq_resid,
        ds['vmax'], ds['sst'] + 273.15, ds['t0'],
        kwargs=dict(CKCD=CKCD),
        input_core_dims=[
            [], [], [],
        ],
        output_core_dims=[
            [],
        ],
        vectorize=True
    )

    result = xr.apply_ufunc(
        decompose_pi,
        ds['vmax'], ds['sst'] + 273.15, ds['t0'],
        kwargs=dict(CKCD=CKCD),
        input_core_dims=[
            [], [], [],
        ],
        output_core_dims=[
            [], [], [], [],
        ],
        vectorize=True
    )

    lnpi, lneff, lndiseq, lnCKCD = result

    out_ds = xr.Dataset({
        'eff': efficiency,
        'diseq': diseq,
        'lnpi': lnpi,
        'lneff': lneff,
        'lndiseq': lndiseq,
        'lnCKCD': lnCKCD[0, 0, 0]
    })

    # add names and units (where applicable)
    out_ds.eff.attrs['standard_name'], out_ds.eff.attrs['units'] = 'Tropical Cyclone Efficiency', 'unitless fraction'
    out_ds.diseq.attrs['standard_name'], out_ds.diseq.attrs['units'] = 'Thermodynamic Disequilibrium', 'J/kg'
    out_ds.lnpi.attrs['standard_name'] = 'Natural log(Potential Intensity)'
    out_ds.lneff.attrs['standard_name'] = 'Natural log(Tropical Cyclone Efficiency)'
    out_ds.lndiseq.attrs['standard_name'] = 'Natural log(Thermodynamic Disequilibrium)'
    out_ds.lnCKCD.attrs['standard_name'], out_ds.lnCKCD.attrs['units'] = 'Natural log(Ck/CD)', 'unitless constant'

    # return the output from pi.py as an xarray data structure
    return out_ds

app = FastAPI()

@app.get("/predict")
async def root():
    ds = xr.open_dataset(_FN)
    pi_ds = run_sample_dataset(_FN)
    # pi_ds.to_netcdf(datdir + 'raw_sample_output.nc')
    # print('...PI computation complete and saved\n')
    #
    # # Perform PI analyses over the whole dataset
    # print('Performing PI analyses...')
    diag_ds = run_sample_analyses(pi_ds, _mdrF, CKCD=0.9)

    # merge the arrays and save the output

    full_ds = xr.Dataset({
    'sst': ds.sst,
    't': ds.t,
    'q': ds.q,
    'msl': ds.msl,
    'lsm': ds.lsm,
    't0': pi_ds.t0,
    'otl': pi_ds.otl,
    'ifl': pi_ds.ifl,
    'vmax': pi_ds.vmax,
    'pmin': pi_ds.pmin,
    'eff': diag_ds.eff,
    'diseq': diag_ds.diseq,
    'lnpi': diag_ds.lnpi,
    'lneff': diag_ds.lneff,
    'lndiseq': diag_ds.lndiseq,
    'lnCKCD': diag_ds.lnCKCD
    })
    plt.rcParams['font.sans-serif'] = ['SimHei']  # 用来正常显示中文标签
    plt.rcParams['axes.unicode_minus'] = False  # 用来正常显示负号
    plt.figure(figsize=(10, 6))
    plt.contourf(full_ds.lon, full_ds.lat, full_ds.vmax.mean(dim=['month']))
    plt.xlabel(u"经度")
    plt.ylabel(u"纬度")
    plt.title(u'热带气旋潜在强度 (ms$^{-1}$)')
    plt.colorbar()
    plt.savefig('./test2.jpg')
    file_like = open('./test2.jpg', mode="rb")
    return StreamingResponse(file_like, media_type="image/jpg")


