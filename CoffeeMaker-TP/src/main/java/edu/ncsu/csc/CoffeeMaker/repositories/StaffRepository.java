package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.ncsu.csc.CoffeeMaker.models.Staff;

/**
 * StaffRepository will provide CRUD operations for the Staff model. Spring will
 * generate appropriate code with JPA.
 *
 * @author ntcampbe
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    /**
     * Finds a Staff object with the provided name. Spring will generate code to
     * make this happen.
     *
     * @param name
     *            Name of the staff
     * @return Found staff, null if none.
     */
    Staff findByName ( String name );
}
