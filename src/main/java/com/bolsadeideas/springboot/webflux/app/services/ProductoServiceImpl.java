package com.bolsadeideas.springboot.webflux.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.webflux.app.model.dao.CategoriaDAO;
import com.bolsadeideas.springboot.webflux.app.model.dao.ProductoDAO;
import com.bolsadeideas.springboot.webflux.app.model.document.Categoria;
import com.bolsadeideas.springboot.webflux.app.model.document.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class ProductoServiceImpl implements ProductService{
	@Autowired
	private ProductoDAO productoDao;
	@Autowired
	private CategoriaDAO categoriaDAO;
	
	@Override
	public Flux<Producto> listar() {
		// TODO Auto-generated method stub
		return productoDao.findAll();
	}

	@Override
	public Mono<Producto> guardar(Producto producto) {
		// TODO Auto-generated method stub
		return productoDao.save(producto);
	}

	@Override
	public Mono<Producto> listarId(String id) {
		// TODO Auto-generated method stub
		return productoDao.findById(id);
	}

	@Override
	public Mono<Void> eliminar(Producto producto) {
		// TODO Auto-generated method stub
		return productoDao.delete(producto);
	}

	@Override
	public Flux<Producto> listarUpperCase() {
		// TODO Auto-generated method stub
		return productoDao.findAll().map(p ->{
			p.setNombre(p.getNombre().toUpperCase());
			return p;
		});
	}

	@Override
	public Flux<Producto> listarUpperCaseRepeat() {
		// TODO Auto-generated method stub
		return listarUpperCase().repeat(5000);
	}

	@Override
	public Mono<Producto> update(Producto producto) {
		// TODO Auto-generated method stub
		return productoDao.save(producto);
	}

	@Override
	public Mono<Categoria> guardarCategoria(Categoria categoria) {
		// TODO Auto-generated method stub
		return categoriaDAO.save(categoria);
	}

	@Override
	public Flux<Categoria> listarCategoria() {
		// TODO Auto-generated method stub
		return categoriaDAO.findAll();
	}

	@Override
	public Mono<Categoria> listarIdCategoria(String id) {
		// TODO Auto-generated method stub
		return categoriaDAO.findById(id);
	}

}
