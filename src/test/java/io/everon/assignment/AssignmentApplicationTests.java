package io.everon.assignment;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * READ-ONLY!
 * This is an internal technical test used by Everon for initial verification of the solution.
 * We expect you to create your own tests with proper coverage of use-cases and layers.
 * Let's pretend it does not exist :)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AssignmentApplicationTests {

    private static final String API_ENDPOINT = "http://localhost:8000";

    @Test
    @DisplayName("Should expose API on " + API_ENDPOINT)
    void shouldReturnEvenForZero() {
        HttpUriRequest request = new HttpGet(API_ENDPOINT);

        try {
            final CloseableHttpResponse ignored = HttpClientBuilder.create().build().execute(request);
        } catch (IOException e) {
            Assertions.fail("Unexpected error occurred!", e);
        }
    }

    @Test
    @DisplayName("Should implement basic flow")
    void shouldImplementSpec() {
        final HttpPost post = post(API_ENDPOINT + "/chargingSessions", "{\"stationId\": \"ABC-12345\"}");

        try {
            final CloseableHttpResponse startSessionResponse = HttpClientBuilder.create().build().execute(post);
            final String responseStr = EntityUtils.toString(startSessionResponse.getEntity());

            final Matcher matcher = Pattern.compile(".+\"id\":\"([0-9|a-z|A-Z|-]+)\".+").matcher(responseStr);
            assertThat(matcher.matches()).isEqualTo(true);
            final String startedSessionId = matcher.group(1);

            final HttpPut put = put(API_ENDPOINT + "/chargingSessions/" + startedSessionId);
            final CloseableHttpResponse stopSessionResponse = HttpClientBuilder.create().build().execute(put);
            final String stopResponseStr = EntityUtils.toString(stopSessionResponse.getEntity());

            assertThat(stopResponseStr.replaceAll("\\s", ""))
                    .contains("\"id\":\"" + startedSessionId + "\"")
                    .contains("\"status\":\"FINISHED\"");

            final HttpGet httpGet = get(API_ENDPOINT + "/chargingSessions/summary");
            final CloseableHttpResponse summaryResponse = HttpClientBuilder.create().build().execute(httpGet);
            final String sessionSummary = EntityUtils.toString(summaryResponse.getEntity());

            await().atMost(3, TimeUnit.SECONDS).until(() -> {
                try {
                    assertThat(sessionSummary.replaceAll("\\s", ""))
                            .contains("\"totalCount\":2")
                            .contains("\"startedCount\":1")
                            .contains("\"stoppedCount\":1");
                } catch (Exception e) {
                    return false;
                }

                return true;
            });

        } catch (IOException e) {
            Assertions.fail("Unexpected error occurred!", e);
        }
    }

    private HttpPut put(String url) {
        HttpPut putRequest = new HttpPut(url);
        putRequest.setHeader("Accept", "application/json");
        putRequest.setHeader("Content-type", "application/json");
        return putRequest;
    }

    private HttpGet get(String url) {
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("Accept", "application/json");
        getRequest.setHeader("Content-type", "application/json");
        return getRequest;
    }

    private HttpPost post(String url, String json) {
        HttpPost postRequest = new HttpPost(url);
        StringEntity entity = new StringEntity(json, "UTF-8");
        postRequest.setEntity(entity);
        postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-type", "application/json");
        return postRequest;
    }
}
