package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIIngredientTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /** The context */
    @Autowired
    private WebApplicationContext context;

    /** The IngredientService */
    @Autowired
    private IngredientService     service;

    /**
     * Set up
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Testing the API endpoints by adding one Ingredient
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testIngredientAPI () throws Exception {
        service.deleteAll();

        final Ingredient ingredient = new Ingredient( "Coffee", 10 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient ) ) );

        Assertions.assertEquals( 1, (int) service.count() );
    }

    /**
     * Attempting to add duplicates
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testAddDupes () throws Exception {
        service.deleteAll();

        final Ingredient ingredient = new Ingredient( "Coffee", 10 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient ) ) );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient ) ) );
        Assertions.assertEquals( 2, (int) service.count() );
    }

    /**
     * Adding a couple of ingredients
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testAddIngredient1 () throws Exception {
        service.deleteAll();

        /*
         * Tests a Ingredient with a duplicate name to make sure it's rejected
         */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Ingredients in the CoffeeMaker" );
        final Ingredient i1 = new Ingredient( "Coffee", 10 );
        final Ingredient i2 = new Ingredient( "Sugar", 10 );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, service.findAll().size(), "There should only one Ingredient in the CoffeeMaker" );
    }

    /**
     * Tests deleting an Ingredient
     */
    @Test
    @Transactional
    public void deleteIngredientTest () throws Exception {

        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        // creating ingredients
        final Ingredient i1 = new Ingredient( "Coffee", 10 );
        final Ingredient i2 = new Ingredient( "Sugar", 10 );

        // save
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, (int) service.count() );

        // delete
        mvc.perform( delete( String.format( "/api/v1/ingredients/%s", "Sugar" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i2 ) ) )
                .andExpect( status().isOk() );

        // check if it is deleted
        Assertions.assertEquals( 1, (int) service.count() );
    }

    /**
     * Tests the deleting all ingredients
     */
    @Test
    @Transactional
    public void deleteIngredientTest2 () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );

        // creating ingredients
        final Ingredient i1 = new Ingredient( "Coffee", 10 );
        final Ingredient i2 = new Ingredient( "Sugar", 10 );

        // save
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, (int) service.count() );

        // delete
        mvc.perform( delete( String.format( "/api/v1/ingredients/%s", "Coffee" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isOk() );

        // check if it is deleted
        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertEquals( i2.getName(), service.findAll().get( 0 ).getName() );
        // delete
        mvc.perform( delete( String.format( "/api/v1/ingredients/%s", "Sugar" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i2 ) ) )
                .andExpect( status().isOk() );
        // check if it is deleted
        Assertions.assertEquals( 0, (int) service.count() );

    }

    /**
     * Test deleting an ingredient that is not added
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void deleteIngredientTest3 () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, (int) service.count() );
        final Ingredient i1 = new Ingredient( "Coffee", 10 );
        // delete
        mvc.perform( delete( String.format( "/api/v1/ingredients/%s", "Coffee" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isNotFound() );
        Assertions.assertEquals( 0, (int) service.count() );
    }

    /**
     * Test retrieving the list of Ingredients
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testIngredientsList () throws Exception {
        service.deleteAll();

        // creating ingredients
        final Ingredient i1 = new Ingredient( "Coffee", 10 );
        final Ingredient i2 = new Ingredient( "Sugar", 10 );

        // save
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        // retrieve the list
        mvc.perform( get( String.format( "/api/v1/ingredients" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i1 ) ) ).andExpect( status().isOk() );

    }

    /**
     * Test retrieving the list of Ingredients
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testGetIngredient () throws Exception {
        service.deleteAll();

        // creating ingredients
        final Ingredient i1 = new Ingredient( "Coffee", 10 );
        final Ingredient i2 = new Ingredient( "Sugar", 10 );

        // save
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        // retriieving an unsaved ingredient
        mvc.perform( get( String.format( "/api/v1/ingredients/%s", "Honey" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i1 ) ) ).andExpect( status().isNotFound() );

        // retriieving the ingredient
        mvc.perform( get( String.format( "/api/v1/ingredients/%s", "Coffee" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isOk() );

        // retriieving the ingredient
        mvc.perform( get( String.format( "/api/v1/ingredients/%s", "Sugar" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        // // retriieving the inventory
        // mvc.perform( get( String.format( "/api/v1/inventory" ).contentType(
        // MediaType.APPLICATION_JSON )
        // .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk()
        // );

    }

}
