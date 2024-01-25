package edu.ncsu.csc.CoffeeMaker.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.Length;

/**
 * This is an abstract class for a User which inherits from DomainObject. This
 * class serves as the base role for which a Customer and a Staff will inherit.
 *
 * @author tlsemel
 *
 */
@MappedSuperclass
abstract public class User extends DomainObject {

    /** User id */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    protected Long    id;

    /** Username of the User */
    @Length ( min = 6, max = 20 )
    protected String  name;

    /** Password of the User */
    protected String  password;

    // /** Time of the User's login */
    // protected String loginTime;

    /** If the user is logged in or not */
    protected boolean isLoggedIn;

    // This will be implemented later for UC10, UC11
    /// ** List of all orders for the User */
    // @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    // private final List<Order> orders = new ArrayList<Order>();

    /**
     * Open User constructor.
     */
    public User () {

    }

    /**
     * Base constructor for users.
     *
     * @param name
     *            the username of the User.
     * @param password
     *            the password of the User.
     */
    public User ( final String name, final String password ) {
        setName( name );
        setPassword( password );
    }

    /**
     * Gets the serializable ID for the User
     *
     * @return the User ID
     */
    @Override
    public Long getId () {
        return this.id;
    }

    /**
     * Returns the username for the User.
     *
     * @return the username of the User
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the username for the User. A username must be a String consisting of
     * 6-20 characters which includes either letters or numbers.
     *
     * @param name
     *            the username to set
     */
    public void setName ( final String name ) {
        if ( !checkName( name ) ) {
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    /**
     * Checks if a charater is a digit and returns true if so.
     *
     * @param digit
     *            the character to check.
     * @return true if the character is a digit, false if not.
     */
    private boolean checkDigit ( final char digit ) {
        if ( digit >= '0' && digit <= '9' ) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a character is a letter and returns true if so.
     *
     * @param alpha
     *            the character to check.
     * @return true if the character is a letter, false if not.
     */
    private boolean checkAlpha ( final char alpha ) {
        if ( ( alpha >= 'a' && alpha <= 'z' ) || ( alpha >= 'A' && alpha <= 'Z' ) ) {
            return true;
        }

        return false;
    }

    /**
     * Checks that the name for a customer is correct. A username must be a
     * String consisting of 6-20 characters which includes either letters or
     * numbers.
     *
     * @param name
     *            the username to check
     * @return true if the username is valid, false if not
     */
    public boolean checkName ( final String name ) {
        // checks if the username is null and the correct length
        if ( name != null && ( name.length() >= 6 && name.length() <= 20 ) ) {
            // for every character, the name is checked for only letters and
            // numbers
            for ( int i = 0; i < name.length(); i++ ) {
                final char a = name.charAt( i );
                if ( !checkDigit( a ) && !checkAlpha( a ) ) {
                    return false;
                }
            }
        }
        else {
            return false;
        }

        return true;
    }

    /**
     * Checks if the password is valid. A password must be a String consisting
     * of 6-20 characters with at least one character being a number, at least
     * one character being a special character('!', '?'), and at least one
     * character being a letter.
     *
     * @param password
     *            the password to check
     * @return true if the password is valid, false if not.
     */
    public boolean checkPass ( final String password ) {
        boolean hasDigit = false;
        boolean hasLetter = false;
        boolean hasSpecial = false;
        final boolean isInvalid = false;

        // checking that the pass is not null and the correct length
        if ( password != null && ( password.length() >= 6 && password.length() <= 20 ) ) {
            for ( int i = 0; i < password.length(); i++ ) {
                final char c = password.charAt( i );

                // checking that the pass has one number
                if ( checkDigit( c ) ) {
                    hasDigit = true;
                }
                // checking that the pass has one letter
                else if ( checkAlpha( c ) ) {
                    hasLetter = true;
                }
                // checking that the pass has one special character
                else if ( c == '!' || c == '?' ) {
                    hasSpecial = true;
                }
                // returning false if any character is not a special, letter, or
                // number
                else {
                    return false;
                }
            }
        }
        else {
            return false;
        }

        // if everything is correct for the pass, true can be returned
        if ( isInvalid || !hasSpecial || !hasDigit || !hasLetter ) {
            return false;
        }

        return true;
    }

    /**
     * Returns if the User is logged in.
     *
     * @return true if the user is logged in, false if not.
     */
    public boolean getIsLoggedIn () {
        return isLoggedIn;
    }

    /**
     * Sets the user to be logged in.
     *
     * @param isLoggedIn
     *            true if the user is logged in, false if not.
     */
    public void setIsLoggedIn ( final boolean isLoggedIn ) {
        this.isLoggedIn = isLoggedIn;
    }

    // /**
    // * Returns the login time for the User.
    // *
    // * @return the loginTime of the User
    // */
    // public String getLoginTime () {
    // return loginTime;
    // }

    // /**
    // * Sets the login time for the User if the user has been logged in.
    // *
    // * @param loginTime
    // * the login time to set
    // */
    // public void setLoginTime ( final String loginTime ) {
    // if ( getIsLoggedIn() ) {
    // this.loginTime = loginTime;
    // }
    // else {
    // throw new IllegalArgumentException();
    // }
    // }

    /**
     * Returns the User's password.
     *
     * @return the password
     */
    public String getPassword () {
        return password;
    }

    /**
     * Sets the User's password. A password must be a String consisting of 6-20
     * characters with at least one character being a number, at least one
     * character being a special character('!', '?'), and at least one character
     * being a letter.
     *
     * @param password
     *            the password to set
     */
    public void setPassword ( final String password ) {
        if ( !checkPass( password ) ) {
            throw new IllegalArgumentException();
        }

        this.password = password;
    }

    /**
     * Sets
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Logs the User into CoffeeMaker if the user is not already logged in.
     *
     * @param username
     *            the User's username
     * @param password
     *            the User's password
     * @return true if the user was able to be logged in, false if otherwise.
     */
    public boolean login ( final String username, final String password ) {
        if ( !getIsLoggedIn() && username == getName() && password == getPassword() ) {
            setIsLoggedIn( true );

            // getting the date for the login time
            final SimpleDateFormat formatter = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );
            final Date date = new Date();
            // setLoginTime( formatter.format( date ) );
            return true;
        }

        return false;
    }

    /**
     * Logs out the User from CoffeeMaker if the user is logged in.
     *
     * @param username
     *            the User's username
     * @return true if the user was able to be logged out, false if otherwise.
     */
    public boolean logout ( final String username ) {
        if ( getIsLoggedIn() && username == getName() ) {
            setIsLoggedIn( false );
            return true;
        }
        return false;
    }

    // will be implemented, a Customer will have a different way to cancel an
    // order list than a Staff member
    // abstract protected boolean cancelOrder(Order order) {
    //
    // }

    // will be implemented, a Customer will have a different order list than a
    // Staff member
    // abstract protected List<Order> getOrders() {
    // return orders;
    // }
}
