package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.common.pagination.PaginationConstraints;
import com.ccsw.tutorial.config.ResponsePage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Author", description = "API de autores")
@RestController
@RequestMapping("/api/author")
public class AuthorController {

    private final AuthorService authorService;
    private final ModelMapper modelMapper;

    public AuthorController(AuthorService authorService, ModelMapper modelMapper) {
        this.authorService = authorService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Listar todos los autores")
    @GetMapping("/all")
    public List<AuthorDto> findAll() {
        return authorService.findAll().stream().map(e -> modelMapper.map(e, AuthorDto.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Listado paginado de autores")
    @GetMapping
    public ResponsePage<AuthorDto> findPage(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Page<Author> pageResult = authorService.findPage(PaginationConstraints.normalizedPage(page, size));
        return new ResponsePage<>(pageResult.map(e -> modelMapper.map(e, AuthorDto.class)));
    }

    @Operation(summary = "Crear autor")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto create(@Valid @RequestBody AuthorDto dto) {
        Author saved = authorService.create(dto);
        return modelMapper.map(saved, AuthorDto.class);
    }

    @Operation(summary = "Actualizar autor")
    @PutMapping("/{id}")
    public AuthorDto update(@PathVariable("id") Long id, @Valid @RequestBody AuthorDto dto) {
        Author saved = authorService.update(id, dto);
        return modelMapper.map(saved, AuthorDto.class);
    }

    @Operation(summary = "Eliminar autor")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        authorService.delete(id);
    }
}
