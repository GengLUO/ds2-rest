package be.kuleuven.foodrestservice.domain;

import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MealsRepository {
    // map: id -> meal
    private static final Map<String, Meal> meals = new ConcurrentHashMap<>();

    @PostConstruct
    public void initData() {

        Meal a = new Meal();
        a.setId("5268203c-de76-4921-a3e3-439db69c462a");
        a.setName("Steak");
        a.setDescription("Steak with fries");
        a.setMealType(MealType.MEAT);
        a.setKcal(1100);
        a.setPrice((10.00));

        meals.put(a.getId(), a);

        Meal b = new Meal();
        b.setId("4237681a-441f-47fc-a747-8e0169bacea1");
        b.setName("Portobello");
        b.setDescription("Portobello Mushroom Burger");
        b.setMealType(MealType.VEGAN);
        b.setKcal(637);
        b.setPrice((7.00));

        meals.put(b.getId(), b);

        Meal c = new Meal();
        c.setId("cfd1601f-29a0-485d-8d21-7607ec0340c8");
        c.setName("Fish and Chips");
        c.setDescription("Fried fish with chips");
        c.setMealType(MealType.FISH);
        c.setKcal(950);
        c.setPrice(5.00);

        meals.put(c.getId(), c);
    }

    public Optional<Meal> findMeal(String id) {
        Assert.notNull(id, "The meal id must not be null");
        Meal meal = meals.get(id);
        return Optional.ofNullable(meal);
    }

    public Collection<Meal> getAllMeal() {
        return meals.values();
    }

    public Meal findCheapestMeal() {
        return getAllMeal().stream()
                .min(Comparator.comparing(Meal::getPrice))
                .orElseThrow(() -> new IllegalArgumentException("No meals available"));
    }

    public Meal findLargestMeal() {
        return getAllMeal().stream()
                .max(Comparator.comparing(Meal::getKcal))
                .orElseThrow(() -> new IllegalArgumentException("No meals available"));
    }

    public Meal addMeal(Meal meal) {
        Assert.notNull(meal, "The meal cannot be null");
        meals.put(meal.getId(), meal);
        return meal;
    }

    public Optional<Meal> updateMeal(String id, Meal updatedMeal) {
        Assert.notNull(id, "The meal id must not be null");
        Assert.notNull(updatedMeal, "The updated meal cannot be null");
        if (!meals.containsKey(id)) {
            return Optional.empty();
        }
        updatedMeal.setId(id);
        meals.put(id, updatedMeal);
        return Optional.of(updatedMeal);
    }

    public Optional<Meal> deleteMeal(String id) {
        Assert.notNull(id, "The meal id must not be null");
        return Optional.ofNullable(meals.remove(id));
    }
}
