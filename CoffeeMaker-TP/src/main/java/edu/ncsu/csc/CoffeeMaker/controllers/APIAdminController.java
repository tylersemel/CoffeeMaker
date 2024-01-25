/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Admin;
import edu.ncsu.csc.CoffeeMaker.services.AdminService;

/**
 * APIAdminController controls the API endpoints for the Admin to interact with
 * the database. The Admin role allows for creation of new admin members.
 *
 * @author tlsemel
 * @author ntcampbe
 *
 *
 *         Suppress Warnings
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIAdminController extends APIController {
    /**
     * adminService object, to be autowired in by Spring to allow for
     * manipulating the Admin model
     */
    @Autowired
    private AdminService        adminService;

    /** The one username the admin can have */
    private static final String ADMIN_NAME = "singleAdmin";

    /** The one password the admin can have */
    private static final String ADMIN_PASS = "adminPass3!";

    /**
     * Login admin.
     *
     * @param admin
     *            the admin
     * @return the response entity
     */
    @PostMapping ( BASE_PATH + "/admin/login" )
    public ResponseEntity loginadmin ( @RequestBody final Admin admin ) {
        Admin a1 = null;

        try {
            a1 = adminService.findByName( admin.getName() );
        }
        catch ( final IllegalArgumentException e ) {
            return new ResponseEntity( errorResponse( "Invalid credentials." ), HttpStatus.FORBIDDEN );
        }

        // if this admin is not in system, then generate and login
        if ( null == a1 ) {
            if ( adminService.findAll().size() == 0 ) {
                a1 = new Admin();
                a1.setIsLoggedIn( true );
                adminService.save( a1 );
                return new ResponseEntity( a1, HttpStatus.OK );
            }
            else {
                return new ResponseEntity(
                        errorResponse( "admin with the name " + admin.getName() + " does not exist." ),
                        HttpStatus.NOT_FOUND );
            }
        }
        if ( !admin.getPassword().equals( a1.getPassword() ) ) {
            return new ResponseEntity( errorResponse( "The wrong password was entered." ), HttpStatus.FORBIDDEN );
        }
        else if ( a1.getIsLoggedIn() ) {
            return new ResponseEntity(
                    errorResponse( "admin with the name " + admin.getName() + " is already logged in." ),
                    HttpStatus.CONFLICT );
        }
        else {
            a1.setIsLoggedIn( true );
            adminService.save( a1 );
            return new ResponseEntity( a1, HttpStatus.OK );
        }
    }

    /**
     * Logout admin.
     *
     * @param admin
     *            the admin
     * @return the response entity
     */
    @PostMapping ( BASE_PATH + "/admin/logout" )
    public ResponseEntity logoutAdmin ( @RequestBody final Admin admin ) {
        final Admin a = adminService.findByName( admin.getName() );

        if ( null == a ) {
            return new ResponseEntity( errorResponse( "Admin " + admin.getName() + "does not exist." ),
                    HttpStatus.CONFLICT );
        }
        else if ( !a.getName().equals( ADMIN_NAME ) ) {
            return new ResponseEntity( errorResponse( "Admin " + admin.getName() + "does not exist." ),
                    HttpStatus.CONFLICT );
        }
        else if ( !a.getIsLoggedIn() ) {
            return new ResponseEntity( errorResponse( "Admin " + admin.getName() + " is already logged out." ),
                    HttpStatus.CONFLICT );
        }
        else {
            a.setIsLoggedIn( false );
            adminService.save( a );
            return new ResponseEntity( a, HttpStatus.OK );
        }
    }

    /**
     * Gets the admin.
     *
     * @return the admin member
     */
    @GetMapping ( BASE_PATH + "/admin" )
    public ResponseEntity getAdmin () {
        if ( adminService.count() == 1 && adminService.findAll().get( 0 ).getName().equals( ADMIN_NAME ) ) {
            return new ResponseEntity( adminService.findAll().get( 0 ), HttpStatus.OK );
        }

        return new ResponseEntity( errorResponse( "No Admin in the system" ), HttpStatus.NOT_FOUND );
    }

    /**
     * Automatically generates the one Admin in the CoffeeMaker system. The
     * admin is the only user allowed to create Staff members.
     *
     * @return the admin
     */
    @PostMapping ( BASE_PATH + "/generateAdmin" )
    public ResponseEntity generateAdmin () {
        if ( adminService.count() != 0 ) {
            return new ResponseEntity( errorResponse( "There can only be one Admin in the system." ),
                    HttpStatus.CONFLICT );
        }

        final Admin admin = new Admin();

        adminService.save( admin );

        return new ResponseEntity( successResponse( "[1401] " + admin.getName() + " was successfully created" ),
                HttpStatus.OK );
    }
}
