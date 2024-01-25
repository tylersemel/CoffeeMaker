/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.repositories.CustomerRepository;

/**
 * The CustomerService will handle CRUD operations on the Customer model.
 *
 */
@Component
@Transactional
public class CustomerService extends Service<Customer, Long> {
    /** The CustomerRepository */
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Retrieves the CustomerRepository.
     */
    @Override
    protected JpaRepository<Customer, Long> getRepository () {
        return customerRepository;
    }

    /**
     * Finds the Customer in the service by the username.
     *
     * @param name
     *            the Customer's username.
     * @return the Customer.
     */
    public Customer findByName ( final String name ) {
        return customerRepository.findByName( name );
    }
}
