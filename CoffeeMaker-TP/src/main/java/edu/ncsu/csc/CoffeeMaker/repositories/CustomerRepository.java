/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.ncsu.csc.CoffeeMaker.models.Customer;

/**
 * CustomerRepository will provide CRUD operations for the Customer model.
 * Spring will generate appropriate code with JPA.
 *
 * @author tlsemel
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds the Customer by the username.
     *
     * @param name
     *            the Customer's username.
     * @return the Customer.
     */
    Customer findByName ( String name );

}
