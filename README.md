# Efficient-Processing-of-data-using-clouds-
 # Comparing MapReduce and Spark in Computing the PCC Matrix in Gene Co-expression Networks
## Overview

Correlation between gene expression profiles across multiple samples and the identification of inter-gene interactions is a critical technique for Co-expression networking. Due to the highly intensive processing of calculating the Pearson’s Correlation Coefficient, PCC, matrix, it often takes too much processing time to accomplish it. Therefore, in this work, Big Data techniques including MapReduce and Spark have been employed in a cloud environment to calculate the PCC matrix to find the dependencies between genes measured in high throughput microarray. A comparison between the running time of each phase in both of MapReduce and Spark approaches has been held. Both these techniques can dramatically speed up the computation allowing users to work with highly intensive processing. However, Spark has yielded a better performance than the MapReduce as it performs the processing in the main memory of the worker nodes and avoids the unnecessary I/O operations with the disks. Spark has yielded 80 times speed up for calculating the PCC of 22777 genes, however the MapReduce attained barely 8 times speed up.
The Pearson correlation coefficient is one of the most popular approaches in measuring the intensity of a linear association between two genes in a Gene Co-expression Network; hence, it has been applied here as a measure of dependencies between interacting genes. Let(X&Y) considered as a pair of gene expression profiles & (n) is the number of pairs in a gene expression data then the PCC can be calculated as PCC=(∑_(i=1)^n▒XY-  (∑_(i=1)^n▒X  ∑_(i=1)^n▒Y)/n   )/(√((∑_(i=1)^n▒〖X^2)-((∑_(i=1)^n▒X)^2/n) 〗) √((∑_(i=1)^n▒〖Y^2)-((∑_(i=1)^n▒Y)^2/n) 〗))
The computation of the Pearson's Correlation Coefficient between genes expressed in a gene expression matrix has a quadratic complexity. Therefore, we are suggesting here a parallel algorithm that will break the entire calculation of PCC matrix into components.
Each technique receives an input matrix comprising the gene expression values for each gene in different conditions, samples. These expression data are saved in a numeric matrix, with n columns, the number of genes, and m rows, the number of samples.
MapReduce:
consists of mappers which perform a large portion of work and reducers which perform a relatively small amount of computation which achieves the best performance. In the implementation of the Multithreaded Mapper implementation, threads from a thread pool invoke a queue of key value pairs in parallel. Multiple threads running a map task can help to speed up the tasks, based on availability of cores in the system. The map and shuffle task only receive a key-value pair input <k1, V1> and get outputs with other key-value pair in parallel. However, the reduce task receives the input from shuffle<K2, List<V2>> , which is a key and a list of values associated with that key. It get all this pairs of values associated with the i-th and j-th columns/variables and compute Pearson correlation. The output of the Reduce phase is (K3,V3) as the PCC between a pair of genes, the i-th and j-th genes. 

Spark: 
integrates the whole functionality in one program. This makes the tool easier to work with the users having only to launch the application once and avoid writing/reading from
disks because it is based on memory which makes it is faster than MapReduce. Spark uses a hybrid approach that combines MPI processes and threads. Each MPI process launches multiple threads to efficiently exploit the cores available on each node and to reduce the memory requirements. Regarding the workload distribution, as the PCC must be calculated for all gene pairs, the workload of this step can be represented with a 2D matrix, where both axes x and y include all genes. Further improvement in the performance of the proposed algorithm has been done by dividing the data into multiple partitions based on the number of threads and execute on available cores on multiple nodes and also only half of the matrix must be calculated Concretely  sum (i, i=1 to N) = N * (N + 1) / 2 which called triangular numbers   (Since  cor(A,B)=cor(B,A)) which  adding  more time for  saving. So, every pair of genes has been handled as a compute job, key them with a unique index, send to any compute unit available, and put them back into the result matrix using their key. 
  A.	Material 
The data employed in this research is a non-benchmarking  dataset for Liver cancer, Hepatocellular Carcinoma(HCC). It is a real data downloaded from GEO, Gene Expression Omnibus data bankand contains  thirty-five Microarray samples of HCC that have been downloaded from. These samples have been collected using the Affymetrix HG-U133A 2.0 platform. HCC is a complication of HCV (Hepatitis C virus) cirrhosis. The raw data has been preprocessed by the Affy package which is provided by the Bioconductor. 
B.	Platform
A cloud platform from IBM, IBM Analytics Engine, IAE has been utilized in this research. The IAE offers a parallel infrastructure for MapReduce and Spark on the IBM cloud. It permits users to upload their data in a layer called the IBM Cloud Object Storage and provides clusters of computing nodes to work on the uploaded data. The separation of the computing and storage layers helps in having more scalability and flexibility in analyzing. Analytics libraries and open-source packages has been employed. More details about the hardware and software employed in each technique 

