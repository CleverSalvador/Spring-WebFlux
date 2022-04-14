package com.bolsadeideas.springboot.webflux.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.webflux.app.model.dao.ProductoDAO;
import com.bolsadeideas.springboot.webflux.app.model.document.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductoRestController.class);
	@Autowired
	private ProductoDAO dao;
	
	@GetMapping("/listar")
	public Flux<Producto> lista(){
		Flux<Producto> productosFlux = dao.findAll().map(p ->{
			p.setNombre(p.getNombre().toUpperCase());
			return p;
		}).doOnNext(e -> log.info(e.getNombre()));
		return productosFlux;
	}
	
	@GetMapping("/{id}")
	public Mono<Producto> buscar(@PathVariable String id){
		//Mono<Producto> producto = dao.findById(id);
		Flux<Producto> productos = dao.findAll();
		Mono<Producto> producto = productos.filter(p -> p.getId().equals(id)).next();
		return producto;
	}
	
}
