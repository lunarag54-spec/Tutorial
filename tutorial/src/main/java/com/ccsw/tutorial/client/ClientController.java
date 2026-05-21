package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Client", description = "API de clientes")
@RequestMapping("/api/client")
@RestController
public class ClientController {

    private final ClientService clientService;
    private final ModelMapper mapper;

    public ClientController(ClientService clientService, ModelMapper mapper) {
        this.clientService = clientService;
        this.mapper = mapper;
    }

    @Operation(summary = "Listar clientes")
    @GetMapping
    public List<ClientDto> findAll() {
        return clientService.findAll().stream().map(e -> mapper.map(e, ClientDto.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Crear cliente")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto create(@Valid @RequestBody ClientDto dto) {
        Client saved = clientService.create(dto);
        return mapper.map(saved, ClientDto.class);
    }

    @Operation(summary = "Actualizar cliente")
    @PutMapping("/{id}")
    public ClientDto update(@PathVariable("id") Long id, @Valid @RequestBody ClientDto dto) {
        Client saved = clientService.update(id, dto);
        return mapper.map(saved, ClientDto.class);
    }

    @Operation(summary = "Eliminar cliente")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        clientService.delete(id);
    }
}
