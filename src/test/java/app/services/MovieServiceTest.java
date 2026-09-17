package app.services;

import app.config.TestHibernateConfig;
import app.daos.ActorDAO;
import app.daos.DAOTestBase;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.CreditsDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Movie;
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

            em.createNativeQuery("DELETE FROM movie_actor")
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM movie_genre")
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM movies")
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM actors")
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM directors")
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM genres")
                    .executeUpdate();

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

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

        GenreDTO genreDTO = GenreDTO.builder()
                .id(18L)
                .name("Drama")
                .build();

        MovieDTO movieDTO = MovieDTO.builder()
                .id(550L)
                .title("Fight Club")
                .overview("A test movie")
                .releaseDate("1999-10-15")
                .rating(8.4)
                .genres(List.of(genreDTO))
                .build();

        Movie savedMovie =
                movieService.saveMovie(movieDTO, emptyCredits());

        assertNotNull(savedMovie.getId());
        assertEquals(550L, savedMovie.getTmdbId());
        assertEquals("Fight Club", savedMovie.getTitle());
        assertEquals("A test movie", savedMovie.getOverview());
        assertEquals(8.4, savedMovie.getRating());

        assertNotNull(savedMovie.getReleaseDate());
        assertEquals(
                "1999-10-15",
                savedMovie.getReleaseDate().toString()
        );

        assertEquals(1, savedMovie.getGenres().size());
        assertEquals(
                "Drama",
                savedMovie.getGenres().iterator().next().getName()
        );
    }

    @Test
    void getMovieByIdShouldReturnMovie() {

        MovieDTO movieDTO = MovieDTO.builder()
                .id(551L)
                .title("Test Movie")
                .overview("Test overview")
                .releaseDate("2020-01-01")
                .rating(7.5)
                .genres(List.of())
                .build();

        Movie savedMovie =
                movieService.saveMovie(movieDTO, emptyCredits());

        Movie foundMovie =
                movieService.getMovieById(savedMovie.getId());

        assertNotNull(foundMovie);
        assertEquals(savedMovie.getId(), foundMovie.getId());
        assertEquals(551L, foundMovie.getTmdbId());
        assertEquals("Test Movie", foundMovie.getTitle());
    }

    @Test
    void getAllMoviesShouldReturnAllMovies() {

        MovieDTO movie1 = MovieDTO.builder()
                .id(552L)
                .title("Test Movie 1")
                .overview("Overview 1")
                .releaseDate("2020-01-01")
                .rating(7.0)
                .genres(List.of())
                .build();

        MovieDTO movie2 = MovieDTO.builder()
                .id(553L)
                .title("Test Movie 2")
                .overview("Overview 2")
                .releaseDate("2021-01-01")
                .rating(8.0)
                .genres(List.of())
                .build();

        movieService.saveMovie(movie1, emptyCredits());
        movieService.saveMovie(movie2, emptyCredits());

        List<Movie> movies = movieService.getAllMovies();

        assertEquals(2, movies.size());

        assertTrue(movies.stream()
                .anyMatch(movie ->
                        movie.getTmdbId().equals(552L)));

        assertTrue(movies.stream()
                .anyMatch(movie ->
                        movie.getTmdbId().equals(553L)));
    }

    @Test
    void updateMovieShouldModifyMovie() {

        MovieDTO movieDTO = MovieDTO.builder()
                .id(554L)
                .title("Original Title")
                .overview("Original overview")
                .releaseDate("2020-01-01")
                .rating(7.0)
                .genres(List.of())
                .build();

        Movie movie =
                movieService.saveMovie(movieDTO, emptyCredits());

        movie.setTitle("Updated Title");
        movie.setRating(9.0);

        Movie updatedMovie =
                movieService.updateMovie(movie);

        assertNotNull(updatedMovie);
        assertEquals("Updated Title", updatedMovie.getTitle());
        assertEquals(9.0, updatedMovie.getRating());
    }

    @Test
    void deleteMovieShouldRemoveMovie() {

        MovieDTO movieDTO = MovieDTO.builder()
                .id(555L)
                .title("Movie To Delete")
                .overview("This movie will be deleted")
                .releaseDate("2020-01-01")
                .rating(6.5)
                .genres(List.of())
                .build();

        Movie movie =
                movieService.saveMovie(movieDTO, emptyCredits());

        boolean deleted =
                movieService.deleteMovie(movie.getId());

        assertTrue(deleted);

        Movie deletedMovie =
                movieService.getMovieById(movie.getId());

        assertNull(deletedMovie);
    }

    private CreditsDTO emptyCredits() {
        return CreditsDTO.builder()
                .cast(List.of())
                .crew(List.of())
                .build();
    }
}