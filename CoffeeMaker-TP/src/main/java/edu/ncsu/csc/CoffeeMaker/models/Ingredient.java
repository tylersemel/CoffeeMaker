package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * Ingredients available for the coffee maker. Ingredient is tied to the
 * database using Hibernate libraries.
 *
 * @author xrose
 *
 */
@Entity
@Table ( name = "ingredient" )
public class Ingredient extends DomainObject {

    /** Ingredient id */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long    id;

    /** Name of the Ingredient */
    private String  name;

    /** Amount */
    @Min ( 0 )
    private Integer amount;

    // @ManyToOne ( cascade = CascadeType.ALL )
    // @JoinColumn ( name = "recipe_id" )
    // private Recipe recipe;
    //
    // /**
    // * @return the recipe
    // */
    // public Recipe getRecipe () {
    // return recipe;
    // }
    //
    // /**
    // * @param recipe
    // * the recipe to set
    // */
    // public void setRecipe ( final Recipe recipe ) {
    // this.recipe = recipe;
    // }

    @Override
    public Long getId () {
        return this.id;
    }

    /**
     * Constructor with fields
     *
     *
     * @param ingredient
     *            the name of the ingredient
     * @param amount
     *            the amount of the ingredient
     */
    public Ingredient ( final String ingredient, final Integer amount ) {
        // super();
        setName( ingredient );
        setAmount( amount );
    }

    /**
     * constructor with no args
     */
    public Ingredient () {
        // super();
    }

    /**
     * returns the Ingredient name
     *
     * @return name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of the ingredient
     *
     * @param ingredient
     *            the name to be set
     */
    public void setName ( final String ingredient ) {
        this.name = ingredient;
    }

    /**
     * returns the Ingredient amount
     *
     * @return amount
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Sets the amount of the ingredient
     *
     * @param amount
     *            the ingredient to have the amount set the amount to be set
     */
    public void setAmount ( final Integer amount ) {
        this.amount = amount;
    }

    /**
     * The set the id
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the Ingredient details as a String
     */
    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", ingredient=" + name + ", amount=" + amount + ", getId()=" + getId()
                + ", getType()=" + getName() + ", getAmount()=" + getAmount() + "]";
    }

}
