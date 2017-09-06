package optimo.beans;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Size;

import org.primefaces.model.UploadedFile;

import optimo.generales.IConstantes;

public class ReporteFalla implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8865643096477783510L;
	private Integer						id;
	private Date							fechaFalla;
	private String						descripcionFalla;
	private String						estado;
	private Date							fechaHoraAtencion;
	private String						conceptoCierreManual;
	private Equipo						equipo;
	private byte[]						archivo;

	private Date							tFechaDesde;
	private Date							tFechaHasta;

	private EstructuraTabla		estructuraTabla;

	private UploadedFile			tFile;
	private String						tFotoDecodificada;
	private String						tNombreFoto;

	public ReporteFalla() {
		this.estructuraTabla = new EstructuraTabla();
		this.equipo = new Equipo();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("reporte_fallas");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);

		this.estructuraTabla.getPersistencia().put("fecha_falla", this.fechaFalla);
		this.estructuraTabla.getPersistencia().put("descripcion_falla", this.descripcionFalla);
		this.estructuraTabla.getPersistencia().put("archivo", this.archivo);
		this.estructuraTabla.getPersistencia().put("estado", this.estado);
		this.estructuraTabla.getPersistencia().put("fecha_atencion", this.fechaHoraAtencion);
		this.estructuraTabla.getPersistencia().put("concepto_cierre_manual", this.conceptoCierreManual);

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

	public Date getFechaFalla() {
		return fechaFalla;
	}

	public void setFechaFalla(Date fechaFalla) {
		this.fechaFalla = fechaFalla;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getDescripcionFalla() {
		return descripcionFalla;
	}

	public void setDescripcionFalla(String descripcionFalla) {
		this.descripcionFalla = descripcionFalla;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaHoraAtencion() {
		return fechaHoraAtencion;
	}

	public void setFechaHoraAtencion(Date fechaHoraAtencion) {
		this.fechaHoraAtencion = fechaHoraAtencion;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getConceptoCierreManual() {
		return conceptoCierreManual;
	}

	public void setConceptoCierreManual(String conceptoCierreManual) {
		this.conceptoCierreManual = conceptoCierreManual;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public Date gettFechaDesde() {
		return tFechaDesde;
	}

	public void settFechaDesde(Date tFechaDesde) {
		this.tFechaDesde = tFechaDesde;
	}

	public Date gettFechaHasta() {
		return tFechaHasta;
	}

	public void settFechaHasta(Date tFechaHasta) {
		this.tFechaHasta = tFechaHasta;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public byte[] getArchivo() {
		return archivo;
	}

	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
	}

	public UploadedFile gettFile() {
		return tFile;
	}

	public void settFile(UploadedFile tFile) {
		this.tFile = tFile;
	}

	public String gettFotoDecodificada() {
		return tFotoDecodificada;
	}

	public void settFotoDecodificada(String tFotoDecodificada) {
		this.tFotoDecodificada = tFotoDecodificada;
	}

	public String gettNombreFoto() {
		return tNombreFoto;
	}

	public void settNombreFoto(String tNombreFoto) {
		this.tNombreFoto = tNombreFoto;
	}
	
	

}
