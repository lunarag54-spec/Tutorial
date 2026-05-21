package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.common.exception.BadRequestException;
import com.ccsw.tutorial.common.exception.ConflictException;
import com.ccsw.tutorial.common.exception.NotFoundException;
import com.ccsw.tutorial.game.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final GameRepository gameRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository, GameRepository gameRepository) {
        this.authorRepository = authorRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public Author get(Long id) {
        return authorRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Author> findPage(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    @Override
    public Author create(AuthorDto dto) {
        if (dto == null) {
            throw new BadRequestException("BAD_REQUEST", "El cuerpo de la petición es obligatorio.");
        }
        Author author = new Author();
        BeanUtils.copyProperties(dto, author, "id");
        return authorRepository.save(author);
    }

    @Override
    public Author update(Long id, AuthorDto dto) {
        if (dto == null) {
            throw new BadRequestException("BAD_REQUEST", "El cuerpo de la petición es obligatorio.");
        }
        Author author = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("AUTHOR_NOT_FOUND", "No existe el autor indicado."));
        BeanUtils.copyProperties(dto, author, "id");
        return authorRepository.save(author);
    }

    @Override
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new NotFoundException("AUTHOR_NOT_FOUND", "No existe el autor indicado.");
        }
        if (gameRepository.countByAuthor_Id(id) > 0) {
            throw new ConflictException("AUTHOR_IN_USE", "Existen juegos asociados a este autor; no se puede eliminar.");
        }
        authorRepository.deleteById(id);
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }
}
