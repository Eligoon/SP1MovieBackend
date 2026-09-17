package app.daos;

import app.config.TestHibernateConfig;
import app.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorDAOTest extends DAOTestBase {

    private static EntityManagerFactory emf;
    private static ActorDAO actorDAO;

    @BeforeAll
    static void setUp() {

        emf = TestHibernateConfig.getEntityManagerFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        actorDAO = new ActorDAO(emf);
    }

    @BeforeEach
    void cleanDatabase() {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.createNativeQuery("DELETE FROM movie_actor")
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM actors")
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
    void createShouldSaveActor() {

        Actor actor = Actor.builder()
                .tmdbId(1001L)
                .name("Test Actor")
                .build();

        Actor createdActor = actorDAO.create(actor);

        assertNotNull(createdActor.getId());
        assertEquals(1001L, createdActor.getTmdbId());
        assertEquals("Test Actor", createdActor.getName());
    }

    @Test
    void getByIdShouldReturnActor() {

        Actor actor = Actor.builder()
                .tmdbId(1002L)
                .name("Test Actor")
                .build();

        Actor createdActor = actorDAO.create(actor);

        Actor foundActor =
                actorDAO.getById(createdActor.getId());

        assertNotNull(foundActor);
        assertEquals(
                createdActor.getId(),
                foundActor.getId()
        );
        assertEquals(
                1002L,
                foundActor.getTmdbId()
        );
        assertEquals(
                "Test Actor",
                foundActor.getName()
        );
    }

    @Test
    void getAllShouldReturnAllActors() {

        Actor actor1 = Actor.builder()
                .tmdbId(1003L)
                .name("Test Actor 1")
                .build();

        Actor actor2 = Actor.builder()
                .tmdbId(1004L)
                .name("Test Actor 2")
                .build();

        actorDAO.create(actor1);
        actorDAO.create(actor2);

        var actors = actorDAO.getAll();

        assertEquals(2, actors.size());

        assertTrue(
                actors.stream()
                        .anyMatch(
                                actor ->
                                        actor.getTmdbId().equals(1003L)
                        )
        );

        assertTrue(
                actors.stream()
                        .anyMatch(
                                actor ->
                                        actor.getTmdbId().equals(1004L)
                        )
        );
    }

    @Test
    void updateShouldModifyActor() {

        Actor actor = Actor.builder()
                .tmdbId(1005L)
                .name("Original Actor")
                .build();

        Actor createdActor = actorDAO.create(actor);

        createdActor.setName("Updated Actor");

        Actor updatedActor =
                actorDAO.update(createdActor);

        assertNotNull(updatedActor);

        assertEquals(
                createdActor.getId(),
                updatedActor.getId()
        );

        assertEquals(
                "Updated Actor",
                updatedActor.getName()
        );
    }

    @Test
    void deleteShouldRemoveActor() {

        Actor actor = Actor.builder()
                .tmdbId(1006L)
                .name("Actor To Delete")
                .build();

        Actor createdActor =
                actorDAO.create(actor);

        boolean deleted =
                actorDAO.delete(createdActor.getId());

        assertTrue(deleted);

        Actor deletedActor =
                actorDAO.getById(createdActor.getId());

        assertNull(deletedActor);
    }
}