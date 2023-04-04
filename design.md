# Vernick-SDK
## Introduction
I've written the core functionality of the API to access the provided API for movie quotes.
The API will get a simple list of movies and then get the details for a specific movie when requested.

## Installation
I didn't get to creating a maven install for it.
Nor does it have a Spring configuration.  It will need a baseURL for the call, and optionally, one could specify the number of retries
I also commented out the logging as it isn't configured correctly, but left it in to get a sense of what I think should be provided.

## Usage
There are three public calls:
- getMovies
- getMovie(movie)
- getQuotes(movie)

A Movie can also provide the available Quotes by lazily calling the SDK under the covers.  Should consider something a little cleaner.

Thanks for having a look.
Russ