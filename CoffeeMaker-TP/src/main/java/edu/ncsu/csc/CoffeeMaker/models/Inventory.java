package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
@Table ( name = "inventory" )
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long                   id;

    /**
     * A hashmap containing Ingredients to allow quicker and better access and
     * updates
     */

    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients = new ArrayList<Ingredient>();

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
    }

    /**
     * Use this to create inventory with specified amts.
     *
     * @param name
     *            the name of the ingredient
     * @param amt
     *            amount to add to the inventory
     */
    public Inventory ( final String name, final Integer amt ) {
        addIngredient( name, amt );
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
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
     * Returns the current amount of ingredient in the inventory. If -1 is
     * returned then there is no ingredient by that name in the list.
     *
     *
     * @param ingredient
     *            the ingredient to return
     *
     * @return amount of chocolate
     */
    public Integer getIngredientAmount ( final Ingredient ingredient ) {
        // traverse to find the Ingredient in the list
        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( ingredient.getName() ) ) {
                return i.getAmount();
            }
        }
        return -1;
    }

    /**
     * returns the list of ingredients
     *
     * @return returns a list of ingredients
     */
    public List<Ingredient> getList () {
        return ingredients;
    }

    /**
     * Adds amount to the Ingredient.
     *
     *
     * @param ingredient
     *            ingredient to set amount to
     * @param amount
     *            amount of ingredient to set
     * @return if ingredient is updated
     */
    public boolean updateIngredient ( final Ingredient ingredient, final Integer amount )
            throws IllegalArgumentException {
        if ( amount < 0 ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }
        if ( amount > 100 ) {
            throw new IllegalArgumentException( "Units of ingredient must be less than 100" );
        }
        try {
            for ( final Ingredient i : ingredients ) {
                if ( ingredient.getName().equals( i.getName() ) ) {
                    // if adding will cause unit overflow
                    if ( i.getAmount() + amount > 100 ) {
                        // do not set the amount
                        return false;
                    }
                    // set the new value by adding the passed amount to the old
                    // value
                    final Integer newVal = i.getAmount() + amount;
                    i.setAmount( newVal );
                    return true;
                }
            }
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }
        return false;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        boolean isEnough = true;
        // for every ingredient associated with a recipe
        for ( final Ingredient ingredient : r.getIngredients() ) {
            // find it in the inventory and check if there is enough
            for ( final Ingredient i : ingredients ) {
                if ( i.getName() == ingredient.getName() ) {
                    if ( i.getAmount() < ingredient.getAmount() ) {
                        System.out.println( "INVEN " + i.getAmount() + i.getName() + " AND RECIPE "
                                + ingredient.getAmount() + ingredient.getName() );
                        isEnough = false;
                    }
                }

            }
        }
        return isEnough;
    }

    /**
     * Assuming that the user has checked that there are enough ingredients to
     * make, the ingredients in the inventory will be utilized
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        // for every ingredient in the recipe.
        if ( enoughIngredients( r ) ) {
            for ( final Ingredient ingredient : r.getIngredients() ) {
                for ( final Ingredient i : ingredients ) {
                    if ( i.getName().equals( ingredient.getName() ) ) {
                        final Integer newAmount = i.getAmount() - ingredient.getAmount();
                        i.setAmount( newAmount );
                    }
                }
            }

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds ingredients to the inventory
     *
     *
     * @param name
     *            name of the new ingredient to be added
     * @param amt
     *            the amount of the new ingredient to be added
     * @return -1 if the amount is greater than 100 or less than or equal to 0,
     *         -2 if it is a duplicate, and 0 if it is added successfully.
     */
    public Integer addIngredient ( final String name, final Integer amt ) {

        if ( amt <= 0 || amt > 100 ) {
            return -1;
        }
        if ( checkDuplicateName( name ) ) {
            return -2;
        }
        final Ingredient i = new Ingredient( name, amt );
        ingredients.add( i );

        return 0;
    }

    /**
     * Checks if there is already an ingredient in the inventory that has the
     * same name. This method is case insensitive, so it checks if the name is
     * there at all.
     *
     * @author tlsemel
     *
     * @param ingredient
     *            the ingredient name to check in the list
     * @return true if there is a duplicate ingredient in the list, false if
     *         there is no ingredient with that name in the list
     */
    public boolean checkDuplicateName ( final String ingredient ) {
        for ( final Ingredient i : ingredients ) {
            if ( i.getName().toLowerCase().equals( ingredient.toLowerCase() ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuffer buf = new StringBuffer();

        for ( final Ingredient i : ingredients ) {
            buf.append( i.getName() + ": " );
            buf.append( i.getAmount() );
            buf.append( "\n" );
        }

        return buf.toString();
    }

}
