package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.exception.BadRequestException;
import com.ccsw.tutorial.common.exception.ConflictException;
import com.ccsw.tutorial.common.exception.NotFoundException;
import com.ccsw.tutorial.game.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final GameRepository gameRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, GameRepository gameRepository) {
        this.categoryRepository = categoryRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public Category get(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category create(CategoryDto dto) {
        if (dto == null) {
            throw new BadRequestException("BAD_REQUEST", "El cuerpo de la petición es obligatorio.");
        }
        String name = normalizeName(dto.getName());
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Long id, CategoryDto dto) {
        if (dto == null) {
            throw new BadRequestException("BAD_REQUEST", "El cuerpo de la petición es obligatorio.");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CATEGORY_NOT_FOUND", "No existe la categoría indicada."));
        category.setName(normalizeName(dto.getName()));
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("CATEGORY_NOT_FOUND", "No existe la categoría indicada.");
        }
        if (gameRepository.countByCategory_Id(id) > 0) {
            throw new ConflictException("CATEGORY_IN_USE", "Existen juegos asociados a esta categoría; no se puede eliminar.");
        }
        categoryRepository.deleteById(id);
    }

    private static String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("BAD_REQUEST", "El nombre de la categoría es obligatorio.");
        }
        return name.trim();
    }
}
