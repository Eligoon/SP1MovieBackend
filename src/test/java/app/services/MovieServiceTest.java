package app.services;

import app.daos.ActorDAO;
import app.daos.DAOTestBase;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.ActorDTO;
import app.dtos.CreditsDTO;
import app.dtos.CrewMemberDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.config.TestHibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest extends DAOTestBase {

    private static EntityManagerFactory emf;

    private static MovieDAO movieDAO;
    private static GenreDAO genreDAO;
    private static ActorDAO actorDAO;
    private static DirectorDAO directorDAO;

    private static MovieService movieService;

    @BeforeAll
    static void setUp() {

        emf = TestHibernateConfig.getEntityManagerFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        movieDAO = new MovieDAO(emf);
        genreDAO = new GenreDAO(emf);
        actorDAO = new ActorDAO(emf);
        directorDAO = new DirectorDAO(emf);

        movieService = new MovieService(
                movieDAO,
                genreDAO,
                actorDAO,
                directorDAO
        );
    }

    @BeforeEach
    void cleanDatabase() {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.createNativeQuery(
                    "DELETE FROM movie_actor"
            ).executeUpdate();

            em.createNativeQuery(
                    "DELETE FROM movie_genre"
            ).executeUpdate();

            em.createQuery(
                    "DELETE FROM Movie"
            ).executeUpdate();

            em.createQuery(
                    "DELETE FROM Actor"
            ).executeUpdate();

            em.createQuery(
                    "DELETE FROM Director"
            ).executeUpdate();

            em.createQuery(
                    "DELETE FROM Genre"
            ).executeUpdate();

            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @AfterAll
    static void tearDown() {
        emf.close();
    }

    @Test
    void saveMovieShouldConvertDTOAndSaveMovie() {

        MovieDTO movieDTO = MovieDTO.builder()
                .id(550L)
                .title("Fight Club")
                .overview("An insomniac...")
                .releaseDate("1999-10-15")
                .rating(8.4)
                .popularity(61.416)
                .genres(List.of(
                        GenreDTO.builder()
                                .id(18L)
                                .name("Drama")
                                .build()
                ))
                .build();

        Movie savedMovie =
                movieService.saveMovie(
                        movieDTO,
                        emptyCredits()
                );

        assertNotNull(savedMovie);
        assertNotNull(savedMovie.getId());
        assertEquals(550L, savedMovie.getTmdbId());
        assertEquals("Fight Club", savedMovie.getTitle());
        assertEquals(8.4, savedMovie.getRating());
        assertEquals(1, savedMovie.getGenres().size());
    }

    @Test
    void getMovieByIdShouldReturnMovie() {

        Movie movie = Movie.builder()
                .tmdbId(551L)
                .title("The Big Lebowski")
                .rating(7.8)
                .build();

        Movie savedMovie = movieDAO.create(movie);

        Movie result =
                movieService.getMovieById(
                        savedMovie.getId()
                );

        assertNotNull(result);
        assertEquals(
                "The Big Lebowski",
                result.getTitle()
        );
    }

    @Test
    void getAllMoviesShouldReturnAllMovies() {

        movieDAO.create(
                Movie.builder()
                        .tmdbId(552L)
                        .title("Movie One")
                        .build()
        );

        movieDAO.create(
                Movie.builder()
                        .tmdbId(553L)
                        .title("Movie Two")
                        .build()
        );

        List<Movie> movies =
                movieService.getAllMovies();

        assertEquals(2, movies.size());
    }

    @Test
    void updateMovieShouldModifyMovie() {

        Movie movie = movieDAO.create(
                Movie.builder()
                        .tmdbId(554L)
                        .title("Old Title")
                        .build()
        );

        movie.setTitle("New Title");

        Movie updated =
                movieService.updateMovie(movie);

        assertEquals(
                "New Title",
                updated.getTitle()
        );
    }

    @Test
    void deleteMovieShouldRemoveMovie() {

        Movie movie = movieDAO.create(
                Movie.builder()
                        .tmdbId(555L)
                        .title("Delete Me")
                        .build()
        );

        boolean deleted =
                movieService.deleteMovie(
                        movie.getId()
                );

        assertTrue(deleted);
        assertNull(
                movieDAO.getById(movie.getId())
        );
    }

    @Test
    void searchMoviesByTitleShouldFindMoviesIgnoringCase() {

        movieDAO.create(
                Movie.builder()
                        .tmdbId(556L)
                        .title("The Dark Knight")
                        .build()
        );

        List<Movie> results =
                movieService.searchMoviesByTitle("dark");

        assertEquals(1, results.size());
        assertEquals(
                "The Dark Knight",
                results.get(0).getTitle()
        );
    }

    @Test
    void getMoviesByGenreShouldReturnMovies() {

        Genre genre = genreDAO.create(
                Genre.builder()
                        .tmdbId(28L)
                        .name("Action")
                        .build()
        );

        Movie movie = Movie.builder()
                .tmdbId(557L)
                .title("Action Movie")
                .genres(
                        new java.util.HashSet<>(
                                List.of(genre)
                        )
                )
                .build();

        movieDAO.create(movie);

        List<Movie> results =
                movieService.getMoviesByGenre(
                        genre.getId()
                );

        assertEquals(1, results.size());
        assertEquals(
                "Action Movie",
                results.get(0).getTitle()
        );
    }

    @Test
    void getAllGenresShouldReturnGenres() {

        genreDAO.create(
                Genre.builder()
                        .tmdbId(18L)
                        .name("Drama")
                        .build()
        );

        genreDAO.create(
                Genre.builder()
                        .tmdbId(28L)
                        .name("Action")
                        .build()
        );

        List<Genre> genres =
                movieService.getAllGenres();

        assertEquals(2, genres.size());
    }

    @Test
    void getAllActorsAndDirectorsShouldReturnActorsAndDirectors() {

        actorDAO.create(
                Actor.builder()
                        .tmdbId(1L)
                        .name("Actor One")
                        .build()
        );

        directorDAO.create(
                Director.builder()
                        .tmdbId(2L)
                        .name("Director One")
                        .build()
        );

        List<Actor> actors =
                movieService.getAllActors();

        List<Director> directors =
                movieService.getAllDirectors();

        assertEquals(1, actors.size());
        assertEquals(1, directors.size());
    }

    @Test
    void getMovieByIdShouldReturnMovieWithActorsAndDirector() {

        Actor actor1 = actorDAO.create(
                Actor.builder()
                        .tmdbId(10L)
                        .name("Actor One")
                        .build()
        );

        Actor actor2 = actorDAO.create(
                Actor.builder()
                        .tmdbId(11L)
                        .name("Actor Two")
                        .build()
        );

        Director director = directorDAO.create(
                Director.builder()
                        .tmdbId(20L)
                        .name("Director One")
                        .build()
        );

        MovieDTO movieDTO = MovieDTO.builder()
                .id(558L)
                .title("Test Movie")
                .build();

        CreditsDTO credits = CreditsDTO.builder()
                .cast(List.of(
                        ActorDTO.builder()
                                .id(10L)
                                .name("Actor One")
                                .build(),
                        ActorDTO.builder()
                                .id(11L)
                                .name("Actor Two")
                                .build()
                ))
                .crew(List.of(
                        CrewMemberDTO.builder()
                                .id(20L)
                                .name("Director One")
                                .job("Director")
                                .build()
                ))
                .build();

        Movie savedMovie =
                movieService.saveMovie(
                        movieDTO,
                        credits
                );

        Movie result =
                movieService.getMovieById(
                        savedMovie.getId()
                );

        assertNotNull(result);
        assertEquals(2, result.getActors().size());
        assertNotNull(result.getDirector());
        assertEquals(
                "Director One",
                result.getDirector().getName()
        );
    }

    @Test
    void getAverageRatingShouldReturnAverage() {

        movieDAO.create(
                Movie.builder()
                        .tmdbId(559L)
                        .title("Movie One")
                        .rating(8.0)
                        .build()
        );

        movieDAO.create(
                Movie.builder()
                        .tmdbId(560L)
                        .title("Movie Two")
                        .rating(6.0)
                        .build()
        );

        assertEquals(
                7.0,
                movieService.getAverageRating()
        );
    }

    @Test
    void getTop10HighestRatedShouldReturnHighestRated() {

        for (int i = 1; i <= 12; i++) {
            movieDAO.create(
                    Movie.builder()
                            .tmdbId(600L + i)
                            .title("Movie " + i)
                            .rating((double) i)
                            .build()
            );
        }

        List<Movie> results =
                movieService.getTop10HighestRated();

        assertEquals(10, results.size());
        assertEquals(12.0, results.get(0).getRating());
    }

    @Test
    void getTop10LowestRatedShouldReturnLowestRated() {

        for (int i = 1; i <= 12; i++) {
            movieDAO.create(
                    Movie.builder()
                            .tmdbId(700L + i)
                            .title("Movie " + i)
                            .rating((double) i)
                            .build()
            );
        }

        List<Movie> results =
                movieService.getTop10LowestRated();

        assertEquals(10, results.size());
        assertEquals(1.0, results.get(0).getRating());
    }

    @Test
    void getTop10MostPopularShouldReturnMostPopular() {

        for (int i = 1; i <= 12; i++) {
            movieDAO.create(
                    Movie.builder()
                            .tmdbId(800L + i)
                            .title("Movie " + i)
                            .popularity((double) i)
                            .build()
            );
        }

        List<Movie> results =
                movieService.getTop10MostPopular();

        assertEquals(10, results.size());
        assertEquals(
                12.0,
                results.get(0).getPopularity()
        );
    }

    private CreditsDTO emptyCredits() {

        return CreditsDTO.builder()
                .cast(List.of())
                .crew(List.of())
                .build();
    }
}