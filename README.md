# Learning To Rank Tools Project

## Description
This project is containing both the ltr-tools cli and the solr configuration to provide backend search functionalities.
The main scope of this project will be to provide usable tools to help with Learning To Rank integrations and developments.

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
Each document is a training sample with its feature vector.
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

## Usage 
1) First of all you need your Solr running, install a Solr version ( 6.0 would be fine but it is not a requirement)
   and copy the configuration from the configuration project.
   This will create 2 collections : models and trainingSet.
   **models** will be used to index the LambdaMART models .
   **trainingSet** will be used to index your training set.

2) Help
   java -jar ltr-tools-1.0.jar -help

3) Model Indexer
   java -jar ltr-tools-1.0.jar -tool modelIndexer -model /models/lambdaMARTModel1.json  

4) Training set Indexer
   java -jar ltr-tools-1.0.jar -tool trainingSetIndexer -trainingSet /trainingSets/training1.txt -features /featureMappings/feature-mapping1.json -categoricalFeatures /feature/categoricalFeatures1.txt
   
5) Top Scoring Leaves Viewer
   java -jar ltr-tools-1.0.jar -tool topScoringLeavesViewer -model /models/lambdaMARTModel1.json -topK 10  

More details are available here : http://alexbenedetti.blogspot.co.uk/2016/08/solr-is-learning-to-rank-better-part-3.html