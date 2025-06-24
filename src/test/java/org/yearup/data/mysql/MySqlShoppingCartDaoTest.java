package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.yearup.models.ShoppingCart;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MySqlShoppingCartDaoTest extends BaseDaoTestClass {

    private MySqlProductDao productDao;
    private MySqlShoppingCartDao shoppingCartDao;

    @BeforeEach
    public void setup() {
        productDao = new MySqlProductDao(dataSource);
        shoppingCartDao = new MySqlShoppingCartDao(dataSource, productDao);
    }

    @Test
    public void getByUserId_shouldReturn_ShoppingCartWithItems()
    {
        // arrange
        int userId = 1;

        // act
        ShoppingCart shoppingCart = shoppingCartDao.getByUserId(userId);

        // assert
        assertFalse(shoppingCart.getItems().isEmpty());
    }
}
