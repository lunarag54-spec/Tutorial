package com.ccsw.tutorial.game;

import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Game", description = "API de juegos")
@RequestMapping("/api/game")
@RestController
public class GameController {

    private final GameService gameService;
    private final ModelMapper mapper;

    public GameController(GameService gameService, ModelMapper mapper) {
        this.gameService = gameService;
        this.mapper = mapper;
    }

    @Operation(summary = "Buscar juegos", description = "Filtros opcionales por título y categoría")
    @GetMapping
    public List<GameDto> find(@RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "idCategory", required = false) Long idCategory) {
        List<Game> games = gameService.find(title, idCategory);
        return games.stream().map(e -> mapper.map(e, GameDto.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Crear juego")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameDto create(@RequestBody GameDto dto) {
        Game saved = gameService.create(dto);
        return mapper.map(saved, GameDto.class);
    }

    @Operation(summary = "Actualizar juego")
    @PutMapping("/{id}")
    public GameDto update(@PathVariable("id") Long id, @RequestBody GameDto dto) {
        Game saved = gameService.update(id, dto);
        return mapper.map(saved, GameDto.class);
    }

    @Operation(summary = "Eliminar juego")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        gameService.delete(id);
    }
}
