package ab.ltr.tools;

import ab.ltr.tools.core.ModelIndexer;
import ab.ltr.tools.core.TopScoringLeavesViewer;
import ab.ltr.tools.core.TrainingSetIndexer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Alessandro
 * @date 26/08/2016
 */
public class CliLtrTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliLtrTools.class);

    public static final String SOLR_URL = "solrURL";
    public static final String TOOL = "tool";
    public static final String TRAINING_SET = "trainingSet";
    public static final String FEATURES = "features";
    public static final String CATEGORICAL_FEATURES = "categoricalFeatures";
    public static final String MODEL = "model";
    public static final String TRAINING_SET_INDEXER = "trainingSetIndexer";
    public static final String MODEL_INDEXER = "modelIndexer";
    public static final String TOP_SCORING_LEAVES_VIEWER = "topScoringLeavesViewer";
    public static final String TOP_LEAVES = "topK";
    public static final String HELP = "help";

    public static void main(String args[]) {
        Options options = initOptions();
        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption(HELP)) {
                formatter.printHelp("ltr-tools", options);
            } else {
                selectAndRunTool(line);
            }
        } catch (ParseException e) {
            LOGGER.error("CLI Parsing failed", e);
        } catch (IOException e) {
            LOGGER.error("IO error", e);
        } catch (SolrServerException e) {
            LOGGER.error("Problem accessing Solr", e);
        } catch (RuntimeException e) {
            LOGGER.error("Internal problem", e);
        }


    }

    private static void selectAndRunTool(CommandLine line) throws IOException, SolrServerException,IllegalArgumentException {
        final String toolSelected = line.getOptionValue(TOOL);
        final String modelPath = line.getOptionValue(MODEL);
        final int topK = NumberUtils.toInt(line.getOptionValue(TOP_LEAVES));
        final String solrURL = line.getOptionValue(SOLR_URL);
        final String trainingSetPath = line.getOptionValue(TRAINING_SET);
        final String featureMappingPath = line.getOptionValue(FEATURES);
        final String categoricalFeaturesPath = line.getOptionValue(CATEGORICAL_FEATURES);

        if(toolSelected==null){
            throw new RuntimeException("-tool option is mandatory!Please select a ltr tool");
        }

        switch (toolSelected) {
            case TRAINING_SET_INDEXER: {
                TrainingSetIndexer indexer = new TrainingSetIndexer(solrURL);
                indexer.indexTrainingSet(trainingSetPath, featureMappingPath, categoricalFeaturesPath);
            }
            break;
            case MODEL_INDEXER: {
                ModelIndexer modelIndexer = new ModelIndexer(solrURL);
                modelIndexer.indexModel(modelPath);
            }
            break;
            case TOP_SCORING_LEAVES_VIEWER: {
                TopScoringLeavesViewer leavesViewer = new TopScoringLeavesViewer(modelPath);
                final String topScoringLeaves = leavesViewer.printTopScoringLeaves(topK);
                System.out.print(topScoringLeaves);
            }
            break;
        }
    }

    private static Options initOptions() {
        Options options = new Options();

        Option help = new Option(HELP, "print this message");

        Option tool = Option.builder(TOOL).argName("name")
            .hasArg()
            .desc("run the selected tool : [modelIndexer,trainingSetIndexer,topScoringLeavesViewer]").build();

        Option trainingSetFile = Option.builder(TRAINING_SET).argName("file")
            .hasArg()
            .desc("use given training set file, the format supported is the RankLib one").build();

        Option featureMappingFile = Option.builder(FEATURES).argName("file")
            .hasArg()
            .desc("use given mapping file to resolve the feature ids to the feature name").build();

        Option categoricalFeatureFile = Option.builder(CATEGORICAL_FEATURES).argName("file")
            .hasArg()
            .desc("use given list of categorical features").build();

        Option modelFile = Option.builder(MODEL).argName("file")
            .hasArg()
            .desc("use given model").build();

        Option topK = Option.builder(TOP_LEAVES).argName("int")
            .hasArg()
            .desc("print the top scoring leaves up to the value given").build();

        Option solrURL = Option.builder(SOLR_URL).argName("URL")
            .hasArg()
            .desc("use given solr base URL").build();

        options.addOption(help);
        options.addOption(tool);
        options.addOption(trainingSetFile);
        options.addOption(featureMappingFile);
        options.addOption(categoricalFeatureFile);
        options.addOption(modelFile);
        options.addOption(topK);
        options.addOption(solrURL);

        return options;
    }
}
