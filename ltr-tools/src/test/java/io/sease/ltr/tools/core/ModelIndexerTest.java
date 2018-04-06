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
public class ModelIndexerTest extends SolrBaseTest{

    public static final String SAMPLE_MODELS = "models";

    private ModelIndexer modelIndexerToTest;

    @Before
    public void setUp(){
        super.initSolrNode(SAMPLE_MODELS);
        modelIndexerToTest=new ModelIndexer();
        setInternalState(modelIndexerToTest,"solr",embeddedSolrServer);
    }

    private String getResourcePath() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(SAMPLE_MODELS).getPath();
    }

    @Test
    public void indexModel_sampleModel_shouldIndexAllModelSplits() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String modelJson = "lambdaMARTModel1.json";

        modelIndexerToTest.indexModel(sampleModelsDirPath + "/" + modelJson);

        final QueryResponse allDocs = embeddedSolrServer.query(new SolrQuery("*:*"));
        final SolrDocumentList results = allDocs.getResults();

        assertThat(results.getNumFound(),is(4L));

        final SolrDocument split1 = results.get(0);
        assertThat(split1.getFieldValue(SolrFields.Models.MODEL_NAME),is("lambdaMARTModel1"));
        assertThat(split1.getFieldValue(SolrFields.Models.FEATURE),is("feature1"));
        assertThat(split1.getFieldValue(SolrFields.Models.THRESHOLD),is(0.5));

        final SolrDocument split2 = results.get(1);
        assertThat(split2.getFieldValue(SolrFields.Models.MODEL_NAME),is("lambdaMARTModel1"));
        assertThat(split2.getFieldValue(SolrFields.Models.FEATURE),is("feature2"));
        assertThat(split2.getFieldValue(SolrFields.Models.THRESHOLD),is(10.0));

        final SolrDocument split3 = results.get(2);
        assertThat(split3.getFieldValue(SolrFields.Models.MODEL_NAME),is("lambdaMARTModel1"));
        assertThat(split3.getFieldValue(SolrFields.Models.FEATURE),is("feature2"));
        assertThat(split3.getFieldValue(SolrFields.Models.THRESHOLD),is(0.8));

        final SolrDocument split4 = results.get(3);
        assertThat(split4.getFieldValue(SolrFields.Models.MODEL_NAME),is("lambdaMARTModel1"));
        assertThat(split4.getFieldValue(SolrFields.Models.FEATURE),is("feature1"));
        assertThat(split4.getFieldValue(SolrFields.Models.THRESHOLD),is(100.0));

    }

    @Test(expected = IOException.class)
    public void indexModel_notFoundModel_shouldThrowException() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String modelJson = "notFound.json";

        modelIndexerToTest.indexModel(sampleModelsDirPath + "/" + modelJson);
    }

    @Test(expected = IOException.class)
    public void indexModel_nullModel_shouldThrowException() throws IOException, SolrServerException {
        String sampleModelsDirPath = getResourcePath();
        String modelJson = null;

        modelIndexerToTest.indexModel(sampleModelsDirPath + "/" + modelJson);
    }
}
