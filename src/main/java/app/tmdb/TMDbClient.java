package app.tmdb;


import app.dtos.CreditsDTO;
import app.dtos.MovieDTO;
import app.exceptions.ApiException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TMDbClient {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String baseUrl;

    public TMDbClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();

        this.apiKey = Utils.getPropertyValue(
                "TMDB_API_KEY",
                "config.properties"
        );

        this.baseUrl = Utils.getPropertyValue(
                "TMDB_BASE_URL",
                "config.properties"
        );
    }

    public MovieDTO getMovie(long movieId) {

        String url = baseUrl + "/movie/" + movieId
                + "?api_key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new ApiException(
                        response.statusCode(),
                        "TMDb request failed: " + response.body()
                );
            }

            return objectMapper.readValue(
                    response.body(),
                    MovieDTO.class
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ApiException(
                    500,
                    "TMDb request was interrupted."
            );

        } catch (IOException e) {
            throw new ApiException(
                    500,
                    "Could not fetch movie from TMDb: "
                            + e.getMessage()
            );
        }
    }

    public CreditsDTO getCredits(long movieId) {

        String url = baseUrl + "/movie/" + movieId
                + "/credits?api_key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new ApiException(
                        response.statusCode(),
                        "TMDb credits request failed: " + response.body()
                );
            }

            return objectMapper.readValue(
                    response.body(),
                    CreditsDTO.class
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ApiException(
                    500,
                    "TMDb credits request was interrupted."
            );

        } catch (IOException e) {
            throw new ApiException(
                    500,
                    "Could not fetch credits from TMDb: "
                            + e.getMessage()
            );
        }
    }
}