package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;

/**
 * IngredientRepository will provide CRUD operations for the Ingredient model.
 * Spring will generate appropriate code with JPA.
 *
 */
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * finds Ingredient by its name
     *
     * @param name
     *            to check
     *
     * @return Ingredient with the given name
     */
    Ingredient findByName ( String name );

}
