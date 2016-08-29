# Configuration

## Description
This Project contains the solr configuration for the ltr-tools.
This configuration is deployed to the solr backend to provide search and analytics for learning to rank.
The only available task is to build an archive for the solr configuration.  

## Ltr-tools Auxiliary Solr
The current configurations specifies 2 collections for the auxiliary Solr.
models - it is used to search and do analitics on the ltr models.
trainingSet - it is used for search and analitics of the ltr training set.

## BUILD DISTRIBUTION
gradle distZip|distTar




