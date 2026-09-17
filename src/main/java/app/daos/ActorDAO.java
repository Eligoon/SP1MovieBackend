package app.daos;

import app.config.HibernateConfig;
import app.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ActorDAO implements IDAO<Actor, Long> {

    private final EntityManagerFactory emf;

    public ActorDAO() {
        this.emf = HibernateConfig.getEntityManagerFactory();
    }

    public ActorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Actor create(Actor actor) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(actor);

            em.getTransaction().commit();

            return actor;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    @Override
    public Actor getById(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Actor.class, id);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Actor> getAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT a FROM Actor a",
                    Actor.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public Actor update(Actor actor) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Actor updatedActor = em.merge(actor);

            em.getTransaction().commit();

            return updatedActor;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Actor actor = em.find(Actor.class, id);

            if (actor == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(actor);

            em.getTransaction().commit();

            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }
}