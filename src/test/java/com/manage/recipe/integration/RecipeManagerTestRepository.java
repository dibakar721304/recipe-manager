package com.manage.recipe.integration;

import com.manage.recipe.model.dao.RecipeDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeManagerTestRepository
        extends JpaRepository<RecipeDAO, Long>, JpaSpecificationExecutor<RecipeDAO> {}
