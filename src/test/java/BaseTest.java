import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.Assert.assertTrue;

abstract public class BaseTest {

    protected DBConnectionProvider connectionProvider;
    protected UserService userService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.2"
    );

    @BeforeEach
    void setUp() {
        connectionProvider = new DBConnectionProvider(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @Test
    @DisplayName("Проврека соедниения с БД")
    void shouldSuccessConnection() throws Exception {
        assertTrue(connectionProvider.checkConnection());
    }
}
