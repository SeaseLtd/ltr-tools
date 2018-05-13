package io.sease.ltr.tools.core;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author abenedetti
 */
public class TopScoringLeavesViewerTest {

    private static final String SAMPLE_MODELS = "models";

    private TopScoringLeavesViewer viewerToTest;

    private String getResourcePath() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(SAMPLE_MODELS).getPath();
    }

    @Test
    public void printTopLeaves_sampleModel_shouldPrintTopScoresWeighted() throws IOException, SolrServerException {
        final int topK = 10;

        String sampleModelsDirPath = getResourcePath();
        String modelJson = "lambdaMARTModel1.json";
        viewerToTest=new TopScoringLeavesViewer(sampleModelsDirPath + "/" + modelJson);
        String expectedTopScoringLeaves = "1000.0 -> feature2 > 0.8, feature1 <= 100.0, \n" +
            "200.0 -> feature2 <= 0.8, \n" +
            "80.0 -> feature1 <= 0.5, \n" +
            "75.0 -> feature1 > 0.5, feature2 > 10.0, \n" +
            "60.0 -> feature2 > 0.8, feature1 > 100.0, \n" +
            "50.0 -> feature1 > 0.5, feature2 <= 10.0, \n";


        String actualTopScoringLeaves = viewerToTest.printTopScoringLeaves( topK);

        assertThat(actualTopScoringLeaves, is(expectedTopScoringLeaves));
    }

    @Test
    public void printTopLeaves_sampleModel_shouldPrintOnlyTopKScoresWeighted() throws IOException, SolrServerException {
        final int topK = 3;

        String sampleModelsDirPath = getResourcePath();
        String modelJson = "lambdaMARTModel1.json";
        viewerToTest=new TopScoringLeavesViewer(sampleModelsDirPath + "/" + modelJson);
        String expectedTopScoringLeaves = "1000.0 -> feature2 > 0.8, feature1 <= 100.0, \n" +
            "200.0 -> feature2 <= 0.8, \n" +
            "80.0 -> feature1 <= 0.5, \n";

        String actualTopScoringLeaves = viewerToTest.printTopScoringLeaves( topK);

        assertThat(actualTopScoringLeaves, is(expectedTopScoringLeaves));
    }

    @Test(expected = IOException.class)
    public void printTopLeaves_modelNotFound_shouldThrowException() throws IOException, SolrServerException {
        final int topK = 3;

        String sampleModelsDirPath = getResourcePath();
        String modelJson = "notFound.json";
        viewerToTest=new TopScoringLeavesViewer(sampleModelsDirPath + "/" + modelJson);

        viewerToTest.printTopScoringLeaves(topK);
    }

    @Test(expected = RuntimeException.class)
    public void printTopLeaves_negativeTopK_shouldThrowException() throws IOException, SolrServerException {
        final int topK = -3;

        viewerToTest=new TopScoringLeavesViewer("");

        viewerToTest.printTopScoringLeaves(topK);
    }

    @Test(expected = RuntimeException.class)
    public void printTopLeaves_nullTopK_shouldThrowException() throws IOException, SolrServerException {
        final int topK = 0;

        viewerToTest=new TopScoringLeavesViewer("");

        viewerToTest.printTopScoringLeaves(topK);
    }
}
