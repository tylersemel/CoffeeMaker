package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests the Database interaction for the RecipeService class.
 *
 * @author Tyler Semel and Rose Xiao
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class TestDatabaseInteraction {

    /** Instance of RecipeService */
    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
    }

    /**
     * Tests the RecipeService class by adding a recipe, editing it, and
     * checking that the edits have been saved
     */
    @Test
    @Transactional
    public void testRecipes () {
        // adding a new recipe to the database
        final Recipe r = new Recipe();
        final Ingredient i1 = new Ingredient( "Coffee", 3 );
        final Ingredient i2 = new Ingredient( "Caramel", 1 );

        r.addIngredient( i1 );
        r.addIngredient( i2 );
        r.setName( "Caramel Coffee" );
        r.setPrice( 4 );

        // saving the new recipe
        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        // checking to make sure only one recipe is in the system
        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        // checking to make the sure the correct recipe was grabbed
        assertEquals( r.getName(), dbRecipe.getName() );
        assertEquals( r.getIngredients().get( 0 ).getName(), i1.getName() );
        assertEquals( r.getIngredients().get( 1 ).getName(), i2.getName() );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );

        assertEquals( 1, recipeService.count() );

        // grabbing the recipe based on the name
        final Recipe getRecipe = recipeService.findByName( "Caramel Coffee" );

        assertEquals( r.getIngredients().get( 0 ).getName(), getRecipe.getIngredients().get( 0 ).getName() );

        // editing the ingredient and price value
        getRecipe.setPrice( 15 );

        recipeService.save( dbRecipe );

        // checking to make sure the edits were made to the existing recipe and
        // not adding a new one
        assertEquals( 1, recipeService.count() );

        assertEquals( 1, recipeService.findAll().size() );

        assertEquals( 15, (int) recipeService.findAll().get( 0 ).getPrice() );
        assertEquals( 3, (int) recipeService.findAll().get( 0 ).getIngredients().get( 0 ).getAmount() );
        assertEquals( 1, (int) recipeService.findAll().get( 0 ).getIngredients().get( 1 ).getAmount() );
    }
}
