package com.ccsw.tutorial.game;

import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.api.ApiErrorResponse;
import com.ccsw.tutorial.game.model.GameDto;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GameIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/api/game";

    public static final Long EXISTS_GAME_ID = 1L;
    public static final Long NOT_EXISTS_GAME_ID = 0L;
    private static final String NOT_EXISTS_TITLE = "NotExists";
    private static final String EXISTS_TITLE = "Aventureros";
    private static final String NEW_TITLE = "Nuevo juego";
    private static final Long NOT_EXISTS_CATEGORY = 0L;
    private static final Long EXISTS_CATEGORY = 3L;

    private static final String TITLE_PARAM = "title";
    private static final String CATEGORY_ID_PARAM = "idCategory";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<GameDto>> responseType = new ParameterizedTypeReference<List<GameDto>>() {
    };

    private final ParameterizedTypeReference<ApiErrorResponse> errorType = new ParameterizedTypeReference<ApiErrorResponse>() {
    };

    private String getUrlWithParams() {
        return UriComponentsBuilder.fromHttpUrl(LOCALHOST + port + SERVICE_PATH)
                .queryParam(TITLE_PARAM, "{" + TITLE_PARAM + "}")
                .queryParam(CATEGORY_ID_PARAM, "{" + CATEGORY_ID_PARAM + "}")
                .encode()
                .toUriString();
    }

    @Test
    public void findWithoutFiltersShouldReturnAllGamesInDB() {
        int GAMES_WITH_FILTER = 6;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, null);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(GAMES_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void findEmptyTitleShouldReturnAllGames() {
        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, "");
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(6, response.getBody().size());
    }

    @Test
    public void findExistsTitleShouldReturnGames() {
        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void findExistsCategoryShouldReturnGames() {
        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, null);
        params.put(CATEGORY_ID_PARAM, EXISTS_CATEGORY);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void findExistsTitleAndCategoryShouldReturnGames() {
        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, EXISTS_CATEGORY);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void findNotExistsTitleShouldReturnEmpty() {
        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NOT_EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(0, response.getBody().size());
    }

    @Test
    public void findNotExistsCategoryShouldReturnEmpty() {
        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, null);
        params.put(CATEGORY_ID_PARAM, NOT_EXISTS_CATEGORY);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(0, response.getBody().size());
    }

    @Test
    public void postShouldCreateNewGame() {
        GameDto dto = buildGameDto(NEW_TITLE);

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NEW_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> before = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);
        assertEquals(0, before.getBody().size());

        restTemplate.postForEntity(LOCALHOST + port + SERVICE_PATH, dto, GameDto.class);

        ResponseEntity<List<GameDto>> after = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);
        assertEquals(1, after.getBody().size());
    }

    @Test
    public void putWithExistIdShouldModifyGame() {
        GameDto dto = buildGameDto(NEW_TITLE);

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NEW_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + EXISTS_GAME_ID, HttpMethod.PUT, new HttpEntity<>(dto), GameDto.class);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertEquals(EXISTS_GAME_ID, response.getBody().get(0).getId());
    }

    @Test
    public void putWithNotExistIdShouldReturnNotFound() {
        GameDto dto = new GameDto();
        dto.setTitle(NEW_TITLE);

        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NOT_EXISTS_GAME_ID, HttpMethod.PUT,
                new HttpEntity<>(dto), errorType);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("GAME_NOT_FOUND", response.getBody().getCode());
    }

    private static GameDto buildGameDto(String title) {
        GameDto dto = new GameDto();
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        dto.setTitle(title);
        dto.setAge("18");
        dto.setAuthor(authorDto);
        dto.setCategory(categoryDto);
        return dto;
    }
}
