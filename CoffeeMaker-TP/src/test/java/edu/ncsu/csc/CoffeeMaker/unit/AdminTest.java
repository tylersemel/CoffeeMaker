package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Admin;

/**
 * The Class AdminTest.
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class AdminTest {

    /** The Admin. */
    private Admin admin;

    /**
     * Create Admin.
     */
    @BeforeEach
    void createAdmin () {

        admin = new Admin();

    }

    /**
     * Test to string.
     */
    @Test
    @Transactional
    void testToString () {
        assertEquals( "Admin username: singleAdmin", admin.toString() );
    }

    // /**
    // * Tests that the admin cannot be created twice
    // */
    // @Test
    // @Transactional
    // void testDupAdmin () {
    // Admin a1 = null;
    // try {
    // a1 = new Admin();
    // fail( "Two admins have been created" );
    // }
    // catch ( final Exception e ) {
    // // do nothing should throw
    // }
    // assertNull( a1 );
    // }

}
