package com.ccsw.tutorial.game;

import com.ccsw.tutorial.author.AuthorService;
import com.ccsw.tutorial.category.CategoryService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.common.exception.BadRequestException;
import com.ccsw.tutorial.common.exception.ConflictException;
import com.ccsw.tutorial.common.exception.NotFoundException;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final LoanRepository loanRepository;

    public GameServiceImpl(GameRepository gameRepository, AuthorService authorService, CategoryService categoryService,
            LoanRepository loanRepository) {
        this.gameRepository = gameRepository;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.loanRepository = loanRepository;
    }

    @Override
    public List<Game> find(String title, Long idCategory) {
        String normalizedTitle = StringUtils.hasText(title) ? title.trim() : null;
        GameSpecification titleSpec = new GameSpecification(new SearchCriteria("title", ":", normalizedTitle));
        GameSpecification categorySpec = new GameSpecification(new SearchCriteria("category.id", ":", idCategory));
        Specification<Game> spec = Specification.where(titleSpec).and(categorySpec);
        return gameRepository.findAll(spec);
    }

    @Override
    public Game create(GameDto dto) {
        if (dto == null) {
            throw new BadRequestException("BAD_REQUEST", "El cuerpo de la petición es obligatorio.");
        }
        Game game = new Game();
        applyDto(game, dto);
        return gameRepository.save(game);
    }

    @Override
    public Game update(Long id, GameDto dto) {
        if (dto == null) {
            throw new BadRequestException("BAD_REQUEST", "El cuerpo de la petición es obligatorio.");
        }
        Game game = gameRepository.findById(id).orElseThrow(() -> new NotFoundException("GAME_NOT_FOUND", "No existe el juego indicado."));
        applyDto(game, dto);
        return gameRepository.save(game);
    }

    @Override
    public void delete(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new NotFoundException("GAME_NOT_FOUND", "No existe el juego indicado.");
        }
        if (loanRepository.existsByGame_Id(id)) {
            throw new ConflictException("GAME_HAS_LOANS", "El juego tiene préstamos asociados; no se puede eliminar.");
        }
        gameRepository.deleteById(id);
    }

    private void applyDto(Game game, GameDto dto) {
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new BadRequestException("BAD_REQUEST", "El título del juego es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getAge())) {
            throw new BadRequestException("BAD_REQUEST", "La edad del juego es obligatoria.");
        }
        if (dto.getAuthor() == null || dto.getAuthor().getId() == null) {
            throw new BadRequestException("BAD_REQUEST", "El autor del juego es obligatorio.");
        }
        if (dto.getCategory() == null || dto.getCategory().getId() == null) {
            throw new BadRequestException("BAD_REQUEST", "La categoría del juego es obligatoria.");
        }

        BeanUtils.copyProperties(dto, game, "id", "author", "category");

        var author = authorService.get(dto.getAuthor().getId());
        if (author == null) {
            throw new BadRequestException("AUTHOR_NOT_FOUND", "No existe el autor indicado.");
        }
        var category = categoryService.get(dto.getCategory().getId());
        if (category == null) {
            throw new BadRequestException("CATEGORY_NOT_FOUND", "No existe la categoría indicada.");
        }

        game.setAuthor(author);
        game.setCategory(category);
    }
}
