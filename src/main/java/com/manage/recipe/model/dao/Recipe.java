package com.manage.recipe.model.dao;

import com.manage.recipe.model.FoodCategory;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @Column(name = "recipe_name")
    private String name;

    @Enumerated(value = EnumType.STRING)
    private FoodCategory foodCategory;

    @Column(name = "servings_number")
    private int servings;

    @Column(name = "ingredients")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Ingredient> ingredients;

    @Column(name = "recipe_instructions")
    private String instructions;
}
