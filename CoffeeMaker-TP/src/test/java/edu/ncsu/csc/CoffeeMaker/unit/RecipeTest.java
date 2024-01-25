package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import javax.validation.ConstraintViolationException;

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
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    /** RecipeService */
    @Autowired
    private RecipeService     service;

    /** RecipeService */
    @Autowired
    private IngredientService ingredientService;

    /**
     * deleting service before each tests
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testAddRecipe () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }
    //
    // /**
    // * Testing no recipes
    // */
    // @Test
    // @Transactional
    // public void testNoRecipes () {
    // Assertions.assertEquals( 0, service.findAll().size(), "There should be no
    // Recipes in the CoffeeMaker" );
    //
    // final Ingredient i1 = new Ingredient( "Coffee", -2 );
    // final Ingredient i2 = new Ingredient( "Mocha", 3 );
    //
    // ingredientService.save( i1 );
    // ingredientService.save( i2 );
    //
    // final Recipe r1 = new Recipe();
    // r1.setName( "Tasty Drink" );
    // r1.setPrice( 12 );
    // r1.addIngredient( i1 );
    //
    // final Recipe r2 = new Recipe();
    // r2.setName( "Mocha" );
    // r2.setPrice( 1 );
    // r1.addIngredient( i2 );
    //
    // final List<Recipe> recipes = List.of( r1, r2 );
    //
    // try {
    // service.saveAll( recipes );
    // Assertions.assertEquals( 0, service.count(),
    // "Trying to save a collection of elements where one is invalid should
    // result in neither getting saved" );
    // }
    // catch ( final Exception e ) {
    // Assertions.assertTrue( e instanceof ConstraintViolationException );
    // }
    //
    // }

    /**
     * Test adding one recipe
     */
    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient i1 = new Ingredient( "Coffee", 2 );
        ingredientService.save( i1 );
        final Recipe r1 = createRecipe( name, 5, i1 );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /**
     * Test2 is done via the API for different validation
     */
    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient i1 = new Ingredient( "Coffee", 2 );
        final Recipe r1 = createRecipe( name, -50, i1 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative price" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    /**
     * Test adding recipe with invalid amount
     */
    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient i1 = new Ingredient( "Coffee", -2 );

        try {
            ingredientService.save( i1 );
            final Recipe r1 = createRecipe( name, 50, i1 );

            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of an ingredient" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    /**
     * Test adding recipe with invalid amount
     */
    @Test
    @Transactional
    public void testAddRecipe7 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        ingredientService.save( i1 );
        final Ingredient i2 = new Ingredient( "Mocha", -2 );

        try {
            ingredientService.save( i2 );
            final Recipe r1 = createRecipe( name, 50, i1 );
            r1.addIngredient( i2 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of an ingredient" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    /**
     * Test adding 2 recipes
     */
    @Test
    @Transactional
    public void testAddRecipe13 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Mocha", 2 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );

        final Recipe r1 = createRecipe( "Coffee", 50, i1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, i2 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    /**
     * Test adding multiple recipes
     */
    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Mocha", 2 );
        final Ingredient i3 = new Ingredient( "Caramel", 3 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );
        ingredientService.save( i3 );

        final Recipe r1 = createRecipe( "Coffee", 50, i1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, i2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, i3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    /**
     * @author ssgowda3
     */
    @Test
    @Transactional
    public void testDeleteRecipe2 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Mocha", 2 );
        final Ingredient i3 = new Ingredient( "Caramel", 3 );
        final Ingredient i4 = new Ingredient( "Matcha", 4 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );
        ingredientService.save( i3 );
        ingredientService.save( i4 );

        final Recipe rec1 = createRecipe( "drink1", 5, i1 );
        service.save( rec1 );

        final Recipe rec2 = createRecipe( "drink2", 1, i2 );
        service.save( rec2 );

        Assertions.assertEquals( 2, service.count(), "There should be two recipe in the database" );

        final Recipe rec3 = createRecipe( "drink3", 6, i3 );
        service.save( rec3 );

        final Recipe rec4 = createRecipe( "drink4", 5, i4 );
        service.save( rec4 );

        Assertions.assertEquals( 4, service.count(), "There should be four recipe in the database" );

        service.delete( rec1 );
        service.delete( rec4 );
        Assertions.assertEquals( 2, service.count(), "There should be two recipe in the database" );

        service.deleteAll();
        Assertions.assertEquals( 0, service.count(), "There should be no recipe in the database" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // saving ingredients to recipes
        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Mocha", 2 );
        final Ingredient i3 = new Ingredient( "Caramel", 3 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );
        ingredientService.save( i3 );

        final Recipe r1 = createRecipe( "Coffee", 50, i1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 2, i2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 3, i3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        final List<Recipe> dbRecipes = service.findAll();

        assertEquals( 3, dbRecipes.size() );
        assertEquals( 3, service.findAll().size() );

        final Recipe getRecipe = service.findByName( "Coffee" );

        assertEquals( r1.getIngredients().get( 0 ).getName(), getRecipe.getIngredients().get( 0 ).getName() );

        // checking recipe 1
        assertEquals( 50, (int) getRecipe.getPrice() );
        assertEquals( 1, (int) service.findAll().get( 0 ).getIngredients().get( 0 ).getAmount() );

        // checking recipe 2 is in the same place
        assertEquals( 2, (int) service.findAll().get( 1 ).getPrice() );
        assertEquals( 2, (int) service.findAll().get( 1 ).getIngredients().get( 0 ).getAmount() );

        // deleting recipe 1
        service.delete( dbRecipes.get( 0 ) );
        assertEquals( 2, service.findAll().size() );

        // now recipe 2 is where recipe 1 is
        assertEquals( 2, (int) service.findAll().get( 0 ).getPrice() );
        Assertions.assertEquals( 2, service.count(), "There should be two Recipes in the CoffeeMaker" );

        // deleting recipe 2
        service.delete( r2 );
        assertEquals( 1, service.findAll().size() );
        Assertions.assertEquals( 1, service.count(), "There should be one Recipe in the CoffeeMaker" );

        // now only recipe 3 is left
        assertEquals( 3, (int) service.findAll().get( 0 ).getPrice() );
        assertEquals( 3, (int) service.findAll().get( 0 ).getIngredients().get( 0 ).getAmount() );

        // no recipes in database
        service.delete( r3 );
        Assertions.assertEquals( 0, service.count(), "There should be zero Recipes in the CoffeeMaker" );

    }

    /**
     * Test deleting a recipe not saved to database and deleting an edited
     * recipe
     */
    @Test
    @Transactional
    public void testDeleteRecipe4 () {

        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Mocha", 2 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );

        final Recipe r1 = createRecipe( "Coffee", 50, i1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 2, i2 );

        Assertions.assertEquals( 1, service.count(), "There should be one Recipes in the CoffeeMaker" );

        // attempting to delete a recipe not saved in database
        service.delete( r2 );

        // Checking to make sure the recipe is not affected
        Assertions.assertEquals( 1, service.count(), "There should be one Recipes in the CoffeeMaker" );
        assertEquals( 50, (int) service.findAll().get( 0 ).getPrice() );
        assertEquals( 1, (int) service.findAll().get( 0 ).getIngredients().get( 0 ).getAmount() );

        // editing the existing recipe
        r1.getIngredients().get( 0 ).setAmount( 5 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one Recipes in the CoffeeMaker" );
        service.delete( r1 );
        Assertions.assertEquals( 0, service.count(), "There should be zero Recipes in the CoffeeMaker" );

    }

    @Test
    @Transactional
    public void testEditRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        ingredientService.save( i1 );

        final Recipe r1 = createRecipe( "Coffee", 50, i1 );
        service.save( r1 );

        r1.setPrice( 70 );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        assertEquals( 1, (int) service.findAll().get( 0 ).getIngredients().get( 0 ).getAmount() );

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    /**
     * This method tests that all Recipe objects can be recognized as either
     * different or the same Recipe objects.
     *
     * @author tlsemel
     */
    @Test
    @Transactional
    public void testEqualsRecipe () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // saving ingredients to recipes
        final Ingredient i1 = new Ingredient( "Coffee", 0 );
        final Ingredient i2 = new Ingredient( "Mocha", 0 );
        final Ingredient i3 = new Ingredient( "Caramel", 0 );

        // check that two recipes with no name are the same
        final Recipe r1 = new Recipe();
        final Recipe r2 = new Recipe();

        // check that different recipes are registered as different objects
        final Recipe r3 = createRecipe( "Latte", 50, i1 );
        final Recipe r4 = createRecipe( "Coffee", 50, i2 );
        final Recipe r5 = createRecipe( "Mocha", 50, i3 );

        r5.setName( null );

        Assertions.assertEquals( "", r1.getName() );
        Assertions.assertEquals( "", r2.getName() );
        Assertions.assertEquals( "Latte", r3.getName() );
        Assertions.assertEquals( "Coffee", r4.getName() );
        Assertions.assertEquals( null, r5.getName() );

        Assertions.assertTrue( r1.equals( r2 ) );
        Assertions.assertTrue( r2.equals( r1 ) );

        Assertions.assertFalse( r1.equals( r3 ) );
        Assertions.assertFalse( r3.equals( r4 ) );
        Assertions.assertFalse( r4.equals( r3 ) );

        Assertions.assertFalse( r5.equals( r4 ) );
        Assertions.assertFalse( r4.equals( r5 ) );

    }

    /**
     * Checks that the hash code is corrected calculated for different Recipe
     * objects.
     *
     * @author tlsemel
     */
    @Test
    @Transactional
    public void testHashRecipe () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // saving ingredients to recipes
        final Ingredient i1 = new Ingredient( "Coffee", 0 );
        final Ingredient i2 = new Ingredient( "Mocha", 0 );

        // check that two recipes with no name have the same hash
        final Recipe r1 = new Recipe();
        final Recipe r2 = new Recipe();

        // check that different recipes have different hashes
        final Recipe r3 = createRecipe( "Latte", 50, i1 );
        final Recipe r4 = createRecipe( "Coffee", 50, i2 );

        Assertions.assertEquals( "", r1.getName() );
        Assertions.assertEquals( "", r2.getName() );
        Assertions.assertEquals( "Latte", r3.getName() );
        Assertions.assertEquals( "Coffee", r4.getName() );

        Assertions.assertEquals( r1.hashCode(), r2.hashCode() );
        Assertions.assertEquals( r2.hashCode(), r1.hashCode() );

        Assertions.assertNotEquals( r1.hashCode(), r3.hashCode() );
        Assertions.assertNotEquals( r1.hashCode(), r4.hashCode() );

    }

    /**
     * Tests the methods that are used to change the recipe
     */
    @Test
    @Transactional
    public void testRecipe () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // saving ingredients to recipes
        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Mocha", 2 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );

        // creating the recipes
        final Recipe rec1 = createRecipe( "drink1", 10, i1 );
        service.save( rec1 );

        final Recipe rec1Find = service.findByName( "drink1" );
        assertEquals( 1, service.count(), "There should be one Recipes in the CoffeeMaker" );

        final Recipe rec2 = createRecipe( "drink2", 30, i2 );
        service.save( rec2 );

        assertEquals( 2, service.count(), "There should be two Recipes in the CoffeeMaker" );
        assertNotEquals( 1, rec1.getId() );

        Assertions.assertEquals( 10, (int) rec1Find.getPrice() );
        assertEquals( 1, (int) service.findAll().get( 0 ).getIngredients().get( 0 ).getAmount() );
        assertEquals( 2, (int) service.findAll().get( 1 ).getIngredients().get( 0 ).getAmount() );

    }

    private Recipe createRecipe ( final String name, final Integer price, final Ingredient i ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( i );

        return recipe;
    }

    /**
     * @author tlsemel
     */
    @Test
    @Transactional
    public void testDeleteIngredientsFromRecipe () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Mocha", 2 );
        final Ingredient i3 = new Ingredient( "Caramel", 3 );

        ingredientService.save( i1 );
        ingredientService.save( i2 );
        ingredientService.save( i3 );
        final Recipe r1 = createRecipe( "Coffee", 50, i1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, i2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, i3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    /**
     * Testing toString
     */
    @Test
    @Transactional
    public void testToString () {

        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Mocha", 2 );
        final Ingredient i3 = new Ingredient( "Caramel", 3 );
        final Recipe r1 = createRecipe( "Coffee", 50, i1 );
        r1.addIngredient( i1 );
        r1.addIngredient( i2 );
        r1.addIngredient( i3 );
        Assertions.assertEquals( "Recipe: Coffee, Ingredient(s): Coffee, Coffee, Mocha, Caramel", r1.toString() );
    }

}
