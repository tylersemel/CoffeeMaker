/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.services.CustomerService;

/**
 * Tests the APICustomerController API endpoints.
 *
 * @author tlsemel
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APICustomerTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /** The context */
    @Autowired
    private WebApplicationContext context;

    /** CustomerService */
    @Autowired
    private CustomerService       service;

    /**
     * Set up before each test is executed.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Testing the API endpoints by creating and adding one Customer
     *
     * @throws Exception
     *             if the customer cannot be created.
     */
    @Test
    @Transactional
    public void testSignUpCustomerAPI () throws Exception {
        service.deleteAll();

        // sign the customer up
        final Customer c1 = new Customer( "custUser1", "password1?" );

        Assertions.assertEquals( "custUser1", c1.getName() );

        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( c1.getName(), service.findByName( "custUser1" ).getName() );
        Assertions.assertEquals( 1, (int) service.count() );
    }

    /**
     * Testing the API endpoints by creating and adding one Customer
     *
     * @throws Exception
     *             if the customer cannot be retrieved.
     */
    @Test
    @Transactional
    public void testGetCustomerAPI () throws Exception {
        service.deleteAll();

        // sign a customer up
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );

        // get that customer
        mvc.perform( get( String.format( "/api/v1/customers/%s", "custUser1" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( c1 ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( c1.getName(), service.findByName( "custUser1" ).getName() );
    }

    /**
     * Testing the API endpoints by logging in a Customer
     *
     * @throws Exception
     *             if the customer cannot be logged in.
     */
    @Test
    @Transactional
    public void testLoginCustomerAPI () throws Exception {
        service.deleteAll();

        // create and sign up a user first
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertEquals( c1.getName(), service.findByName( "custUser1" ).getName() );

        // should log the user in and then set the isLoggedIn to true
        mvc.perform( post( "/api/v1/customers/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );

        Assertions.assertTrue( service.findByName( c1.getName() ).getIsLoggedIn() );

        // try to log the customer in again

        mvc.perform( post( "/api/v1/customers/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 1, (int) service.count() );

        Assertions.assertTrue( service.findByName( c1.getName() ).getIsLoggedIn() );
    }

    /**
     * Testing the API endpoints by logging in a Customer
     *
     * @throws Exception
     *             if the customer cannot be logged in.
     */
    @Test
    @Transactional
    public void testInvalidLoginCustomerAPI () throws Exception {
        service.deleteAll();

        final Customer c1 = new Customer( "custUser1", "password1?" );

        // should log the user in and then set the isLoggedIn to true
        mvc.perform( post( "/api/v1/customers/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 0, (int) service.count() );

        // should not log the user in
        mvc.perform( post( "/api/v1/customers/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 0, (int) service.count() );
        Assertions.assertFalse( c1.getIsLoggedIn() );
    }

    /**
     * Testing the API endpoints by logging out a Customer
     *
     * @throws Exception
     *             if the customer cannot be logged out.
     */
    @Test
    @Transactional
    public void testLogoutCustomerAPI () throws Exception {
        service.deleteAll();

        // sign up a user first
        final Customer c1 = new Customer( "custUser1", "password1?" );
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertEquals( c1.getName(), service.findByName( "custUser1" ).getName() );

        // try to log a user out that hasnt logged in
        mvc.perform( post( "/api/v1/customers/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isNotAcceptable() );

        Assertions.assertEquals( 1, (int) service.count() );

        Assertions.assertFalse( c1.getIsLoggedIn() );
        Assertions.assertFalse( service.findByName( c1.getName() ).getIsLoggedIn() );

        // log a user in and then log them out
        mvc.perform( post( "/api/v1/customers/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );

        // check that the customer is logged in
        Assertions.assertTrue( service.findByName( c1.getName() ).getIsLoggedIn() );
        Assertions.assertEquals( "custUser1", service.findByName( c1.getName() ).getName() );

        Assertions.assertEquals( c1.getName(), service.findByName( c1.getName() ).getName() );

        mvc.perform( post( "/api/v1/customers/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        // check that the count of the service is still the same
        Assertions.assertEquals( 1, (int) service.count() );

        Assertions.assertFalse( c1.getIsLoggedIn() );
        Assertions.assertFalse( service.findByName( c1.getName() ).getIsLoggedIn() );
    }

    /**
     * Testing the API endpoints by signing up an invalid Customer
     *
     * @throws Exception
     *             if the customer cannot be signed up.
     */
    @Test
    @Transactional
    public void testInvalidSignUp () throws Exception {
        service.deleteAll();

        final Customer c1 = new Customer( "custUser1", "password1?" );

        Assertions.assertEquals( "custUser1", c1.getName() );

        // sign up a new user
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( c1.getName(), service.findByName( "custUser1" ).getName() );
        Assertions.assertEquals( 1, (int) service.count() );

        // try signing up the same user
        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isConflict() );
    }
}
