package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.api.ApiErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoryIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/api/category";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<CategoryDto>> responseType = new ParameterizedTypeReference<List<CategoryDto>>() {
    };

    private final ParameterizedTypeReference<ApiErrorResponse> errorType = new ParameterizedTypeReference<ApiErrorResponse>() {
    };

    @Test
    public void findAllShouldReturnAllCategories() {
        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(3, response.getBody().size());
    }

    public static final Long NEW_CATEGORY_ID = 4L;
    public static final String NEW_CATEGORY_NAME = "CAT4";

    @Test
    public void postShouldCreateNewCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        ResponseEntity<CategoryDto> created = restTemplate.postForEntity(LOCALHOST + port + SERVICE_PATH, dto, CategoryDto.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody().getId());

        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(4, response.getBody().size());

        CategoryDto categorySearch = response.getBody().stream().filter(item -> item.getId().equals(NEW_CATEGORY_ID)).findFirst().orElse(null);
        assertNotNull(categorySearch);
        assertEquals(NEW_CATEGORY_NAME, categorySearch.getName());
    }

    public static final Long MODIFY_CATEGORY_ID = 3L;

    @Test
    public void putWithExistIdShouldModifyCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_CATEGORY_ID, HttpMethod.PUT, new HttpEntity<>(dto), CategoryDto.class);

        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(3, response.getBody().size());

        CategoryDto categorySearch = response.getBody().stream().filter(item -> item.getId().equals(MODIFY_CATEGORY_ID)).findFirst().orElse(null);
        assertNotNull(categorySearch);
        assertEquals(NEW_CATEGORY_NAME, categorySearch.getName());
    }

    @Test
    public void putWithNotExistIdShouldReturnNotFound() {
        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CATEGORY_ID, HttpMethod.PUT,
                new HttpEntity<>(dto), errorType);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("CATEGORY_NOT_FOUND", response.getBody().getCode());
    }

    @Test
    public void deleteCreatedCategoryShouldSucceed() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Temporal");
        ResponseEntity<CategoryDto> created = restTemplate.postForEntity(LOCALHOST + port + SERVICE_PATH, dto, CategoryDto.class);
        Long id = created.getBody().getId();

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + id, HttpMethod.DELETE, null, Void.class);

        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(3, response.getBody().size());
    }

    @Test
    public void deleteCategoryInUseShouldReturnConflict() {
        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/2", HttpMethod.DELETE, null, errorType);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CATEGORY_IN_USE", response.getBody().getCode());
    }

    @Test
    public void deleteWithNotExistsIdShouldReturnNotFound() {
        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CATEGORY_ID, HttpMethod.DELETE,
                null, errorType);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("CATEGORY_NOT_FOUND", response.getBody().getCode());
    }
}
