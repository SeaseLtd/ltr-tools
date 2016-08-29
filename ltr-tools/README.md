# Learning To Rank Tools Project

## Description
The ltr-tools is a command line interface to run a set of utility tasks to visualize, debug and understand better Learning To Rank models and training sets.
The ltr-tools project is meant to work with a Solr backend, related configuration are in the configuration project ( and they are used in the tests).

Tools available so far :

1) Model Indexer

This tool allow to index the splitting point of your lambdaMART model.
Each splitting branch point is indexed.
This allow to visualize the most occurring features and thresholds in the branches.
Can be really useful to :
- find anomalies in the model and in the training set( e.g. thresholds that don't make sense)
- find important features and thresholds


2) Training Set Indexer

This tool allow to index the training set.
Each document is the a training sample with its feature vector.
It is really useful to explore and possibly visualize the training set.
It could be important also to validate specific leaf paths from the model

3) Top Scoring Leaves Viewer

This tool visits the lambdaMART model and prints the top scoring leaves.
It is really useful to find out the most promising paths in the trees of the ensemble.
Can be used to discuss with the business people if the most promising paths make sense in the domain .

## BUILD ( includes Unit Tests )
gradle build

## UNIT TESTS
gradle test

## Reports
/ltr-tools/build/reports/test/index.html
