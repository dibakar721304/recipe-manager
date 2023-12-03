package com.manage.recipe.integration;

import com.manage.recipe.model.dao.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeManagerTestRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {}
