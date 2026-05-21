package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.author.model.AuthorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {

    Author get(Long id);

    Page<Author> findPage(Pageable pageable);

    Author create(AuthorDto dto);

    Author update(Long id, AuthorDto dto);

    void delete(Long id);

    List<Author> findAll();
}
