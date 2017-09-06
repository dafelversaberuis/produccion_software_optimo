package optimo.beans;

import java.io.Serializable;

public class ActividadInformeEquipo implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5972491411933264159L;
	private Integer						id;
	private String						actividad;
	private String						indicativoVigencia;
	private Equipo						equipo;
	private Integer						posicion;

	private boolean						tSeleccionado;

	private EstructuraTabla		estructuraTabla;

	public ActividadInformeEquipo() {
		this.estructuraTabla = new EstructuraTabla();
		this.equipo = new Equipo();
	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("actividades_informe_equipo");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("actividad", this.actividad);
		this.estructuraTabla.getPersistencia().put("indicativo_vigencia", this.indicativoVigencia);
		this.estructuraTabla.getPersistencia().put("posicion", this.posicion);
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

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public String getIndicativoVigencia() {
		return indicativoVigencia;
	}

	public void setIndicativoVigencia(String indicativoVigencia) {
		this.indicativoVigencia = indicativoVigencia;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public Integer getPosicion() {
		return posicion;
	}

	public void setPosicion(Integer posicion) {
		this.posicion = posicion;
	}

	public boolean istSeleccionado() {
		return tSeleccionado;
	}

	public void settSeleccionado(boolean tSeleccionado) {
		this.tSeleccionado = tSeleccionado;
	}

}
