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

import edu.ncsu.csc.CoffeeMaker.models.Staff;
import edu.ncsu.csc.CoffeeMaker.services.AdminService;
import edu.ncsu.csc.CoffeeMaker.services.CustomerService;
import edu.ncsu.csc.CoffeeMaker.services.StaffService;

/**
 * The Class APIStaffController. Endpoints for Frontend to talk to
 *
 * @author ntcampbe
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIStaffController extends APIController {

    /** The staff service. */
    @Autowired
    private StaffService    staffService;

    /** The admin service. */
    @Autowired
    private AdminService    adminService;

    /** The customer service. */
    @Autowired
    private CustomerService customerService;

    /**
     * Gets the staff.
     *
     * @return the staff
     */
    @GetMapping ( BASE_PATH + "/staffs" )
    public List<Staff> getStaff () {
        return staffService.findAll();
    }

    /**
     * Login staff.
     *
     * @param staff
     *            the staff
     * @return the response entity
     */
    @PostMapping ( BASE_PATH + "/staffs/login" )
    public ResponseEntity loginStaff ( @RequestBody final Staff staff ) {
        Staff s1 = null;

        try {
            s1 = staffService.findByName( staff.getName() );
        }
        catch ( final IllegalArgumentException e ) {
            return new ResponseEntity( errorResponse( "Invalid credentials." ), HttpStatus.FORBIDDEN );
        }

        if ( null == s1 ) {
            return new ResponseEntity( errorResponse( "Staff with the name " + staff.getName() + " does not exist." ),
                    HttpStatus.NOT_FOUND );
        }
        if ( !staff.getPassword().equals( s1.getPassword() ) ) {
            return new ResponseEntity( errorResponse( "The wrong password was entered." ), HttpStatus.FORBIDDEN );
        }
        else if ( s1.getIsLoggedIn() ) {
            return new ResponseEntity(
                    errorResponse( "Staff with the name " + staff.getName() + " is already logged in." ),
                    HttpStatus.CONFLICT );
        }
        else {
            s1.setIsLoggedIn( true );
            staffService.save( s1 );
            return new ResponseEntity( s1, HttpStatus.OK );
        }
    }

    /**
     * Logout staff.
     *
     * @param staff
     *            the staff
     * @return the response entity
     */
    @PostMapping ( BASE_PATH + "/staffs/logout" )
    public ResponseEntity logoutStaff ( @RequestBody final Staff staff ) {
        final Staff s1 = staffService.findByName( staff.getName() );

        if ( null == s1 ) {
            return new ResponseEntity( errorResponse( "Staff with the name " + staff.getName() + "does not exist." ),
                    HttpStatus.CONFLICT );
        }
        else if ( !s1.getIsLoggedIn() ) {
            return new ResponseEntity(
                    errorResponse( "Staff with the name " + staff.getName() + " is already logged out." ),
                    HttpStatus.CONFLICT );
        }
        else {
            s1.setIsLoggedIn( false );
            staffService.save( s1 );
            return new ResponseEntity( s1, HttpStatus.OK );
        }
    }

    /**
     * Gets the staff member.
     *
     * @param name
     *            the staff
     * @return the staff member
     */
    @GetMapping ( BASE_PATH + "/staffs/{name}" )
    public ResponseEntity getStaffMember ( @PathVariable final String name ) {
        final Staff s1 = staffService.findByName( name );
        // if ingredient was null
        return null == s1
                ? new ResponseEntity( errorResponse( "No Staff found with the username " + name ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( s1, HttpStatus.OK );
    }

    /**
     * Creates the staff. Only the Admin has the ability to do this.
     *
     * @param staff
     *            the staff
     * @return the response entity
     */
    @PostMapping ( BASE_PATH + "/staffs" )
    public ResponseEntity signUpStaff ( @RequestBody final Staff staff ) {
        if ( staff == null ) {
            return new ResponseEntity( errorResponse( "[805] Staff was not able to be signed up." ),
                    HttpStatus.CONFLICT );
        } // makes sure that there is only one, logged in Admin accessing this
          // endpoint
        else if ( adminService.count() == 1 && adminService.findAll().get( 0 ).getIsLoggedIn() ) {
            // checks that no other user has the same username
            if ( null != staffService.findByName( staff.getName() )
                    || null != adminService.findByName( staff.getName() )
                    || null != customerService.findByName( staff.getName() ) ) {
                return new ResponseEntity(
                        errorResponse( "[1201] Staff with the name " + staff.getName() + " already exists." ),
                        HttpStatus.CONFLICT );
            }
            else if ( !staff.checkName( staff.getName() ) ) {
                return new ResponseEntity( errorResponse( "[1202] Invalid username for a Customer." ),
                        HttpStatus.CONFLICT );
            }
            else if ( !staff.checkPass( staff.getPassword() ) ) {
                return new ResponseEntity( errorResponse( "[1202] Invalid password for a Customer." ),
                        HttpStatus.CONFLICT );
            }

            staffService.save( staff );
            return new ResponseEntity( successResponse( "[1200] " + staff.getName() + " was successfully created" ),
                    HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "[805] Only the Admin is allowed to sign up Staff members." ),
                    HttpStatus.NOT_ACCEPTABLE );
        }
    }
}