Scientific international Paper :
  Nada Hassan Osman, Nagwan Abdel Samee, and Rania Ahmed Abdel Azeem Abul Seoud. "Comparing MapReduce and Spark in Computing the PCC Matrix in Gene Co-expression Networks.", International Journal of Advanced Computer Science and Applications,  Volume 12 Issue 9, 2021, pp.330-337. https://doi.org/10.14569/IJACSA.2021.0120937 
[Comparing_MapReduce_and_Spark_in_Computing_the_PCC_Matrix.pdf](https://github.com/Nadasawah90/Efficient-Processing-of-data-using-clouds-/files/9230497/Comparing_MapReduce_and_Spark_in_Computing_the_PCC_Matrix.pdf)
  # Simple Pearson Correlation Using Map/Reduce

This project demonstrates a very simple map/reduce example to compute the Pearson correlation coefficients for columns in a table. The project uses the [mincemeat map/reduce library for Python](https://github.com/michaelfairley/mincemeatpy) in order to show how the data pipeline works.

The map and reduce functions are inspired by (but corrected) [Computing Pearson Correlation using Hadoop’s Map/Reduce (M/R) Paradigm](https://vangjee.wordpress.com/2012/02/29/computing-pearson-correlation-using-hadoops-mapreduce-mr-paradigm/).


## Overview

I'm using a very simple dataset in CSV format which contains a header row and several data rows. Each data row contains an identifier and several values. We want to ignore the identifier and find correlations between the remaining rows.


## Computing Pearson Correlation Coefficient

The Pearson correlation coefficient between two columns is computed using the following algorithm (note that this contains redundant parenthesis for clarity):

    numerator =
        sum of products - (product of sums / count)
    denominator =
        sqrt (
            ( (sum of x^2) - (sum of x)^2 / count) ^ 2
            *
            ( (sum of y^2) - (sum of y)^2 / count) ^ 2
        )
    result =
        numerator / denominator

A sample R script has been included along with the resulting output to see the target values we are trying to compute. For this small dataset even R is overkill, but this helps us validate that our algorithm computes the right answers. See `correlate.r` and `correlate.r.Rout` along with `data.csv` to view our expected answers.


## Reading the Data

Load the CSV file. Parse the header row for column names separate out the data rows.

    with open('data.csv') as infile:
        lines = infile.read().split('\n')
        headers = lines[0].split(',')
        lines = [ line.split(',') for line in lines[1:-1] ]
    
    datasource = { line[0]: [ float(x) for x in line[1:] ] for line in lines }


## Mapper

The mapper receives rows from the dataset and emits one key:value pair for each combination of values in that row. The key for each output pair is the pair of indices of the values, and the value is the pair of values themselves. For instance, if the row in question contains 7 in position 1 and 3 in position 4 then there should be one row emitted with key `(1, 4)` (the indices) and value
`(7, 3)`.

Since row `p1` contains 1, 1, 3, and -1, the following key:value pairs should be emitted by the mapper for this row:

    (0,1): (1,1)
    (0,2): (1,3)
    (0,3): (1,-1)
    (1,2): (1,3)
    (1,3): (1,-1)
    (2,3): (3,-1)

Note that order doesn't matter, we simply enumerate them in this order so that it's easy to write and easy to verify.


## Reducer

The reducer receives key:value-list pairs for the results of the map function. In our case, the reducer will be called once for each unique index pair generated by the mappers and the value list for each of these will be a list of all the value pairs associated with that index pair. In this way we are receiving two columns as pairwise rows down the table.

The reducer simply follows the formula given earlier to compute the coefficient for those two rows and outputs the coefficient as a single value.


## Using Mincemeat

The `mincemeat` library for Python makes testing distributed map/reduce jobs much easier, as it takes care of all the plumbing for us allowing us to focus on just the mapper and reducer function.

To run the server (our script):

    python <scriptname> <datafilename> <outputfilename>

To run the worker(s):

    python path/to/mincemeat.py -p <password> <hostname>

**Note**: change `path/to/mincemeat.py` to be whatever path gets you to the `mincemeat` repo mentioned above. Where this appears will depend on how you install it.

Since we're running everthing on localhost and this computes extremely fast for our small dataset, we only need to run these two lines (each in separate terminal windows):

    python correlate.py data1.csv data1.out
    python path/to/mincemeat.py -p changeme localhost

The output will be pairs of columns by name (`a`, `b`, `c`, and `d`) and the correlation coefficient between those two columns.


## Validation

An additional script has been written to generate files with the expected output of the correlation computations. This script uses the `pandas` library to show how to perform the same analysis quickly in a few lines of code (the point of our algorithm is not to be performant, but rather to demonstrate the algorithm). It also creates files we can `diff` against to compare our output to the output from a trusted source.

To run it type:

    python validate.py <datafilename> <outputfilename>

The expected output for data1.csv and data2.csv has already been computed and included.


