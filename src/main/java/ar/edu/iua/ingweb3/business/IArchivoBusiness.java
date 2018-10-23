package ar.edu.iua.ingweb3.business;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.iua.ingweb3.business.impl.util.fs.ArchivoFSNotFoundException;
import ar.edu.iua.ingweb3.model.Archivo;
import ar.edu.iua.ingweb3.model.exception.NotFoundException;

public interface IArchivoBusiness {
	
	//FileSystem
	public String saveToFS(MultipartFile mf) throws BusinessException;
	public Resource loadFromFS(String nombreArchivo) throws BusinessException, NotFoundException;
	public void deleteFromFS(String nombreArchivo) throws ArchivoFSNotFoundException, NotFoundException, BusinessException;
	
	
	public Archivo getArchivoFromFS(Resource resource) throws BusinessException, NotFoundException;
	
	public List<Archivo> getAlll() throws BusinessException;
	public List<Archivo> searchByName(String input) throws BusinessException;
	public List<Archivo> searchByMime(String mime) throws BusinessException;
	public List<Archivo> searchByTamanios(long tamMin, long tamMax) throws BusinessException;
	
	
	//**************** Base de Datos **************************
	public Archivo getOne(int id) throws BusinessException, NotFoundException;
	public Archivo add(Archivo archivo) throws BusinessException;
	public void deleteArchivo(int id) throws BusinessException, NotFoundException;
	public List<Archivo> getAll() throws BusinessException;
	public List <Archivo> getByName(String name)throws BusinessException, NotFoundException;
	public List <Archivo> getByMime(String mime)throws BusinessException, NotFoundException;
		
	public Archivo addArchivo(MultipartFile file) throws BusinessException, IOException;
}
