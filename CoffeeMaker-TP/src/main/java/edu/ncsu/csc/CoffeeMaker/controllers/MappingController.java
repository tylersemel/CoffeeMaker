package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns
 * the approprate HTML page in the /src/main/resources/templates folder. For a
 * larger application, this should be split across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html .
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/index", "/" } )
    public String index ( final Model model ) {
        return "index";
    }

    /**
     * On a GET request to /recipe, the RecipeController will return
     * /src/main/resources/templates/recipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/recipe", "/recipe.html" } )
    public String addRecipePage ( final Model model ) {
        return "recipe";
    }

    /**
     * On a GET request to /deleterecipe, the DeleteRecipeController will return
     * /src/main/resources/templates/deleterecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/deleterecipe", "/deleterecipe.html" } )
    public String deleteRecipeForm ( final Model model ) {
        return "deleterecipe";
    }

    /**
     * On a GET request to /editrecipe, the EditRecipeController will return
     * /src/main/resources/templates/editrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/editrecipe", "/editrecipe.html" } )
    public String editRecipeForm ( final Model model ) {
        return "editrecipe";
    }

    /**
     * Handles a GET request for inventory. The GET request provides a view to
     * the client that includes the list of the current ingredients in the
     * inventory and a form where the client can enter more ingredients to add
     * to the inventory.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/inventory", "/inventory.html" } )
    public String inventoryForm ( final Model model ) {
        return "inventory";
    }

    /**
     * On a GET request to /makecoffee, the MakeCoffeeController will return
     * /src/main/resources/templates/makecoffee.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/makecoffee", "/makecoffee.html" } )
    public String makeCoffeeForm ( final Model model ) {
        return "makecoffee";
    }

    /**
     * On a GET request to /addNewIngredient, the MakeCoffeeController will
     * return /src/main/resources/templates/addNewIngredient.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/addNewIngredient", "/addNewIngredient.html" } )
    public String addNewIngredient ( final Model model ) {
        return "addNewIngredient";
    }

    // ---------Start of TP-------------\\

    /**
     * On a GET request to /customerSignup, the MakeCoffeeController will return
     * /src/main/resources/templates/customerSignup.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customerSignup", "/customerSignup.html" } )
    public String customerSignup ( final Model model ) {
        return "customerSignup";
    }

    /**
     * On a GET request to /customerHomepage, the MakeCoffeeController will
     * return /src/main/resources/templates/customerHomepage.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customerHomepage", "/customerHomepage.html" } )
    public String customerHomepage ( final Model model ) {
        return "customerHomepage";
    }

    /**
     * On a GET request to /staffLogin, the MakeCoffeeController will return
     * /src/main/resources/templates/staffLogin.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/staffLogin", "/staffLogin.html" } )
    public String staffLogin ( final Model model ) {
        return "staffLogin";
    }

    /**
     * On a GET request to /staffHomepage, the MakeCoffeeController will return
     * /src/main/resources/templates/staffHomepage.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/staffHomepage", "/staffHomepage.html" } )
    public String staffHomepage ( final Model model ) {
        return "staffHomepage";
    }

    /**
     * On a GET request to /menu, the MakeCoffeeController will return
     * /src/main/resources/templates/menu.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/menu", "/menu.html" } )
    public String menu ( final Model model ) {
        return "menu";
    }

    /**
     * On a GET request to /customerOrders, the MakeCoffeeController will return
     * /src/main/resources/templates/customerOrders.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customerOrders", "/customerOrders.html" } )
    public String customerOrders ( final Model model ) {
        return "customerOrders";
    }

    /**
     * On a GET request to /staffOrders, the MakeCoffeeController will return
     * /src/main/resources/templates/staffOrders.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/staffOrders", "/staffOrders.html" } )
    public String staffOrders ( final Model model ) {
        return "staffOrders";
    }

    /**
     * On a GET request to /adminHomepage, the MakeCoffeeController will return
     * /src/main/resources/templates/adminHomepage.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/adminHomepage", "/adminHomepage.html" } )
    public String adminHomepage ( final Model model ) {
        return "adminHomepage";
    }

    /**
     * On a GET request to /createStaff, the MakeCoffeeController will return
     * /src/main/resources/templates/createStaff.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/createStaff", "/createStaff.html" } )
    public String createStaff ( final Model model ) {
        return "createStaff";
    }
}
