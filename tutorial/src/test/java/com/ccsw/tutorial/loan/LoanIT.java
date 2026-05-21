package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.api.ApiErrorResponse;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/api/prestamos";

    private static final Long GAME_1 = 1L;
    private static final Long GAME_2 = 2L;
    private static final Long GAME_3 = 3L;
    private static final Long CLIENT_1 = 1L;
    private static final Long CLIENT_2 = 2L;

    private static final LocalDate D1 = LocalDate.of(2026, 6, 1);
    private static final LocalDate D5 = LocalDate.of(2026, 6, 5);
    private static final LocalDate D3 = LocalDate.of(2026, 6, 3);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ParameterizedTypeReference<ApiErrorResponse> errorType = new ParameterizedTypeReference<ApiErrorResponse>() {
    };

    private String baseUrl() {
        return LOCALHOST + port + SERVICE_PATH;
    }

    private static LoanRequestDto loan(Long gameId, Long clientId, LocalDate start, LocalDate end) {
        LoanRequestDto dto = new LoanRequestDto();
        dto.setGameId(gameId);
        dto.setClientId(clientId);
        dto.setStartDate(start);
        dto.setEndDate(end);
        return dto;
    }

    private JsonNode getPageJson(String url) {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        return response.getBody();
    }

    @Test
    public void findWithoutLoansShouldReturnEmptyPage() {
        JsonNode body = getPageJson(baseUrl());
        assertEquals(0, body.get("totalElements").asLong());
        assertTrue(body.get("content").isEmpty());
    }

    @Test
    public void postCreateThenListContainsLoan() {
        ResponseEntity<LoanDto> created = restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D5), LoanDto.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().getId());
        assertEquals(GAME_1, created.getBody().getGame().getId());
        assertEquals(CLIENT_1, created.getBody().getClient().getId());
        assertEquals(D1, created.getBody().getStartDate());
        assertEquals(D5, created.getBody().getEndDate());

        JsonNode list = getPageJson(baseUrl());
        assertEquals(1, list.get("totalElements").asLong());
    }

    @Test
    public void postWithSpanishJsonKeysReturnsSpanishResponse() {
        ResponseEntity<JsonNode> created = restTemplate.postForEntity(baseUrl(),
                Map.of("idJuego", GAME_1, "idCliente", CLIENT_1, "fechaInicio", "2026-06-01", "fechaFin", "2026-06-05"), JsonNode.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody().get("juego"));
        assertNotNull(created.getBody().get("cliente"));
        assertEquals("2026-06-01", created.getBody().get("fechaInicio").asText());
    }

    @Test
    public void findWithIdJuegoIdClienteAndFechaBusquedaFilters() {
        restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D5), LoanDto.class);
        restTemplate.postForEntity(baseUrl(), loan(GAME_2, CLIENT_1, D1, D5), LoanDto.class);

        String byGame = UriComponentsBuilder.fromHttpUrl(baseUrl()).queryParam("idJuego", GAME_1).toUriString();
        assertEquals(1, getPageJson(byGame).get("totalElements").asLong());

        String byClient = UriComponentsBuilder.fromHttpUrl(baseUrl()).queryParam("idCliente", CLIENT_1).toUriString();
        assertEquals(2, getPageJson(byClient).get("totalElements").asLong());

        String byDate = UriComponentsBuilder.fromHttpUrl(baseUrl()).queryParam("fechaBusqueda", D3).toUriString();
        assertEquals(2, getPageJson(byDate).get("totalElements").asLong());

        String byDateOutside = UriComponentsBuilder.fromHttpUrl(baseUrl()).queryParam("fechaBusqueda", LocalDate.of(2026, 5, 31)).toUriString();
        assertEquals(0, getPageJson(byDateOutside).get("totalElements").asLong());
    }

    @Test
    public void paginationShouldRespectPageAndSize() {
        restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D1), LoanDto.class);
        restTemplate.postForEntity(baseUrl(), loan(GAME_2, CLIENT_1, D1, D1), LoanDto.class);

        String paged = UriComponentsBuilder.fromHttpUrl(baseUrl()).queryParam("page", 0).queryParam("size", 1).toUriString();
        JsonNode r = getPageJson(paged);
        assertEquals(2, r.get("totalElements").asLong());
        assertEquals(1, r.get("content").size());
    }

    @Test
    public void paginationWithInvalidSizeShouldUseDefault() {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl()).queryParam("page", 0).queryParam("size", 0).toUriString();
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void postWithoutRequiredFieldsShouldReturnBadRequest() {
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(baseUrl(), "{}", ApiErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void overlappingGameSameClientShouldReturnBadRequest() {
        restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D5), LoanDto.class);

        LoanRequestDto overlap = loan(GAME_1, CLIENT_1, D3, LocalDate.of(2026, 6, 10));
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(baseUrl(), overlap, ApiErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("LOAN_GAME_NOT_AVAILABLE", response.getBody().getCode());
    }

    @Test
    public void overlappingGameDifferentClientsShouldReturnBadRequest() {
        restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D5), LoanDto.class);

        LoanRequestDto otherClient = loan(GAME_1, CLIENT_2, D3, LocalDate.of(2026, 6, 10));
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(baseUrl(), otherClient, ApiErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("LOAN_GAME_NOT_AVAILABLE", response.getBody().getCode());
    }

    @Test
    public void putOverlappingGameShouldReturnBadRequest() {
        ResponseEntity<LoanDto> first = restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D5), LoanDto.class);
        restTemplate.postForEntity(baseUrl(), loan(GAME_2, CLIENT_2, D1, D5), LoanDto.class);

        LoanRequestDto update = loan(GAME_2, CLIENT_1, D3, D5);
        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(baseUrl() + "/" + first.getBody().getId(), HttpMethod.PUT,
                new HttpEntity<>(update), errorType);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("LOAN_GAME_NOT_AVAILABLE", response.getBody().getCode());
    }

    @Test
    public void clientWithTwoLoansSameDaysThirdLoanShouldReturnBadRequest() {
        restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D5), LoanDto.class);
        restTemplate.postForEntity(baseUrl(), loan(GAME_2, CLIENT_1, D1, D5), LoanDto.class);

        LoanRequestDto third = loan(GAME_3, CLIENT_1, D1, D5);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(baseUrl(), third, ApiErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("LOAN_CLIENT_MAX_ACTIVE_GAMES", response.getBody().getCode());
    }

    @Test
    public void endBeforeStartShouldReturnBadRequest() {
        LoanRequestDto dto = loan(GAME_1, CLIENT_1, D5, D1);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(baseUrl(), dto, ApiErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("LOAN_DATES_INCONSISTENT", response.getBody().getCode());
    }

    @Test
    public void durationMoreThan14DaysShouldReturnBadRequest() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 15);
        LoanRequestDto dto = loan(GAME_1, CLIENT_1, start, end);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(baseUrl(), dto, ApiErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("LOAN_DURATION_EXCEEDS_MAX", response.getBody().getCode());
    }

    @Test
    public void unknownGameShouldReturnBadRequest() {
        LoanRequestDto dto = loan(0L, CLIENT_1, D1, D5);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(baseUrl(), dto, ApiErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("LOAN_GAME_NOT_FOUND", response.getBody().getCode());
    }

    @Test
    public void unknownClientShouldReturnBadRequest() {
        LoanRequestDto dto = loan(GAME_1, 99L, D1, D5);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(baseUrl(), dto, ApiErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("LOAN_CLIENT_NOT_FOUND", response.getBody().getCode());
    }

    @Test
    public void deleteExistingThenDeleteAgainNotFound() {
        ResponseEntity<LoanDto> created = restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D5), LoanDto.class);
        Long id = created.getBody().getId();

        ResponseEntity<Void> del = restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, del.getStatusCode());

        ResponseEntity<ApiErrorResponse> delAgain = restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.DELETE, null, errorType);
        assertEquals(HttpStatus.NOT_FOUND, delAgain.getStatusCode());
        assertEquals("LOAN_NOT_FOUND", delAgain.getBody().getCode());
    }

    @Test
    public void putWithUnknownIdShouldReturnNotFound() {
        LoanRequestDto dto = loan(GAME_1, CLIENT_1, D1, D5);
        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(baseUrl() + "/9999", HttpMethod.PUT, new HttpEntity<>(dto), errorType);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("LOAN_NOT_FOUND", response.getBody().getCode());
    }

    @Test
    public void putUpdateReturnsLoanWithSameId() {
        ResponseEntity<LoanDto> created = restTemplate.postForEntity(baseUrl(), loan(GAME_1, CLIENT_1, D1, D5), LoanDto.class);
        Long id = created.getBody().getId();

        LoanRequestDto update = loan(GAME_2, CLIENT_1, D1, D5);
        ResponseEntity<LoanDto> put = restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.PUT, new HttpEntity<>(update),
                new ParameterizedTypeReference<LoanDto>() {
                });
        assertEquals(HttpStatus.OK, put.getStatusCode());
        assertEquals(id, put.getBody().getId());
        assertEquals(GAME_2, put.getBody().getGame().getId());
    }
}
