/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * The Order class in which a User is able to place an order with their already
 * chosen recipes.
 *
 * @author ntcampbe, tlsemel
 *
 */
@Entity
@Table ( name = "\"Order\"" )
public class Order extends DomainObject {

    /** id for order entry. */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long               id;

    /** The recipes. */
    @OneToMany ( cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true )
    @JoinTable ( name = "order_recipes", joinColumns = @JoinColumn ( name = "order_id" ),
            inverseJoinColumns = @JoinColumn ( name = "recipe_id" ) )
    private final List<Recipe> recipes;

    /** The status. */
    private OrderStatus        status;

    /** The status. */
    private String             name;

    /** The staff. */
    @OneToOne ( orphanRemoval = true, cascade = CascadeType.ALL )
    private Staff              staff;

    /** The customer. */
    @OneToOne ( orphanRemoval = true, cascade = CascadeType.ALL )
    private Customer           customer;

    /** The time the order was placed */
    private String             time;

    /** The total cost of the order */
    @Min ( 0 )
    private Integer            totalCost;

    /**
     * Instantiates a new order. Name is set to blank, recipes list is
     * instantiated, and the order status is set to 'not started.'
     */
    public Order () {
        this.name = "";
        this.recipes = new ArrayList<Recipe>();
        this.status = OrderStatus.NOT_STARTED;
        this.totalCost = 0;

    }

    /**
     * Instantiates a new order.
     *
     * @param recipes
     *            the recipe that starts the order
     * @param time
     *            the time the order is placed
     */
    public Order ( final List<Recipe> recipes, final String time ) {
        this.name = "";
        setTime( time );
        setStatus( OrderStatus.NOT_STARTED );
        this.recipes = recipes;
        setTotalCost();
    }

    /**
     * Returns the order's name (id).
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the order's name (id).
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the total price.
     *
     * @return the total price
     */
    private int setTotalCost () {
        if ( recipes == null ) {
            throw new IllegalArgumentException( "Invalid order" );
        }
        Integer total = 0;
        for ( final Recipe recipe : recipes ) {
            total += recipe.getPrice();
        }
        totalCost = total;
        return total;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public OrderStatus getStatus () {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status
     *            the new status
     */
    public void setStatus ( final OrderStatus status ) {
        if ( status == null ) {
            throw new IllegalArgumentException( "Invalid order status" );
        }
        this.status = status;
    }

    /**
     * Gets the staff.
     *
     * @return the staff
     */
    public Staff getStaff () {
        return staff;
    }

    /**
     * Sets the staff.
     *
     * @param staff
     *            the new staff
     */
    public void setStaff ( final Staff staff ) {
        this.staff = staff;
    }

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public Customer getCustomer () {
        return customer;
    }

    /**
     * Sets the customer.
     *
     * @param customer
     *            the new customer
     */
    public void setCustomer ( final Customer customer ) {
        this.customer = customer;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public String getTime () {
        return time;
    }

    /**
     * Sets the time.
     *
     * @param time
     *            the new time
     */
    public void setTime ( final String time ) {
        if ( time == null ) {
            throw new IllegalArgumentException( "Invalid time" );
        }
        this.time = time;
    }

    /**
     * Gets the recipes.
     *
     * @return the recipes
     */
    public List<Recipe> getRecipes () {
        return recipes;
    }

    /**
     * The total cost
     *
     * @return the total cost of the order
     */
    public Integer getTotalCost () {
        return totalCost;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString () {
        String tempString = "Order id is " + getId();
        for ( final Recipe recipe : recipes ) {
            tempString += "\n\t " + recipe.getName();
        }
        return tempString;
    }

    /**
     * Gets the id.
     *
     * @return the id
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
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     *
     * The Enum OrderStatus.
     */
    public enum OrderStatus {

        /** The not started. */
        NOT_STARTED,
        /** The in progress. */
        IN_PROGRESS,
        /** The completed. */
        COMPLETED,
        /** The picked up. */
        PICKED_UP,
        /** The canceled. */
        CANCELED

    }
}
