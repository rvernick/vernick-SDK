package com.lordQuotes.models;

import com.google.gson.annotations.Expose;
import com.lordQuotes.apis.MovieAccess;

import java.util.List;

public class Movie {
    @Expose
    private String id;
    @Expose
    private String title;

    private List<Quote> quotes;
    private MovieAccess movieAccessSDK;

    public Movie(String id, String title) {
        this.id = id;
        this.title = title;
    }
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * The available quotes from the movie
     * @return
     */
    public List<Quote> getQuotes() {
        if (quotes == null) {
            quotes = movieAccessSDK.getQuotes(this);
        }
        return quotes;
    }
    public void setQuotes(List<Quote> quoteList) {
        quotes = quoteList;
    }

    public void setMovieAccessSDK(MovieAccess aMovieAccessSDK) {
        movieAccessSDK = aMovieAccessSDK;
    }
}
