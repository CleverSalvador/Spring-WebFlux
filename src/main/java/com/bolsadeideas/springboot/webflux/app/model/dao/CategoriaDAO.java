package com.bolsadeideas.springboot.webflux.app.model.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bolsadeideas.springboot.webflux.app.model.document.Categoria;

public interface CategoriaDAO extends ReactiveMongoRepository<Categoria, String>{

}
