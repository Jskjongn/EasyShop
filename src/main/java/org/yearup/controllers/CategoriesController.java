package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// annotations for class
@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;

    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao)
    {
        this.categoryDao = categoryDao;
    }

    @GetMapping
    public List<Category> getAll()
    {
        // find and return all categories
        return categoryDao.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        try
        {
            var category = categoryDao.getById(id);

            if(category == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            return category;
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // decided to do this method in the ProductsController instead
//    @GetMapping("{categoryId}/products")
//    public List<Product> getProductsById(@PathVariable int categoryId)
//    {
//        // get a list of product by categoryId
//        return null;
//    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category
        return null;
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
    }
}
