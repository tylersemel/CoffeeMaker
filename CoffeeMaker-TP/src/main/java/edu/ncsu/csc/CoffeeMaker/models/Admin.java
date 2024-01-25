/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;

/**
 * Admin inherits from the abstract class User. Admin has the ability to make an
 * order, cancel an order, add and update Recipes, and add and update Inventory
 *
 * @author ntcampbe
 *
 */
@Entity
public class Admin extends User {

    /** The one username the admin can have */
    private static final String ADMIN_NAME = "singleAdmin";

    /** The one password the admin can have */
    private static final String ADMIN_PASS = "adminPass3!";

    /**
     * Admin constructor that sets the username and password.
     *
     */
    public Admin () {
        setName( ADMIN_NAME );
        setPassword( ADMIN_PASS );
    }

    @Override
    public String toString () {
        return "Admin username: " + getName();
    }

}
