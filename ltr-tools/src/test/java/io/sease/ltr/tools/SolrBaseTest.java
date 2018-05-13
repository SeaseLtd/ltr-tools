package io.sease.ltr.tools;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.junit.After;

import java.io.File;

import static java.lang.ClassLoader.getSystemClassLoader;

/**
 * Supertype layer for all Solr integration tests.
 *
 * @author abenedetti
 */
public class SolrBaseTest {
    protected CoreContainer solrNodesContainer;
    protected SolrClient embeddedSolrServer;

    /**
     * Test Solr instance shutdown procedure.
     *
     * @throws Exception hopefully never.
     */
    @After
    public  void tearDown() throws Exception {
        if (embeddedSolrServer != null) {
            embeddedSolrServer.deleteByQuery("*:*");
            embeddedSolrServer.commit();
            embeddedSolrServer.close();
            solrNodesContainer.shutdown();
        }
    }

    /**
     * Initialises the test Solr instance.
     *
     * @param collection the Solr collection.
     */
    protected void initSolrNode(final String collection) {
        final String solrHomePath = new File("build/solr").getPath();
        solrNodesContainer = new CoreContainer(solrHomePath);
        solrNodesContainer.load();
        embeddedSolrServer = new EmbeddedSolrServer(solrNodesContainer, collection);
    }

    /**
     * Returns the path associated with the given resource name.
     *
     * @param resourceName the resource name.
     * @return the path associated with the given resource name.
     */
    protected String getResourcePath(final String resourceName) {
        return getSystemClassLoader().getResource(resourceName).getPath();
    }
}
