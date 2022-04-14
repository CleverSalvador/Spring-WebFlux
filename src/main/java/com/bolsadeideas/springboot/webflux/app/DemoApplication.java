package com.bolsadeideas.springboot.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;


import com.bolsadeideas.springboot.webflux.app.model.document.Categoria;
import com.bolsadeideas.springboot.webflux.app.model.document.Producto;
import com.bolsadeideas.springboot.webflux.app.services.ProductService;
import com.bolsadeideas.springboot.webflux.app.services.ProductoServiceImpl;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner{

	
	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);
	public static void main(String[] args) {SpringApplication.run(DemoApplication.class, args);	}
	@Autowired
	private ReactiveMongoTemplate rmt;
	@Autowired
	private ProductService service;
	
	@Override
	public void run(String... args) throws Exception {
		rmt.dropCollection("producto").subscribe();
		rmt.dropCollection("categorias").subscribe();
		
		Categoria electronica = new Categoria("Electronica");
		Categoria deporte = new Categoria("Deporte");
		Categoria mueble = new Categoria("Mueble");
		Categoria computacion = new Categoria("Computacion");
		
		
		Flux.just(electronica,deporte,mueble,computacion)
		.flatMap(c -> service.guardarCategoria(c))
		.doOnNext(c -> log.info("Categoria creada :" + c.getNombre()))
			.thenMany(Flux.just(
					new Producto("tv" , 123.23,electronica),
					new Producto("Mouse",456.23,computacion),
					new Producto("Laptop",456.23,computacion),
					new Producto("Bicicleta",456.23,deporte),
					new Producto("Cajon de madera",456.23,mueble),
					new Producto("Computadora",456.23,computacion),
					new Producto("Circuito xp",456.23,electronica),
					new Producto("Motor gpr", 1300.00,electronica))
			.flatMap(producto ->{
				producto.setFecha(new Date());
				return service.guardar(producto);
			}))			
		.subscribe(e -> log.info("Insert : " + e.getNombre() + " " + e.getPrecio()));
	}

}
