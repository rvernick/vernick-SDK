package com.lordQuotes.apis;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lordQuotes.models.Movie;
import com.lordQuotes.models.Quote;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieAccessTest {

    @Test
    void createZeroMovies() {
        ArrayList<Movie> source = new ArrayList<>();
        assertEquals(source, MovieAccess.parseMovies(""));
        assertEquals(source, MovieAccess.parseMovies(jsonFromMovies(source)));
    }
    
    @Test
    void createOneMovie() {
        ArrayList<Movie> source = new ArrayList<>();
        Movie movie1 = new Movie("1", "Fellowship of the Ring");
        source.add(movie1);
        String oneMovie = jsonFromMovies(source);

        List<Movie> movieList = MovieAccess.parseMovies(oneMovie);
        assertEquals("1", movieList.get(0).getId());
        assertEquals("Fellowship of the Ring", movieList.get(0).getTitle());
    }
    
    @Test
    void createTwoMovies() {
        ArrayList<Movie> source = new ArrayList<>();
        source.add(new Movie("1", "Fellowship of the Ring"));
        source.add(new Movie("2", "Two Towers"));
        String twoMovieBody = jsonFromMovies(source);
        List<Movie> movieList = MovieAccess.parseMovies(twoMovieBody);
        assertEquals(2, movieList.size());
        assertEquals("1", movieList.get(0).getId());
        assertEquals("Fellowship of the Ring", movieList.get(0).getTitle());
        assertEquals("2", movieList.get(1).getId());
        assertEquals("Two Towers", movieList.get(1).getTitle());
    }

    @Test
    void parseEmptyMovie() {
        Movie result = MovieAccess.parseMovie("{ }");
        assertNull(result);
    }

    @Test
    void parseMovie() {
        Gson gson = new Gson();
        Movie movie1 = new Movie("1", "Fellowship of the Ring");
        Movie result = MovieAccess.parseMovie(gson.toJson(movie1));
        assertEquals("1", result.getId());
        assertEquals("Fellowship of the Ring", result.getTitle());
    }

    @Test
    void parseBadMovie() {
        String unexpectedBody = "{\"badID\":\"1\"}";
        Movie result = MovieAccess.parseMovie(unexpectedBody);
        assertNull(result);

        unexpectedBody = "Unparsable";
        try {
            result = MovieAccess.parseMovie(unexpectedBody);
            fail("Expected exception");
        } catch (JsonSyntaxException jse) {
            assertNull(result);
        }
    }

    @Test
    void parseOneQuote() {
        String quote1 = "[{\"id\":\"1\",\"text\":\"My precious\"}]";
        List<Quote> result = MovieAccess.parseQuotes(quote1);
        assertEquals(1, result.size());
        assertEquals("My precious", result.get(0).getText());
    }

    private String jsonFromMovies(List<Movie> movies) {
        Gson gson = new Gson();
        return gson.toJson(movies);
    }
}