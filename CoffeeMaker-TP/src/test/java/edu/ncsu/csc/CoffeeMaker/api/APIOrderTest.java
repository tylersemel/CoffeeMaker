package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Order.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.Staff;
import edu.ncsu.csc.CoffeeMaker.services.CustomerService;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.StaffService;

/**
 * Tests the Order class and its service model.
 *
 * @author tlsemel
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIOrderTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc                 mvc;

    /** The context */
    @Autowired
    private WebApplicationContext   context;

    /** The OrderService */
    @Autowired
    private OrderService            orderService;

    /** The CustomerService */
    @Autowired
    private CustomerService         customerService;

    /** The StaffService */
    @Autowired
    private StaffService            staffService;

    /** The IngredientService */
    @Autowired
    private IngredientService       ingredientService;

    /** The InventoryService */
    @Autowired
    private InventoryService        inventoryService;

    /** The RecipeService */
    @Autowired
    private RecipeService           recipeService;

    /** Order name */
    public static final String      NAME     = "2";
    /** Order time placed */
    public static final String      TIME     = "29-Mar-2023 12:31:17PM";
    /** Order status */
    public static final OrderStatus STATUS   = OrderStatus.NOT_STARTED;
    /** Order's customer */
    public final Customer           customer = new Customer( "ntcampbe", "cookie2!" );
    /** Order recipe list */
    public final List<Recipe>       recipes  = new ArrayList<Recipe>();

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        orderService.deleteAll();

    }

    /**
     * Tests that a customer can place an order.
     *
     * @throws Exception
     *             if the user cannot place an order.
     */
    @Test
    @Transactional
    public void testPlaceOrder () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );

        // Assertions.assertEquals( order, orderService.findById( order.getId()
        // ) );

        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

    }

    /**
     * Testing that an invalid order cannot be placed
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testPlaceOrderInvalid () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );
        final Order order2 = new Order( recipes, TIME );

        // cust does not exist
        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "ntcampbe" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isConflict() );

        // valid add
        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );
        // duplicate name
        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "ntcampbe" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order2 ) ) )
                .andExpect( status().isConflict() );

        Assertions.assertEquals( 1, (int) orderService.count() );

    }

    /**
     * Testing that an order can be picked up
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testPickUpOrder () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        customerService.save( c1 );
        Assertions.assertEquals( 1, (int) customerService.count() );

        // have one staff to make the order
        final Staff s1 = new Staff( "staffUser1", "password1?" );
        staffService.save( s1 );
        Assertions.assertEquals( 1, (int) staffService.count() );

        // create new inventory
        final Inventory ivt = new Inventory();
        inventoryService.save( ivt );

        // ingredients to save
        final Ingredient i1 = new Ingredient( "Chocolate Syrup", 2 );
        final Ingredient i2 = new Ingredient( "Coffee", 3 );
        ingredientService.save( i1 );
        ingredientService.save( i2 );
        Assertions.assertEquals( 2, (int) ingredientService.count() );

        ivt.addIngredient( i1.getName(), 3 );
        ivt.addIngredient( i2.getName(), 10 );
        inventoryService.save( ivt );
        Assertions.assertEquals( 1, (int) inventoryService.count() );
        // Assertions.assertEquals( inventoryService.findAll().get( 0 ),
        // inventoryService.getInventory() );

        // recipe that contains ingredients
        final Recipe r1 = createRecipe( "MochaLatte", 5, i1 );
        r1.addIngredient( i2 );

        recipes.add( r1 );
        recipeService.save( r1 );
        Assertions.assertEquals( 1, (int) recipeService.count() );

        // create order
        final Order order = new Order( recipes, TIME );

        // PLACE ORDER from customer
        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        // update to in progress by staff
        mvc.perform( put( String.format( "/api/v1/orders/inprogress/%s", "staffUser1" ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );

        // update to in complete by staff
        mvc.perform( put( String.format( "/api/v1/orders/complete/%s", "6" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );

        // update to pick up from customer
        mvc.perform( put( String.format( "/api/v1/orders/pickup/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );

    }

    /**
     * Testing in progress
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testInProgress () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );

        // valid add
        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );
        // Assertions.assertEquals( order, );

        final Staff s1 = new Staff( "staffUser1", "password1?" );

        staffService.save( s1 );

        mvc.perform( put( String.format( "/api/v1/orders/inprogress/%s", "staffUser1" ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );
    }

    /**
     * Testing in progress invalid
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testInProgressInvalid () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );
        final Order order2 = new Order( recipes, TIME );

        // Assertions.assertEquals( order.get, (int) orderService. );

        // valid add
        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        final Staff s1 = new Staff( "staffUser1", "password1?" );

        staffService.save( s1 );

        // Invalid Staff
        mvc.perform( put( String.format( "/api/v1/orders/inprogress/%s", "notStaffUser" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isConflict() );

        // Invalid Staff
        mvc.perform( put( String.format( "/api/v1/orders/inprogress/%s", "staffUser" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order2 ) ) )
                .andExpect( status().isConflict() );

        // valid Add
        mvc.perform( put( String.format( "/api/v1/orders/inprogress/%s", "staffUser1" ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );
    }

    /**
     * Testing complete invalid
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testCompleteInvalid () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        customerService.save( c1 );
        Assertions.assertEquals( 1, (int) customerService.count() );

        final String n1 = "Coffee";
        final Ingredient i1 = new Ingredient( "Coffee", 2 );
        final Recipe r1 = createRecipe( n1, 5, i1 );

        final String n2 = "Green Tea";
        final Ingredient i2 = new Ingredient( "Matcha", 3 );
        final Recipe r2 = createRecipe( n2, 6, i2 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );

        // add them to the list and save in the service
        recipes.add( r1 );
        recipes.add( r2 );

        recipeService.save( r1 );
        recipeService.save( r2 );

        final Order order = new Order( recipes, TIME );
        final Order order2 = new Order( recipes, TIME );

        // valid add
        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        final Staff s1 = new Staff( "staffUser1", "password1?" );

        staffService.save( s1 );

        // valid Add
        mvc.perform( put( String.format( "/api/v1/orders/inprogress/%s", "staffUser1" ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );

        final Inventory ivtInventory = inventoryService.getInventory();

        ivtInventory.addIngredient( "Coffee", 20 );
        ivtInventory.addIngredient( "Matcha", 20 );

        inventoryService.save( ivtInventory );

        // Valid
        mvc.perform(
                put( String.format( "/api/v1/orders/complete/%s", "100" ) ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) )
                .andExpect( status().isOk() );

        // invalid order
        mvc.perform( put( String.format( "/api/v1/orders/complete/%s", "100" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order2 ) ) )
                .andExpect( status().isConflict() );

        // not num passed
        mvc.perform( put( String.format( "/api/v1/orders/complete/%s", "NotNum" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isConflict() );

        // to little money
        mvc.perform( put( String.format( "/api/v1/orders/complete/%s", "1" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order ) ) ).andExpect( status().isConflict() );

    }

    /**
     * Testing canceling an orer
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testCancelOrder () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );

        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        // final Order order2 = orderService.findAll().get( 0 );
        mvc.perform( put( String.format( "/api/v1/orders/cancel/%s", orderService.findAll().get( 0 ).getName() ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );
    }

    /**
     * Testing an invalid cancel
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testCancelOrderInvalid () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );

        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        mvc.perform( put( String.format( "/api/v1/orders/cancel/%s", "Not" ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 1, (int) orderService.count() );
    }

    /**
     * Testing getting the order
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testGetOrder () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );

        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        mvc.perform( get( String.format( "/api/v1/orders/%s", orderService.findAll().get( 0 ).getName() ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        mvc.perform( get( String.format( "/api/v1/orders" ) ) ).andExpect( status().isOk() );

    }

    /**
     * Testing getting an invalid order
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testGetOrderInvalid () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );

        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        mvc.perform( get( String.format( "/api/v1/orders/%s", "notName" ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 1, (int) orderService.count() );
    }

    /**
     * Testing getting an order from a customer
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testGetOrderByCustomer () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) customerService.count() );

        final Order order = new Order( recipes, TIME );

        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        mvc.perform( get( String.format( "/api/v1/orders/customer/%s", "custUser1" ) ) ).andExpect( status().isOk() );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Ingredient i ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( i );

        return recipe;
    }

    /**
     * Testing picking up an order
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testPickUp () throws Exception {
        // have one customer saved in the service so that an order can be placed
        final Customer c1 = new Customer( "custUser1", "password1?" );
        customerService.save( c1 );
        Assertions.assertEquals( 1, (int) customerService.count() );

        final String n1 = "Coffee";
        final Ingredient i1 = new Ingredient( "Coffee", 2 );
        final Recipe r1 = createRecipe( n1, 5, i1 );

        final String n2 = "Green Tea";
        final Ingredient i2 = new Ingredient( "Matcha", 3 );
        final Recipe r2 = createRecipe( n2, 6, i2 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );

        // add them to the list and save in the service
        recipes.add( r1 );
        recipes.add( r2 );

        recipeService.save( r1 );
        recipeService.save( r2 );

        final Order order = new Order( recipes, TIME );

        // valid add
        mvc.perform( post( String.format( "/api/v1/orders/place/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( order ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) orderService.count() );

        final Staff s1 = new Staff( "staffUser1", "password1?" );

        staffService.save( s1 );

        // valid Add
        mvc.perform( put( String.format( "/api/v1/orders/inprogress/%s", "staffUser1" ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );

        Inventory ivtInventory = inventoryService.getInventory();

        ivtInventory.addIngredient( "Coffee", 20 );
        ivtInventory.addIngredient( "Matcha", 20 );

        inventoryService.save( ivtInventory );

        mvc.perform(
                put( String.format( "/api/v1/orders/complete/%s", "100" ) ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) )
                .andExpect( status().isOk() );

        ivtInventory = inventoryService.getInventory();

        assertEquals( 17, ivtInventory.getIngredientAmount( i2 ) );
        assertEquals( 18, ivtInventory.getIngredientAmount( i1 ) );

        mvc.perform( put( String.format( "/api/v1/orders/pickup/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );

    }
}
