package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

    @Autowired
    private IngredientService     ingredientService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe();
        r.addIngredient( new Ingredient( "Sugar", 10 ) );
        r.addIngredient( new Ingredient( "Coffee", 10 ) );
        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe r = new Recipe();
        r.addIngredient( new Ingredient( "Sugar", 10 ) );
        r.addIngredient( new Ingredient( "Coffee", 10 ) );
        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );
        r1.getIngredients().clear();
        Assertions.assertEquals( 0, r1.getIngredients().size() );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isNotAcceptable() );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 0 );

        final Ingredient i1 = new Ingredient( "Sugar", 10 );
        final Ingredient i2 = new Ingredient( "Coffee", 10 );

        // ingredientService.save( i1 );
        // ingredientService.save( i2 );

        r2.addIngredient( i1 );
        r2.addIngredient( i2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 0, 2, 1, 2 );
        r4.addIngredient( new Ingredient( "Milk", 5 ) );
        r4.addIngredient( new Ingredient( "Coco", 5 ) );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );

        return recipe;
    }

    /**
     * Tests deleting a recipe
     */
    @Test
    @Transactional
    public void deleteRecipeTest () throws Exception {

        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        // create recipe
        final Recipe rec = new Recipe();
        rec.setName( "drink1" );
        rec.setPrice( 10 );
        final Ingredient i1 = new Ingredient( "Sugar", 10 );
        final Ingredient i2 = new Ingredient( "Coffee", 10 );

        // ingredientService.save( i1 );
        // ingredientService.save( i2 );

        rec.addIngredient( i1 );
        rec.addIngredient( i2 );

        // save
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( rec ) ) ).andExpect( status().isOk() );

        final Recipe rec1 = new Recipe();
        rec1.setName( "drink2" );

        rec1.setPrice( 8 );
        final Ingredient i3 = new Ingredient( "Sugar", 10 );
        final Ingredient i4 = new Ingredient( "Coffee", 10 );

        // ingredientService.save( i3 );
        // ingredientService.save( i4 );

        rec1.addIngredient( i3 );
        rec1.addIngredient( i4 );

        // save
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( rec1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, (int) service.count() );

        // delete
        mvc.perform( delete( String.format( "/api/v1/recipes/%s", "drink2" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( rec1 ) ) ).andExpect( status().isOk() );

        // check if it is deleted
        Assertions.assertEquals( 1, (int) service.count() );
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testDeleteRecipe () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        final Recipe r = new Recipe();

        r.setPrice( 10 );
        r.setName( "Mocha" );
        r.addIngredient( new Ingredient( "Sugar", 10 ) );
        r.addIngredient( new Ingredient( "Coffee", 10 ) );
        // saving the recipe
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        // String.format( "/api/v1/makecoffee/%s", "Mocha"
        // deleting the recipe
        mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 0, (int) service.count() );
    }

    /**
     * Tests the RecipeService class can delete an edited Recipe from a list of
     * Recipes.
     */
    @Test
    @Transactional
    public void testDeleteRecipe2 () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        final Recipe r1 = new Recipe();

        r1.setPrice( 10 );
        r1.setName( "Mocha" );

        final Ingredient i1 = new Ingredient( "Sugar", 10 );
        final Ingredient i2 = new Ingredient( "Coffee", 10 );

        // ingredientService.save( i1 );
        // ingredientService.save( i2 );

        final Ingredient i3 = new Ingredient( "Sugar", 10 );
        final Ingredient i4 = new Ingredient( "Coffee", 10 );

        r1.addIngredient( i1 );
        r1.addIngredient( i2 );

        final Recipe r2 = new Recipe();

        r2.setPrice( 15 );
        r2.setName( "Latte" );

        r2.addIngredient( i3 );
        r2.addIngredient( i4 );
        final Recipe r3 = new Recipe();

        final Ingredient i5 = new Ingredient( "Sugar", 10 );
        final Ingredient i6 = new Ingredient( "Coffee", 10 );

        r3.setPrice( 5 );
        r3.setName( "Chocolate" );
        r3.addIngredient( i5 );
        r3.addIngredient( i6 );
        // saving the recipes
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r3 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 3, (int) service.count() );

        r2.setPrice( 25 );

        // deleting the edited recipe
        mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, (int) service.count() );
    }

    /**
     * Tests the RecipeService class by deleting an unsaved Recipe
     */
    @Test
    @Transactional
    public void testGetDeleteRecipe3 () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        final Recipe r1 = new Recipe();

        r1.setPrice( 10 );
        r1.setName( "Mocha" );
        r1.addIngredient( new Ingredient( "Sugar", 10 ) );
        r1.addIngredient( new Ingredient( "Coffee", 10 ) );

        // deleting an unsaved recipe
        mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isNotFound() );
        // saving the recipe
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );
        // deleting an unsaved recipe
        mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );
    }

    /**
     * Tests the RecipeService class by getting an unsaved Recipe and then saved
     * Recipe
     */
    @Test
    @Transactional
    public void testGetRecipe () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        final Recipe r1 = new Recipe();

        r1.setPrice( 10 );
        r1.setName( "Mocha" );
        r1.addIngredient( new Ingredient( "Sugar", 10 ) );
        r1.addIngredient( new Ingredient( "Coffee", 10 ) );
        // getting an unsaved recipe
        mvc.perform( get( String.format( "/api/v1/recipes/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isNotFound() );
        // saving the recipe
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );
        // retriieving the recipe
        mvc.perform( get( String.format( "/api/v1/recipes/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

    }

    /**
     * Test getting the list of ingredients
     */
    @Test
    @Transactional
    public void testGetRecipe1 () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        final Recipe r1 = new Recipe();

        r1.setPrice( 10 );
        r1.setName( "Mocha" );
        r1.addIngredient( new Ingredient( "Sugar", 10 ) );
        r1.addIngredient( new Ingredient( "Coffee", 20 ) );

        final Recipe r2 = new Recipe();

        r2.setPrice( 15 );
        r2.setName( "Latte" );

        service.save( r1 );
        service.save( r2 );
        // retriieving the recipe
        mvc.perform( get( String.format( "/api/v1/recipes" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

    }

    /**
     * Test updating recipe
     */
    @Test
    @Transactional
    public void testUpdate () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        final Recipe r1 = new Recipe();

        r1.setPrice( 10 );
        r1.setName( "Mocha" );
        r1.addIngredient( new Ingredient( "Sugar", 10 ) );
        r1.addIngredient( new Ingredient( "Coffee", 20 ) );

        mvc.perform( put( String.format( "/api/v1/recipes/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isNotFound() );

        service.save( r1 );
        // updating the recipe
        mvc.perform( put( String.format( "/api/v1/recipes/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

    }

}
