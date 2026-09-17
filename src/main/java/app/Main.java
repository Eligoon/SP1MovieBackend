package app;

import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.MovieDTO;
import app.dtos.MovieResultsDTO;
import app.services.MovieService;
import app.tmdb.TMDbClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String[] args) {

        ObjectMapper objectMapper = new ObjectMapper();
        TMDbClient tmdbClient = new TMDbClient(objectMapper);

        // Create DAOs
        MovieDAO movieDAO = new MovieDAO();
        GenreDAO genreDAO = new GenreDAO();
        ActorDAO actorDAO = new ActorDAO();
        DirectorDAO directorDAO = new DirectorDAO();

        // Create service
        MovieService movieService = new MovieService(
                movieDAO,
                genreDAO,
                actorDAO,
                directorDAO
        );


        // Fetch first page to determine total number of pages


        MovieResultsDTO firstPage = tmdbClient.discoverDanishMovies(
                "2021-09-17",
                "2026-09-17",
                1
        );

        int totalPages = firstPage.getTotalPages();

        System.out.println();
        System.out.println("Danish movies found:");
        System.out.println("Total results: " + firstPage.getTotalResults());
        System.out.println("Total pages: " + totalPages);


        // Import all Danish movies


        System.out.println();
        System.out.println("Starting import...");

        for (int page = 1; page <= totalPages; page++) {

            System.out.println();
            System.out.println(
                    "Processing page " +
                            page +
                            " of " +
                            totalPages
            );

            MovieResultsDTO results;

            // already fetched page 1
            if (page == 1) {
                results = firstPage;
            } else {
                results = tmdbClient.discoverDanishMovies(
                        "2021-09-17",
                        "2026-09-17",
                        page
                );
            }


            // Process each movie on the current page


            results.getResults().forEach(movie -> {

                try {

                    // Get complete movie information
                    MovieDTO movieDetails =
                            tmdbClient.getMovie(movie.getId());

                    // Get actors and director
                    var credits =
                            tmdbClient.getCredits(movie.getId());

                    // Convert DTOs to entities and save
                    movieService.saveMovie(
                            movieDetails,
                            credits
                    );

                    System.out.println(
                            "Saved: " +
                                    movieDetails.getTitle() +
                                    " (" +
                                    movieDetails.getReleaseDate() +
                                    ")"
                    );

                } catch (Exception e) {

                    System.out.println(
                            "Could not save movie " +
                                    movie.getId() +
                                    " - " +
                                    movie.getTitle()
                    );

                    System.out.println(
                            "Reason: " +
                                    e.getMessage()
                    );
                }
            });
        }


        // Import finished


        System.out.println();
        System.out.println("Import finished.");


        // Show movies currently stored in database


        System.out.println();
        System.out.println("Movies currently in database:");

        movieService.getAllMovies().forEach(movie ->
                System.out.println(
                        movie.getId() +
                                " - " +
                                movie.getTitle()
                )
        );

        System.out.println();
        System.out.println("Database counts:");
        System.out.println("Movies: " + movieService.getAllMovies().size());
        System.out.println("Actors: " + movieService.getAllActors().size());
        System.out.println("Directors: " + movieService.getAllDirectors().size());
        System.out.println("Genres: " + movieService.getAllGenres().size());
    }
}