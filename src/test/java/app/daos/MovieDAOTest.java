package app.daos;

import app.config.TestHibernateConfig;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieDAOTest extends DAOTestBase {

    private static EntityManagerFactory emf;
    private static MovieDAO movieDAO;

    @BeforeAll
    static void setUp() {

        emf = TestHibernateConfig.getEntityManagerFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        movieDAO = new MovieDAO(emf);
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
    void createShouldSaveMovie() {

        Movie movie = Movie.builder()
                .tmdbId(550L)
                .title("Fight Club")
                .overview("Test overview")
                .rating(8.4)
                .build();

        Movie createdMovie = movieDAO.create(movie);

        assertNotNull(createdMovie.getId());
        assertEquals(550L, createdMovie.getTmdbId());
        assertEquals("Fight Club", createdMovie.getTitle());
    }

    @Test
    void getByIdShouldReturnMovie() {

        Movie movie = Movie.builder()
                .tmdbId(551L)
                .title("Test Movie")
                .overview("Test overview")
                .rating(8.0)
                .build();

        Movie createdMovie = movieDAO.create(movie);

        Movie foundMovie =
                movieDAO.getById(createdMovie.getId());

        assertNotNull(foundMovie);
        assertEquals(
                createdMovie.getId(),
                foundMovie.getId()
        );
        assertEquals(
                551L,
                foundMovie.getTmdbId()
        );
        assertEquals(
                "Test Movie",
                foundMovie.getTitle()
        );
    }

    @Test
    void getAllShouldReturnAllMovies() {

        Movie movie1 = Movie.builder()
                .tmdbId(552L)
                .title("Test Movie 1")
                .overview("Overview 1")
                .rating(7.5)
                .build();

        Movie movie2 = Movie.builder()
                .tmdbId(553L)
                .title("Test Movie 2")
                .overview("Overview 2")
                .rating(8.0)
                .build();

        movieDAO.create(movie1);
        movieDAO.create(movie2);

        var movies = movieDAO.getAll();

        assertEquals(2, movies.size());

        assertTrue(
                movies.stream()
                        .anyMatch(
                                movie ->
                                        movie.getTmdbId().equals(552L)
                        )
        );

        assertTrue(
                movies.stream()
                        .anyMatch(
                                movie ->
                                        movie.getTmdbId().equals(553L)
                        )
        );
    }

    @Test
    void updateShouldModifyMovie() {

        Movie movie = Movie.builder()
                .tmdbId(554L)
                .title("Original Title")
                .overview("Original overview")
                .rating(7.0)
                .build();

        Movie createdMovie = movieDAO.create(movie);

        createdMovie.setTitle("Updated Title");
        createdMovie.setRating(9.0);

        Movie updatedMovie =
                movieDAO.update(createdMovie);

        assertNotNull(updatedMovie);

        assertEquals(
                createdMovie.getId(),
                updatedMovie.getId()
        );

        assertEquals(
                "Updated Title",
                updatedMovie.getTitle()
        );

        assertEquals(
                9.0,
                updatedMovie.getRating()
        );
    }

    @Test
    void deleteShouldRemoveMovie() {

        Movie movie = Movie.builder()
                .tmdbId(555L)
                .title("Movie To Delete")
                .overview("This movie will be deleted")
                .rating(6.5)
                .build();

        Movie createdMovie = movieDAO.create(movie);

        boolean deleted =
                movieDAO.delete(createdMovie.getId());

        assertTrue(deleted);

        Movie deletedMovie =
                movieDAO.getById(createdMovie.getId());

        assertNull(deletedMovie);
    }
}