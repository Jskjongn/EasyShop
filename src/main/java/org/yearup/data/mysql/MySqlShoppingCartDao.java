package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    private ProductDao productDao;

    // constructor
    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    // view items in a users shopping cart
    @Override
    public ShoppingCart getByUserId(int userId) {
        // creates new shopping cart
        ShoppingCart shoppingCart = new ShoppingCart();
        // creates map to add item to shopping cart
        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        try (
                Connection connection = getConnection();
                // gets all columns from shopping cart where user id equals the users input
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            *
                        FROM
                            shopping_cart
                        WHERE
                            user_id = ?;
                        """);
        ) {
            // sets parameter
            preparedStatement.setInt(1, userId);

            // executes and stores results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // gets product id and quantity values from columns in table
                    int productId = resultSet.getInt("product_id");
                    int quantity = resultSet.getInt("quantity");
                    // gets product from product id
                    Product product = productDao.getById(productId);

                    // creates a shopping cart item
                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    // sets product object to a shopping cart item
                    shoppingCartItem.setProduct(product);
                    shoppingCartItem.setQuantity(quantity);

                    // puts the shopping cart item into the shopping cart
                    items.put(productId, shoppingCartItem);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // sets item and returns the shopping cart
        shoppingCart.setItems(items);
        return shoppingCart;
    }

    // add items to a shopping cart
    @Override
    public ShoppingCartItem addToCart(int userId, int productId) {
        // creates new shopping cart item
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();

        try (
                Connection connection = getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement("""
                        INSERT INTO shopping_cart (user_id, product_id, quantity)
                        VALUES (?, ?, ?);
                        """);
        ) {
            // sets user input and adds default quantity of 1 to quantity and creates new row
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, shoppingCartItem.getQuantity());
            // executes
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                // gets product and sets it as an item
                Product product = productDao.getById(productId);
                shoppingCartItem.setProduct(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCartItem;
    }

    @Override
    public void update(int userId, int productId) {

    }

    @Override
    public void delete(int userId) {

    }
}
