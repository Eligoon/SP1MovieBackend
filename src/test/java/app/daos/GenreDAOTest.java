package app.daos;

import app.config.TestHibernateConfig;
import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreDAOTest extends DAOTestBase {

    private static EntityManagerFactory emf;
    private static GenreDAO genreDAO;

    @BeforeAll
    static void setUp() {

        emf = TestHibernateConfig.getEntityManagerFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        genreDAO = new GenreDAO(emf);
    }

    @BeforeEach
    void cleanDatabase() {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.createNativeQuery("DELETE FROM movie_genre")
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
    void createShouldSaveGenre() {

        Genre genre = Genre.builder()
                .tmdbId(18L)
                .name("Drama")
                .build();

        Genre createdGenre = genreDAO.create(genre);

        assertNotNull(createdGenre.getId());
        assertEquals(18L, createdGenre.getTmdbId());
        assertEquals("Drama", createdGenre.getName());
    }

    @Test
    void getByIdShouldReturnGenre() {

        Genre genre = Genre.builder()
                .tmdbId(53L)
                .name("Thriller")
                .build();

        Genre createdGenre = genreDAO.create(genre);

        Genre foundGenre =
                genreDAO.getById(createdGenre.getId());

        assertNotNull(foundGenre);
        assertEquals(
                createdGenre.getId(),
                foundGenre.getId()
        );
        assertEquals(
                53L,
                foundGenre.getTmdbId()
        );
        assertEquals(
                "Thriller",
                foundGenre.getName()
        );
    }

    @Test
    void getAllShouldReturnAllGenres() {

        Genre genre1 = Genre.builder()
                .tmdbId(1001L)
                .name("Test Drama")
                .build();

        Genre genre2 = Genre.builder()
                .tmdbId(1002L)
                .name("Test Thriller")
                .build();

        genreDAO.create(genre1);
        genreDAO.create(genre2);

        var genres = genreDAO.getAll();

        assertEquals(2, genres.size());

        assertTrue(
                genres.stream()
                        .anyMatch(
                                genre ->
                                        genre.getTmdbId().equals(1001L)
                        )
        );

        assertTrue(
                genres.stream()
                        .anyMatch(
                                genre ->
                                        genre.getTmdbId().equals(1002L)
                        )
        );
    }

    @Test
    void updateShouldModifyGenre() {

        Genre genre = Genre.builder()
                .tmdbId(80L)
                .name("Crime")
                .build();

        Genre createdGenre = genreDAO.create(genre);

        createdGenre.setName("Updated Crime");

        Genre updatedGenre =
                genreDAO.update(createdGenre);

        assertNotNull(updatedGenre);

        assertEquals(
                createdGenre.getId(),
                updatedGenre.getId()
        );

        assertEquals(
                "Updated Crime",
                updatedGenre.getName()
        );
    }

    @Test
    void deleteShouldRemoveGenre() {

        Genre genre = Genre.builder()
                .tmdbId(99L)
                .name("Test Genre")
                .build();

        Genre createdGenre =
                genreDAO.create(genre);

        boolean deleted =
                genreDAO.delete(createdGenre.getId());

        assertTrue(deleted);

        Genre deletedGenre =
                genreDAO.getById(createdGenre.getId());

        assertNull(deletedGenre);
    }
}