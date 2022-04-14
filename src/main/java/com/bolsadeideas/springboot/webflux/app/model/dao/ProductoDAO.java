package com.bolsadeideas.springboot.webflux.app.model.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bolsadeideas.springboot.webflux.app.model.document.Producto;

public interface ProductoDAO extends ReactiveMongoRepository<Producto, String>{
	/*podemos crear otros metodos*/

}
