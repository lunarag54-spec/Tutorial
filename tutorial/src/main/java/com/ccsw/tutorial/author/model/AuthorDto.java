package com.ccsw.tutorial.author.model;

import jakarta.validation.constraints.NotBlank;

public class AuthorDto {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String nationality;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
