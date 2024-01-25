package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Order.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.Staff;
import edu.ncsu.csc.CoffeeMaker.services.CustomerService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.StaffService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Inventory.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 * @author ntcampbe
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /** The order service. */
    @Autowired
    private OrderService     orderService;

    /** The customer service. */
    @Autowired
    private CustomerService  customerService;

    /** The staff service. */
    @Autowired
    private StaffService     staffService;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model.
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model.
     */
    @Autowired
    private RecipeService    recipeService;

    /**
     * Gets all orders.
     *
     * @return all orders in system
     */
    @GetMapping ( BASE_PATH + "/orders" )
    public List<Order> getOrders () {
        return orderService.findAll();
    }

    /**
     * Gets the order with given name
     *
     * @param name
     *            the name of order to be retrived
     * @return the order that was asked for
     */
    @GetMapping ( BASE_PATH + "/orders/{name}" )
    public ResponseEntity getOrder ( @PathVariable final String name ) {
        final Order orderRetreived = orderService.findByName( name );
        if ( orderRetreived == null ) {
            return new ResponseEntity( errorResponse( "No order could be found" ), HttpStatus.CONFLICT );
        }
        return new ResponseEntity( orderRetreived, HttpStatus.OK );
    }

    /**
     * Gets all orders by customer.
     *
     * @param name
     *            the name of customer
     * @return all orders by customer.
     */
    @GetMapping ( BASE_PATH + "/orders/customer/{name}" )
    public List<Order> getOrderByCustomer ( @PathVariable final String name ) {
        final Customer customer = customerService.findByName( name );
        if ( customer == null ) {
            throw new IllegalArgumentException( "Invalid Customer" );
        }
        final List<Order> custOrders = orderService.findAll();
        for ( final Order order : custOrders ) {
            if ( !order.getCustomer().equals( customer ) ) {
                custOrders.remove( order );
            }
        }
        return custOrders;
    }

    /**
     * Cancel order.
     *
     * @param name
     *            the name of the customer
     * @return the response entity
     */
    @PutMapping ( BASE_PATH + "/orders/cancel/{name}" )
    public ResponseEntity cancelOrder ( @PathVariable final String name ) {
        final Order orderRetreived = orderService.findByName( name );
        if ( orderRetreived == null ) {
            return new ResponseEntity( errorResponse( "No order could be found" ), HttpStatus.CONFLICT );
        }
        orderRetreived.setStatus( OrderStatus.CANCELED );
        orderService.save( orderRetreived );
        return new ResponseEntity( orderRetreived, HttpStatus.OK );
    }

    // This is gonna need more methods/routs to do statues changes
    // @PostMapping ( BASE_PATH + "/orders/{status}" )
    // public ResponseEntity updateOrderStatus ( @PathVariable final String
    // status, @RequestBody final Order order ) {
    // final Order o = orderService.findByName( order.getName() );
    // if ( o == null ) {
    // return new ResponseEntity( errorResponse( "No order could be found" ),
    // HttpStatus.CONFLICT );
    // }
    //
    // // This loops through enums and set status if it equals the given value
    // for ( final OrderStatus os : OrderStatus.values() ) {
    // if ( os.toString().equalsIgnoreCase( status ) ) {
    // o.setStatus( os );
    // orderService.save( o );
    // // Successful case
    // return new ResponseEntity( o, HttpStatus.OK );
    // }
    // }
    // // if it reaches here, the status is valid
    // return new ResponseEntity( errorResponse( "Invalid Status" ),
    // HttpStatus.CONFLICT );
    //
    // }

    /**
     * Place an order.
     *
     * @param name
     *            the name of customer that places order
     * @param order
     *            the order that is to be placed
     * @return the response entity
     */
    @PostMapping ( BASE_PATH + "/orders/place/{name}" )
    public ResponseEntity placeOrder ( @PathVariable final String name, @RequestBody final Order order ) {
        if ( order == null ) {
            return new ResponseEntity( errorResponse( "Invalid Order" ), HttpStatus.CONFLICT );
        }
        final Customer customer = customerService.findByName( name );
        if ( customer == null ) {
            return new ResponseEntity( errorResponse( "The customer does not exist" ), HttpStatus.CONFLICT );
        }

        order.setCustomer( customer );
        order.setStatus( OrderStatus.NOT_STARTED );
        orderService.save( order );
        final Order order2 = orderService.findByName( order.getName() );
        order2.setName( order2.getId().toString() );
        orderService.save( order2 );
        return new ResponseEntity( order2, HttpStatus.OK );
    }

    /**
     * Sets the order to inprogress.
     *
     * @param name
     *            the name of staff assigned to order
     * @param order
     *            the order to be inProgress
     * @return the response entity
     */
    // IN_progress
    @PutMapping ( BASE_PATH + "/orders/inprogress/{name}" )
    public ResponseEntity setInProgress ( @PathVariable final String name, @RequestBody final Order order ) {
        final Order orderRetreived = orderService.findByName( order.getName() );
        if ( orderRetreived == null ) {
            return new ResponseEntity( errorResponse( "Order does not exists" ), HttpStatus.CONFLICT );
        }
        final Staff staff = staffService.findByName( name );
        if ( staff == null ) {
            return new ResponseEntity( errorResponse( "The staff does not exist" ), HttpStatus.CONFLICT );
        }
        orderRetreived.setStaff( staff );
        orderRetreived.setStatus( OrderStatus.IN_PROGRESS );
        orderService.save( orderRetreived );
        return new ResponseEntity( orderRetreived, HttpStatus.OK );
    }

    /**
     * Sets the order complete.
     *
     * @param amount
     *            the amount paid
     * @param order
     *            the order to be completed
     * @return the response entity with change
     */
    // Complete
    @PutMapping ( BASE_PATH + "/orders/complete/{amount}" )
    public ResponseEntity setComplete ( @PathVariable final String amount, @RequestBody final Order order ) {
        final Order orderRetreived = orderService.findByName( order.getName() );
        if ( orderRetreived == null ) {
            return new ResponseEntity( errorResponse( "Order does not exist" ), HttpStatus.CONFLICT );
        }
        // changing to Int
        int amountValue = 0;
        try {
            amountValue = Integer.parseInt( amount );
        }
        catch ( final NumberFormatException e ) {
            // passed in non int String parsable value
            return new ResponseEntity( errorResponse( "Invalid Value" ), HttpStatus.CONFLICT );
        }

        // see if number is big enough
        final int change = amountValue - orderRetreived.getTotalCost();
        if ( change < 0 ) {
            return new ResponseEntity( errorResponse( "Not enough money paid" ), HttpStatus.CONFLICT );
        }

        // see if enough ingredients in inventory
        if ( !checkIngredient( orderRetreived.getRecipes() ) ) {
            // System.out.println( orderRetreived.getRecipes() );
            return new ResponseEntity( errorResponse( "Not enough inventory!" ), HttpStatus.CONFLICT );
        }

        // make the actual order
        for ( final Recipe recipe : order.getRecipes() ) {
            final Recipe r = recipeService.findByName( recipe.getName() );
            if ( !makeCoffee( r ) ) {
                return new ResponseEntity( errorResponse( "Not enough inventory" ), HttpStatus.CONFLICT );
            }
        }

        // sets correct status
        orderRetreived.setStatus( OrderStatus.COMPLETED );
        orderService.save( orderRetreived );
        return new ResponseEntity<String>( successResponse( String.valueOf( change ) ), HttpStatus.OK );
    }

    /**
     * Helper method to make coffee.
     *
     * @param toPurchase
     *            recipe that we want to make
     * @return change if there was enough money to make the coffee, throws
     *         exceptions if not
     */
    private boolean makeCoffee ( final Recipe toPurchase ) {
        final Inventory inventory = inventoryService.getInventory();

        if ( toPurchase == null ) {
            throw new IllegalArgumentException( "Recipe not found" );
        }
        else if ( inventory.useIngredients( toPurchase ) ) {
            inventoryService.save( inventory );
            return true;
        }
        else {
            // not enough inventory
            return false;
        }
    }

    /**
     * Check if enough ingredients exists .
     *
     * @param recipes
     *            the recipes in order
     * @return true, if successful
     */
    private boolean checkIngredient ( final List<Recipe> recipes ) {
        final Inventory inventory = inventoryService.getInventory();
        // System.out.println( inventory );

        if ( recipes == null ) {
            throw new IllegalArgumentException( "Recipe not found" );
        }
        boolean enoughIngreidents = true;
        for ( final Recipe recipe : recipes ) {
            // need to update method
            if ( !inventory.enoughIngredients( recipe ) ) {

                enoughIngreidents = false;
            }
        }
        // not enough money
        return enoughIngreidents;
    }

    /**
     * Sets the order as picked up.
     *
     * @param name
     *            the name of the customer
     *
     * @param order
     *            the order to pick up
     *
     * @return the response entity
     */
    // Picked up
    @PutMapping ( BASE_PATH + "/orders/pickup/{name}" )
    private ResponseEntity setPickUp ( @PathVariable final String name, @RequestBody final Order order ) {
        final Order orderRetreived = orderService.findByName( order.getName() );
        if ( orderRetreived == null ) {
            return new ResponseEntity( errorResponse( "Order does not exist" ), HttpStatus.CONFLICT );
        }

        orderRetreived.setStatus( OrderStatus.PICKED_UP );
        orderService.save( orderRetreived );
        return new ResponseEntity( orderRetreived, HttpStatus.OK );
    }

}
