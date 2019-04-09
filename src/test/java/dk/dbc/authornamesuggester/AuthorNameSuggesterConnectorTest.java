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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
        authorList.add(",Sophie Engberg Sonne");

        AuthorNameSuggestions authorNameSuggestions = connector.getSuggestions(authorList);
        assertThat(authorNameSuggestions.getAutNames().size(), is(1));
        assertThat(authorNameSuggestions.getAutNames().get(0).getInputName(), is(",Sophie Engberg Sonne"));
        assertThat(authorNameSuggestions.getAutNames().get(0).getAuthority(), is("19212041"));
        assertThat(authorNameSuggestions.getNerNames().size(), is(1));
        assertThat(authorNameSuggestions.getNerNames().get(0).getInputName(), is("Sophie Engberg Sonne"));
        assertThat(authorNameSuggestions.getNerNames().get(0).getAuthority(), is("19212041"));
    }

    @Test
    void getSuggestionsForTwoAuthorsTest() throws AuthorNameSuggesterConnectorException {
        List<String> authorList = new ArrayList<>();
        authorList.add("Maria Engberg Sonne");
        authorList.add("Simon Roliggaard");

        AuthorNameSuggestions authorNameSuggestions = connector.getSuggestions(authorList);
        assertThat(authorNameSuggestions.getAutNames().size(), is(1));
        assertThat(authorNameSuggestions.getAutNames().get(0).getInputName(), is("Simon Roliggaard"));
        assertThat(authorNameSuggestions.getAutNames().get(0).getAuthority(), is("19172422"));
        assertThat(authorNameSuggestions.getNerNames().size(), is(2));
        assertThat(authorNameSuggestions.getNerNames().get(0).getInputName(), is("Maria Engberg Sonne"));
        assertThat(authorNameSuggestions.getNerNames().get(0).getAuthority(), is(nullValue()));
        assertThat(authorNameSuggestions.getNerNames().get(1).getInputName(), is("Simon Roliggaard"));
        assertThat(authorNameSuggestions.getNerNames().get(1).getAuthority(), is("19172422"));

        final Map<String, String> expectedFields = new HashMap<>();
        expectedFields.put("700a", "Sonne");
        expectedFields.put("700h", "Maria Engberg");
        assertThat("exact-match-names size",
                authorNameSuggestions.getExactMatchNames().size(), is(1));
        assertThat("exact-match-names input-name",
                authorNameSuggestions.getExactMatchNames().get(0).getInputName(), is("Maria Engberg Sonne"));
        assertThat("exact-match-names fields",
                authorNameSuggestions.getExactMatchNames().get(0).getFields(), is(expectedFields));
        assertThat("exact-match-names term-fo",
                authorNameSuggestions.getExactMatchNames().get(0).getTermFo(), is("Sonne Maria Engberg"));
    }

    @Test
    void getSuggestionForSentence() throws AuthorNameSuggesterConnectorException {
        List<String> authorList = new ArrayList<>();
        authorList.add(",Bjarne B. Christensen, Generalsekretær I Sex & Samfund");

        AuthorNameSuggestions authorNameSuggestions = connector.getSuggestions(authorList);
        assertThat(authorNameSuggestions.getAutNames().size(), is(0));
        assertThat(authorNameSuggestions.getNerNames().size(), is(1));
        assertThat(authorNameSuggestions.getNerNames().get(0).getInputName(), is("Bjarne B . Christensen"));
        assertThat(authorNameSuggestions.getNerNames().get(0).getAuthority(), nullValue());
    }

}
