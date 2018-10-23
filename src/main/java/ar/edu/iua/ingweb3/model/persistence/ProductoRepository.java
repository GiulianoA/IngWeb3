package ar.edu.iua.ingweb3.model.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.ingweb3.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
	

	public List<Producto> findByDescripcionContaining(String descripcion);
	public List<Producto> findByPrecioBetween(double precioDesde, double precioHasta);
}
