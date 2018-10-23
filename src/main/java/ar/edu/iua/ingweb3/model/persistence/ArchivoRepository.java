package ar.edu.iua.ingweb3.model.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.iua.ingweb3.model.Archivo;

public interface ArchivoRepository extends JpaRepository<Archivo, Integer> {
	
	public List<Archivo> findByNombreContaining(String nombre);
	
	public List<Archivo> findByMime(String mime);
	
}