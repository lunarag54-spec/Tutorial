package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.pagination.PaginationConstraints;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Tag(name = "Préstamos", description = "API de préstamos de juegos (ciclo de vida y reglas de negocio)")
@RequestMapping("/api/prestamos")
@RestController
public class PrestamoController {

    private final LoanService loanService;
    private final ModelMapper mapper;

    public PrestamoController(LoanService loanService, ModelMapper mapper) {
        this.loanService = loanService;
        this.mapper = mapper;
    }

    @Operation(summary = "Listar préstamos", description = "Paginación (page, size) y filtros opcionales idJuego, idCliente, fechaBusqueda (inclusive entre inicio y fin)")
    @GetMapping
    public ResponsePage<LoanDto> listar(@RequestParam(value = "idJuego", required = false) Long idJuego,
            @RequestParam(value = "idCliente", required = false) Long idCliente,
            @RequestParam(value = "fechaBusqueda", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaBusqueda,
            @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<Loan> pageResult = loanService.find(idJuego, idCliente, fechaBusqueda, PaginationConstraints.normalizedPage(page, size));
        return new ResponsePage<>(
                pageResult.getContent().stream().map(e -> mapper.map(e, LoanDto.class)).collect(Collectors.toList()),
                pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Operation(summary = "Crear préstamo", description = "Valida reglas de negocio y devuelve el préstamo creado con ID")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto crear(@Valid @RequestBody LoanRequestDto dto) {
        Loan saved = loanService.create(dto);
        return mapper.map(saved, LoanDto.class);
    }

    @Operation(summary = "Actualizar préstamo", description = "Mismas validaciones que la creación")
    @PutMapping("/{id}")
    public LoanDto actualizar(@PathVariable("id") Long id, @Valid @RequestBody LoanRequestDto dto) {
        Loan saved = loanService.update(id, dto);
        return mapper.map(saved, LoanDto.class);
    }

    @Operation(summary = "Eliminar préstamo", description = "Borra por identificador")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") Long id) {
        loanService.delete(id);
    }
}
