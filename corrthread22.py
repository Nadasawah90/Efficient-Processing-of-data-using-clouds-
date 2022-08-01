from __future__ import print_function
import numpy as np
import pandas as pd
import time
from scipy.stats import pearsonr
from pyspark import SparkContext, SparkConf
from scipy.sparse import coo_matrix
from numpy import array
from scipy.sparse import coo_matrix
from pyspark import SparkConf, SparkContext
from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils
import operator
from pyspark.sql import SQLContext
import pandas as pd
from pyspark.sql import SQLContext
import pyspark
from pyspark.sql import Row
import csv
from pyspark.ml.feature import StringIndexer, VectorAssembler, OneHotEncoder

from pyspark.ml import Pipeline
from pyspark.mllib.evaluation import RegressionMetrics, BinaryClassificationMetrics
from pyspark.mllib.util import MLUtils
from pyspark.ml.feature import OneHotEncoder, StringIndexer, IndexToString, VectorAssembler
from pyspark.ml.classification import RandomForestClassifier
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
from pyspark.ml import Pipeline, Model
from pyspark.ml.feature import StandardScaler
from pyspark.ml.linalg import Vectors
from pyspark.ml.stat import Correlation

import numpy as np
import pandas as pd
from numpy import genfromtxt

csv = pd.read_csv("HCC.csv", skiprows=1)
# use skiprows if you want to skip headers
df_csv = pd.DataFrame(data=csv)
transposed_csv = df_csv.T
conv_arr= transposed_csv.values

conv_arr
nThreads = [8]
dt = np.zeros(len(nThreads))
for i in range(len(nThreads)):
    NMACHINES = nThreads[i]
    NPARTITIONS = NMACHINES*4
 
conf = (SparkConf()
            #.setMaster("local[%s]" % NMACHINES)
            #.setAppName("corr400")
            .set("spark.driver.maxResultSize", "10g"))
            #.set("spark.executor.memory", "20g")
            #.set("spark.driver.memory", "50g"))
sc = SparkContext.getOrCreate()
m_local = sc.broadcast(conv_arr[:,:])
indices = sc.parallelize(range(m_local.value.shape[1]))
cart = indices.cartesian(indices)
unique_pairs = cart.filter(lambda p: p[0] <= p[1]).repartition(NPARTITIONS)
def corr(pair):
        x = m_local.value[:, pair[0]]
        y = m_local.value[:, pair[1]]
        pcc = pearsonr(x, y)
        
        return (pair[0], pair[1], pcc[0], pcc[1])
results = unique_pairs.map(corr)
results

tmp = np.array(results.collect())

pcc_mat = coo_matrix((tmp[:,2], (tmp[:,0].astype(int), tmp[:,1].astype(int)))).todense()

#my_df = pd.DataFrame(pcc_mat)
#my_df.to_csv('result16000th.csv', index=False)

sc.stop()
    


