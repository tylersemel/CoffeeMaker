package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;
    // creating the ingredients
    final Ingredient         i1 = new Ingredient( "Coffee", 5 );
    final Ingredient         i2 = new Ingredient( "Mocha", 5 );
    final Ingredient         i3 = new Ingredient( "Caramel", 5 );
    final Ingredient         i4 = new Ingredient( "Milk", 5 );

    @BeforeEach
    public void setup () {
        // clearing the inventory
        inventoryService.deleteAll();
    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory ivt = new Inventory();
        // setting an ingredient in inventory
        ivt.addIngredient( i1.getName(), 100 );

        // making a recipoe
        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        // add same ingredient that requires 5 units to recipe
        recipe.addIngredient( i1 );

        recipe.setPrice( 5 );
        // use ingredient
        ivt.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 95, (int) ivt.getIngredientAmount( i1 ) );
    }

    @Test
    @Transactional
    public void testAddInventory1 () {
        final Inventory ivt = inventoryService.getInventory();
        Assertions.assertEquals( 1, (int) inventoryService.count() );

        // creating the Ingredients in the Inventory
        ivt.addIngredient( i1.getName(), 10 );
        ivt.addIngredient( i2.getName(), 20 );
        ivt.addIngredient( i3.getName(), 30 );
        ivt.addIngredient( i4.getName(), 40 );

        /* Save and retrieve again to update with ivt */
        inventoryService.save( ivt );

        Assertions.assertEquals( 10, (int) ivt.getIngredientAmount( i1 ),
                "Adding to the inventory should result in correctly-updated values for coffee" );
        Assertions.assertEquals( 20, (int) ivt.getIngredientAmount( i2 ),
                "Adding to the inventory should result in correctly-updated values for mocha" );
        Assertions.assertEquals( 30, (int) ivt.getIngredientAmount( i3 ),
                "Adding to the inventory should result in correctly-updated values caramel" );
        Assertions.assertEquals( 40, (int) ivt.getIngredientAmount( i4 ),
                "Adding to the inventory should result in correctly-updated values milk" );

    }

    /**
     * Checking inventory after trying to add negative amount
     */
    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            ivt.addIngredient( i1.getName(), -2 );
        }
        catch ( final IllegalArgumentException iae ) {

            Assertions.assertEquals( -1, (int) ivt.getIngredientAmount( i1 ),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes" );
        }
    }

    /**
     * Checking inventory after a successful inventory update
     */
    @Test
    @Transactional
    public void testAddInventory3 () {
        final Inventory ivt = inventoryService.getInventory();
        // add ingredient
        // creating the Ingredients in the Inventory
        ivt.addIngredient( i1.getName(), 10 );
        ivt.addIngredient( i2.getName(), 20 );
        ivt.addIngredient( i3.getName(), 30 );
        ivt.addIngredient( i4.getName(), 40 );

        // update the ingredient amount
        ivt.updateIngredient( i1, 50 );
        ivt.updateIngredient( i2, 50 );
        ivt.updateIngredient( i3, 50 );
        ivt.updateIngredient( i4, 50 );
        Assertions.assertEquals( 60, (int) ivt.getIngredientAmount( i1 ),
                "Adding to the inventory should result in correctly-updated values for coffee" );
        Assertions.assertEquals( 70, (int) ivt.getIngredientAmount( i2 ),
                "Adding to the inventory should result in correctly-updated values for coffee" );

        Assertions.assertEquals( 80, (int) ivt.getIngredientAmount( i3 ),
                "Adding to the inventory should result in correctly-updated values for coffee" );

        Assertions.assertEquals( 90, (int) ivt.getIngredientAmount( i4 ),
                "Adding to the inventory should result in correctly-updated values for coffee" );

    }

    /**
     * Checking the inventory after a recipe with more ingredients is called
     */
    @Test
    @Transactional
    public void testInventoryAfterUse () {
        final Inventory ivt = new Inventory();
        ivt.addIngredient( i1.getName(), 10 );
        ivt.addIngredient( i2.getName(), 20 );
        ivt.addIngredient( i3.getName(), 30 );
        ivt.addIngredient( i4.getName(), 40 );

        final Recipe recipe = new Recipe();

        recipe.setName( "Caramel" );

        recipe.setPrice( 5 );
        // adding ingredients - each should have a unit of 5
        recipe.addIngredient( i1 );
        recipe.addIngredient( i2 );
        recipe.addIngredient( i3 );
        recipe.addIngredient( i4 );

        // checking amount
        Assertions.assertEquals( 10, (int) ivt.getIngredientAmount( i1 ) );
        Assertions.assertEquals( 20, (int) ivt.getIngredientAmount( i2 ) );
        Assertions.assertEquals( 30, (int) ivt.getIngredientAmount( i3 ) );
        Assertions.assertEquals( 40, (int) ivt.getIngredientAmount( i4 ) );

        // making coffee and checking the inventory
        ivt.useIngredients( recipe );
        // checking amount after use
        Assertions.assertEquals( 5, (int) ivt.getIngredientAmount( i1 ) );
        Assertions.assertEquals( 15, (int) ivt.getIngredientAmount( i2 ) );
        Assertions.assertEquals( 25, (int) ivt.getIngredientAmount( i3 ) );
        Assertions.assertEquals( 35, (int) ivt.getIngredientAmount( i4 ) );

    }

    /**
     * Testing the updateIngredient with invalid inputs
     */
    @Test
    @Transactional
    public void testUpdateInventoryInvalid () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.updateIngredient( i1, 10 );
        ivt.updateIngredient( i2, 20 );
        ivt.updateIngredient( i3, 30 );
        ivt.updateIngredient( i4, 40 );

        // using a negative amt
        final Integer amt = -1;
        // testing invalid products
        final Exception e1 = assertThrows( IllegalArgumentException.class, () -> ivt.updateIngredient( i1, amt ) );
        assertEquals( "Units of ingredient must be a positive integer", e1.getMessage() );

        final Integer amt2 = 110;
        final Exception e2 = assertThrows( IllegalArgumentException.class, () -> ivt.updateIngredient( i2, amt2 ) );
        assertEquals( "Units of ingredient must be less than 100", e2.getMessage() );

    }

    /*
     * Testing to make sure a coffee cannot be made with insufficient inventory
     */
    @Test
    @Transactional
    public void lowInventory () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.addIngredient( i1.getName(), 3 );
        ivt.addIngredient( i2.getName(), 3 );
        ivt.addIngredient( i3.getName(), 3 );
        ivt.addIngredient( i4.getName(), 3 );

        final Recipe recipe = new Recipe();

        recipe.setName( "Caramel" );

        recipe.setPrice( 5 );
        // adding ingredients - each should have a unit of 5
        recipe.addIngredient( i1 );
        recipe.addIngredient( i2 );
        recipe.addIngredient( i3 );
        recipe.addIngredient( i4 );
        // making coffee and checking the inventory
        assertFalse( ivt.useIngredients( recipe ) );

    }

    @Test
    @Transactional
    public void testToString () {

        final Inventory ivt = inventoryService.getInventory();
        ivt.updateIngredient( i1, 10 );
        ivt.updateIngredient( i2, 20 );
        ivt.updateIngredient( i3, 30 );
        ivt.updateIngredient( i4, 40 );
        assertEquals(
                "Ingredient [id=null, ingredient=Coffee, amount=5, getId()=null, getType()=Coffee, getAmount()=5]",
                i1.toString() );

    }

}
