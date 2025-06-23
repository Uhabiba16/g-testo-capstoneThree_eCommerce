package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.UserDao;
import org.yearup.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlUserDao extends MySqlDaoBase implements UserDao {
    @Autowired
    public MySqlUserDao(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    public User create(User newUser) {
        String query = "INSERT INTO users (username, hashed_password, role) VALUES (?, ?, ?)";
        String hashedPassword = new BCryptPasswordEncoder().encode(newUser.getPassword());

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query,
                        PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, newUser.getUsername());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, newUser.getRole());

            int rowsAffected= preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("New User Added Successfully");
                ResultSet resultSet = preparedStatement.getGeneratedKeys();

                if (resultSet.next()) {//FIXME
                    int generatedUserId = resultSet.getInt(1);
                    User user = getUserById(generatedUserId);
                    if(user != null){
                        user.setPassword("");
                    }
                    return user;
                } else {
                    System.out.println("No User Created...");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            if (resultSet.next()) {
                do {
                    User user = mapRow(resultSet);
                    users.add(user);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                } else {
                    System.out.println("NO User Found By ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getByUserName(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, username);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                } else {
                    System.out.println("No User Found By Username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getIdByUsername(String username) {
        User user = getByUserName(username);

        if (user != null) {
            return user.getId();
        }

        return -1;
    }

    @Override
    public boolean exists(String username) {
        User user = getByUserName(username);
        return user != null;
    }

    private User mapRow(ResultSet row) throws SQLException {
        int userId = row.getInt("user_id");
        String username = row.getString("username");
        String hashedPassword = row.getString("hashed_password");
        String role = row.getString("role");

        return new User(userId, username, hashedPassword, role);
    }
}
