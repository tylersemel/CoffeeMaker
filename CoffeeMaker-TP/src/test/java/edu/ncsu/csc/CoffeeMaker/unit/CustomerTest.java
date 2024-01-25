package edu.ncsu.csc.CoffeeMaker.unit;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.services.CustomerService;

/**
 * Tests the Customer class as well as the User class and its service model.
 *
 * @author tlsemel
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class CustomerTest {

    /** The CustomerService */
    @Autowired
    private CustomerService customerService;

    /**
     * Sets up the Ingredient Service.
     */
    @BeforeEach
    public void setup () {
        customerService.deleteAll();
    }

    /**
     * Tests that a Customer can be created and counted in a list in the
     * customerService.
     */
    @Test
    @Transactional
    public void testCreateCustomer () {

        final Customer c1 = new Customer( "custUser1", "password1?" );

        customerService.save( c1 );

        final Customer c2 = new Customer( "custUser2", "password2?" );

        customerService.save( c2 );

        Assert.assertEquals( 2, customerService.count() );

    }

    /**
     * Tests that a Customer can be edited and counted in a list in the
     * customerService.
     */
    @Test
    @Transactional
    public void testGetCustomer () {

        final Customer c1 = new Customer( "custUser1", "password1?" );

        customerService.save( c1 );

        final Customer c2 = new Customer( "custUser2", "password2?" );

        customerService.save( c2 );

        // check that both customers were saved and can be found in the service
        Assert.assertEquals( c1, customerService.findByName( "custUser1" ) );

        Assert.assertEquals( "custUser1", customerService.findByName( "custUser1" ).getName() );

        Assert.assertEquals( 2, customerService.count() );

    }

    /**
     * Tests that a Customer can be edited and counted in a list in the
     * customerService.
     */
    @Test
    @Transactional
    public void testGetCustomerInvalid () {

        final Customer c1 = new Customer( "custUser1", "password1?" );

        customerService.save( c1 );

        Assert.assertEquals( c1, customerService.findByName( "custUser1" ) );

        Assert.assertEquals( "custUser1", customerService.findByName( "custUser1" ).getName() );

        Assert.assertEquals( 1, customerService.count() );

        // check that a customer cannot be called with an invalid name
        Assert.assertNull( customerService.findByName( "invalidCust" ) );

        Assert.assertEquals( 1, customerService.count() );

    }

    /**
     * Tests that a Customer can be edited and counted in a list in the
     * customerService.
     */
    @Test
    @Transactional
    public void testCustomerToString () {

        final Customer c1 = new Customer( "custUser1", "password1?" );

        customerService.save( c1 );

        Assert.assertEquals( 1, customerService.count() );

        Assert.assertEquals( "Customer username: custUser1", c1.toString() );

    }
}
