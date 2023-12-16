package ru.practicum.ewm.categories;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {
    protected static Category toCategory(CategoryDto dto) {
        Category result = new Category();
        result.setName(dto.getName());
        return result;
    }

    protected static Category toCategory(long catId, CategoryDto dto) {
        Category result = new Category();
        result.setId(catId);
        result.setName(dto.getName());
        return result;
    }

    public static CategoryDto toCategoryDto(Category category) {
        CategoryDto result = new CategoryDto();
        result.setId(category.getId());
        result.setName(category.getName());
        return result;
    }

    protected static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}
