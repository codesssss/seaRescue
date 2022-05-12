import netCDF4 as nc
import numpy as np
import pyPI
file = 'data/full_sample_output.nc'
# file = 'data/sample_data.nc'
# temperatures (T) and mixing ratios (R) on a pressure grid (P), sea surface
# temperatures (SST), and mean sea-level pressures (MSL)
# T=[1,2,3]
# print(pyPI.pi(1,101,1,400,T))
dataset = nc.Dataset(file)
print(dataset.variables.keys())
for item in dataset.variables.keys():
    print(dataset.variables[item])
# if type(dataset.variables['lnpi'][:]) == np.ma.core.MaskedConstant:
print(dataset.variables['lnpi'][:])
# <class 'netCDF4._netCDF4.Variable'>
# float64 lat(lat)
#     _FillValue: nan
#     standard_name: Latitude
#     units: degrees
# unlimited dimensions:
# current shape = (73,)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 lon(lon)
#     _FillValue: nan
#     standard_name: Longitude
#     units: degrees
# unlimited dimensions:
# current shape = (144,)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 month(month)
#     _FillValue: nan
#     standard_name: Month
#     units: Month Number
# unlimited dimensions:
# current shape = (12,)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 vmax(month, lat, lon)
#     _FillValue: nan
#     standard_name: Maximum Potential Intensity
#     units: m/s
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 pmin(month, lat, lon)
#     _FillValue: nan
#     standard_name: Minimum Central Pressure
#     units: hPa
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# int32 ifl(month, lat, lon)
#     standard_name: pyPI Flag
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on, default _FillValue of -2147483647 used
#
# <class 'netCDF4._netCDF4.Variable'>
# float64 t0(month, lat, lon)
#     _FillValue: nan
#     standard_name: Outflow Temperature
#     units: K
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 otl(month, lat, lon)
#     _FillValue: nan
#     standard_name: Outflow Temperature Level
#     units: hPa
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 sst(month, lat, lon)
#     _FillValue: nan
#     standard_name: Sea Surface Temperature
#     units: degrees C
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 p(p)
#     _FillValue: nan
#     standard_name: Atmospheric Pressure
#     units: hPa
# unlimited dimensions:
# current shape = (31,)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 t(month, p, lat, lon)
#     _FillValue: nan
#     standard_name: Atmospheric Temperature
#     units: degrees C
# unlimited dimensions:
# current shape = (12, 31, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 q(month, p, lat, lon)
#     _FillValue: nan
#     standard_name: Specific Humidity
#     units: g/kg
# unlimited dimensions:
# current shape = (12, 31, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 msl(month, lat, lon)
#     _FillValue: nan
#     standard_name: Mean Sea Level Pressure
#     units: hPa
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 lsm(lat, lon)
#     _FillValue: nan
#     standard_name: ERA-I Land-sea Mask
#     units: 0=Ocean, 1=Land
# unlimited dimensions:
# current shape = (73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 eff(month, lat, lon)
#     _FillValue: nan
#     standard_name: Tropical Cyclone Efficiency
#     units: unitless fraction
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 diseq(month, lat, lon)
#     _FillValue: nan
#     standard_name: Thermodynamic Disequilibrium
#     units: J/kg
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 lnpi(month, lat, lon)
#     _FillValue: nan
#     standard_name: Natural log(Potential Intensity)
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 lneff(month, lat, lon)
#     _FillValue: nan
#     standard_name: Natural log(Tropical Cyclone Efficiency)
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 lndiseq(month, lat, lon)
#     _FillValue: nan
#     standard_name: Natural log(Thermodynamic Disequilibrium)
# unlimited dimensions:
# current shape = (12, 73, 144)
# filling on
# <class 'netCDF4._netCDF4.Variable'>
# float64 lnCKCD()
#     _FillValue: nan
#     standard_name: Natural log(Ck/CD)
#     units: unitless constant
# unlimited dimensions:
# current shape = ()
# filling on
#
# Process finished with exit code 0
