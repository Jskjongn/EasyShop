package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {

    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        // creates new list of categories
        List<Category> categories = new ArrayList<>();

        try (
                Connection connection = getConnection();
                // gets all columns from categories
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            *
                        FROM
                            categories;
                        """);
                // executes and stores results
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            // creates new category object and stores in list
            while (resultSet.next()) {
                Category category = mapRow(resultSet);
                categories.add(category);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // returns list
        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        // get category by id
        try (
                Connection connection = getConnection();
                // gets all columns from categories where category id equals a certain id
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            *
                        FROM
                            categories
                        WHERE
                            category_id = ?;
                        """);
        ) {
            preparedStatement.setInt(1, categoryId);

            // executes and stores results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // creates new category object
                while (resultSet.next()) {
                    // returns new category
                    return mapRow(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category) {
        // create a new category
        try (
                Connection connection = getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement("""
                        INSERT INTO categories (name, description)
                        VALUES
                        (?, ?);
                        """, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                // retrieve the generated keys
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        // retrieve the auto-incremented ID
                        int categoryId = generatedKeys.getInt(1);

                        // get the newly inserted category
                        return getById(categoryId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category) {
        // update category
    }

    @Override
    public void delete(int categoryId) {
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
