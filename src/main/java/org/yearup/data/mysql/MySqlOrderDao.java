package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.ProductDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    private ShoppingCartDao shoppingCartDao;
    private ProfileDao profileDao;
    private ProductDao productDao;

    @Autowired
    public MySqlOrderDao(DataSource dataSource, ShoppingCartDao shoppingCartDao, ProfileDao profileDao, ProductDao productDao) {
        super(dataSource);
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
        this.productDao = productDao;
    }

    @Override
    public Order createOrder(int userId) {
        // gets user profile details
        Profile profile = profileDao.getByUserId(userId);

        // creates new order to set order details from profile
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setShippingAmount(new BigDecimal(2.99));

        // gets users shopping cart and items
        ShoppingCart shoppingCart = shoppingCartDao.getByUserId(userId);

        try (
                Connection connection = getConnection();
                // insert new row into orders table
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                        VALUES (?, ?, ?, ?, ?, ?, ?);
                        """, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            // sets the parameters with order details
            preparedStatement.setInt(1, order.getUserId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(order.getDate()));
            preparedStatement.setString(3, order.getAddress());
            preparedStatement.setString(4, order.getCity());
            preparedStatement.setString(5, order.getState());
            preparedStatement.setString(6, order.getZip());
            preparedStatement.setBigDecimal(7, order.getShippingAmount());

            // executes query and stores rows updated
            int rows = preparedStatement.executeUpdate();

            // if rows updated greater than 0
            if (rows > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                // get the keys and set order id
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                }
            }

            try (
                    PreparedStatement preparedStatement1 = connection.prepareStatement("""
                            INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                            VALUES (?, ?, ?, ?, ?)
                            """)
            ) {
                // for each item in the shopping cart
                for (ShoppingCartItem shoppingCartItem : shoppingCart.getItems().values()) {
                    // gets product details
                    Product product = shoppingCartItem.getProduct();
                    // sets parameters
                    preparedStatement1.setInt(1, order.getOrderId());
                    preparedStatement1.setInt(2, product.getProductId());
                    preparedStatement1.setBigDecimal(3, product.getPrice());
                    preparedStatement1.setInt(4, shoppingCartItem.getQuantity());
                    preparedStatement1.setBigDecimal(5, shoppingCartItem.getDiscountPercent());
                    // executes
                    preparedStatement1.executeUpdate();
                }
            }
            // clears shopping cart after user checks out
            shoppingCartDao.delete(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // returns order
        return order;
    }
}
