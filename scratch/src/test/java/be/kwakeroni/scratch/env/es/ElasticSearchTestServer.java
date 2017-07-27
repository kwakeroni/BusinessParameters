package be.kwakeroni.scratch.env.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Created by kwakeroni on 27/07/17.
 */
public class ElasticSearchTestServer {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchTestServer.class);

    public static void main(String[] _) {

        ElasticSearchTestNode node = ElasticSearchTestNode.getRunningInstance();
        ElasticSearchTestData data = new ElasticSearchTestData();

        LOG.info("ElasticSearch test server started.");

        JOptionPane.showMessageDialog(null, String.format("ElasticSearch test server was started.%nPress OK to shutdown."), "ElasticSearch Test Server", JOptionPane.INFORMATION_MESSAGE);
    }

}
