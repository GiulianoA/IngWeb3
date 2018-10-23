package ar.edu.iua.ingweb3.web.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ar.edu.iua.ingweb3.business.BusinessException;
import ar.edu.iua.ingweb3.business.IArchivoBusiness;
import ar.edu.iua.ingweb3.business.impl.util.fs.ArchivoFSNotFoundException;
import ar.edu.iua.ingweb3.model.Archivo;
import ar.edu.iua.ingweb3.model.exception.NotFoundException;

@RestController
@RequestMapping(Constantes.URL_ARCHIVOS)
public class ArchivosRESTController {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private IArchivoBusiness archivoBusiness;

	@PostMapping(value = { "/fs", "/fs/" })
	public ResponseEntity<Archivo> uploadFS(@RequestParam("file") MultipartFile file) {
		Archivo r;

		try {
			r = guardarFSImpl(file);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", r.getDownloadUri());
			return new ResponseEntity<Archivo>(r, responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
			return new ResponseEntity<Archivo>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Archivo guardarFSImpl(MultipartFile file) throws BusinessException {
		String fileName = archivoBusiness.saveToFS(file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(Constantes.URL_ARCHIVOS + "/fs/").path(fileName).toUriString();
		Archivo r = new Archivo();
		r.setNombre(fileName);
		r.setLength(file.getSize());
		r.setDownloadUri(fileDownloadUri);
		r.setMime(file.getContentType());
		return r;

	}

	private String getMime(String path, HttpServletRequest request) {
		String mime = request.getServletContext().getMimeType(path);
		if (mime == null)
			mime = "application/octet-stream";
		return mime;
	}

	@PostMapping(value = { "/fs/multi", "/fs/multi/" })
	public ResponseEntity<List<Object>> uploadFSMulti(@RequestParam("files") MultipartFile[] files) {

		List<Object> r = Arrays.asList(files).stream().map(file -> {
			try {
				return guardarFSImpl(file);
			} catch (BusinessException e) {
				return new ResponseEntity<Archivo>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}).collect(Collectors.toList());
		return new ResponseEntity<List<Object>>(r, HttpStatus.CREATED);
	}

	private String getMime(Resource resource, HttpServletRequest request) {
		String mime = null;
		try {
			request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			log.info("No se pudo determinar el tipo mime");
		}
		if (mime == null) {
			mime = "application/octet-stream";
		}
		return mime;
	}

	@GetMapping("/fs/{fileName:.+}")
	public ResponseEntity<Resource> downloadFileFS(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource;
		try {
			resource = archivoBusiness.loadFromFS(fileName);
		} catch (BusinessException e) {
			return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		}

		String mime = getMime(resource, request);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(mime))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@DeleteMapping("/fs/{fileName:.+}")
	public ResponseEntity<Object> deleteFileFS(@PathVariable String fileName, HttpServletRequest request) {
		try {
			archivoBusiness.deleteFromFS(fileName);
			
		} catch (ArchivoFSNotFoundException e) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		} catch (NotFoundException e) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		} catch (BusinessException e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@GetMapping(value = {"/fs", "/fs/"})
	public ResponseEntity<List<Archivo>> getAlll(
			@RequestParam(required=false, value="q", defaultValue="*") String q,
			@RequestParam(required=false, value="m", defaultValue="*") String mime,
			@RequestParam(required=false, value="tamMin", defaultValue="-1") long tamMin,
			@RequestParam(required=false, value="tamMax", defaultValue="-1") long tamMax
			) {
		
		try {
		    if (!q.equals("*") && q.trim().length() > 0) {
		    	return new ResponseEntity<List<Archivo>>(archivoBusiness.searchByName(q), HttpStatus.OK);
		    } else if (tamMin != -1 && tamMax != -1 && tamMin <= tamMax) {
				return new ResponseEntity<List<Archivo>>(archivoBusiness.searchByTamanios(tamMin, tamMax), HttpStatus.OK);
			} else if (mime.equals("*") || mime.trim().length() == 0) {
				return new ResponseEntity<List<Archivo>>(archivoBusiness.getAlll(), HttpStatus.OK);
			} else {
				return new ResponseEntity<List<Archivo>>(archivoBusiness.searchByMime(mime), HttpStatus.OK);
			}
		} catch (BusinessException e) {
			return new ResponseEntity<List<Archivo>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	

	
	
	//****************************************** Base de Datos ****************************************
	
	@GetMapping(value= {"","/"})
	public ResponseEntity<List<Archivo>> listar(@RequestParam(required = false, value = "q", defaultValue = "*") String q,
			@RequestParam(required = false, value = "m", defaultValue = "*") String m){
			
		
		try {
			if(q.equals("*") && m.equals("*")){
				return new ResponseEntity<List<Archivo>>(archivoBusiness.getAll(),HttpStatus.OK);
			}else if(!m.equals("*")){
				return new ResponseEntity<List<Archivo>>(archivoBusiness.getByMime(m), HttpStatus.OK);
			}else {
				return new ResponseEntity<List<Archivo>>(archivoBusiness.getByName(q), HttpStatus.OK);
			}
			
		} catch (Exception e) {
			return new ResponseEntity<List<Archivo>>( HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		
	}
		
	@PostMapping(value = { "", "/" })
	public ResponseEntity <Archivo>add(@RequestParam("file") MultipartFile file) {
		System.out.println("Entro");
		try {
			Archivo a = archivoBusiness.addArchivo(file);
			return new ResponseEntity<Archivo>(a, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<Archivo>( HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	
	@DeleteMapping("/{fileId}")
	public ResponseEntity<Archivo> deleteArchivo(@PathVariable int fileId) {
		try {
			archivoBusiness.deleteArchivo(fileId);
		} catch (BusinessException e) {
			return new ResponseEntity<Archivo>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<Archivo>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity(HttpStatus.OK);
				
	}
	
	

}
