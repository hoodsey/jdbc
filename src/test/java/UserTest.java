import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.*;;

class UserTest extends BaseTest {

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        userService = new UserService(connectionProvider);
    }

    @Test
    @DisplayName("Проврека соедниения с БД")
    void shouldSuccessConnection() throws Exception {
        assertTrue(connectionProvider.checkConnection());
    }

    @Test
    @DisplayName("Проврека создания пользователя")
    void shouldSuccessCreateUser() throws Exception {
        Integer id = userService.createUser("John", 30);
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                Assertions.assertTrue(rs.next());
            } catch (Exception e) {
                throw new RuntimeException("Пользователь не найден");
            }
        }
    }

    @Test
    @DisplayName("Проврека изменения возраста пользователя")
    void shouldSuccessUpdateUser() throws Exception {
        Integer id = userService.createUser("John", 30);
        userService.updateUserAge(id, 32);
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT age FROM users WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int age = rs.getInt("age");
                    assertEquals(32, age, "Возвраст не был обновлен");
                } else {
                    throw new RuntimeException("Пользователь не найден");
                }
            }
        }
    }

    @Test
    @DisplayName("Проврека удаления пользователя")
    void shouldDeleteUser() throws Exception {
        Integer id = userService.createUser("John", 30);
        userService.deleteUser(id);
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                Assertions.assertFalse(rs.next());
            } catch (Exception e) {
                throw new RuntimeException("Пользователь найден");
            }
        }
    }
}