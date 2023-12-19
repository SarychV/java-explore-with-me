package ru.practicum.ewm.categories;

import ru.practicum.ewm.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);

    void deleteCategory(long catId);

    List<CategoryDto> findCategories(int from, int size);

    CategoryDto findCategoryById(long catId);
}
