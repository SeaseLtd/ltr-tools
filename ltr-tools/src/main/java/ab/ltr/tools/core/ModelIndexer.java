package ab.ltr.tools.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static ab.ltr.tools.config.JsonModelFields.FEATURE_SPLIT;
import static ab.ltr.tools.config.JsonModelFields.LEFT;
import static ab.ltr.tools.config.JsonModelFields.RIGHT;
import static ab.ltr.tools.config.JsonModelFields.PARAMS;
import static ab.ltr.tools.config.JsonModelFields.NAME;
import static ab.ltr.tools.config.JsonModelFields.THRESHOLD_SPLIT;
import static ab.ltr.tools.config.JsonModelFields.TREES;
import static ab.ltr.tools.config.JsonModelFields.VALUE;
import static ab.ltr.tools.config.SolrFields.Models.FEATURE;
import static ab.ltr.tools.config.SolrFields.Models.MODEL_NAME;
import static ab.ltr.tools.config.SolrFields.Models.THRESHOLD;

/**
 * @author Alessandro
 * @date 28/08/2016
 */
public class ModelIndexer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelIndexer.class);

    private SolrClient solr;

    public ModelIndexer(String solrURL) {
        HttpSolrClient.Builder solrClientBuilder = new HttpSolrClient.Builder(solrURL);
        solr = solrClientBuilder.build();
    }

    public ModelIndexer() {
    }

    public void indexModel(String modelPath) throws IOException, SolrServerException {

        File jsonModel = new File(modelPath);
        ObjectMapper jacksonObjectMapper = new ObjectMapper();

        JsonNode model = jacksonObjectMapper.readTree(jsonModel);

        String modelName = model.get(NAME).asText();
        ArrayNode trees = (ArrayNode) model.get(PARAMS).get(TREES);
        int treeCounter=1;
        int treeEnsembleSize=trees.size();
        for (JsonNode t : trees) {
            Iterator<JsonNode> elements = t.elements();
            JsonNode singleTree = null;
            while (elements.hasNext()) {
                singleTree = elements.next();
                visit(solr, modelName, singleTree);
                LOGGER.debug(String.format("Trees %s/%s indexed",treeCounter,treeEnsembleSize));
                treeCounter++;
            }
        }
        solr.commit();
        LOGGER.info("Model indexing complete");
    }

    private void visit(SolrClient solr, String modelName, JsonNode node) throws IOException, SolrServerException {
        if (node != null && !node.has(VALUE) && !(node instanceof TextNode)) {
            String feature = node.get(FEATURE_SPLIT).asText();
            double threshold = Double.parseDouble(node.get(THRESHOLD_SPLIT).asText());

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField(MODEL_NAME, modelName);
            doc.addField(FEATURE, feature);
            doc.addField(THRESHOLD, threshold);
            solr.add(doc);

            visit(solr, modelName, node.get(LEFT));
            visit(solr, modelName, node.get(RIGHT));
        }
    }

}
