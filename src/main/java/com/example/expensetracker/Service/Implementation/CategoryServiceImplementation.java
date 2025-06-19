package com.example.expensetracker.Service.Implementation;

import com.example.expensetracker.Entity.Category;
import com.example.expensetracker.Payload.CategoryDTO;
import com.example.expensetracker.Repository.CategoryRepository;
import com.example.expensetracker.Service.CategoryService;
import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImplementation implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category newCategory = new Category();
        newCategory.setName(categoryDTO.getName());
        if(categoryDTO.getCategoryId()==null){
            categoryDTO.setCategoryId(newCategory.getCategoryId());
        }
        categoryRepository.save(newCategory);
        return modelMapper.map(newCategory, CategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new RuntimeException("No categories found");
        }
        List<CategoryDTO> categoriesDTO = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        return categoriesDTO;
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        Category targetCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return modelMapper.map(targetCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category targetCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(targetCategory);

        return modelMapper.map(targetCategory, CategoryDTO.class);
    }
}
