package app.daos;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class DAOTestBase {

    protected static PostgreSQLContainer<?> postgres;

    @BeforeAll
    static void startContainer() {

        postgres = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpassword");

        postgres.start();
    }

    @AfterAll
    static void stopContainer() {

        if (postgres != null) {
            postgres.stop();
        }
    }
}