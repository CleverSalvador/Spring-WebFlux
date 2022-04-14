package com.bolsadeideas.springboot.webflux.app.services;

import com.bolsadeideas.springboot.webflux.app.model.document.Categoria;
import com.bolsadeideas.springboot.webflux.app.model.document.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
	
	public Flux<Producto> listar();
	public Mono<Producto> guardar(Producto producto);
	public Mono<Producto> update(Producto producto);
	public Mono<Producto> listarId(String id);
	public Mono<Void> eliminar(Producto producto);
	public Flux<Producto> listarUpperCase();
	public Flux<Producto> listarUpperCaseRepeat();
	public Mono<Categoria> guardarCategoria(Categoria categoria);
	public Flux<Categoria> listarCategoria();
	public Mono<Categoria> listarIdCategoria(String id);
}
