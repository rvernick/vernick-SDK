package com.lordQuotes.apis;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lordQuotes.models.Movie;
import com.lordQuotes.models.Quote;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import java.util.logging.Logger;

//import static java.util.logging.Level.*;

/**
 * MovieAccess provides the ability to easily access the available movies and their quotes.
 * In configuration, you must provide a base URL
 * Optionally, you may provide a number of retries for it to reattempt when there is a problem with the call
 */
public class MovieAccess {
    private String baseURL;
//    private Logger logger = Logger.getLogger("MovieAccess");
    private int retries = 3;

    /**
     * getMovies returns a list of Movie objects which can be used to access Quotes
     * @return List of Movies for which Quotes might be available
     */
    public List<Movie> getMovies() {
        HttpRequest request = makeRequest("/movie");

        HttpResponse<String> response = getResponseWithRetries(request, retries);
        if (response == null) {
            return null;
        }
        List<Movie> result = safelyParseMovies(response.body());
        result.stream().forEach(movie -> {movie.setMovieAccessSDK(this);});
        return result;
    }

    /**
     *
     * @param id the id of the Movie to be retrieved
     * @return The Movie being requested
     */
    public Movie getMovie(String id) {
        HttpRequest request = makeRequest("/movie/" + id);
        HttpResponse<String> response = getResponseWithRetries(request, retries);
        if (response == null) {
            return null;
        }
        Movie result = safelyParseMovie(response.body());
        result.setMovieAccessSDK(this);
        result.setQuotes(getQuotes(result));
        return result;
    }

    /**
     * Get the quotes for the provided movie
     * @param movie
     * @return The available quotes from the provided movie
     */
    public List<Quote> getQuotes(Movie movie) {
        HttpRequest request = makeRequest("/movie/" + movie.getId() + "/quote");
        HttpResponse<String> response = getResponseWithRetries(request, retries);
        if (response == null) {
            return null;
        }
        return safelyParseQuotes(response.body());
    }

    private Movie safelyParseMovie(String body) {
        try {
            return parseMovie(body);
        } catch (JsonSyntaxException jse) {
//            logger.info("Unable to parse Movie: " + body);
            return null;
        }
    }
    protected static Movie parseMovie(String body) {
        if (body.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();
        Movie result = gson.fromJson(body, Movie.class);
        if (result.getId() == null && result.getTitle() == null) {
            return null;
        }
        return result;
    }

    private List<Movie> safelyParseMovies(String body) {
        try {
            return parseMovies(body);
        } catch (JsonSyntaxException jse) {
//            logger.info("Unable to parse Movies: " + body);
            return null;
        }
    }
    protected static List<Movie> parseMovies(String body) {
        if (body.isEmpty()) {
            return new ArrayList<>();
        }
        Type listOfMovieType = new TypeToken<ArrayList<Movie>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(body, listOfMovieType);
    }

    private List<Quote> safelyParseQuotes(String body) {
        try {
            return parseQuotes(body);
        } catch (JsonSyntaxException jse) {
//            logger.info("Unable to parse Quotes: " + body);
            return null;
        }
    }

    protected static List<Quote> parseQuotes(String body) {
        if (body.isEmpty()) {
            return new ArrayList<>();
        }
        Type listOfMovieType = new TypeToken<ArrayList<Quote>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(body, listOfMovieType);
    }
    private boolean isValidResponse(HttpResponse<String> response) {
        return response.statusCode() == 200 || response.statusCode() == 201;
    }

    private void handleInvalidResponse(HttpResponse<String> response) {
        throw new RuntimeException("Invalid response");
    }

    private HttpRequest makeRequest(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(getBaseURL() + endpoint))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private HttpResponse<String> getResponseWithRetries(HttpRequest request, int attemptsRemaining) {
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (!isValidResponse(response)) {
                handleInvalidResponse(response);
                return null;
            }
            return response;
        } catch (IOException ioException) {
            if (attemptsRemaining < 1) {
//                logger.log(SEVERE, "Failed due to IO Exception", ioException);
                return null;
            }
//            logger.log(WARNING, "Retrying after IO Exception", ioException);
            return getResponseWithRetries(request, attemptsRemaining - 1);
        } catch (InterruptedException ie) {
//            logger.log(INFO, "Call interrupted.  Returning empty list", ie);
            return null;
        }
    }

    private String getBaseURL() {
        return baseURL;
    }
    public void setBaseURL(String url) {
        baseURL = url;
    }
    public void setRetries(int retryAttempts) {
        retries = retryAttempts;
    }
}
