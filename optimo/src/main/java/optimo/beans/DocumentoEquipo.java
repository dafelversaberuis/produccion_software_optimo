package optimo.beans;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.primefaces.model.UploadedFile;

import optimo.generales.IConstantes;

public class DocumentoEquipo implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8675485074461182941L;
	private Integer						id;
	private String						nombre;
	private ClaseDocumento		claseDocumento;
	private Equipo						equipo;
	private byte[]						archivo;
	private String						poseeDocumento;
	private String						contentType;
	private String						extensionArchivo;

	private UploadedFile			tFile;

	private EstructuraTabla		estructuraTabla;

	public DocumentoEquipo() {
		this.estructuraTabla = new EstructuraTabla();
		this.claseDocumento = new ClaseDocumento();
		this.equipo = new Equipo();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("documentos_equipos");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("nombre", this.nombre);
		this.estructuraTabla.getPersistencia().put("archivo", this.archivo);
		this.estructuraTabla.getPersistencia().put("posee_documento", this.poseeDocumento);
		this.estructuraTabla.getPersistencia().put("content_type", this.contentType);
		this.estructuraTabla.getPersistencia().put("extension_archivo", this.extensionArchivo);

		if (this.claseDocumento != null && this.claseDocumento.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_clase_documento", this.claseDocumento.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_clase_documento", this.claseDocumento.getId());
		}

		if (this.equipo != null && this.equipo.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_equipo", this.equipo.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_equipo", this.equipo.getId());
		}

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public ClaseDocumento getClaseDocumento() {
		return claseDocumento;
	}

	public void setClaseDocumento(ClaseDocumento claseDocumento) {
		this.claseDocumento = claseDocumento;
	}

	public byte[] getArchivo() {
		return archivo;
	}

	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
	}

	public String getPoseeDocumento() {
		return poseeDocumento;
	}

	public void setPoseeDocumento(String poseeDocumento) {
		this.poseeDocumento = poseeDocumento;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public UploadedFile gettFile() {
		return tFile;
	}

	public void settFile(UploadedFile tFile) {
		this.tFile = tFile;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getExtensionArchivo() {
		return extensionArchivo;
	}

	public void setExtensionArchivo(String extensionArchivo) {
		this.extensionArchivo = extensionArchivo;
	}

}
