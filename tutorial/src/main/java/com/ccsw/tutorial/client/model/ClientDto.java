package com.ccsw.tutorial.client.model;

import jakarta.validation.constraints.NotBlank;


public class ClientDto {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    
    public Long getId() {

        return this.id;
    }

    
    public void setId(Long id) {

        this.id = id;
    }

    
    public String getName() {

        return this.name;
    }

    
    public void setName(String name) {

        this.name = name;
    }
}
