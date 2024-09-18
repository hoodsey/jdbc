import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;;

@Testcontainers
class UserTest extends BaseTest {

    private Integer id;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        userService = new UserService(connectionProvider);
        try {
            id = userService.createUser("John", 30);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании пользователя");
        }
    }

    @Test
    @DisplayName("Проврека создания пользователя")
    void shouldSuccessCreateUser() throws Exception {
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