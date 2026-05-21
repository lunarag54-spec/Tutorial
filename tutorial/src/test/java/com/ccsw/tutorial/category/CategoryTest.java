package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.exception.NotFoundException;
import com.ccsw.tutorial.game.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void findAllShouldReturnAllCategories() {
        List<Category> list = new ArrayList<>();
        list.add(mock(Category.class));

        when(categoryRepository.findAll()).thenReturn(list);

        List<Category> categories = categoryService.findAll();

        assertNotNull(categories);
        assertEquals(1, categories.size());
    }

    public static final String CATEGORY_NAME = "CAT1";

    @Test
    public void createShouldInsert() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(CATEGORY_NAME);

        ArgumentCaptor<Category> category = ArgumentCaptor.forClass(Category.class);

        categoryService.create(categoryDto);

        verify(categoryRepository).save(category.capture());
        assertEquals(CATEGORY_NAME, category.getValue().getName());
    }

    public static final Long EXISTS_CATEGORY_ID = 1L;

    @Test
    public void updateExistsCategoryIdShouldUpdate() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(CATEGORY_NAME);

        Category category = mock(Category.class);
        when(categoryRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(category));

        categoryService.update(EXISTS_CATEGORY_ID, categoryDto);

        verify(categoryRepository).save(category);
    }

    @Test
    public void updateNotExistsCategoryIdShouldThrowNotFound() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(CATEGORY_NAME);

        when(categoryRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.update(EXISTS_CATEGORY_ID, categoryDto));
    }

    @Test
    public void deleteExistsCategoryIdShouldDelete() {
        when(categoryRepository.existsById(EXISTS_CATEGORY_ID)).thenReturn(true);
        when(gameRepository.countByCategory_Id(EXISTS_CATEGORY_ID)).thenReturn(0L);

        categoryService.delete(EXISTS_CATEGORY_ID);

        verify(categoryRepository).deleteById(EXISTS_CATEGORY_ID);
    }

    @Test
    public void deleteNotExistsCategoryIdShouldThrowNotFound() {
        when(categoryRepository.existsById(EXISTS_CATEGORY_ID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> categoryService.delete(EXISTS_CATEGORY_ID));
    }
}
