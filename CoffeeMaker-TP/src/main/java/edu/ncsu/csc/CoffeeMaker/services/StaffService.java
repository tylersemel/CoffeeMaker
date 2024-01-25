package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Staff;
import edu.ncsu.csc.CoffeeMaker.repositories.StaffRepository;

/**
 * The RecipeService is used to handle CRUD operations on the Staff model. In
 * addition to all functionality from `Service`, we also have functionality for
 * retrieving a single Staff by name.
 *
 * @author ntcampbe
 *
 */
@Component
@Transactional
public class StaffService extends Service<Staff, Long> {

    /**
     * StaffRepository, to be autowired in by Spring and provide CRUD operations
     * on Staff model.
     */
    @Autowired
    StaffRepository staffRepository;

    @Override
    protected JpaRepository<Staff, Long> getRepository () {
        return staffRepository;
    }

    /**
     * Find a staff with the provided name
     *
     * @param name
     *            Name of the staff to find
     * @return found staff, null if none
     */
    public Staff findByName ( final String name ) {
        System.out.println( "NAME OF ACCOUNT: " + name );
        return staffRepository.findByName( name );
    }

}
