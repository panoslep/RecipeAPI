package com.example.demo.services;

import com.example.demo.exceptions.NoSuchRecipeException;
import com.example.demo.models.Recipe;
import com.example.demo.models.Review;
import com.example.demo.repositories.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe) throws IllegalStateException {
        recipe.validate();
        recipe = recipeRepo.save(recipe);
        recipe.generateLocationURI();
        return recipe;
    }

    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);

        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe with ID " + id + " could be found.");
        }

        Recipe recipe = recipeOptional.get();

        recipe.generateLocationURI();



        return recipe;
    }

    public ArrayList<Recipe> getRecipesByName(String name) throws NoSuchRecipeException {
        ArrayList<Recipe> matchingRecipes = recipeRepo.findByNameContaining(name);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name.");
        }

        for (Recipe r : matchingRecipes) {
            r.generateLocationURI();
        }
        return matchingRecipes;
    }

//    public ArrayList<Recipe> getRecipesByaverageRating(int averageRating) throws NoSuchRecipeException {
//        ArrayList<Recipe> matchingRecipes = recipeRepo.findByNameContaining(name);
//
//        if (matchingRecipes.isEmpty()) {
//            throw new NoSuchRecipeException("No recipes could be found with that rating.");
//        }
//
//        for (Recipe r : matchingRecipes) {
//            r.generateLocationURI();
//        }
//        return matchingRecipes;
//    }


    public ArrayList<Recipe> getAllRecipes() throws NoSuchRecipeException {
        ArrayList<Recipe> recipes = new ArrayList<>(recipeRepo.findAll());

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes yet :( feel free to add one though");
        }
        return recipes;
    }

    public  ArrayList<Recipe> getRecipesByRating(Double rating) throws NoSuchRecipeException {
        ArrayList<Recipe> recipes = new ArrayList<>(recipeRepo.findAll());
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getAverageRating() < rating) {
                recipes.remove(i);
            }

        }

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes with this rating");
        }
        return recipes;
    }


    @Transactional
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(e.getMessage() + " Could not delete.");
        }
    }

    @Transactional
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }

            double avRating = 0;
            // Iterate through reviews to find the average rating
            Collection<Review> tempReviews = recipe.getReviews();
            if (!tempReviews.isEmpty()) {
                Iterator<Review> iterator = tempReviews.iterator();
                int count = 0;
                while (iterator.hasNext()) {
                    count++;
                    avRating += iterator.next().getRating();
                }
                avRating /= count;
            }
            recipe.setAverageRating(avRating);
            recipe.validate();
            Recipe savedRecipe = recipeRepo.save(recipe);
            savedRecipe.generateLocationURI();
            return savedRecipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("The recipe you passed in did not have an ID found in the database." +
                    " Double check that it is correct. Or maybe you meant to POST a recipe not PATCH one.");
        }
    }
}