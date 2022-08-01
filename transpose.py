# -*- coding: utf-8 -*-
"""
Created on Thu Nov 21 13:16:13 2019

@author: Mahmoud
"""
import numpy as np
import pandas as pd
from numpy import genfromtxt

csv = pd.read_csv("GSECSV.csv")
# use skiprows if you want to skip headers
df_csv = pd.DataFrame(data=csv)
inverse = df_csv.T
print(inverse)

np.savetxt('GSE7124inverse.txt',inverse,fmt='%s',delimiter=',')


#transposed_csv.to_csv(r'transpose.csv')