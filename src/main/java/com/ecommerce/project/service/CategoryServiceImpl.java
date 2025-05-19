package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()){
            throw new APIException("No category created till now");
        }
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category categoryFromDB = categoryRepository.findByCategoryNameIgnoreCase(category.getCategoryName());

        if (categoryFromDB != null){
            throw new APIException("Category with the name "+categoryDTO.getCategoryName()+" already exists !!!");
        }
        Category savedCategory =  categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            categoryRepository.deleteById(categoryId);
            return modelMapper.map(category.get(), CategoryDTO.class);
        } else {
            throw new ResourceNotFoundException("Category","categoryId",categoryId);
        }

    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        // Get the existing category form DB
        Category categoryFromDB = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));
        // Update the category info with the one in request body
        categoryFromDB.setCategoryName(categoryDTO.getCategoryName());
        // Save to database
        Category saveCategory = categoryRepository.save(categoryFromDB);
        return modelMapper.map(saveCategory, CategoryDTO.class);
//        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
//        if (categoryOptional.isPresent()){
//            Category savedCategory = categoryOptional.get();
//            savedCategory.setCategoryName(categoryDTO.getCategoryName());;
//            categoryRepository.save(savedCategory);
//            return modelMapper.map(savedCategory, CategoryDTO.class);
//        }else {
//            throw new ResourceNotFoundException("Category","categoryId",categoryId);
//        }
    }
}
