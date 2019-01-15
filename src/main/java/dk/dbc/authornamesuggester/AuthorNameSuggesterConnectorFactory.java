/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.authornamesuggester;

import dk.dbc.httpclient.HttpClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.Client;

@ApplicationScoped
public class AuthorNameSuggesterConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorNameSuggesterConnectorFactory.class);

    public static AuthorNameSuggesterConnector create(String informediaBaseUrl) {
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating AuthorNameSuggesterConnector for: {}", informediaBaseUrl);
        return new AuthorNameSuggesterConnector(client, informediaBaseUrl);
    }

    public static AuthorNameSuggesterConnector create(String informediaBaseUrl, AuthorNameSuggesterConnector.TimingLogLevel level) {
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating AuthorNameSuggesterConnector for: {}", informediaBaseUrl);
        return new AuthorNameSuggesterConnector(client, informediaBaseUrl, level);
    }

    @Inject
    @ConfigProperty(name = "AUTHOR_NAME_SUGGESTER_URL")
    private String baseURL;

    @Inject
    @ConfigProperty(name = "AUTHOR_NAME_SUGGESTER_TIMING_LOG_LEVEL", defaultValue = "INFO")
    private String level;

    AuthorNameSuggesterConnector connector;

    @PostConstruct
    public void initializeConnector() {
        connector = AuthorNameSuggesterConnectorFactory.create(baseURL, AuthorNameSuggesterConnector.TimingLogLevel.valueOf(level));
    }

    @Produces
    public AuthorNameSuggesterConnector getInstance() {
        return connector;
    }

    @PreDestroy
    public void tearDownConnector() {
        connector.close();
    }
}
