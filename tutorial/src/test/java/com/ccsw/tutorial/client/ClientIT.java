package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.api.ApiErrorResponse;
import com.ccsw.tutorial.loan.model.LoanRequestDto;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClientIT {

    private static final String CLIENT_PATH = "/api/client";
    private static final String LOAN_PATH = "/api/prestamos";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String clientUrl() {
        return "http://localhost:" + port + CLIENT_PATH;
    }

    private String loanUrl() {
        return "http://localhost:" + port + LOAN_PATH;
    }

    @Test
    public void deleteClientWithLoansShouldReturnConflict() {
        LoanRequestDto loan = new LoanRequestDto();
        loan.setGameId(1L);
        loan.setClientId(1L);
        loan.setStartDate(LocalDate.of(2026, 7, 1));
        loan.setEndDate(LocalDate.of(2026, 7, 5));
        restTemplate.postForEntity(loanUrl(), loan, Void.class);

        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(clientUrl() + "/1", HttpMethod.DELETE, null,
                new ParameterizedTypeReference<ApiErrorResponse>() {
                });

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CLIENT_HAS_LOANS", response.getBody().getCode());
    }

    @Test
    public void createClientShouldReturnCreated() {
        ClientDto dto = new ClientDto();
        dto.setName("Cliente Test");

        ResponseEntity<ClientDto> response = restTemplate.postForEntity(clientUrl(), dto, ClientDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ResponseEntity<List<ClientDto>> list = restTemplate.exchange(clientUrl(), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ClientDto>>() {
                });
        assertEquals(4, list.getBody().size());
    }
}
