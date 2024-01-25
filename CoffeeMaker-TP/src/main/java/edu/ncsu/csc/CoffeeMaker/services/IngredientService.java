package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.repositories.IngredientRepository;

/**
 * The IngredientService will handle CRUD operations on the Ingredient model.
 *
 */
@Component
@Transactional
public class IngredientService extends Service<Ingredient, Long> {

    /** IngredientRepository to talk to DB */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * gets the repository
     *
     * @return Ingredient Repository
     */
    @Override
    protected JpaRepository<Ingredient, Long> getRepository () {
        return ingredientRepository;
    }

    /**
     * finds ingredient by its name
     *
     * @param name
     *            of ingredient to look for
     * @return Ingredient with given name
     */
    public Ingredient findByName ( final String name ) {
        return ingredientRepository.findByName( name );
    }

}
