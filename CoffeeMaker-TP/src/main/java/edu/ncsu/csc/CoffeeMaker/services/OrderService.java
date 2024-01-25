package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

/**
 * The OrderService will handle CRUD operations on the Ingredient model.
 *
 */
@Component
@Transactional
public class OrderService extends Service<Order, Long> {

    /** OrderRepository */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * The orderReposiotry to return
     */
    @Override
    protected JpaRepository<Order, Long> getRepository () {
        return orderRepository;
    }

    /**
     * Finds an order by its id
     *
     * @return the order
     */
    public Order findByName ( final String name ) {
        return orderRepository.findByName( name );
    }

}
