package io.sease.ltr.tools;

import io.sease.ltr.tools.core.ModelIndexer;
import io.sease.ltr.tools.core.TopScoringLeavesViewer;
import io.sease.ltr.tools.core.TrainingSetIndexer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Alessandro
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CliLtrTools.class})
public class CliLtrToolsTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

    @Test
    public void cli_testOption_shouldPrintHelp(){
        String[] args=new String[]{"-help"};
        CliLtrTools.main(args);
        final String expectedHelpMessage = "usage: ltr-tools\n" +
            " -categoricalFeatures <file>   use given list of categorical features\n" +
            " -features <file>              use given mapping file to resolve the\n" +
            "                               feature ids to the feature name\n" +
            " -help                         print this message\n" +
            " -model <file>                 use given model\n" +
            " -solrURL <URL>                use given solr base URL\n" +
            " -tool <name>                  run the selected tool :\n" +
            "                               [modelIndexer,trainingSetIndexer,topScoring\n" +
            "                               LeavesViewer]\n" +
            " -topK <int>                   print the top scoring leaves up to the\n" +
            "                               value given\n" +
            " -trainingSet <file>           use given training set file, the format\n" +
            "                               supported is the RankLib one\n";
        assertThat(outContent.toString(),is(expectedHelpMessage));
    }

    @Test
    public void cli_toolModelIndexer_shouldCreateModelIndexer() throws Exception {
        ModelIndexer modelIndexer=new ModelIndexer("sampleSolr");
        whenNew(ModelIndexer.class).withArguments("sampleSolr").
            thenReturn(modelIndexer);
        String[] args=new String[]{"-tool","modelIndexer","-solrURL","sampleSolr"};
        CliLtrTools.main(args);
        verifyNew(ModelIndexer.class).withArguments("sampleSolr");
    }

    @Test
    public void cli_toolModelIndexer_shouldPassCorrectParameters() throws Exception {
        ModelIndexer modelIndexer=mock(ModelIndexer.class);
        whenNew(ModelIndexer.class).withArguments("sampleSolr").
            thenReturn(modelIndexer);
        String[] args=new String[]{"-tool","modelIndexer","-solrURL","sampleSolr","-model","sampleModel"};
        ArgumentCaptor<String> modelPathCaptor = ArgumentCaptor.forClass(String.class);


        CliLtrTools.main(args);

        verify(modelIndexer, times(1)).indexModel(modelPathCaptor.capture());
        assertThat(modelPathCaptor.getValue(), is("sampleModel"));
    }

    @Test
    public void cli_toolTopLeavesViewer_shouldCreateTopLeavesViewer() throws Exception {
        TopScoringLeavesViewer viewer=new TopScoringLeavesViewer("sampleModel");
        whenNew(TopScoringLeavesViewer.class).withArguments("sampleModel").
            thenReturn(viewer);
        String[] args=new String[]{"-tool","topScoringLeavesViewer","-model","sampleModel"};
        CliLtrTools.main(args);
        verifyNew(TopScoringLeavesViewer.class).withArguments("sampleModel");
    }

    @Test
    public void cli_toolTopLeavesViewer_shouldPassCorrectParameters() throws Exception {
        TopScoringLeavesViewer viewerMock=mock(TopScoringLeavesViewer.class);
        whenNew(TopScoringLeavesViewer.class).withArguments("sampleModel").
            thenReturn(viewerMock);
        String[] args=new String[]{"-tool","topScoringLeavesViewer","-model","sampleModel","-topK","5"};
        ArgumentCaptor<Integer> topKCaptor = ArgumentCaptor.forClass(Integer.class);

        CliLtrTools.main(args);

        verify(viewerMock, times(1)).printTopScoringLeaves(topKCaptor.capture());
        assertThat(topKCaptor.getValue(), is(5));
    }

    @Test
    public void cli_toolTrainingSetIndexer_shouldCreateTrainingSetIndexer() throws Exception {
        TrainingSetIndexer trainingSetIndexer=new TrainingSetIndexer("sampleSolr");
        whenNew(TrainingSetIndexer.class).withArguments("sampleSolr").
            thenReturn(trainingSetIndexer);
        String[] args=new String[]{"-tool","trainingSetIndexer","-solrURL","sampleSolr"};
        CliLtrTools.main(args);
        verifyNew(TrainingSetIndexer.class).withArguments("sampleSolr");
    }

    @Test
    public void cli_toolTrainingSetIndexer_shouldPassCorrectParameters() throws Exception {
        TrainingSetIndexer trainingSetIndexerMock=mock(TrainingSetIndexer.class);
        whenNew(TrainingSetIndexer.class).withArguments("sampleSolr").
            thenReturn(trainingSetIndexerMock);
        String[] args=new String[]{"-tool","trainingSetIndexer","-solrURL","sampleSolr",
            "-trainingSet","sampleTrainingSet","-categoricalFeatures","sampleCategorical","-features","featuresMapping"};
        ArgumentCaptor<String> trainingSetCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> featuresCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> categoricalFeaturesCaptor = ArgumentCaptor.forClass(String.class);


        CliLtrTools.main(args);

        verify(trainingSetIndexerMock, times(1)).indexTrainingSet(trainingSetCaptor.capture(),featuresCaptor.capture(),categoricalFeaturesCaptor.capture());
        assertThat(trainingSetCaptor.getValue(), is("sampleTrainingSet"));
        assertThat(featuresCaptor.getValue(), is("featuresMapping"));
        assertThat(categoricalFeaturesCaptor.getValue(), is("sampleCategorical"));
    }

}
