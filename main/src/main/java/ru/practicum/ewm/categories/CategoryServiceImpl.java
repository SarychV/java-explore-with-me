package ru.practicum.ewm.categories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        log.info("categoryRepository.save() was invoked with arguments category={}", category);
        Category returnedCategory = categoryRepository.save(category);
        CategoryDto result = CategoryMapper.toCategoryDto(returnedCategory);
        log.info("In AdminCategoriesController was returned categoryDto={}", result);
        return result;
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(
                String.format("Category with id=%d was not found", catId)));
        Category category = CategoryMapper.toCategory(catId, categoryDto);
        log.info("categoryRepository.save() was invoked with arguments category={}", category);
        Category returnedCategory = categoryRepository.save(category);
        CategoryDto result = CategoryMapper.toCategoryDto(returnedCategory);
        return result;
    }

    @Override
    public void deleteCategory(long catId) {
        categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(
                String.format("Category with id=%d was not found", catId)));
        log.info("categoryRepository.deleteById was invoked with catId={}", catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> findCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<Category> returnedCategories = categories.stream().collect(Collectors.toList());
        List<CategoryDto> result = CategoryMapper.toCategoryDtoList(returnedCategories);
        log.info("In PublicCategoriesController was returned List<CategoryDto>={}", result);
        return result;
    }

    @Override
    public CategoryDto findCategoryById(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(
           String.format("Category with id=%d was not found", catId)));
        CategoryDto result = CategoryMapper.toCategoryDto(category);
        log.info("In PublicCategoriesController was returned categoryDto={}", result);
        return result;
    }
}
