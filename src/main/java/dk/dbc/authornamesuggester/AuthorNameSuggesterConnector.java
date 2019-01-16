/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.authornamesuggester;

import dk.dbc.httpclient.FailSafeHttpClient;
import dk.dbc.httpclient.HttpPost;
import dk.dbc.invariant.InvariantUtil;
import dk.dbc.util.Stopwatch;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AuthorNameSuggesterConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorNameSuggesterConnector.class);

    public enum TimingLogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private static final String URL_SUGGEST = "/api/names/suggest";
    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
            .retryOn(Collections.singletonList(ProcessingException.class))
            .retryIf((Response response) -> response.getStatus() == 404 || response.getStatus() == 502)
            .withDelay(10, TimeUnit.SECONDS)
            .withMaxRetries(6);

    private final FailSafeHttpClient failSafeHttpClient;
    private final String baseUrl;
    private final LogLevelMethod logger;

    /**
     * Returns new instance with default retry policy
     *
     * @param httpClient web resources client
     * @param baseUrl    base URL for author name suggester api endpoint
     */
    public AuthorNameSuggesterConnector(Client httpClient, String baseUrl) {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl, TimingLogLevel.INFO);
    }

    /**
     * Returns new instance with default retry policy
     *
     * @param httpClient web resources client
     * @param baseUrl    base URL for author name suggester api endpoint
     * @param level      log level
     */
    public AuthorNameSuggesterConnector(Client httpClient, String baseUrl, TimingLogLevel level) {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl, level);
    }

    /**
     * Returns new instance with custom retry policy
     *
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl            base URL for author name suggester api endpoint
     */
    public AuthorNameSuggesterConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl) {
        this(failSafeHttpClient, baseUrl, TimingLogLevel.INFO);
    }

    /**
     * Returns new instance with custom retry policy
     *
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl            base URL for author name suggester api endpoint
     * @param level              log level
     */
    public AuthorNameSuggesterConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl, TimingLogLevel level) {
        this.failSafeHttpClient = InvariantUtil.checkNotNullOrThrow(failSafeHttpClient, "failSafeHttpClient");
        this.baseUrl = InvariantUtil.checkNotNullNotEmptyOrThrow(baseUrl, "baseUrl");
        switch (level) {
            case TRACE:
                logger = LOGGER::trace;
                break;
            case DEBUG:
                logger = LOGGER::debug;
                break;
            case INFO:
                logger = LOGGER::info;
                break;
            case WARN:
                logger = LOGGER::warn;
                break;
            case ERROR:
                logger = LOGGER::error;
                break;
            default:
                logger = LOGGER::info;
                break;
        }
    }

    public void close() {
        failSafeHttpClient.getClient().close();
    }

    public AuthorNameSuggestions getSuggestions(List<String> authorList) throws AuthorNameSuggesterConnectorException{
        final String body = "[\"" + String.join("\",\"", authorList) + "\"]";

        return postRequest(URL_SUGGEST, body, AuthorNameSuggestions.class);
    }


    private <S, T> T postRequest(String path, S data, Class<T> returnType) throws AuthorNameSuggesterConnectorException {
        logger.log("POST {} with data {}", path, data);
        final Stopwatch stopwatch = new Stopwatch();
        try {
            final HttpPost httpPost = new HttpPost(failSafeHttpClient)
                    .withBaseUrl(baseUrl)
                    .withPathElements(path)
                    .withJsonData(data)
                    .withHeader("Accept", "application/json")
                    .withHeader("Content-type", "application/json");
            final Response response = httpPost.execute();
            assertResponseStatus(response, Response.Status.OK);
            return readResponseEntity(response, returnType);
        } finally {
            logger.log("POST {} took {} milliseconds", path,
                    stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));
        }
    }

    private <T> T readResponseEntity(Response response, Class<T> type)
            throws AuthorNameSuggesterConnectorException {
        final T entity = response.readEntity(type);
        if (entity == null) {
            throw new AuthorNameSuggesterConnectorException(
                    String.format("Author Name Suggester service returned with null-valued %s entity",
                            type.getName()));
        }
        return entity;
    }

    private void assertResponseStatus(Response response, Response.Status expectedStatus)
            throws AuthorNameSuggesterConnectorUnexpectedStatusCodeException {
        final Response.Status actualStatus =
                Response.Status.fromStatusCode(response.getStatus());
        if (actualStatus != expectedStatus) {
            throw new AuthorNameSuggesterConnectorUnexpectedStatusCodeException(
                    String.format("Author Name Suggester service returned with unexpected status code: %s",
                            actualStatus),
                    actualStatus.getStatusCode());
        }
    }

    @FunctionalInterface
    interface LogLevelMethod {
        void log(String format, Object... objs);
    }
}
