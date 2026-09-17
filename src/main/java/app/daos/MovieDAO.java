package app.daos;

import app.config.HibernateConfig;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MovieDAO implements IDAO<Movie, Long> {

    private final EntityManagerFactory emf;

    public MovieDAO() {
        this.emf = HibernateConfig.getEntityManagerFactory();
    }

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Movie create(Movie movie) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(movie);

            em.getTransaction().commit();

            return movie;

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
    public Movie getById(Long id) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Movie.class, id);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Movie> getAll() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT m FROM Movie m",
                    Movie.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public Movie update(Movie movie) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Movie updatedMovie = em.merge(movie);

            em.getTransaction().commit();

            return updatedMovie;

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

            Movie movie = em.find(Movie.class, id);

            if (movie == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(movie);

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

    public Movie getByTmdbId(Long tmdbId) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT m FROM Movie m WHERE m.tmdbId = :tmdbId",
                            Movie.class
                    )
                    .setParameter("tmdbId", tmdbId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    public List<Movie> searchByTitle(String searchString) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "WHERE LOWER(m.title) LIKE LOWER(:searchString)",
                            Movie.class)
                    .setParameter("searchString", "%" + searchString + "%")
                    .getResultList();

        } finally {
            em.close();
        }
    }
}