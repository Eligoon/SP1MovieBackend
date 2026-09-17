package app.daos;

import app.config.HibernateConfig;
import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GenreDAO implements IDAO<Genre, Long> {

    private final EntityManagerFactory emf;

    public GenreDAO() {
        this.emf = HibernateConfig.getEntityManagerFactory();
    }

    @Override
    public Genre create(Genre genre) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(genre);

            em.getTransaction().commit();

            return genre;

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
    public Genre getById(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Genre.class, id);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Genre> getAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT g FROM Genre g",
                    Genre.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public Genre update(Genre genre) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Genre updatedGenre = em.merge(genre);

            em.getTransaction().commit();

            return updatedGenre;

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

            Genre genre = em.find(Genre.class, id);

            if (genre == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(genre);

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