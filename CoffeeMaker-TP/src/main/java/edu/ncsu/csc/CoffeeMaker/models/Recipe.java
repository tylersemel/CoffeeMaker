package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
@Table ( name = "recipe" )
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long                   id;

    /** Recipe name */
    private String                 name;

    /** Recipe price */
    @Min ( 0 )
    private Integer                price;

    /** Ingredients List */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true )
    // @JoinTable ( name = "recipe_ingredients", joinColumns = @JoinColumn (
    // name = "recipe_id" ),
    // inverseJoinColumns = @JoinColumn ( name = "ingredient_id" ) )
    private final List<Ingredient> ingredients = new ArrayList<Ingredient>();

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        // this.ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Adds an Ingredient to the ingredients list.
     *
     * @param ingredient
     *            the ingredient to add.
     */
    public void addIngredient ( final Ingredient ingredient ) {
        ingredients.add( ingredient );
    }

    /**
     * Returns a list of ingredients.
     *
     * @return a list of ingredients.
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Get an ingredient by name
     *
     * @param ingr
     *            to get the name of
     *
     * @return ingr that has the same name
     */
    public Ingredient getIngredientByName ( final Ingredient ingr ) {
        for ( final Ingredient i : this.getIngredients() ) {
            if ( i.getName().equals( ingr.getName() ) ) {
                return i;
            }
        }
        return null;
    }

    /**
     * Determines if recipe contains given Ingredient
     *
     * @param ingr
     *            to check if it contains
     *
     * @return true if the ingredient is contained within the recipe
     */
    public boolean containsIngredient ( final Ingredient ingr ) {
        for ( final Ingredient i : this.getIngredients() ) {
            if ( i.getName().equals( ingr.getName() ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the recipe
     *
     * @param r
     *            recipe to be updated
     * @param ls
     *            list of ingredients to add
     */
    public void updateRecipe ( final Recipe r, final List<Ingredient> ls ) {
        this.name = r.getName();
        this.price = r.getPrice();

        final List<Ingredient> newList = new ArrayList<Ingredient>();
        // ingredients.clear();
        // System.out.println( r.getIngredients().toString() );
        for ( final Ingredient i : r.getIngredients() ) {
            if ( containsIngredient( i ) ) {
                // System.out.println( "UPDATE" + i.getAmount() );

                this.getIngredientByName( i ).setAmount( i.getAmount() );
                newList.add( this.getIngredientByName( i ) );
                // System.out.println( r.getIngredientByName( i ) );
            }
            else {
                // System.out.println( "NEW" + i.toString() );
                final Ingredient newIngr = new Ingredient();
                newIngr.setAmount( i.getAmount() );
                newIngr.setName( i.getName() );

                // this.addIngredient( newIngr );
                newList.add( newIngr );
            }
        }
        ingredients.clear();
        for ( final Ingredient i : newList ) {
            ingredients.add( i );
        }

    }

    /**
     * Returns the name of the recipe.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuffer buffer = new StringBuffer();

        if ( !getName().equals( "" ) ) {
            buffer.append( "Recipe: " + getName() + ", " );
            buffer.append( "Ingredient(s): " );

            for ( final Ingredient i : ingredients ) {
                buffer.append( i.getName() );
                if ( !i.equals( ingredients.get( ingredients.size() - 1 ) ) ) {
                    buffer.append( ", " );
                }
            }
        }
        else {
            return "Recipe incomplete.";
        }

        return buffer.toString();
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
