package optimo.beans;

import java.io.InputStream;
import java.io.Serializable;

import org.primefaces.model.UploadedFile;

public class ActividadMantenimiento implements Serializable {

	/**
	 * 
	 */
	private static final long				serialVersionUID	= -3706517746437587319L;
	private Integer									id;
	private String									estadoInicial;
	private String									estadoFinal;
	private String									descripcion;
	private String									recomendaciones;

	private String									archivoDecodificado;

	private InformeMantenimiento		informeMantenimiento;
	private ActividadInformeEquipo	actividadInformeEquipo;

	private String									tFotoDecodificada;
	private String									tNombreFoto;

	private byte[]									archivo;
	private UploadedFile						tFile;

	private Object									objeto;

	private EstructuraTabla					estructuraTabla;

	public ActividadMantenimiento() {
		this.estructuraTabla = new EstructuraTabla();
		this.actividadInformeEquipo = new ActividadInformeEquipo();
		this.informeMantenimiento = new InformeMantenimiento();
	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("actividades_mantenimiento");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("estado_inicial", this.estadoInicial);
		this.estructuraTabla.getPersistencia().put("estado_final", this.estadoFinal);
		this.estructuraTabla.getPersistencia().put("archivo", this.archivo);
		this.estructuraTabla.getPersistencia().put("descripcion", this.descripcion);
		this.estructuraTabla.getPersistencia().put("recomendaciones", this.recomendaciones);

		this.estructuraTabla.getPersistencia().put("archivo_decodificado", this.archivoDecodificado);

		if (this.actividadInformeEquipo != null && this.actividadInformeEquipo.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_actividad", this.actividadInformeEquipo.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_actividad", this.actividadInformeEquipo.getId());
		}

		if (this.informeMantenimiento != null && this.informeMantenimiento.getCronograma() != null && this.informeMantenimiento.getCronograma().getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_cronograma", this.informeMantenimiento.getCronograma().getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_cronograma", this.informeMantenimiento.getCronograma().getId());
		}

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEstadoInicial() {
		return estadoInicial;
	}

	public void setEstadoInicial(String estadoInicial) {
		this.estadoInicial = estadoInicial;
	}

	public String getEstadoFinal() {
		return estadoFinal;
	}

	public void setEstadoFinal(String estadoFinal) {
		this.estadoFinal = estadoFinal;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getRecomendaciones() {
		return recomendaciones;
	}

	public void setRecomendaciones(String recomendaciones) {
		this.recomendaciones = recomendaciones;
	}

	public byte[] getArchivo() {
		return archivo;
	}

	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
	}

	public InformeMantenimiento getInformeMantenimiento() {
		return informeMantenimiento;
	}

	public void setInformeMantenimiento(InformeMantenimiento informeMantenimiento) {
		this.informeMantenimiento = informeMantenimiento;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public ActividadInformeEquipo getActividadInformeEquipo() {
		return actividadInformeEquipo;
	}

	public void setActividadInformeEquipo(ActividadInformeEquipo actividadInformeEquipo) {
		this.actividadInformeEquipo = actividadInformeEquipo;
	}

	public String gettFotoDecodificada() {
		return tFotoDecodificada;
	}

	public void settFotoDecodificada(String tFotoDecodificada) {
		this.tFotoDecodificada = tFotoDecodificada;
	}

	public UploadedFile gettFile() {
		return tFile;
	}

	public void settFile(UploadedFile tFile) {
		this.tFile = tFile;
	}

	public String gettNombreFoto() {
		return tNombreFoto;
	}

	public void settNombreFoto(String tNombreFoto) {
		this.tNombreFoto = tNombreFoto;
	}

	public String getArchivoDecodificado() {
		return archivoDecodificado;
	}

	public void setArchivoDecodificado(String archivoDecodificado) {
		this.archivoDecodificado = archivoDecodificado;
	}

	public Object getObjeto() {
		return objeto;
	}

	public void setObjeto(Object objeto) {
		this.objeto = objeto;
	}
	
	

}
