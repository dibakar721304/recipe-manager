package com.manage.recipe.repository;

import com.manage.recipe.model.dao.IngredientDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientDAO, Long> {}
