/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.authornamesuggester;

import com.github.tomakehurst.wiremock.WireMockServer;
import dk.dbc.httpclient.HttpClient;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AuthorNameSuggesterConnectorTestWireMockRecorder {

        /*
        Steps to reproduce wiremock recording:

        * Start standalone runner
            java -jar wiremock-standalone-{WIRE_MOCK_VERSION}.jar --proxy-all="{INFOMEDIA_BASE_URL}" --record-mappings --verbose

        * Run the main method of this class

        * Replace content of src/test/resources/{__files|mappings} with that produced by the standalone runner
     */

    public static void main(String[] args) throws AuthorNameSuggesterConnectorException {
        AuthorNameSuggesterConnectorTest.connector = new AuthorNameSuggesterConnector(
                AuthorNameSuggesterConnectorTest.CLIENT, "http://localhost:8080");
        final AuthorNameSuggesterConnectorTest InfomediaConnectorTest = new AuthorNameSuggesterConnectorTest();
        allTests(InfomediaConnectorTest);
    }

    private static void allTests(AuthorNameSuggesterConnectorTest connectorTest)
            throws AuthorNameSuggesterConnectorException {
        connectorTest.getSuggestionsForSingleAuthorTest();
        connectorTest.getSuggestionsForTwoAuthorsTest();
    }

}
