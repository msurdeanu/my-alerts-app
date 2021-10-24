package org.myalerts.helper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class HttpRequestHelper {

    private final HttpClient.Builder clientBuilder = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(10));

    private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
        .timeout(Duration.ofSeconds(60));

    public HttpRequestHelper withClientVersion2() {
        clientBuilder.version(HttpClient.Version.HTTP_2);

        return this;
    }

    public HttpRequestHelper withClientConnectionTimeout(final long seconds) {
        clientBuilder.connectTimeout(Duration.ofSeconds(seconds));

        return this;
    }

    public HttpRequestHelper withRequestUri(final String uri) {
        requestBuilder.uri(URI.create(uri));

        return this;
    }

    public HttpRequestHelper withRequestHeader(final String name, final String value) {
        requestBuilder.header(name, value);

        return this;
    }

    public HttpRequestHelper withRequestTimeout(final long seconds) {
        requestBuilder.timeout(Duration.ofSeconds(seconds));

        return this;
    }

    public HttpResponse<String> doGetRequest() throws IOException, InterruptedException {
        return clientBuilder.build().send(requestBuilder.GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> doPostRequest(final String requestBody) throws IOException, InterruptedException {
        return clientBuilder.build().send(requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build(), HttpResponse.BodyHandlers.ofString());
    }

}