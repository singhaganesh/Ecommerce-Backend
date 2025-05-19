package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryNameIgnoreCase(@NotBlank(message = "Category name must not be empty") String categoryName);
}
