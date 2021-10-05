package com.example.demo.services;

import com.example.demo.repositories.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {


    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    RecipeService recipeService;
}
