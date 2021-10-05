package com.example.demo.repositories;

import com.example.demo.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RatingRepo extends JpaRepository<Rating, Long> {

    ArrayList<Rating> rating = findByRecipeId(Long recipeId);

}
