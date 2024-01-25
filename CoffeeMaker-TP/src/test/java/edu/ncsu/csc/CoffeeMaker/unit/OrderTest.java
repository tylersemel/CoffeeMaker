package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Order.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.Staff;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests the Order class and its service model.
 *
 * @author tlsemel
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class OrderTest {
    /** Order time placed */
    public static final String      TIME     = "29-Mar-2023 12:31:17PM";
    /** Order status */
    public static final OrderStatus STATUS   = OrderStatus.NOT_STARTED;
    /** Order's customer */
    public final Customer           CUSTOMER = new Customer( "ntcampbe", "cookie2!" );
    /** Order recipe list */
    public final List<Recipe>       recipes  = new ArrayList<Recipe>();

    public final Staff              STAFF    = new Staff( "ghgupton", "cookie2!" );

    /** RecipeService */
    @Autowired
    private IngredientService       ingredientService;

    /** OrderService */
    @Autowired
    private OrderService            orderService;

    /** RecipeService */
    @Autowired
    private RecipeService           recipeService;

    /**
     * deleting service before each tests
     */
    @BeforeEach
    public void setup () {
        orderService.deleteAll();
    }

    /**
     * Test that an order can be created.
     */
    @Transactional
    @Test
    void testOrderCreation () {
        final Order order = new Order();

        order.setTime( TIME );
        order.setCustomer( CUSTOMER );

        // saving the order to the order service
        orderService.save( order );

        // making sure that the order information is saved and retrievable
        assertEquals( TIME, order.getTime() );
        assertEquals( STATUS, order.getStatus() );
        assertEquals( CUSTOMER, order.getCustomer() );
        assertEquals( 0, order.getRecipes().size() );
        assertNull( order.getStaff() );

        // making sure that the order information is saved and retrievable from
        // the order service
        assertEquals( order, orderService.findById( order.getId() ) );
        assertEquals( TIME, orderService.findById( order.getId() ).getTime() );
        assertEquals( STATUS, orderService.findById( order.getId() ).getStatus() );
        assertEquals( CUSTOMER, orderService.findById( order.getId() ).getCustomer() );
        assertEquals( 0, orderService.findById( order.getId() ).getRecipes().size() );
        assertNull( orderService.findById( order.getId() ).getStaff() );
        assertEquals( 0, (int) orderService.findById( order.getId() ).getTotalCost() );
    }

    @Transactional
    @Test
    void testWithRecipes () {
        // create a recipe
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
        orderService.save( order );

        // check size of recipes list and contents are correct
        // assertEquals( 2, order.getRecipes().size() );
        // assertEquals( 2, orderService.findById( order.getId()
        // ).getRecipes().size() );
        //
        // assertEquals( r1, order.getRecipes().get( 0 ) );
        // assertEquals( r2, order.getRecipes().get( 1 ) );
        //
        // assertEquals( r1, orderService.findById( order.getId()
        // ).getRecipes().get( 0 ) );
        // assertEquals( r2, orderService.findById( order.getId()
        // ).getRecipes().get( 1 ) );
        // assertEquals( 11, orderService.findById( order.getId()
        // ).getTotalCost() );
    }

    @Transactional
    @Test
    void testWithStaff () {
        // create a recipe
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

        order.setStaff( STAFF );
        orderService.save( order );

        // // making sure that the order information is saved and retrievable
        // assertEquals( TIME, order.getTime() );
        // assertEquals( STATUS, order.getStatus() );
        // assertNull( order.getCustomer() );
        // assertEquals( STAFF, order.getStaff() );
        //
        // // check size of recipes list and contents are correct
        // assertEquals( 2, order.getRecipes().size() );
        // assertEquals( 2, orderService.findById( order.getId()
        // ).getRecipes().size() );
        //
        // assertEquals( r1, order.getRecipes().get( 0 ) );
        // assertEquals( r2, order.getRecipes().get( 1 ) );
        //
        // assertEquals( r1, orderService.findById( order.getId()
        // ).getRecipes().get( 0 ) );
        // assertEquals( r2, orderService.findById( order.getId()
        // ).getRecipes().get( 1 ) );
        // assertEquals( 11, orderService.findById( order.getId()
        // ).getTotalCost() );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Ingredient i ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( i );

        return recipe;
    }
}
