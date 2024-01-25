package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Staff;
import edu.ncsu.csc.CoffeeMaker.services.AdminService;
import edu.ncsu.csc.CoffeeMaker.services.StaffService;

/**
 * The Class APIStaffTest.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIStaffTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API.
     */
    private MockMvc               mvc;

    /** The context. */
    @Autowired
    private WebApplicationContext context;

    /** The staff service. */
    @Autowired
    private StaffService          staffService;

    /** The staff service. */
    @Autowired
    private AdminService          adminService;

    /** The one username the admin can have */
    private static final String   ADMIN_NAME = "singleAdmin";

    // /** The one password the admin can have */
    // private static final String ADMIN_PASS = "adminPass3!";

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        staffService.deleteAll();
    }

    /**
     * Create the admin before signing up Staff
     *
     * @throws Exception
     *             if Admin cannot be generated
     */
    public void generateAdmin () throws Exception {
        // generate the admin
        mvc.perform( post( "/api/v1/generateAdmin" ) ).andExpect( status().isOk() );
        Assertions.assertEquals( adminService.findAll().get( 0 ), adminService.findByName( ADMIN_NAME ) );
        assertEquals( 1, (int) adminService.count() );

        // log the admin in
        mvc.perform( post( "/api/v1/admin/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( adminService.findAll().get( 0 ) ) ) ).andExpect( status().isOk() );
        assertTrue( adminService.findAll().get( 0 ).getIsLoggedIn() );
    }

    /**
     * tests succesful sign up of Staff.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testSignUpStaff () throws Exception {
        staffService.deleteAll();

        generateAdmin();

        final Staff staff = new Staff( "ntcampbe", "cookie1!" );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/staffs" ) ).andExpect( status().isOk() );
    }

    /**
     * tests invalid sign up of Staff.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testSignUpStaffInvalid () throws Exception {
        staffService.deleteAll();

        // try to sign up staff without admin
        final Staff s1 = new Staff( "ntcampbe", "cookie1!" );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( s1 ) ) ).andExpect( status().isNotAcceptable() );

        // now try to sign up staff with duplicate credentials
        generateAdmin();

        final Staff s2 = new Staff( "ntcampbe", "cookie2!" );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( s1 ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( s2 ) ) ).andExpect( status().isConflict() );

        // try creating a staff user with the same username as a customer
        final Customer c1 = new Customer( "testUser", "cookie3!" );
        final Staff s3 = new Staff( "testUser", "cookie4!" );

        mvc.perform( post( "/api/v1/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( s3 ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 1, staffService.findAll().size() );
    }

    /**
     * Tests that cannot create invalid staff.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testCreateStaffInvalid () throws Exception {
        staffService.deleteAll();

        generateAdmin();

        final Staff staff = new Staff( "ntcampbe", "cookie1!" );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/staffs" ) ).andExpect( status().isOk() );

        final Staff missingStaff = new Staff( "ntcampbe", "cookie2!" );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( missingStaff ) ) ).andExpect( status().is( 409 ) );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().is( 409 ) );
    }

    /**
     * test normal login.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testlogin () throws Exception {

        final Staff staff = new Staff( "ntcampbe", "cookie1!" );

        generateAdmin();

        // creating staff
        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );
        // logging in
        mvc.perform( post( "/api/v1/staffs/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

    }

    /**
     * tests invalid logins.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testloginInvalid () throws Exception {

        generateAdmin();

        final Staff staff = new Staff( "ntcampbe", "cookie1!" );

        // creating staff
        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );
        // logging in
        mvc.perform( post( "/api/v1/staffs/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

        // diffrent username diffrent pswd
        final Staff missingStaff = new Staff( "ghgupt", "cookie2!" );

        // attempt to login as staff not in system
        mvc.perform( post( "/api/v1/staffs/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( missingStaff ) ) ).andExpect( status().is( 404 ) );

        // attempt to login as logged in staff
        mvc.perform( post( "/api/v1/staffs/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().is( 409 ) );

        // same username diffrent password
        final Staff sameNameStaff = new Staff( "ntcampbe", "Notaname32!" );

        // same username but fails when passwords are diffrent
        mvc.perform( post( "/api/v1/staffs/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sameNameStaff ) ) ).andExpect( status().is( 403 ) );
    }

    /**
     * Testlogout.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testlogout () throws Exception {
        generateAdmin();

        final Staff staff = new Staff( "ntcampbe", "cookie1!" );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/staffs/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

        // normal login
        mvc.perform( post( "/api/v1/staffs/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );
    }

    /**
     * Test logout invalid.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testlogoutInvalid () throws Exception {
        generateAdmin();

        // currently Admin is still logged in to create staff, so a staff member
        // is able to log in as the same time as another user is logged in. this
        // can be changed later on to allow only one user to access the
        // CoffeeMaker at a time

        final Staff staff = new Staff( "ntcampbe", "cookie1!" );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/staffs/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/staffs/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );
        // already logged out
        mvc.perform( post( "/api/v1/staffs/logout" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().is( 409 ) );
    }

    /**
     * Test get staff member.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @Transactional
    public void testgetStaffMember () throws Exception {
        generateAdmin();

        final Staff staff = new Staff( "ntcampbe", "cookie1!" );

        mvc.perform( post( "/api/v1/staffs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/staffs/ntcampbe" ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/staffs/notAuser" ) ).andExpect( status().is( 404 ) );
    }
}
