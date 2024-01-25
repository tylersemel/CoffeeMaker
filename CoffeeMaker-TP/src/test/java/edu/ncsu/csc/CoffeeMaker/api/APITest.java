package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

/**
 * To allow API tests to run w/o needing to explicitly launch the Jetty
 *
 * @author Rose Xiao Testing CoffeeMaker's REST API endpoints
 */
@RunWith ( SpringRunner.class )
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /** The context */
    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Testing the database
     *
     * @throws Exception
     *             the exception
     * @throws UnsupportedEncodingException
     *             the exception
     */
    @Test
    @Transactional
    public void testingAPI () throws UnsupportedEncodingException, Exception {
        // Call API end point to retrieve all recipes
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final Recipe r = new Recipe();
        // checking if the database contains "Mocha"
        if ( !recipe.contains( "Mocha" ) ) {

            final Ingredient i1 = new Ingredient( "Sugar", 10 );
            final Ingredient i2 = new Ingredient( "Coffee", 10 );

            // ingredientService.save( i1 );
            // ingredientService.save( i2 );

            r.addIngredient( i1 );
            r.addIngredient( i2 );

            Assertions.assertEquals( r.getIngredients().size(), 2 );

            r.setPrice( 9 );
            r.setName( "Mocha" );

            // adding the recipe to database as JSON through POST request
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

        }
        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        // checking to make sure the recipe is in database
        assertTrue( recipe.contains( "Mocha" ) );

        final Inventory i = new Inventory();
        i.addIngredient( "suger", 1 );
        final Ingredient i2 = new Ingredient( "Caramel", 2 );
        // updating the inventory
        mvc.perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isConflict() );
        i2.setAmount( -3 );
        mvc.perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isNotAcceptable() );

        final Ingredient i3 = new Ingredient( "Vanilla", 2 );
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i3 ) ) ).andExpect( status().isConflict() );

    }

    /**
     * Test mapping
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testMapping () throws Exception {
        mvc.perform( get( "/index" ) );
        mvc.perform( get( "/recipe" ) );
        mvc.perform( get( "/deleterecipe" ) );
        mvc.perform( get( "/editrecipe" ) );
        mvc.perform( get( "/inventory" ) );
        mvc.perform( get( "/makecoffee" ) );
        mvc.perform( get( "/addNewIngredient" ) );
        mvc.perform( get( "/customerSignup" ) );
        mvc.perform( get( "/staffLogin" ) );
        mvc.perform( get( "/staffHomepage" ) );
        mvc.perform( get( "/adminHomepage" ) );
        mvc.perform( get( "/createStaff" ) );

    }

}
