package com.manage.recipe.repository;

import com.manage.recipe.model.dao.RecipeDAO;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeDAO, Long> {
    Optional<RecipeDAO> findByName(String name);
}
