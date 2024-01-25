/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.services.AdminService;
import edu.ncsu.csc.CoffeeMaker.services.CustomerService;
import edu.ncsu.csc.CoffeeMaker.services.StaffService;

/**
 * APICustomerController controls the API endpoints for the Customer to interact
 * with the database.
 *
 * @author tlsemel
 *
 *
 *         Suppress Warnings
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APICustomerController extends APIController {

    /**
     * customerService object, to be autowired in by Spring to allow for
     * manipulating the Customer model
     */
    @Autowired
    private CustomerService customerService;

    /** The staff service. */
    @Autowired
    private StaffService    staffService;

    /** The admin service. */
    @Autowired
    private AdminService    adminService;

    /**
     * Chec
     *
     * @return the list of customers.
     */
    @GetMapping ( BASE_PATH + "/customers" )
    public List<Customer> getCustomers () {
        return customerService.findAll();
    }

    /**
     * REST API method to provide GET access to a specific Customer, as
     * indicated by the path variable provided (the username of the Customer
     * desired)
     *
     *
     * @param name
     *            Customer's username
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/customers/{name}" )
    public ResponseEntity getCustomer ( @PathVariable final String name ) {
        final Customer c1 = customerService.findByName( name );
        // if ingredient was null
        return null == c1
                ? new ResponseEntity( errorResponse( "[805] No customer found with the username " + name ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( c1, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to allow a Customer to be signed
     * up.
     *
     * @param customer
     *            The Customer to be signed up.
     * @return response to the request indicating success if the Customer could
     *         be signed up, or an error if not.
     */
    @PostMapping ( BASE_PATH + "/customers" )
    public ResponseEntity signUpCustomer ( @RequestBody final Customer customer ) {
        if ( customer == null ) {
            return new ResponseEntity( errorResponse( "[805] Customer does not exist." ), HttpStatus.FORBIDDEN );
        }
        else if ( null != customerService.findByName( customer.getName() )
                || null != adminService.findByName( customer.getName() )
                || null != staffService.findByName( customer.getName() ) )

        {
            return new ResponseEntity(
                    errorResponse( "[1201] Customer with the name " + customer.getName() + " already exists." ),
                    HttpStatus.CONFLICT );
        }
        else if ( !customer.checkName( customer.getName() ) ) {
            return new ResponseEntity( errorResponse( "[1202] Invalid username for a Customer." ),
                    HttpStatus.CONFLICT );
        }
        else if ( !customer.checkPass( customer.getPassword() ) ) {
            return new ResponseEntity( errorResponse( "[1202] Invalid password for a Customer." ),
                    HttpStatus.CONFLICT );
        }
        else {
            customerService.save( customer );
            return new ResponseEntity( successResponse( "[1200] " + customer.getName() + " was successfully created" ),
                    HttpStatus.OK );
        }
    }

    /**
     * REST API method to provide PUT access to a specific Customer, allowing
     * the Customer to log in.
     *
     * @param customer
     *            The Customer to be logged in.
     * @return response to the request indicating success if the Customer could
     *         be logged in, or an error if not.
     */
    @PostMapping ( BASE_PATH + "/customers/login" )
    public ResponseEntity loginCustomer ( @RequestBody final Customer customer ) {
        System.out.println( "HERE at LOGIN" );
        final Customer c1 = customerService.findByName( customer.getName() );

        if ( null == c1 ) {
            return new ResponseEntity(
                    errorResponse( "[805] Customer with the name " + customer.getName() + " does not exist." ),
                    HttpStatus.CONFLICT );
        }
        else if ( c1.getIsLoggedIn() ) {
            return new ResponseEntity(
                    errorResponse( "[802] Customer with the name " + customer.getName() + "is already logged in." ),
                    HttpStatus.CONFLICT );
        }
        else if ( c1.login( c1.getName(), c1.getPassword() ) ) {
            customerService.save( c1 );

            return new ResponseEntity( successResponse( "[801] " + c1.getName() + "was successfully logged in." ),
                    HttpStatus.OK );
        }
        else {
            return new ResponseEntity(
                    errorResponse( "[802] Customer could not be logged in. Invalid password or username." ),
                    HttpStatus.CONFLICT );
        }

    }

    /**
     * REST API method to provide PUT access to a specific Customer, allowing
     * the Customer to log out.
     *
     * @param customer
     *            The Customer to be logged in.
     * @return response to the request indicating success if the Customer could
     *         be logged out, or an error if not.
     */
    @PostMapping ( BASE_PATH + "/customers/logout" )
    public ResponseEntity logoutCustomer ( @RequestBody final Customer customer ) {
        final Customer c1 = customerService.findByName( customer.getName() );

        if ( null == c1 ) {
            return new ResponseEntity(
                    errorResponse( "[805] Customer with the name " + customer.getName() + "does not exist." ),
                    HttpStatus.CONFLICT );
        }
        else if ( !c1.getIsLoggedIn() ) {
            return new ResponseEntity(
                    errorResponse( "[804] Customer with the name " + customer.getName() + " is already logged out." ),
                    HttpStatus.NOT_ACCEPTABLE );
        }
        else if ( c1.logout( c1.getName() ) ) {
            customerService.save( c1 );
            return new ResponseEntity( successResponse( "[803] " + c1.getName() + "was successfully logged out." ),
                    HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "[804] Customer could not be logged out. Invalid username." ),
                    HttpStatus.CONFLICT );
        }

    }

    // might need a way to delete customer accounts later on

}
