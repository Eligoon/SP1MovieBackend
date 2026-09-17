package app.daos;

import app.config.HibernateConfig;
import app.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DirectorDAO implements IDAO<Director, Long> {

    private final EntityManagerFactory emf;

    public DirectorDAO() {
        this.emf = HibernateConfig.getEntityManagerFactory();
    }

    @Override
    public Director create(Director director) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(director);

            em.getTransaction().commit();

            return director;

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
    public Director getById(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Director.class, id);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Director> getAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT d FROM Director d",
                    Director.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public Director update(Director director) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Director updatedDirector = em.merge(director);

            em.getTransaction().commit();

            return updatedDirector;

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

            Director director = em.find(Director.class, id);

            if (director == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(director);

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