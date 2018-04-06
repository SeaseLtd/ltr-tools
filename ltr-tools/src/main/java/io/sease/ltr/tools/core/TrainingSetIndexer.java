package io.sease.ltr.tools.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.sease.ltr.tools.config.SolrFields.TrainingSet.RELEVANCY;

/**
 * @author Alessandro
 * @date 28/08/2016
 */
public class TrainingSetIndexer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingSetIndexer.class);
    public static final String CATEGORICAL_PREFIX = "cat_";
    private SolrClient solr;

    public TrainingSetIndexer(String solrURL) {
        HttpSolrClient.Builder solrClientBuilder = new HttpSolrClient.Builder(solrURL);
        solr = solrClientBuilder.build();
    }

    public TrainingSetIndexer() {
    }

    public void indexTrainingSet(String trainingSetPath, String featureId2featureNameMapPath, String categoricalFeaturesPath) throws IOException, SolrServerException {
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        Map<String, String> featureId2featureName = new HashMap<>();
        List<String> categoricalFeatureNames = new ArrayList<>();

        if (featureId2featureNameMapPath != null) {
            featureId2featureName = getFeatureId2featureNameMap(jacksonObjectMapper, featureId2featureNameMapPath);
        }
        if (categoricalFeaturesPath != null) {
            categoricalFeatureNames = Files.lines(Paths.get(categoricalFeaturesPath), Charset.defaultCharset()).collect(Collectors.toList());
        }

        final long trainingSetSize;
        try (Stream<String> lines = Files.lines(Paths.get(trainingSetPath))) {
            trainingSetSize = lines.count();
        }
        final long percentageUnit = trainingSetSize / 100;

        try (Stream<String> lines = Files.lines(Paths.get(trainingSetPath), Charset.defaultCharset())) {
            List<String> finalCategoricalFeatureNames = categoricalFeatureNames;
            Map<String, String> finalFeatureId2featureName = featureId2featureName;
            lines.forEach(new Consumer<String>() {
                double counter = 1;

                public void accept(String line) {
                    indexTrainingSample(solr, finalFeatureId2featureName, finalCategoricalFeatureNames, line);
                    if (counter % percentageUnit == 0) {
                        LOGGER.info(Math.round((counter / trainingSetSize) * 100) + "% Complete");
                    }
                    counter++;
                }
            });

        }
        solr.commit();
    }

    private Map<String, String> getFeatureId2featureNameMap(ObjectMapper jacksonObjectMapper, String headerPath) throws IOException {
        FileReader trainingSetReader = new FileReader(headerPath);
        BufferedReader headerReader = new BufferedReader(trainingSetReader);

        String firstLine = headerReader.readLine();
        headerReader.close();

        JsonNode jsonNode = jacksonObjectMapper.readTree(firstLine);

        return jacksonObjectMapper.convertValue(jsonNode, Map.class);
    }

    private void indexTrainingSample(SolrClient solr, Map<String, String> featureId2featureNameMap, List<String> categoricalFeatureNames, String line) {
        try {
            final String[] vectorComponents = line.replaceAll("\\s?+#.*$", "").split("\\s");
            if (vectorComponents.length > 1) {
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField(RELEVANCY, vectorComponents[0]);
                for (int i = 2; i < vectorComponents.length; i++) {
                    final String[] feature2value = vectorComponents[i].split(":");
                    final String featureId = feature2value[0];
                    String featureName = featureId2featureNameMap.getOrDefault(featureId, featureId);
                    String featureValue = feature2value[1];

                    final int lastSeparatorIndex = featureName.lastIndexOf("_");
                    if (lastSeparatorIndex != -1) {
                        String featureNamePrefix = featureName.substring(0, lastSeparatorIndex);
                        if (categoricalFeatureNames.contains(featureNamePrefix)) {
                            featureValue = featureName.substring(lastSeparatorIndex + 1);
                            featureName = CATEGORICAL_PREFIX + featureNamePrefix;
                        }
                    }
                    doc.addField(featureName, featureValue);
                }
                solr.add(doc);
            }
        } catch (SolrServerException e) {
            LOGGER.error("Solr error while indexing training samples", e);
        } catch (IOException e) {
            LOGGER.error("IO error while adding documents to Solr", e);
        }
    }

}
