package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * Repository for setting up the User superclass.
 *
 * @author tlsemel
 *
 */
@NoRepositoryBean
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Base findByName for users.
     *
     * @param name
     *            the username of the User.
     * @return the User.
     */
    User findByName ( String name );
}
