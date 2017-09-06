package optimo.beans;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import optimo.generales.IConstantes;

public class RepuestoEquipo implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1638410470371596657L;
	private Integer						id;
	private String						nombre;
	private String						marca;
	private String						modelo;
	private String						descripcion;
	private String						ubicacion;
	private String						periodicidad;
	private Integer						valorPeriodicidad;
	private Equipo						equipo;

	private EstructuraTabla		estructuraTabla;

	public RepuestoEquipo() {
		this.estructuraTabla = new EstructuraTabla();

		this.equipo = new Equipo();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("repuestos_equipos");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("nombre", this.nombre);
		this.estructuraTabla.getPersistencia().put("marca", this.marca);
		this.estructuraTabla.getPersistencia().put("modelo", this.modelo);
		this.estructuraTabla.getPersistencia().put("descripcion", this.descripcion);
		this.estructuraTabla.getPersistencia().put("ubicacion", this.ubicacion);
		this.estructuraTabla.getPersistencia().put("periodicidad", this.periodicidad);
		this.estructuraTabla.getPersistencia().put("valor_periodicidad", this.valorPeriodicidad);

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

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getPeriodicidad() {
		return periodicidad;
	}

	public void setPeriodicidad(String periodicidad) {
		this.periodicidad = periodicidad;
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

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Min(value = 1, message = IConstantes.VALIDACION_MINIMO_ENTERO)
	public Integer getValorPeriodicidad() {
		return valorPeriodicidad;
	}

	public void setValorPeriodicidad(Integer valorPeriodicidad) {
		this.valorPeriodicidad = valorPeriodicidad;
	}

}
