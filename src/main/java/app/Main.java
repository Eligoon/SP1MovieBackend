package app;

import app.dtos.MovieDTO;
import app.tmdb.TMDbClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String[] args) {

        ObjectMapper objectMapper = new ObjectMapper();

        TMDbClient client = new TMDbClient(objectMapper);

        MovieDTO movie = client.getMovie(550);

        System.out.println("ID: " + movie.getId());
        System.out.println("Title: " + movie.getTitle());
        System.out.println("Overview: " + movie.getOverview());
        System.out.println("Release date: " + movie.getReleaseDate());
        System.out.println("Rating: " + movie.getRating());

        System.out.println("Genres:");

        movie.getGenres().forEach(
                genre -> System.out.println(
                        genre.getId() + " - " + genre.getName()
                )
        );
    }
}