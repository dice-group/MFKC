# A High-Performance Approach to String Similarity using Most Frequent K Characters

## Abstract

The Data Web has been growing significantly over the last decades. Thus, linking resources across heterogeneous knowledge bases becomes an increasingly difficult problem, in particular w.r.t. the runtime of link discovery tasks. Consequently, it is of utmost importance to provide time-efficient approaches for link discovery on the Data Web. While a number of scalable approaches have been developed for various measures, the most frequent k characters measure has not been tackled in previous works. We hence present a sequence of filters that allow discarding comparisons when executing bounded similarity computations without losing any recall. Therewith, we can reduce the runtime of bounded similarity computations by approximately 70%.  Our experiments with a single-threaded, a parallel and a GPU implementation of our filters suggests that our approach scales well even when dealing with millions of potential comparisons.

## Pre-requisites

* Java 8+ (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Jena API (https://jena.apache.org/download/index.cgi).

## How to run

Run the `main` method in the following classes to see the results.

* **MFKC.java** is the implementation of the algorithm.
* **MFKC_Parallel.java** is the parallel implementation using Java 8 resources.
* In **MFKC_Sep.java**, the filters are executed separately.
* **ExperimentMFKC.java** contains all experiments.

Datasets used:

* **dbPediaPlaces.tsv** is the dataset from DBpedia
* **linkedGeoPlaces_million.tsv** is the dataset from LinkedGeoData
* **dbpedia_yago_labels_10k.tsv** contains label alignments - [download](https://s3.eu-central-1.amazonaws.com/tommaso-soru/files/dbpedia_yago_labels_10k.tsv)
