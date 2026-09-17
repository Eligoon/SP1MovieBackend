package app.daos;

import app.config.HibernateConfig;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class MovieDAO implements IDAO<Movie, Long> {

    private final EntityManagerFactory emf;

    public MovieDAO() {
        this.emf = HibernateConfig.getEntityManagerFactory();
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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public java.util.List<Movie> getAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Movie update(Movie movie) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}