package optimo.beans;

import java.io.Serializable;

public class ActividadNoPreventiva implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3170346571303432302L;
	private Integer								id;
	private String								estadoInicial;
	private String								estadoFinal;
	private String								descripcionActividad;
	private String								observaciones;

	private InformeMantenimiento	informeMantenimiento;

	private EstructuraTabla				estructuraTabla;

	public ActividadNoPreventiva() {
		this.estructuraTabla = new EstructuraTabla();
		this.informeMantenimiento = new InformeMantenimiento();
	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("actividades_no_preventivas");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("estado_inicial", this.estadoInicial);
		this.estructuraTabla.getPersistencia().put("estado_final", this.estadoFinal);
		this.estructuraTabla.getPersistencia().put("descripcion_actividad", this.descripcionActividad);
		this.estructuraTabla.getPersistencia().put("observaciones", this.observaciones);

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

	public String getDescripcionActividad() {
		return descripcionActividad;
	}

	public void setDescripcionActividad(String descripcionActividad) {
		this.descripcionActividad = descripcionActividad;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

}
