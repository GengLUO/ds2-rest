package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
public class MealsRestRpcStyleController {

    private final MealsRepository mealsRepository;

    @Autowired
    MealsRestRpcStyleController(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/restrpc/meals/{id}")
    Meal getMealById(@PathVariable String id) {
        Optional<Meal> meal = mealsRepository.findMeal(id);

        return meal.orElseThrow(() -> new MealNotFoundException(id));
    }

    @GetMapping("/restrpc/meals")
    Collection<Meal> getMeals() {
        return mealsRepository.getAllMeal();
    }

    @GetMapping("/restrpc/meals/cheapest")
    public Meal getCheapestMeal() {
        return mealsRepository.findCheapestMeal();
    }

    @GetMapping("/restrpc/meals/largest")
    public Meal getLargestMeal() {
        return mealsRepository.findLargestMeal();
    }

    @PostMapping("/restrpc/meals")
    @ResponseStatus(HttpStatus.CREATED)
    public Meal addMeal(@RequestBody Meal meal) {
        return mealsRepository.addMeal(meal);
    }

    @PutMapping("/restrpc/meals/{id}")
    public Meal updateMeal(@PathVariable String id, @RequestBody Meal updatedMeal) {
        return mealsRepository.updateMeal(id, updatedMeal)
                .orElseThrow(() -> new MealNotFoundException(id));
    }

    @DeleteMapping("/restrpc/meals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable String id) {
        mealsRepository.deleteMeal(id)
                .orElseThrow(() -> new MealNotFoundException(id));
    }

}
