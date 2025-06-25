package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

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

    @Test
    public void addToCart_shouldAdd_itemsToShoppingCart()
    {
        // arrange
        int userId = 1;
        int productId = 1;

        // act
        ShoppingCartItem shoppingCartItem = shoppingCartDao.addToCart(userId, productId);

        // assert
        assertEquals(1, shoppingCartItem.getProductId());
        assertEquals(1, shoppingCartItem.getQuantity());
    }

    @Test
    public void update_shouldUpdate_itemQuantityInShoppingCart()
    {
        // arrange
        int userId = 1;
        int productId = 1;

        ShoppingCartItem shoppingCartItem = shoppingCartDao.addToCart(userId, productId);
        shoppingCartItem.setQuantity(2);

        // act
        shoppingCartDao.update(userId, productId, shoppingCartItem);

        // assert
        assertEquals(2, shoppingCartItem.getQuantity());
    }

    @Test
    public void delete_shouldClear_shoppingCart()
    {
        // arrange
        int userId = 1;
        int productId = 1;

        shoppingCartDao.addToCart(userId, productId);

        // act
        shoppingCartDao.delete(userId);
        ShoppingCart shoppingCart = shoppingCartDao.getByUserId(userId);

        // assert
        assertTrue(shoppingCart.getItems().isEmpty());
    }
}
