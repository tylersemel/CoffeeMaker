/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Admin;
import edu.ncsu.csc.CoffeeMaker.repositories.AdminRepository;

/**
 * The AdminService will handle CRUD operations on the Admin model.
 *
 */
@Component
@Transactional
public class AdminService extends Service<Admin, Long> {
    /** The AdminRepository */
    @Autowired
    AdminRepository adminRepository;

    /**
     * Retrieves the AdminRepository.
     */
    @Override
    protected JpaRepository<Admin, Long> getRepository () {
        return adminRepository;
    }

    /**
     * Finds the Admin in the service by the username.
     *
     * @param name
     *            the Admin's username.
     * @return the Admin.
     */
    public Admin findByName ( final String name ) {
        return adminRepository.findByName( name );
    }
}
