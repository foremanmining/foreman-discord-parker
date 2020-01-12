package mn.foreman.parker.discord.bot;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link PoolsClient} contains utilities to get information from the Foreman
 * API.
 */
@Component
public class PoolsClient {

    /** The API token. */
    private final String apiToken;

    /** The API url. */
    private final String apiUrl;

    /** The template to use for making REST calls. */
    private final RestTemplate restTemplate;

    /**
     * A Foreman API client that reads from the pools API.
     *
     * @param restTemplate The template.
     * @param apiUrl       The API url.
     * @param apiToken     The API token.
     */
    @Autowired
    public PoolsClient(
            final RestTemplate restTemplate,
            @Value("${foreman.apiUrl}") final String apiUrl,
            @Value("${foreman.apiToken}") final String apiToken) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
    }

    /**
     * Creates the expected auth headers.
     *
     * @param apiToken The API token.
     *
     * @return The auth headers.
     */
    public static HttpHeaders toHeaders(final String apiToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Token " + apiToken);
        return headers;
    }

    /**
     * Gets all of the known pools from the Foreman API.
     *
     * @return The known pools.
     */
    public List<Pool> getAll() {
        final HttpEntity<String> entity =
                new HttpEntity<>(
                        toHeaders(
                                this.apiToken));
        final ResponseEntity<Pool[]> responseEntity =
                this.restTemplate.exchange(
                        this.apiUrl,
                        HttpMethod.GET,
                        entity,
                        Pool[].class);
        final Pool[] pools = responseEntity.getBody();
        return pools != null
                ? Arrays.asList(pools)
                : Collections.emptyList();
    }

    /** A model object representation of a pool from Foreman. */
    @Data
    @NoArgsConstructor
    public static class Pool {

        /** The stratum. */
        private String stratum;
    }
}
