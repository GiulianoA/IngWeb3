package ar.edu.iua.ingweb3.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.ingweb3.business.BusinessException;
import ar.edu.iua.ingweb3.business.IProductoBusiness;
import ar.edu.iua.ingweb3.model.Producto;
import ar.edu.iua.ingweb3.model.exception.NotFoundException;

@RestController
@RequestMapping(Constantes.URL_PRODUCTOS)
public class ProductosRESTController {

	@Autowired
	private IProductoBusiness productoBusiness;

	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Producto>> lista(
			@RequestParam(required = false, value = "q", defaultValue = "*") String q,
			@RequestParam(required = false, value = "precio_desde", defaultValue = "-1") double precioDesde,
			@RequestParam(required = false, value = "precio_hasta", defaultValue = "-1") double precioHasta) {

		try {
			if (precioDesde != -1 && precioHasta != -1 && precioDesde <= precioHasta) {
				return new ResponseEntity<List<Producto>>(productoBusiness.searchByPrecios(precioDesde, precioHasta),
						HttpStatus.OK);
			} else if (q.equals("*") || q.trim().length() == 0) {
				return new ResponseEntity<List<Producto>>(productoBusiness.getAll(), HttpStatus.OK);
			} else {
				return new ResponseEntity<List<Producto>>(productoBusiness.search(q), HttpStatus.OK);
			}
		} catch (BusinessException e) {
			return new ResponseEntity<List<Producto>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Producto> uno(@PathVariable("id") int id) {
		try {
			return new ResponseEntity<Producto>(productoBusiness.getOne(id), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<Producto>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<Producto>(HttpStatus.NOT_FOUND);
		}

	}

	@RequestMapping(value = { "", "/" }, method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody Producto producto) {
		try {
			productoBusiness.add(producto);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", "/productos/" + producto.getId());
			return new ResponseEntity<Object>(responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = { "", "/" }, method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody Producto producto) {
		try {
			productoBusiness.update(producto);
			return new ResponseEntity<Object>(HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}

	}

	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<Producto> delete(@PathVariable("id") int id) {
		try {
			Producto p = new Producto();
			p.setId(id);
			productoBusiness.delete(p);
			return new ResponseEntity<Producto>(HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<Producto>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<Producto>(HttpStatus.NOT_FOUND);
		}

	}

}
