package app.daos;

import app.config.TestHibernateConfig;
import app.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectorDAOTest extends DAOTestBase {

    private static EntityManagerFactory emf;
    private static DirectorDAO directorDAO;

    @BeforeAll
    static void setUp() {
        emf = TestHibernateConfig.getEntityManagerFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        directorDAO = new DirectorDAO(emf);
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Movies reference directors, so remove movies first.
            em.createNativeQuery("DELETE FROM movie_actor").executeUpdate();
            em.createNativeQuery("DELETE FROM movie_genre").executeUpdate();
            em.createNativeQuery("DELETE FROM movies").executeUpdate();
            em.createNativeQuery("DELETE FROM directors").executeUpdate();

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
    void createShouldSaveDirector() {
        Director director = Director.builder()
                .tmdbId(2001L)
                .name("Test Director")
                .build();

        Director createdDirector = directorDAO.create(director);

        assertNotNull(createdDirector.getId());
        assertEquals(2001L, createdDirector.getTmdbId());
        assertEquals("Test Director", createdDirector.getName());
    }

    @Test
    void getByIdShouldReturnDirector() {
        Director director = Director.builder()
                .tmdbId(2002L)
                .name("Test Director")
                .build();

        Director createdDirector = directorDAO.create(director);

        Director foundDirector =
                directorDAO.getById(createdDirector.getId());

        assertNotNull(foundDirector);
        assertEquals(createdDirector.getId(), foundDirector.getId());
        assertEquals(2002L, foundDirector.getTmdbId());
        assertEquals("Test Director", foundDirector.getName());
    }

    @Test
    void getAllShouldReturnAllDirectors() {
        Director director1 = Director.builder()
                .tmdbId(2003L)
                .name("Test Director 1")
                .build();

        Director director2 = Director.builder()
                .tmdbId(2004L)
                .name("Test Director 2")
                .build();

        directorDAO.create(director1);
        directorDAO.create(director2);

        var directors = directorDAO.getAll();

        assertEquals(2, directors.size());

        assertTrue(directors.stream()
                .anyMatch(director ->
                        director.getTmdbId().equals(2003L)));

        assertTrue(directors.stream()
                .anyMatch(director ->
                        director.getTmdbId().equals(2004L)));
    }

    @Test
    void updateShouldModifyDirector() {
        Director director = Director.builder()
                .tmdbId(2005L)
                .name("Original Director")
                .build();

        Director createdDirector = directorDAO.create(director);

        createdDirector.setName("Updated Director");

        Director updatedDirector =
                directorDAO.update(createdDirector);

        assertNotNull(updatedDirector);
        assertEquals(
                createdDirector.getId(),
                updatedDirector.getId()
        );
        assertEquals(
                "Updated Director",
                updatedDirector.getName()
        );
    }

    @Test
    void deleteShouldRemoveDirector() {
        Director director = Director.builder()
                .tmdbId(2006L)
                .name("Director To Delete")
                .build();

        Director createdDirector = directorDAO.create(director);

        boolean deleted =
                directorDAO.delete(createdDirector.getId());

        assertTrue(deleted);

        Director deletedDirector =
                directorDAO.getById(createdDirector.getId());

        assertNull(deletedDirector);
    }
}