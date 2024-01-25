/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;

/**
 * Staff inherits from the abstract class User. Staff has the ability to make an
 * order, cancel an order, add and update Recipes, and add and update Inventory
 *
 * @author ntcampbe
 *
 */
@Entity
public class Staff extends User {

    // @Id
    // @GeneratedValue
    // private Long id;

    /**
     * Default staff constructor.
     */
    public Staff () {

    }

    /**
     * Staff constructor that sets the username and password.
     *
     * @param name
     *            the username of a Staff.
     * @param password
     *            the password of a Staff.
     */
    public Staff ( final String name, final String password ) {
        setName( name );
        setPassword( password );
    }

    @Override
    public String toString () {
        return "Staff username: " + getName();
    }

}
