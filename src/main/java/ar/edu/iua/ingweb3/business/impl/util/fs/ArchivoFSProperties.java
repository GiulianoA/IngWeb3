package ar.edu.iua.ingweb3.business.impl.util.fs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="archivo")
public class ArchivoFSProperties {

	// propiedad --> archivo.directorio-almacenamiento
	private String directorioAlmacenamiento;

	public String getDirectorioAlmacenamiento() {
		return directorioAlmacenamiento;
	}

	public void setDirectorioAlmacenamiento(String directorioAlmacenamiento) {
		this.directorioAlmacenamiento = directorioAlmacenamiento;
	}
	
}
