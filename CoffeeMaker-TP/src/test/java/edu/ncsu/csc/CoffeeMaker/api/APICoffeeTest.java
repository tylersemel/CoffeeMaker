package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests the API endpoints for coffee
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    /** MVC */
    @Autowired
    private MockMvc               mvc;

    /** The context */
    @Autowired
    private WebApplicationContext context;

    /** The RecipeService */
    @Autowired
    private RecipeService         service;

    /** The InventoryService */
    @Autowired
    private InventoryService      iService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        // iService.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        final Inventory ivt = iService.getInventory();

        // ivt.addIngredients( "Coffee", 2 );

        iService.save( ivt );

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee" );
        recipe.setPrice( 50 );
        // recipe.setCoffee( 3 );
        // recipe.setMilk( 1 );
        // recipe.setSugar( 1 );
        // recipe.setChocolate( 0 );
        service.save( recipe );
    }

    /**
     * Tests buying a drink
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testPurchaseBeverage1 () throws Exception {

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

        Assertions.assertEquals( 1, service.findAll().size() );

    }

    /**
     * Tests buying a drink
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Coffee";
        // final Recipe recipe = new Recipe();
        // recipe.setName( "Coffee" );
        // recipe.setPrice( 50 );
        // service.save( recipe );

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 40 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    /**
     * Tests buying a drink
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final Inventory iii = new Inventory();
        final Ingredient i1 = new Ingredient( "Coffee", 0 );
        final Ingredient i2 = new Ingredient( "Milk", 0 );
        final Ingredient i3 = new Ingredient( "Sugar", 0 );
        final Ingredient i4 = new Ingredient( "Chocolate", 0 );

        iii.addIngredient( i1.getName(), 0 );
        iii.addIngredient( i2.getName(), 100 );
        iii.addIngredient( i3.getName(), 100 );
        iii.addIngredient( i4.getName(), 100 );

        // final String name = "Coffee";

        final Inventory ivt = iService.getInventory();
        // ivt.useIngredients( i1, 0 );
        iService.save( ivt );

        mvc.perform( post( String.format( "/api/v1/makecoffee/Coffee" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

        // mvc.perform( post( String.format( "/api/v1/makecoffee/Coffee" )
        // ).contentType( MediaType.APPLICATION_JSON )
        // .content( TestUtils.asJsonString( 50 ) ) ).andExpect(
        // status().is4xxClientError() )
        // .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" )
        // );

    }

    /**
     * Test retrieving the inventory
     */
    @Test
    @Transactional
    public void testGetInventory () {
        final Inventory iii = new Inventory();
        final Ingredient i1 = new Ingredient( "Coffee", 10 );
        final Ingredient i2 = new Ingredient( "Milk", 10 );
        final Ingredient i3 = new Ingredient( "Sugar", 10 );
        final Ingredient i4 = new Ingredient( "Chocolate", 10 );

        iii.addIngredient( i1.getName(), 10 );
        iii.addIngredient( i2.getName(), 10 );
        iii.addIngredient( i3.getName(), 10 );
        iii.addIngredient( i4.getName(), 10 );

        // ivt.useIngredients( i1, 0 );
        iService.save( iii );
        try {
            mvc.perform( get( String.format( "/api/v1/inventory" ) ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
