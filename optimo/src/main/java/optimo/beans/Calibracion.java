package optimo.beans;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Size;

import org.primefaces.model.UploadedFile;

import optimo.generales.IConstantes;

public class Calibracion implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7691012784845278966L;
	private Integer						id;
	private Equipo						equipo;
	private byte[]						archivo;
	private String						contentType;
	private String						extensionArchivo;
	private String						firmaClienteMomento;
	private Date							fechaCalibracion;

	private UploadedFile			tFile;

	private EstructuraTabla		estructuraTabla;

	public Calibracion() {
		this.estructuraTabla = new EstructuraTabla();
		this.equipo = new Equipo();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("calibraciones");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("archivo", this.archivo);
		this.estructuraTabla.getPersistencia().put("firma_cliente_momento", this.firmaClienteMomento);
		this.estructuraTabla.getPersistencia().put("content_type", this.contentType);
		this.estructuraTabla.getPersistencia().put("extension_archivo", this.extensionArchivo);
		this.estructuraTabla.getPersistencia().put("fecha_calibracion", this.fechaCalibracion);

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

	public byte[] getArchivo() {
		return archivo;
	}

	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
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

	public String getFirmaClienteMomento() {
		return firmaClienteMomento;
	}

	public void setFirmaClienteMomento(String firmaClienteMomento) {
		this.firmaClienteMomento = firmaClienteMomento;
	}

	public Date getFechaCalibracion() {
		return fechaCalibracion;
	}

	public void setFechaCalibracion(Date fechaCalibracion) {
		this.fechaCalibracion = fechaCalibracion;
	}

}
