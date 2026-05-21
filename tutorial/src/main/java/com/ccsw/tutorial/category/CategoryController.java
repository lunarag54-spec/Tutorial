package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Category", description = "API de categorías")
@RequestMapping("/api/category")
@RestController
public class CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper mapper;

    public CategoryController(CategoryService categoryService, ModelMapper mapper) {
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @Operation(summary = "Listar categorías")
    @GetMapping
    public List<CategoryDto> findAll() {
        return categoryService.findAll().stream().map(e -> mapper.map(e, CategoryDto.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Crear categoría")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody CategoryDto dto) {
        Category saved = categoryService.create(dto);
        return mapper.map(saved, CategoryDto.class);
    }

    @Operation(summary = "Actualizar categoría")
    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable("id") Long id, @Valid @RequestBody CategoryDto dto) {
        Category saved = categoryService.update(id, dto);
        return mapper.map(saved, CategoryDto.class);
    }

    @Operation(summary = "Eliminar categoría")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
    }
}
