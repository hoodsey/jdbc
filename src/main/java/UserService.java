import java.sql.*;

public class UserService {
    private final DBConnectionProvider connectionProvider;

    public UserService(DBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        createUsersTableIfNotExists();
    }

    public Integer createUser(String name, Integer age) throws Exception {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "insert into users(name,age) values(?,?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.execute();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            } else {
                throw new RuntimeException("Ошибка при создании пользователя");
            }
        }
    }

    public void updateUserAge(Integer id, Integer age) {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE users SET age = ? WHERE id = ?"
            );
            pstmt.setInt(1, age);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(Integer id) {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?"
            );
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createUsersTableIfNotExists() {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    """
                            create table if not exists users (
                                id SERIAL PRIMARY KEY,
                                name varchar not null,
                                age int not null
                            )
                            """
            );
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
