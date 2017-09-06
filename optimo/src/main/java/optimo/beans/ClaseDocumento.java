package optimo.beans;

import java.io.Serializable;

import javax.validation.constraints.Size;

import optimo.generales.IConstantes;

public class ClaseDocumento implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7839599846454962168L;
	private Integer						id;
	private String						nombre;
	private String						indicativoVigencia;
	private String						euipoBiomedico;

	private EstructuraTabla		estructuraTabla;

	public ClaseDocumento() {
		this.estructuraTabla = new EstructuraTabla();
	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("clases_documentos");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("nombre", this.nombre);
		this.estructuraTabla.getPersistencia().put("indicativo_vigencia", this.indicativoVigencia);
		this.estructuraTabla.getPersistencia().put("equipo_biomedico", this.euipoBiomedico);

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

	public String getIndicativoVigencia() {
		return indicativoVigencia;
	}

	public void setIndicativoVigencia(String indicativoVigencia) {
		this.indicativoVigencia = indicativoVigencia;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public String getEuipoBiomedico() {
		return euipoBiomedico;
	}

	public void setEuipoBiomedico(String euipoBiomedico) {
		this.euipoBiomedico = euipoBiomedico;
	}

}
