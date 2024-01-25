/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;

/**
 * Customer inherits from the abstract class User. Customer has the ability to
 * place an order, cancel an order, and pick up an order.
 *
 * @author tlsemel
 *
 */
@Entity
public class Customer extends User {
    /**
     * Open constructor.
     */
    public Customer () {

    }

    /**
     * Customer constructor that sets the username and password.
     *
     * @param name
     *            the username of a Customer.
     * @param password
     *            the password of a Customer.
     */
    public Customer ( final String name, final String password ) {
        setName( name );
        setPassword( password );
    }

    // Implement in I2

    // private List<Order> getOrders() { }

    // private boolean placeOrder() { }

    // private boolean cancelOrder() { }

    // private boolean pickUpOrder() { }

    @Override
    public String toString () {
        return "Customer username: " + getName();
    }

}
