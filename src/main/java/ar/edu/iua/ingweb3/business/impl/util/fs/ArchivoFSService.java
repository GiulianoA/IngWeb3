package ar.edu.iua.ingweb3.business.impl.util.fs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.iua.ingweb3.model.Archivo;
import ar.edu.iua.ingweb3.web.services.Constantes;

@Service
public class ArchivoFSService {

	private final Path localizacionAlmacenamiento;

	@Autowired
	public ArchivoFSService(ArchivoFSProperties prop) {

		this.localizacionAlmacenamiento = Paths.get(prop.getDirectorioAlmacenamiento()).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.localizacionAlmacenamiento);
		} catch (IOException e) {
			throw new ArchivoFSException(
					"No se ha podido crear el directorio para almacenar archivos: " + this.localizacionAlmacenamiento,
					e);
		}
	}

	public String almacenarArchivo(MultipartFile file) throws ArchivoFSException {
		String nombreArchivo = StringUtils.cleanPath(file.getOriginalFilename());
		Path targetLocation = this.localizacionAlmacenamiento.resolve(nombreArchivo);
		try {
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new ArchivoFSException("No se pudo almacenar el archivo " + nombreArchivo, e);
		}
		return nombreArchivo;
	}
	
	public Resource cargarArchivo(String nombreArchivo) {
		Path path=this.localizacionAlmacenamiento.resolve(nombreArchivo).normalize();
		try {
			Resource resource = new UrlResource(path.toUri());
			if(resource.exists()) {
				return resource;
			} else {
				throw new ArchivoFSNotFoundException("Archivo no encontrado: "+nombreArchivo);
			}
		} catch (MalformedURLException e) {
			throw new ArchivoFSNotFoundException("Archivo no encontrado: "+nombreArchivo);
		}
	}
	
	public void eliminarArchivo(String nombreArchivo) throws ArchivoFSNotFoundException {
		Path path=this.localizacionAlmacenamiento.resolve(nombreArchivo).normalize();
		try {
			Resource resource = new UrlResource(path.toUri());
			if(resource.exists()) {
				resource.getFile().delete();
				/*
				Logger log =  LoggerFactory.getLogger(ArchivoFSService.class);
				log.info("File deleted: " + deleted);
				*/	
			} else {
				throw new ArchivoFSNotFoundException("Archivo no encontrado: "+nombreArchivo);
			}
		} catch (MalformedURLException e) {
			throw new ArchivoFSNotFoundException("Archivo no encontrado: "+nombreArchivo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Archivo getArchivo(Resource resource) {
		Path path = this.localizacionAlmacenamiento.resolve(resource.getFilename()).normalize();
		Archivo r;

		try {
			r = new Archivo();

			File f = resource.getFile();

			r.setNombre(f.getName());
			//r.setId(id);
			r.setMime(Files.probeContentType(path));
			r.setLength(f.length());
			r.setDownloadUri(Constantes.URL_ARCHIVOS + "/fs/" + r.getNombre());
		} catch (IOException e1) {
			throw new ArchivoFSException("Excepcion de entrada/salida: " + resource.getFilename());
		}

		return r;
	}
	
	public List<Archivo> cargarArchivos() {
		List<Archivo> archivos;
		
		try {
			archivos = Files
					.walk(this.localizacionAlmacenamiento)
					.filter(Files::isRegularFile)
					.map(path -> {
						Resource resource = null;
						try {
							resource = new UrlResource(path.toUri());
							return getArchivo(resource);
						} catch (MalformedURLException e) {
							throw new ArchivoFSNotFoundException("Archivo no encontrado: " + resource.getFilename());
						}
					}).collect(Collectors.toList());
		} catch (IOException e) {
			throw new ArchivoFSException("Excepcion de entrada/salida: " + e);
		}

		return archivos;
	}
	public List<Archivo> cargarArchivosPorMimeType(String mime) {
		List<Archivo> archivos = cargarArchivos();
		List<Archivo> r = new ArrayList<>();

		for (Archivo a : archivos) {
			if (a.getMime().equalsIgnoreCase(mime)) {
				r.add(a);
			}
		}
		archivos = null;

		return r;
	}
	
	public List<Archivo> cargarArchivosPorNombreConteniendo(String input) {
		List<Archivo> archivos = cargarArchivos();
		List<Archivo> r = new ArrayList<>();

		for (Archivo a : archivos) {
			if (a.getNombre().contains(input)) {
				r.add(a);
			}
		}
		archivos = null;

		return r;
	}

	public List<Archivo> cargarArchivosPorRangoTamanios(long tamMin, long tamMax) {
		List<Archivo> archivos = cargarArchivos();
		List<Archivo> r = new ArrayList<>();

		for (Archivo a : archivos) {
			if (a.getLength() >= tamMin && a.getLength() <= tamMax) {
				r.add(a);
			}
		}
		archivos = null;

		return r;
	}
}
