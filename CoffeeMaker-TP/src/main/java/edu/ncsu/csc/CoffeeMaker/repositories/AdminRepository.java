/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Admin;

/**
 * AdminRepository will provide CRUD operations for the Admin model. Spring will
 * generate appropriate code with JPA.
 *
 * @author tlsemel
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {
    /**
     * Finds the Admin by the username.
     *
     * @param name
     *            the Admin's username.
     * @return the Admin.
     */
    Admin findByName ( String name );
}
