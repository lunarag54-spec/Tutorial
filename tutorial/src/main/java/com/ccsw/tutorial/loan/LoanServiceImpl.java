package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.common.exception.BadRequestException;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.loan.exception.LoanNotFoundException;
import com.ccsw.tutorial.loan.exception.LoanValidationErrorCode;
import com.ccsw.tutorial.loan.exception.LoanValidationException;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private static final int MAX_INCLUSIVE_CALENDAR_DAYS = 14;

    private final LoanRepository loanRepository;
    private final GameRepository gameRepository;
    private final ClientRepository clientRepository;

    public LoanServiceImpl(LoanRepository loanRepository, GameRepository gameRepository, ClientRepository clientRepository) {
        this.loanRepository = loanRepository;
        this.gameRepository = gameRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Page<Loan> find(Long gameId, Long clientId, LocalDate date, Pageable pageable) {
        return loanRepository.findAll(LoanSpecification.withFilters(gameId, clientId, date), pageable);
    }

    @Override
    public Loan create(LoanRequestDto dto) {
        validateRequired(dto);
        validateBusinessRules(dto, null);

        Loan loan = new Loan();
        applyDto(loan, dto);
        Loan saved = loanRepository.save(loan);
        return loanRepository.findById(saved.getId()).orElseThrow(() -> new LoanNotFoundException(saved.getId()));
    }

    @Override
    public Loan update(Long id, LoanRequestDto dto) {
        validateRequired(dto);
        if (!loanRepository.existsById(id)) {
            throw new LoanNotFoundException(id);
        }
        validateBusinessRules(dto, id);

        Loan loan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException(id));
        applyDto(loan, dto);
        loanRepository.save(loan);
        return loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException(id));
    }

    private void applyDto(Loan loan, LoanRequestDto dto) {
        loan.setGame(gameRepository.findById(dto.getGameId()).orElseThrow(
                () -> new LoanValidationException(LoanValidationErrorCode.LOAN_GAME_NOT_FOUND, "No existe el juego indicado.")));
        loan.setClient(clientRepository.findById(dto.getClientId()).orElseThrow(
                () -> new LoanValidationException(LoanValidationErrorCode.LOAN_CLIENT_NOT_FOUND, "No existe el cliente indicado.")));
        loan.setStartDate(dto.getStartDate());
        loan.setEndDate(dto.getEndDate());
    }

    @Override
    public void delete(Long id) {
        if (!loanRepository.existsById(id)) {
            throw new LoanNotFoundException(id);
        }
        loanRepository.deleteById(id);
    }

    private void validateRequired(LoanRequestDto dto) {
        if (dto == null) {
            throw new BadRequestException("LOAN_REQUIRED_FIELDS",
                    "Los campos idJuego (o gameId), idCliente (o clientId), fechaInicio (o startDate) y fechaFin (o endDate) son obligatorios.");
        }
        if (dto.getGameId() == null || dto.getClientId() == null || dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new LoanValidationException(LoanValidationErrorCode.LOAN_REQUIRED_FIELDS,
                    "Los campos idJuego (o gameId), idCliente (o clientId), fechaInicio (o startDate) y fechaFin (o endDate) son obligatorios.");
        }
    }

    private void validateBusinessRules(LoanRequestDto dto, Long excludeLoanId) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new LoanValidationException(LoanValidationErrorCode.LOAN_DATES_INCONSISTENT,
                    "La fecha de fin debe ser posterior o igual a la fecha de inicio.");
        }

        long inclusiveDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (inclusiveDays > MAX_INCLUSIVE_CALENDAR_DAYS) {
            throw new LoanValidationException(LoanValidationErrorCode.LOAN_DURATION_EXCEEDS_MAX,
                    "La duración del préstamo no puede superar 14 días naturales (contando inicio y fin).");
        }

        gameRepository.findByIdForUpdate(dto.getGameId()).orElseThrow(
                () -> new LoanValidationException(LoanValidationErrorCode.LOAN_GAME_NOT_FOUND, "No existe el juego indicado."));

        if (!clientRepository.existsById(dto.getClientId())) {
            throw new LoanValidationException(LoanValidationErrorCode.LOAN_CLIENT_NOT_FOUND, "No existe el cliente indicado.");
        }

        if (loanRepository.existsOverlappingLoanForGame(dto.getGameId(), dto.getStartDate(), dto.getEndDate(), excludeLoanId)) {
            throw new LoanValidationException(LoanValidationErrorCode.LOAN_GAME_NOT_AVAILABLE,
                    "El juego ya tiene otro préstamo activo que solapa con las fechas solicitadas.");
        }

        assertClientHasAtMostTwoLoansPerDay(dto.getClientId(), dto.getStartDate(), dto.getEndDate(), excludeLoanId);
    }

    
    private void assertClientHasAtMostTwoLoansPerDay(Long clientId, LocalDate start, LocalDate end, Long excludeLoanId) {
        List<Loan> overlapping = loanRepository.findOverlappingForClient(clientId, start, end, excludeLoanId);
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            final LocalDate d = day;
            long concurrent = overlapping.stream().filter(l -> covers(l, d)).count();
            if (concurrent >= 2) {
                throw new LoanValidationException(LoanValidationErrorCode.LOAN_CLIENT_MAX_ACTIVE_GAMES,
                        "El cliente ya tiene el máximo de 2 juegos prestados en una o más fechas del periodo solicitado.");
            }
        }
    }

    private static boolean covers(Loan loan, LocalDate day) {
        return !loan.getStartDate().isAfter(day) && !loan.getEndDate().isBefore(day);
    }
}
