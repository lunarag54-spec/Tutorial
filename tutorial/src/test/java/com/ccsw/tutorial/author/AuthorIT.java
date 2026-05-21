package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.common.api.ApiErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthorIT {

    private static final String AUTHOR_PATH = "/api/author";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + AUTHOR_PATH;
    }

    @Test
    public void findPageShouldReturnPagedAuthors() {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl()).queryParam("page", 0).queryParam("size", 2).toUriString();
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(6, response.getBody().get("totalElements").asLong());
        assertEquals(2, response.getBody().get("content").size());
    }

    @Test
    public void createAuthorShouldReturnCreated() {
        AuthorDto dto = new AuthorDto();
        dto.setName("Nuevo Autor");
        dto.setNationality("ES");

        ResponseEntity<AuthorDto> response = restTemplate.postForEntity(baseUrl(), dto, AuthorDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    public void deleteAuthorWithGamesShouldReturnConflict() {
        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(baseUrl() + "/1", HttpMethod.DELETE, null,
                new ParameterizedTypeReference<ApiErrorResponse>() {
                });
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("AUTHOR_IN_USE", response.getBody().getCode());
    }

    @Test
    public void findAllEndpointShouldReturnAuthors() {
        ResponseEntity<List<AuthorDto>> response = restTemplate.exchange(baseUrl() + "/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<AuthorDto>>() {
                });
        assertEquals(6, response.getBody().size());
    }
}
