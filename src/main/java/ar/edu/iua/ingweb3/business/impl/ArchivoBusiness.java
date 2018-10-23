package ar.edu.iua.ingweb3.business.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.iua.ingweb3.business.BusinessException;
import ar.edu.iua.ingweb3.business.IArchivoBusiness;
import ar.edu.iua.ingweb3.business.impl.util.fs.ArchivoFSNotFoundException;
import ar.edu.iua.ingweb3.business.impl.util.fs.ArchivoFSService;
import ar.edu.iua.ingweb3.model.Archivo;
import ar.edu.iua.ingweb3.model.exception.NotFoundException;
import ar.edu.iua.ingweb3.model.persistence.ArchivoRepository;

@Service
public class ArchivoBusiness implements IArchivoBusiness {

	@Autowired
	private ArchivoFSService archivoFSService;
	@Autowired
	private ArchivoRepository archivoRepository;

	@Override
	public String saveToFS(MultipartFile mf) throws BusinessException {
		return archivoFSService.almacenarArchivo(mf);
	}

	@Override
	public Resource loadFromFS(String nombreArchivo) throws BusinessException, NotFoundException {
		try {
			return archivoFSService.cargarArchivo(nombreArchivo);

		} catch (ArchivoFSNotFoundException e) {
			throw new NotFoundException(e);
		}
	}

	@Override
	public void deleteFromFS(String nombreArchivo) throws NotFoundException{
		try {
			archivoFSService.eliminarArchivo(nombreArchivo);
		} catch (ArchivoFSNotFoundException e) {
			throw new NotFoundException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public Archivo getArchivoFromFS(Resource resource) throws BusinessException, NotFoundException {		
		return archivoFSService.getArchivo(resource);
	}
	
	@Override
	public List<Archivo> getAlll() throws BusinessException {
		return archivoFSService.cargarArchivos();
	}
	
	@Override
	public List<Archivo> searchByName(String input) throws BusinessException {
		return archivoFSService.cargarArchivosPorNombreConteniendo(input);
	}
	
	@Override
	public List<Archivo> searchByMime(String mime) throws BusinessException {
		return archivoFSService.cargarArchivosPorMimeType(mime);
	}
	
	@Override
	public List<Archivo> searchByTamanios(long tamMin, long tamMax) throws BusinessException {
		return archivoFSService.cargarArchivosPorRangoTamanios(tamMin, tamMax);
	}
	
	
	//**************** Base de Datos **************************
	
	@Override
	public Archivo getOne(int id) throws BusinessException, NotFoundException {
		Optional<Archivo> pr = null;
		try {
			pr = archivoRepository.findById(id);
			
		}catch (Exception e) {
			throw new BusinessException(e);
		}
		if(!pr.isPresent()) {
			throw new NotFoundException();
		}
		return pr.get();
	}

	@Override
	public Archivo add(Archivo archivo) throws BusinessException {
		archivoRepository.save(archivo);
		return archivo;
	}

	@Override
	public void deleteArchivo(int id) throws BusinessException, NotFoundException {
		Archivo a=new Archivo();
		a.setId(id);
		archivoRepository.delete(a);

	}

	@Override
	public List<Archivo> getAll() throws BusinessException {

		return archivoRepository.findAll();
	}
	
	@Override
	public List<Archivo> getByName(String name) throws BusinessException, NotFoundException {
		
		return  archivoRepository.findByNombreContaining(name);
	}
	
	@Override
	public List<Archivo> getByMime(String mime) throws BusinessException, NotFoundException {
		
		return archivoRepository.findByMime(mime);
	}
	public Archivo addArchivo(MultipartFile file) throws BusinessException, IOException{
		
		
		String nombreArchivo = StringUtils.cleanPath(file.getOriginalFilename());
		Archivo r1 = new Archivo();
		r1.setNombre(nombreArchivo);
		r1.setLength(file.getSize());
		r1.setContenido(file.getBytes());
		r1.setMime(file.getContentType());
		archivoRepository.save(r1);
		return r1;
			
	}

}
