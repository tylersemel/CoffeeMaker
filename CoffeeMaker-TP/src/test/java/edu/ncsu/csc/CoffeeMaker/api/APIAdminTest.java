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
import edu.ncsu.csc.CoffeeMaker.models.Admin;
import edu.ncsu.csc.CoffeeMaker.services.AdminService;

/**
 * Tests the APIAdminController API endpoints.
 *
 * @author tlsemel
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIAdminTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /** The context */
    @Autowired
    private WebApplicationContext context;

    /** AdminService */
    @Autowired
    private AdminService          service;

    /** The one username the admin can have */
    private static final String   ADMIN_NAME = "singleAdmin";

    /** The one password the admin can have */
    private static final String   ADMIN_PASS = "adminPass3!";

    /**
     * Set up before each test is executed.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Testing that there can only be one Admin created.
     *
     * @throws Exception
     *             if a second Admin is created.
     */
    @Test
    @Transactional
    public void testSignUpAdminInvalid () throws Exception {
        service.deleteAll();

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );

        // second generation of the admin that should not go through
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );
    }

    public void testSignUpNoGeneration () throws Exception {

        Assertions.assertEquals( 0, (int) service.count() );

        mvc.perform( post( "/api/v1/admin/login" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );

    }

    /**
     * Testing the API endpoints by generating the admin and getting the admin
     *
     * @throws Exception
     *             if the admin cannot be retrieved.
     */
    @Test
    @Transactional
    public void testGetAdmin () throws Exception {
        service.deleteAll();

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );

        // get the admin
        mvc.perform( get( String.format( "/api/v1/admin" ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( service.findAll().get( 0 ).getName(), service.findByName( ADMIN_NAME ).getName() );
        Assertions.assertEquals( service.findAll().size(), service.count() );

    }

    /**
     * Testing the API endpoints by generating the admin and getting the admin
     *
     * @throws Exception
     *             if the admin cannot be retrieved.
     */
    @Test
    @Transactional
    public void testGetAdminInvalid () throws Exception {
        service.deleteAll();

        // get the admin
        mvc.perform( get( String.format( "/api/v1/admin" ) ) ).andExpect( status().isNotFound() );

        Assertions.assertEquals( 0, (int) service.count() );

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );

        // get the admin
        mvc.perform( get( String.format( "/api/v1/admin" ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( service.findAll().get( 0 ).getName(), service.findByName( ADMIN_NAME ).getName() );
        Assertions.assertEquals( service.findAll().size(), service.count() );

    }

    /**
     * Testing the API endpoints by logging in the Admin
     *
     * @throws Exception
     *             if the admin cannot be logged in.
     */
    @Test
    @Transactional
    public void testLoginAdmin () throws Exception {
        service.deleteAll();

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );

        final Admin a = service.findAll().get( 0 );

        // should log the admin in and then set the isLoggedIn to true
        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );

        Assertions.assertTrue( service.findByName( ADMIN_NAME ).getIsLoggedIn() );
        Assertions.assertTrue( a.getIsLoggedIn() );

        // try to log the admin in again

        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertTrue( service.findByName( a.getName() ).getIsLoggedIn() );
    }

    /**
     * Testing the API endpoints by logging in the Admin
     *
     * @throws Exception
     *             if the admin cannot be logged in.
     */
    @Test
    @Transactional
    public void testLoginAdminInvalid () throws Exception {
        service.deleteAll();

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );

        // trying to log the admin in with the wrong name

        Admin admin = new Admin();
        admin.setName( "InvalidName" );

        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( admin ) ) ).andExpect( status().isNotFound() );

        admin = new Admin();
        // should not log the admin in because of the invalid password
        admin.setPassword( "invalidPass1!" );

        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( admin ) ) ).andExpect( status().isForbidden() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertFalse( service.findByName( ADMIN_NAME ).getIsLoggedIn() );

        service.findAll().get( 0 ).setPassword( ADMIN_PASS );

        // try to log the admin in again, correctly

        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( service.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
    }

    /**
     * Testing the API endpoints by logging out the Admin
     *
     * @throws Exception
     *             if the admin cannot be logged out.
     */
    @Test
    @Transactional
    public void testLogoutAdmin () throws Exception {
        service.deleteAll();

        Assertions.assertEquals( 0, (int) service.count() );

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );

        final edu.ncsu.csc.CoffeeMaker.models.Admin a = service.findAll().get( 0 );

        // should log the admin in and then set the isLoggedIn to true
        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertTrue( service.findByName( ADMIN_NAME ).getIsLoggedIn() );
        Assertions.assertTrue( a.getIsLoggedIn() );

        // log the admin out

        mvc.perform( post( "/api/v1/admin/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertFalse( service.findByName( a.getName() ).getIsLoggedIn() );
    }

    /**
     * Testing the API endpoints by logging out the Admin
     *
     * @throws Exception
     *             if the admin cannot be logged out.
     */
    @Test
    @Transactional
    public void testLogoutAdminInvalid1 () throws Exception {
        service.deleteAll();

        Assertions.assertEquals( 0, (int) service.count() );

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );
        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertEquals( service.findAll().get( 0 ), service.findByName( ADMIN_NAME ) );

        final edu.ncsu.csc.CoffeeMaker.models.Admin a = service.findAll().get( 0 );

        // should log the admin in and then set the isLoggedIn to true
        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertTrue( service.findByName( ADMIN_NAME ).getIsLoggedIn() );
        Assertions.assertTrue( a.getIsLoggedIn() );

        // log the admin out
        mvc.perform( post( "/api/v1/admin/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertFalse( service.findByName( a.getName() ).getIsLoggedIn() );

        // try to log out again
        mvc.perform( post( "/api/v1/admin/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertFalse( service.findByName( a.getName() ).getIsLoggedIn() );
    }

    /**
     * Testing the API endpoints by logging out the Admin
     *
     * @throws Exception
     *             if the admin cannot be logged out.
     */
    @Test
    @Transactional
    public void testLogoutAdminInvalid2 () throws Exception {
        service.deleteAll();

        // try to log out admin that hasnt been created
        mvc.perform( post( "/api/v1/admin/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new Admin() ) ) ).andExpect( status().isConflict() );

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        final edu.ncsu.csc.CoffeeMaker.models.Admin a = service.findAll().get( 0 );

        // should log the admin in and then set the isLoggedIn to true
        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertTrue( service.findByName( ADMIN_NAME ).getIsLoggedIn() );
        Assertions.assertTrue( a.getIsLoggedIn() );

        // log the admin out
        mvc.perform( post( "/api/v1/admin/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertFalse( service.findByName( a.getName() ).getIsLoggedIn() );

    }

    /**
     * Testing the API endpoints by logging out the Admin
     *
     * @throws Exception
     *             if the admin cannot be logged out.
     */
    @Test
    @Transactional
    public void testLogoutAdminInvalid3 () throws Exception {
        service.deleteAll();

        // the first generation of the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );

        final edu.ncsu.csc.CoffeeMaker.models.Admin a = service.findAll().get( 0 );

        // try log the admin out that hasnt been logged in
        mvc.perform( post( "/api/v1/admin/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertFalse( service.findByName( a.getName() ).getIsLoggedIn() );

        // should log the admin in and then set the isLoggedIn to true
        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertTrue( service.findByName( ADMIN_NAME ).getIsLoggedIn() );

        // log the admin out for real
        mvc.perform( post( "/api/v1/admin/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );
        Assertions.assertFalse( service.findByName( a.getName() ).getIsLoggedIn() );

    }
}
