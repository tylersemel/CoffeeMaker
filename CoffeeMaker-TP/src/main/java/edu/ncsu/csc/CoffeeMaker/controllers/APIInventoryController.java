package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Inventory.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIInventoryController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService service;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory. This will convert the Inventory to JSON.
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/inventory" )
    public List<Ingredient> getInventory () {
        return service.getInventory().getList();
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's singleton
     * Inventory. This will update the Inventory of the CoffeeMaker by adding
     * amounts from the Inventory provided to the CoffeeMaker's stored inventory
     *
     * @param i
     *            to add to inventory
     *
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity updateInventory ( @RequestBody final Ingredient i ) {
        final Inventory inventoryCurrent = service.getInventory();
        if ( !inventoryCurrent.updateIngredient( i, i.getAmount() ) ) {
            return new ResponseEntity( inventoryCurrent, HttpStatus.CONFLICT );
        }
        service.save( inventoryCurrent );
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Ingredient model. This is
     * used to create a new Ingredient by automatically converting the JSON
     * RequestBody provided to a Ingredient object. Invalid JSON will fail.
     *
     * @author xrose
     *
     * @param i
     *            The valid Ingredient to be saved.
     * @return ResponseEntity indicating success if the Ingredient could be
     *         saved to the inventory, or an error if it could not be
     */

    @PostMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity addIngredient ( @RequestBody final Ingredient i ) {
        final Inventory inventoryCurrent = service.getInventory();
        final Integer dupe = inventoryCurrent.addIngredient( i.getName(), i.getAmount() );

        if ( dupe == 0 ) {
            service.save( inventoryCurrent );
            return new ResponseEntity( successResponse( i.getName() + " successfully created" ), HttpStatus.OK );
        }
        else if ( dupe == -2 ) {
            return new ResponseEntity( errorResponse( "Unable to add ingredient with duplicate name" ),
                    HttpStatus.CONFLICT );
        }
        else if ( dupe == -1 ) {
            return new ResponseEntity( errorResponse( "Unable to add ingredient with invalid units" ),
                    HttpStatus.NOT_ACCEPTABLE );
        }
        else {
            return new ResponseEntity(
                    errorResponse( "Insufficient space in recipe book for ingredient " + i.getName() ),
                    HttpStatus.INSUFFICIENT_STORAGE );
        }

    }

}
