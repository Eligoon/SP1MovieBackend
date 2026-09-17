package app.config;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public final class TestHibernateConfig {

    private TestHibernateConfig() {
    }

    public static EntityManagerFactory getEntityManagerFactory(
            String jdbcUrl,
            String username,
            String password
    ) {

        Properties props = new Properties();

        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.connection.url", jdbcUrl);
        props.put("hibernate.connection.username", username);
        props.put("hibernate.connection.password", password);

        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");

        Configuration configuration = new Configuration();
        configuration.setProperties(props);

        configuration
                .addAnnotatedClass(Movie.class)
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Genre.class)
                .addAnnotatedClass(Director.class);

        ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

        SessionFactory sessionFactory =
                configuration.buildSessionFactory(serviceRegistry);

        return sessionFactory.unwrap(EntityManagerFactory.class);
    }
}