package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MySqlCategoryDaoTest extends BaseDaoTestClass {

    private MySqlCategoryDao categoryDao;

    @BeforeEach
    public void setup() {

        categoryDao = new MySqlCategoryDao(dataSource);
    }

    @Test
    public void getAllCategories_shouldReturn_allCategories()
    {
        // arrange
        List<Category> categories = categoryDao.getAllCategories();

        // assert
        assertFalse(categories.isEmpty());

        for (Category category : categories) {
            assertNotNull(category.getCategoryId());
            assertNotNull(category.getName());
            assertNotNull(category.getDescription());
        }
    }

    @Test
    public void getById_shouldReturn_theCorrectCategory()
    {
        // arrange
        int categoryId = 1;

        // act
        Category id = categoryDao.getById(categoryId);

        // assert
        assertEquals(1, id.getCategoryId());
    }

    @Test
    public void create_shouldReturn_theNewCategory()
    {
        // arrange
        Category newCategory = new Category()
        {{
            setName("Toys & Games");
            setDescription("Have all the fun you need with our inventory full of toys and games to take away the boredom");
        }};

        // act
        Category actual = categoryDao.create(newCategory);

        // assert
        assertEquals(4, actual.getCategoryId());
        assertEquals("Toys & Games", actual.getName());
        assertEquals("Have all the fun you need with our inventory full of toys and games to take away the boredom", actual.getDescription());
    }
}
