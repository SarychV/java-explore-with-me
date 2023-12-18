package ru.practicum.ewm.categories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
@Validated
public class PublicCategoriesController {
    private final CategoryService categoryService;

    public PublicCategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Получение категорий
    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("categoryService.findCategories() was invoked with from={}, size={}", from, size);
        return categoryService.findCategories(from, size);
    }

    // Получение информации о категории по ее идентификатору
    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable @Positive long catId) {
        log.info("categoryService.findCategoryById() was invoked with catId={}", catId);
        return categoryService.findCategoryById(catId);
    }
}
