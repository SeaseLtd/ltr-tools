package ab.ltr.tools;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.junit.After;

import java.io.IOException;

public class SolrBaseTest {
    public final String SOLR_HOME = "solr";

    protected CoreContainer solrNodesContainer;
    protected SolrClient embeddedSolrServer;

    @After
    public  void tearDown() throws IOException, SolrServerException {
        if (embeddedSolrServer != null) {
            embeddedSolrServer.deleteByQuery("*:*");
            embeddedSolrServer.commit();
            embeddedSolrServer.close();
            solrNodesContainer.shutdown();
        }
    }

    protected void initSolrNode(String collection) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String solrHomePath = classLoader.getResource(SOLR_HOME).getPath();
        solrNodesContainer = new CoreContainer(solrHomePath);
        solrNodesContainer.load();
        embeddedSolrServer = new EmbeddedSolrServer(solrNodesContainer, collection);
    }
}
