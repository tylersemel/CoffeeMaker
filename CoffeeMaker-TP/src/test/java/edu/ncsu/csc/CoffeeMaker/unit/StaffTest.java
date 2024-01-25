package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Staff;

/**
 * The Class StaffTest.
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class StaffTest {

    /** The valid username string. */
    private static final String VALID_NAME = "ntcampbe";

    /** The valid password string. */
    private static final String VALID_PASS = "Cookie2!";

    /** The staff. */
    private Staff               staff;

    /**
     * Create staff.
     */
    @BeforeEach
    void createStaff () {

        staff = new Staff( VALID_NAME, VALID_PASS );

    }

    /**
     * Test to string.
     */
    @Test
    @Transactional
    void testToString () {
        assertEquals( "Staff username: ntcampbe", staff.toString() );
    }

    /**
     * Tests invalid username
     */
    @Test
    @Transactional
    void testInvalidName () {

        try {
            staff = new Staff( "!nvalidString", VALID_PASS );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            // should fail
        }

    }

    /**
     * Test invalid password
     */
    @Test
    @Transactional
    void testInvalidPassword () {

        try {
            staff = new Staff( VALID_NAME, "InvalidPassword" );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            // should fail
        }

    }
}
