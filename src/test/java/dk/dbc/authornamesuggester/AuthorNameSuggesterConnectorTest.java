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
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AuthorNameSuggesterConnectorTest {

    private static WireMockServer wireMockServer;
    private static String wireMockHost;

    final static Client CLIENT = HttpClient.newClient(new ClientConfig()
            .register(new JacksonFeature()));
    static AuthorNameSuggesterConnector connector;

    @BeforeAll
    static void startWireMockServer() {
        wireMockServer = new WireMockServer(options().dynamicPort()
                .dynamicHttpsPort());
        wireMockServer.start();
        wireMockHost = "http://localhost:" + wireMockServer.port();
        configureFor("localhost", wireMockServer.port());
    }

    @BeforeAll
    static void setConnector() {
        connector = new AuthorNameSuggesterConnector(CLIENT, wireMockHost, AuthorNameSuggesterConnector.TimingLogLevel.INFO);
    }

    @AfterAll
    static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    void getSuggestionsForSingleAuthorTest() throws AuthorNameSuggesterConnectorException {
        List<String> authorList = new ArrayList<>();
        authorList.add("Sophie Engberg Sonne");

        Suggestions suggestions = connector.getSuggestions(authorList);
        assertThat(suggestions.getAuthorityIds().size(), is(1));
        assertThat(suggestions.getAuthorityIds().get(0), is("19212041"));
    }

    // Disabled for now as wire mock returns the wrong result
    //@Test
    void getSuggestionsForTwoAuthorsTest() throws AuthorNameSuggesterConnectorException {
        List<String> authorList = new ArrayList<>();
        authorList.add("Sophie Engberg Sonne");
        authorList.add("Simon Roliggaard");

        Suggestions suggestions = connector.getSuggestions(authorList);

        System.out.println(suggestions);
        assertThat(suggestions.getAuthorityIds().size(), is(2));
        assertThat(suggestions.getAuthorityIds().get(0), is("19212041"));
        assertThat(suggestions.getAuthorityIds().get(1), is("19172422"));
    }

}
