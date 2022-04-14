package com.bolsadeideas.springboot.webflux.app.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.bolsadeideas.springboot.webflux.app.model.dao.ProductoDAO;
import com.bolsadeideas.springboot.webflux.app.model.document.Categoria;
import com.bolsadeideas.springboot.webflux.app.model.document.Producto;
import com.bolsadeideas.springboot.webflux.app.services.ProductService;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@SessionAttributes(names = "producto")
@Controller
public class ProductoController {
	private static final Logger log =  LoggerFactory.getLogger(ProductoController.class);
	
	/*@Autowired
	private ProductoDAO dao;*/
	@Autowired
	private ProductService productoservice;
	
	@Value("${config.uploads.path}")
	private String path;
	
	
	@GetMapping({"/lista" , "/"})
	public String lista(Model model) {
		
		Flux<Producto> productos = productoservice.listar();
		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "listado de Productos");
		return "lista";
	}
	
	@GetMapping("/form")
	public Mono<String> crear(Model model){
		model.addAttribute("producto", new Producto());
		model.addAttribute("titulo", "Formulario de Producto");
		model.addAttribute("tituloboton", "Guardar");
		return Mono.just("/form");
	}
	
	@PostMapping("/form")
	public 	Mono<String> guardar(@Valid Producto producto, BindingResult bindingResult, Model model, @RequestPart FilePart file, SessionStatus sessionStatus){
		if(bindingResult.hasErrors()) {
			model.addAttribute("titulo", "Error en el Formulario de Producto");
			model.addAttribute("tituloboton", "Guardar");
			return Mono.just("/form");
		}else {
		sessionStatus.setComplete();
		Mono<Categoria> categoria = productoservice.listarIdCategoria(producto.getCategoria().getId());		
				return categoria.flatMap(c -> {/*recibimos la categoria*/
					if(producto.getFecha() == null) {
						producto.setFecha(new Date());
					}
					if(!file.filename().isEmpty()) {
						producto.setFoto(UUID.randomUUID().toString() +"-"+ file.filename()
						.replace(" ", "")
						.replace(":", "")
						.replace("\\", "")
						);
					}
					producto.setCategoria(c);/*enviamos la categoria recibida al produto*/
					return productoservice.guardar(producto);/*guardamos el flujo*/
				})
				.flatMap(p -> {
					if(!file.filename().isEmpty()) {
						return file.transferTo(new File(path + p.getFoto()));
					}
					return Mono.empty();
				})
				.thenReturn("redirect:/lista?success=producto+guardado+con+exito");
		}
	}
	
	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id,Model model){
		Mono<Producto> productoMono = productoservice.listarId(id).defaultIfEmpty(new Producto());/*si el usuario cambia otro id en la url al momento de editar */
		model.addAttribute("titulo","Edicion de Producto");
		model.addAttribute("producto", productoMono);
		model.addAttribute("tituloboton", "Editar");
		return Mono.just("/form");
	}
	
	@GetMapping("eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id){
		return productoservice.listarId(id)/*buscamos el id*/
				.defaultIfEmpty(new Producto()) /*si en listarid es vacio le pasamos un nuevo producto*/
				.flatMap(p ->{/*consultamos si el id es null*/
					if(p.getId()==null) {
						return Mono.error(new InterruptedException("No existe el producto a eliminar"));
					}
					return Mono.just(p);/*retorna un flujo producto no un objeto*/
				})
				.flatMap(p ->{	/*transformamos este mono en un objeto*/
				log.info("Eliminando producto : " + p.getNombre()); 
			return productoservice.eliminar(p);
				})
			.thenReturn("redirect:/lista?success=producto+eliminado+con+exito")
					.onErrorResume(ex -> Mono.just("redirect:/lista?error=no+existe+el+producto+a+eliminar"));
	}
	@ModelAttribute("categorias")
	public Flux<Categoria> catMono(){
		return productoservice.listarCategoria();
	}
	
	@GetMapping("/ver/{id}")
	public Mono<String> detalle(Model model, @PathVariable String id){
		return productoservice.listarId(id)/*busqueda del producto por el id*/
				.doOnNext(p -> {/*añadiendo al model el producto encontrado*/
					model.addAttribute("producto",p);
					model.addAttribute("titulo","Detalle del Producto");
				}).defaultIfEmpty(new Producto())/*en caso de no haber el producto asigna una instancia del producto*/
				.flatMap(p ->{/*verificamos que el producto exista */
					if(p.getId()== null) {
						return Mono.error(new InterruptedException("No existe el producto"));
					}
					return Mono.just(p);
				})
				.thenReturn("ver")/*retorna el string*/
				.onErrorResume(ex -> Mono.just("redirect:/lista?error=no+existe+el+producto"));
	}
	
	@GetMapping("uploads/img/{nombreFoto:.+}")
	public Mono<ResponseEntity<Resource>> verFoto(@PathVariable String nombreFoto) throws MalformedURLException{
		Path ruta = Paths.get(path).resolve(nombreFoto).toAbsolutePath();
		
		Resource imagen  = new UrlResource(ruta.toUri());
		
		return Mono.just(
				ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imagen.getFilename() + "\"")
				.body(imagen)
				);
	}
	
	/******************************************************************************************************/
	@GetMapping("/lista-datadriver")
	public String listaDataDriver(Model model) {
		
		Flux<Producto> productos = productoservice.listarUpperCase().delayElements(Duration.ofSeconds(1));
		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));
		model.addAttribute("titulo", "listado de Productos");
		return "lista";
	}
	@GetMapping("/listafull")
	public String listafull(Model model) {
		
		Flux<Producto> productos = productoservice.listarUpperCaseRepeat();
		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "listado de Productos");
		return "lista";
	}
	
	@GetMapping("/lista-chunked")
	public String listachunked(Model model) {
		Flux<Producto> productos = productoservice.listarUpperCaseRepeat();
		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "listado de Productos");
		return "lista-chunked";
	}
}
