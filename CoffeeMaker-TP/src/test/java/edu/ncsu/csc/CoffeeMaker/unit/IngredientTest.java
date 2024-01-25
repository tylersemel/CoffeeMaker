package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Assert;
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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class IngredientTest {
    @Autowired
    private IngredientService ingredientService;

    /** Ingredient name to test */
    private final String      coffee        = "COFFEE";
    /** Ingredient name to test */
    private final String      caramel       = "CARAMEL";
    /** Ingredient name to test */
    private final String      pumpkin_spice = "PUMPKIN_SPICE";
    /** Ingredient name to test */
    private final String      vanilla_syrup = "VANILLA_SYRUP";

    /**
     * Sets up the Ingredient Service.
     */
    @BeforeEach
    public void setup () {
        ingredientService.deleteAll();

    }

    /**
     * Tests that an ingredient can be created in the database.
     *
     * @author rxiao3
     */
    @Test
    @Transactional
    public void testCreateIngredients () {

        final Ingredient i1 = new Ingredient( coffee, 5 );

        ingredientService.save( i1 );

        final Ingredient i2 = new Ingredient( caramel, 3 );

        ingredientService.save( i2 );

        Assert.assertEquals( 2, ingredientService.count() );

    }

    /**
     * Tests that an ingredient with an invalid amount cannot be saved.
     *
     * @author rxiao3
     */
    @Test
    @Transactional
    public void testSavingInvalidAmt () {
        final Ingredient i = new Ingredient( pumpkin_spice, -3 );

        try {
            ingredientService.save( i );
            final List<Ingredient> ingredients = ingredientService.findAll();
            Assertions.assertEquals( 1, ingredientService.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
            Assertions.assertEquals( i, ingredients.get( 0 ), "The retrieved recipe should match the created one" );

        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    /**
     * Tests retrieving an unsaved ingredient
     *
     * @author rxiao3
     */
    @Test
    @Transactional
    public void testRetreivingUnsavedIngredient () {
        final Ingredient i = new Ingredient( pumpkin_spice, 3 );
        try {

            Assertions.assertEquals( 0, ingredientService.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );

        }

    }

    /**
     * Tests that two different ingredients can be retrieved.
     *
     * @author rxiao3
     */
    @Test
    @Transactional
    public void testRetreivingTypes () {
        final Ingredient i1 = new Ingredient( vanilla_syrup, 5 );
        final Ingredient i2 = new Ingredient( pumpkin_spice, 3 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );

        final List<Ingredient> ingredients = ingredientService.findAll();
        Assertions.assertEquals( 2, ingredients.size(),
                "Creating two ingredients should result in two ingredients in the database" );

        Assertions.assertEquals( i1, ingredients.get( 0 ), "The retrieved recipe should match the created one" );
        Assertions.assertEquals( i2, ingredients.get( 1 ), "The retrieved recipe should match the created one" );

    }

    /**
     * Tests that two ingredients can be retrieved from the service.
     *
     * @author rxiao3
     */
    @Test
    @Transactional
    public void testRetreivingTwo () {
        final Ingredient i1 = new Ingredient( coffee, 5 );
        final Ingredient i2 = new Ingredient( caramel, 3 );
        final Ingredient i3 = new Ingredient( pumpkin_spice, 4 );
        final Ingredient i4 = new Ingredient( vanilla_syrup, 2 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );
        ingredientService.save( i3 );
        ingredientService.save( i4 );

        final List<Ingredient> ingredients = ingredientService.findAll();
        Assert.assertEquals( 4, ingredientService.count() );
        Assert.assertEquals( 4, ingredients.size() );
        assertEquals( i1, ingredients.get( 0 ) );
        assertEquals( vanilla_syrup, ingredients.get( 3 ).getName() );
        assertEquals( pumpkin_spice, ingredients.get( 2 ).getName() );
        assertEquals( caramel, ingredients.get( 1 ).getName() );
        assertEquals( coffee, ingredients.get( 0 ).getName() );

        assertEquals( (Integer) 2, ingredients.get( 3 ).getAmount() );
        assertEquals( (Integer) 4, ingredients.get( 2 ).getAmount() );
        assertEquals( (Integer) 3, ingredients.get( 1 ).getAmount() );
        assertEquals( (Integer) 5, ingredients.get( 0 ).getAmount() );

    }

    /**
     * Tests that one ingredient can be deleted.
     *
     * @author rxiao3
     */
    @Test
    @Transactional
    public void testDeletingOne () {
        final Ingredient i1 = new Ingredient( coffee, 5 );
        final Ingredient i2 = new Ingredient( caramel, 3 );
        final Ingredient i3 = new Ingredient( pumpkin_spice, 4 );
        final Ingredient i4 = new Ingredient( vanilla_syrup, 2 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );
        ingredientService.save( i3 );
        ingredientService.save( i4 );

        final List<Ingredient> ingredients = ingredientService.findAll();
        Assert.assertEquals( ingredients.size(), ingredientService.count() );

        // deleting the last ingredient
        ingredientService.delete( i4 );
        final List<Ingredient> ingredients1 = ingredientService.findAll();
        Assert.assertEquals( ingredients1.size(), ingredientService.count() );
        Assertions.assertEquals( 3, ingredientService.findAll().size() );

    }

    /**
     * Tests that one ingredient can be successfully updated.
     *
     * @author rxiao3, tlsemel
     */
    @Test
    @Transactional
    public void testUpdateOne () {
        // creating two ingredients and saving them
        final Ingredient i1 = new Ingredient( coffee, 5 );
        final Ingredient i2 = new Ingredient( caramel, 3 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );

        // the ingredients are in the ingredientService
        final List<Ingredient> ingredients = ingredientService.findAll();
        Assert.assertEquals( ingredients.size(), ingredientService.count() );

        // Ingredients have correct information
        assertEquals( coffee, ingredients.get( 0 ).getName() );
        assertEquals( caramel, ingredients.get( 1 ).getName() );

        assertEquals( (Integer) 5, ingredients.get( 0 ).getAmount() );
        assertEquals( (Integer) 3, ingredients.get( 1 ).getAmount() );

        // editing the original amouunt and name for the first ingredient
        i1.setAmount( 9 );

        assertEquals( (Integer) 9, ingredients.get( 0 ).getAmount() );
        assertEquals( (Integer) 3, ingredients.get( 1 ).getAmount() );

    }

    /**
     * Tests that the ingredientService can use deleteAll.
     *
     * @author tlsemel
     */
    @Test
    @Transactional
    public void testDeleteAll () {
        Assertions.assertEquals( 0, ingredientService.findAll().size(),
                "There should be no Recipes in the CoffeeMaker" );
        // adding and saving four ingredients to the IngredientService
        final Ingredient i1 = new Ingredient( coffee, 5 );
        final Ingredient i2 = new Ingredient( caramel, 3 );
        final Ingredient i3 = new Ingredient( pumpkin_spice, 4 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );
        ingredientService.save( i3 );

        final List<Ingredient> ingredients = ingredientService.findAll();
        Assert.assertEquals( 3, ingredientService.count() );

        // deleting all ingredients
        ingredientService.deleteAll();
        Assertions.assertEquals( 0, ingredientService.count(),
                "`ingredientService.deleteAll()` should remove everything" );

    }

    /**
     * Tests that an already deleted ingredient cannot be deleted again.
     *
     * @author tlsemel
     */
    @Test
    @Transactional
    public void testDeleteMore () {
        Assertions.assertEquals( 0, ingredientService.findAll().size(),
                "There should be no Recipes in the CoffeeMaker" );
        // adding and saving four ingredients to the IngredientService
        final Ingredient i1 = new Ingredient( coffee, 5 );
        final Ingredient i2 = new Ingredient( caramel, 3 );
        final Ingredient i3 = new Ingredient( pumpkin_spice, 4 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );
        ingredientService.save( i3 );

        final List<Ingredient> ingredients = ingredientService.findAll();
        Assert.assertEquals( ingredients.size(), ingredientService.count() );

        // deleting all ingredients
        ingredientService.deleteAll();
        Assertions.assertEquals( 0, ingredientService.count(),
                "`ingredientService.deleteAll()` should remove everything" );

        try {
            ingredientService.delete( i1 );
            Assertions.assertEquals( 0, ingredientService.count(),
                    "Trying to delete an ingredient already removed from the list" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    /**
     * Tests that the toString can be returned for an ingredient.
     *
     * @author tlsemel
     */
    @Test
    @Transactional
    public void testToString () {
        final Ingredient i1 = new Ingredient( coffee, 5 );

        ingredientService.save( i1 );

        final List<Ingredient> ingredients = ingredientService.findAll();

        // the string between the saved ingredients and the first is the same
        assertEquals( i1.toString(), ingredients.get( 0 ).toString() );
    }

}
