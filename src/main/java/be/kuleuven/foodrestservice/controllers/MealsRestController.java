package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.domain.Order;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class MealsRestController {

    private final MealsRepository mealsRepository;

    @Autowired
    MealsRestController(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/rest/meals/{id}")
    EntityModel<Meal> getMealById(@PathVariable String id) {
        Meal meal = mealsRepository.findMeal(id).orElseThrow(() -> new MealNotFoundException(id));

        return mealToEntityModel(id, meal);
    }

    @GetMapping("/rest/meals")
    CollectionModel<EntityModel<Meal>> getMeals() {
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }
        return CollectionModel.of(mealEntityModels,
                linkTo(methodOn(MealsRestController.class).getMeals()).withSelfRel());
    }

    @GetMapping("/rest/meals/cheapest")
    public EntityModel<Meal> getCheapestMeal() {
        Meal meal = mealsRepository.findCheapestMeal();
        return mealToEntityModel(meal.getId(), meal);
    }

    @GetMapping("/rest/meals/largest")
    public EntityModel<Meal> getLargestMeal() {
        Meal meal = mealsRepository.findLargestMeal();
        return mealToEntityModel(meal.getId(), meal);
    }

    @PostMapping("/rest/meals")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Meal> addMeal(@RequestBody Meal meal) {
        Meal newMeal = mealsRepository.addMeal(meal);
        return mealToEntityModel(newMeal.getId(),newMeal);
    }

    @PutMapping("/rest/meals/{id}")
    public EntityModel<Meal> updateMeal(@PathVariable String id, @RequestBody Meal updatedMeal) {
        Meal meal = mealsRepository.updateMeal(id, updatedMeal)
                .orElseThrow(() -> new MealNotFoundException(id));
        return mealToEntityModel(meal.getId(),meal);
    }

    @DeleteMapping("/rest/meals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable String id) {
        mealsRepository.deleteMeal(id)
                .orElseThrow(() -> new MealNotFoundException("Could not find meal " + id));
    }

    @PostMapping("/rest/orders")
    public ResponseEntity<String> orderMeals(@RequestBody Order order) {
        // Validate the order details
        if (order.getMealIds() == null || order.getMealIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meal IDs must not be empty");
        }

        // Fetch the meals and calculate the total price
        double totalPrice = 0;
        for (String mealId : order.getMealIds()) {
            Meal meal = mealsRepository.findMeal(mealId)
                    .orElseThrow(() -> new MealNotFoundException("Could not find meal " + mealId));
            totalPrice += meal.getPrice();
        }

        // Here, you might want to implement additional logic such as updating inventory, saving the order details, etc.

        // Create a simple confirmation message
        String confirmationMessage = String.format("Order placed successfully! Total price: %.2f", totalPrice);

//        // Create and return the response entity with HATEOAS support
//        EntityModel<String> entityModel = EntityModel.of(confirmationMessage,
//                linkTo(methodOn(MealsRestController.class).orderMeals(order)).withSelfRel());
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(confirmationMessage);
    }


    private EntityModel<Meal> mealToEntityModel(String id, Meal meal) {
        return EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(id)).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("rest/meals"));
    }

}
