package ab.ltr.tools.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeMap;

import static ab.ltr.tools.config.JsonModelFields.FEATURE_SPLIT;
import static ab.ltr.tools.config.JsonModelFields.LEFT;
import static ab.ltr.tools.config.JsonModelFields.PARAMS;
import static ab.ltr.tools.config.JsonModelFields.RIGHT;
import static ab.ltr.tools.config.JsonModelFields.THRESHOLD_SPLIT;
import static ab.ltr.tools.config.JsonModelFields.TREES;
import static ab.ltr.tools.config.JsonModelFields.VALUE;
import static ab.ltr.tools.config.JsonModelFields.WEIGHT;

/**
 * @author Alessandro
 * @date 28/08/2016
 */
public class TopScoringLeavesViewer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopScoringLeavesViewer.class);

    private String modelPath;

    public TopScoringLeavesViewer(String modelPath) {
        this.modelPath = modelPath;
    }

    public String printTopScoringLeaves(int topK) throws IOException, SolrServerException {
        if(topK<=0){
            throw new RuntimeException("top K scoring leaves to show must be > 0");
        }
        File jsonModel = new File(modelPath);
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        TreeMap<Double, String> score2path = new TreeMap<>();
        StringBuilder result=new StringBuilder();

        JsonNode model = jacksonObjectMapper.readTree(jsonModel);
        ArrayNode trees = (ArrayNode) model.get(PARAMS).get(TREES);
        int treeCounter=1;
        int treeEnsembleSize=trees.size();
        for (JsonNode tree : trees) {
            Double treeWeight=Double.parseDouble(tree.get(WEIGHT).asText());
            Iterator<JsonNode> elements = tree.elements();
            JsonNode rootNode = null;
            while (elements.hasNext()) {
                rootNode = elements.next();
            }
            populateScore2Paths(score2path,treeWeight, rootNode, new StringBuilder());
            LOGGER.debug(String.format("Trees %s/%s visited",treeCounter,treeEnsembleSize));
            treeCounter++;
        }

        final NavigableSet<Double> scoresDescendant = score2path.descendingKeySet();
        for (Double score : scoresDescendant) {
            result.append(score + " -> " + score2path.get(score)+"\n");
            topK--;
            if (topK <= 0) {
                break;
            }
        }
        LOGGER.info(String.format("Top Scoring leaves calculus complete"));
        return result.toString();
    }

    private void populateScore2Paths(TreeMap<Double, String> score2path, Double treeWeight, JsonNode node, StringBuilder path) throws IOException, SolrServerException {
        if (node != null && !node.has(VALUE)) {
            String feature = node.get(FEATURE_SPLIT).asText();
            String threshold = node.get(THRESHOLD_SPLIT).asText();

            populateScore2Paths(score2path, treeWeight, node.get(LEFT), new StringBuilder(path.toString()).append(feature + " <= " + threshold + ", "));
            populateScore2Paths(score2path, treeWeight, node.get(RIGHT), new StringBuilder(path.toString()).append(feature + " > " + threshold + ", "));
        } else if (node != null) {
            double leafScore = Double.parseDouble(node.get(VALUE).asText());
            if(treeWeight!=null){
                leafScore*=treeWeight;
            }
            score2path.put(leafScore, path.toString());
        }
    }


}
