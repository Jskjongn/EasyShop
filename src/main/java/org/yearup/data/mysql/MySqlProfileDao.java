package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao {

    public MySqlProfileDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // gets a profile by id
    @Override
    public Profile getByUserId(int userId) {
        // creates new profile
        Profile profile = new Profile();

        try (
                Connection connection = getConnection();
                // connects and creates query
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            *
                        FROM
                            profiles
                        WHERE
                            user_id = ?;
                        """)
        ) {
            // sets parameter with user input
            preparedStatement.setInt(1, userId);
            // executes and stores results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // gets values columns and sets them into a profile properties
                while (resultSet.next()) {
                    profile.setUserId(resultSet.getInt("user_id"));
                    profile.setFirstName(resultSet.getString("first_name"));
                    profile.setLastName(resultSet.getString("last_name"));
                    profile.setPhone(resultSet.getString("phone"));
                    profile.setEmail(resultSet.getString("email"));
                    profile.setAddress(resultSet.getString("address"));
                    profile.setCity(resultSet.getString("city"));
                    profile.setState(resultSet.getString("state"));
                    profile.setZip(resultSet.getString("zip"));
                    // returns profile
                    return profile;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // updates a profile by user id
    @Override
    public void update(int userId, Profile profile) {

        try (
                Connection connection = getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement("""
                        UPDATE
                            profiles
                        SET
                            first_name = ?
                            , last_name = ?
                            , phone = ?
                            , email = ?
                            , address = ?
                            , city = ?
                            , state = ?
                            , zip = ?
                        WHERE
                            user_id = ?;
                        """)
        ) {
            preparedStatement.setString(1, profile.getFirstName());
            preparedStatement.setString(2, profile.getLastName());
            preparedStatement.setString(3, profile.getPhone());
            preparedStatement.setString(4, profile.getEmail());
            preparedStatement.setString(5, profile.getAddress());
            preparedStatement.setString(6, profile.getCity());
            preparedStatement.setString(7, profile.getState());
            preparedStatement.setString(8, profile.getZip());
            preparedStatement.setInt(9, userId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
