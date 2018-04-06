package io.sease.ltr.tools.core;

import io.sease.ltr.tools.SolrBaseTest;
import io.sease.ltr.tools.config.SolrFields;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.reflect.Whitebox.setInternalState;


/**
 * @author Alessandro
 * @date 28/08/2016
 */
public class TrainingSetIndexerTest extends SolrBaseTest{

    public static final String SAMPLE_TRAINING_SET = "trainingSet";

    private TrainingSetIndexer trainingSetIndexerToTest;

    @Before
    public void setUp(){
        super.initSolrNode(SAMPLE_TRAINING_SET);
        trainingSetIndexerToTest=new TrainingSetIndexer();
        setInternalState(trainingSetIndexerToTest,"solr",embeddedSolrServer);
    }

    private String getResourcePath() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(SAMPLE_TRAINING_SET).getPath();
    }

    @Test
    public void indexTrainingSet_trainingSetWithCategoricalFeature_shouldIndexCategoriesValues() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String trainingSet="trainingSet1";
        String trainingSetHeader="trainingSet-header.json";
        String trainingSetCategoricalList="trainingSet-categorical";

        trainingSetIndexerToTest.indexTrainingSet(sampleModelsDirPath+"/"+trainingSet,sampleModelsDirPath+"/"+trainingSetHeader,sampleModelsDirPath+"/"+trainingSetCategoricalList);

        final QueryResponse allDocs = embeddedSolrServer.query(new SolrQuery("*:*"));
        final SolrDocumentList results = allDocs.getResults();

        assertThat(results.getNumFound(),is(4L));

        final SolrDocument sample1 = results.get(0);
        assertThat(sample1.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(1.0));
        assertThat(sample1.getFieldValue("product_price"),is(300.0));
        assertThat(sample1.getFieldValue("product_rating"),is(4.0));
        assertThat(sample1.getFieldValue("cat_product_colour"),is("red"));
        assertThat(sample1.getFieldValue("cat_product_size"),is("S"));

        final SolrDocument sample2 = results.get(1);
        assertThat(sample2.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(4.0));
        assertThat(sample2.getFieldValue("product_price"),is(250.0));
        assertThat(sample2.getFieldValue("product_rating"),is(4.5));
        assertThat(sample2.getFieldValue("cat_product_colour"),is("green"));//green
        assertThat(sample2.getFieldValue("cat_product_size"),is("M"));

        final SolrDocument sample3 = results.get(2);
        assertThat(sample3.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(5.0));
        assertThat(sample3.getFieldValue("product_price"),is(450.0));
        assertThat(sample3.getFieldValue("product_rating"),is(5.0));
        assertThat(sample3.getFieldValue("cat_product_colour"),is("blue"));
        assertThat(sample3.getFieldValue("cat_product_size"),is("S"));

        final SolrDocument sample4 = results.get(3);
        assertThat(sample4.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(2.0));
        assertThat(sample4.getFieldValue("product_price"),is(200.0));
        assertThat(sample4.getFieldValue("product_rating"),is(3.5));
        assertThat(sample4.getFieldValue("cat_product_colour"),is("red"));
        assertThat(sample4.getFieldValue("cat_product_size"),is("L"));

    }

    @Test
    public void indexTrainingSet_trainingSetWithNoCategoricalFeature_shouldIndexPlainFeatures() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String trainingSet="trainingSet1";
        String trainingSetHeader="trainingSet-header.json";

        trainingSetIndexerToTest.indexTrainingSet(sampleModelsDirPath+"/"+trainingSet,sampleModelsDirPath+"/"+trainingSetHeader,null);

        final QueryResponse allDocs = embeddedSolrServer.query(new SolrQuery("*:*"));
        final SolrDocumentList results = allDocs.getResults();

        assertThat(results.getNumFound(),is(4L));

        final SolrDocument sample1 = results.get(0);
        assertThat(sample1.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(1.0));
        assertThat(sample1.getFieldValue("product_price"),is(300.0));
        assertThat(sample1.getFieldValue("product_rating"),is(4.0));
        assertThat(sample1.getFieldValue("product_colour_red"),is(1.0));
        assertThat(sample1.getFieldValue("product_size_S"),is(1.0));

        final SolrDocument sample2 = results.get(1);
        assertThat(sample2.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(4.0));
        assertThat(sample2.getFieldValue("product_price"),is(250.0));
        assertThat(sample2.getFieldValue("product_rating"),is(4.5));
        assertThat(sample2.getFieldValue("product_colour_green"),is(1.0));
        assertThat(sample2.getFieldValue("product_size_M"),is(1.0));

        final SolrDocument sample3 = results.get(2);
        assertThat(sample3.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(5.0));
        assertThat(sample3.getFieldValue("product_price"),is(450.0));
        assertThat(sample3.getFieldValue("product_rating"),is(5.0));
        assertThat(sample3.getFieldValue("product_colour_blue"),is(1.0));
        assertThat(sample3.getFieldValue("product_size_S"),is(1.0));

        final SolrDocument sample4 = results.get(3);
        assertThat(sample4.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(2.0));
        assertThat(sample4.getFieldValue("product_price"),is(200.0));
        assertThat(sample4.getFieldValue("product_rating"),is(3.5));
        assertThat(sample4.getFieldValue("product_colour_red"),is(1.0));
        assertThat(sample4.getFieldValue("product_size_L"),is(1.0));
    }

    @Test
    public void indexTrainingSet_noFeatureMappingSpecified_shouldIndexPlainFeaturesNames() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String trainingSet="trainingSet1";

        trainingSetIndexerToTest.indexTrainingSet(sampleModelsDirPath+"/"+trainingSet,null,null);

        final QueryResponse allDocs = embeddedSolrServer.query(new SolrQuery("*:*"));
        final SolrDocumentList results = allDocs.getResults();

        assertThat(results.getNumFound(),is(4L));

        final SolrDocument sample1 = results.get(0);
        assertThat(sample1.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(1.0));
        assertThat(sample1.getFieldValue("1"),is(300.0));
        assertThat(sample1.getFieldValue("2"),is(4.0));
        assertThat(sample1.getFieldValue("3"),is(1.0));
        assertThat(sample1.getFieldValue("6"),is(1.0));

        final SolrDocument sample2 = results.get(1);
        assertThat(sample2.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(4.0));
        assertThat(sample2.getFieldValue("1"),is(250.0));
        assertThat(sample2.getFieldValue("2"),is(4.5));
        assertThat(sample2.getFieldValue("4"),is(1.0));
        assertThat(sample2.getFieldValue("7"),is(1.0));

        final SolrDocument sample3 = results.get(2);
        assertThat(sample3.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(5.0));
        assertThat(sample3.getFieldValue("1"),is(450.0));
        assertThat(sample3.getFieldValue("2"),is(5.0));
        assertThat(sample3.getFieldValue("5"),is(1.0));
        assertThat(sample3.getFieldValue("6"),is(1.0));

        final SolrDocument sample4 = results.get(3);
        assertThat(sample4.getFieldValue(SolrFields.TrainingSet.RELEVANCY),is(2.0));
        assertThat(sample4.getFieldValue("1"),is(200.0));
        assertThat(sample4.getFieldValue("2"),is(3.5));
        assertThat(sample4.getFieldValue("3"),is(1.0));
        assertThat(sample4.getFieldValue("8"),is(1.0));
    }

    @Test(expected = IOException.class)
    public void indexTrainingSet_trainingSetNotFound_shouldThrowException() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String trainingSet="notFound";
        String trainingSetHeader="trainingSet-header.json";
        String trainingSetCategoricalList="trainingSet-categorical";

        trainingSetIndexerToTest.indexTrainingSet(sampleModelsDirPath+"/"+trainingSet,sampleModelsDirPath+"/"+trainingSetHeader,sampleModelsDirPath+"/"+trainingSetCategoricalList);
    }

    @Test(expected = IOException.class)
    public void indexTrainingSet_mappingNotFound_shouldThrowException() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String trainingSet="trainingSet1";
        String trainingSetHeader="notFound.json";
        String trainingSetCategoricalList="trainingSet-categorical";

        trainingSetIndexerToTest.indexTrainingSet(sampleModelsDirPath+"/"+trainingSet,sampleModelsDirPath+"/"+trainingSetHeader,sampleModelsDirPath+"/"+trainingSetCategoricalList);
    }

    @Test(expected = IOException.class)
    public void indexTrainingSet_categoricalFeaturesNotFound_shouldThrowException() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String trainingSet="trainingSet1";
        String trainingSetHeader="trainingSet-header.json";
        String trainingSetCategoricalList="notFound";

        trainingSetIndexerToTest.indexTrainingSet(sampleModelsDirPath+"/"+trainingSet,sampleModelsDirPath+"/"+trainingSetHeader,sampleModelsDirPath+"/"+trainingSetCategoricalList);
    }
}
