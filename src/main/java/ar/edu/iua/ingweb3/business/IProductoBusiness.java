package ar.edu.iua.ingweb3.business;

import java.util.List;

import ar.edu.iua.ingweb3.model.Producto;
import ar.edu.iua.ingweb3.model.exception.NotFoundException;

public interface IProductoBusiness {

	public List<Producto> getAll() throws BusinessException;
	public List<Producto> search(String part) throws BusinessException;
	public List<Producto> searchByPrecios(double precioDesde, double precioHasta) throws BusinessException;
	public Producto getOne(int id) throws BusinessException, NotFoundException;
	public Producto add(Producto producto) throws BusinessException;
	public Producto update(Producto producto) throws BusinessException, NotFoundException;
	public void delete(Producto producto) throws BusinessException, NotFoundException;

}
