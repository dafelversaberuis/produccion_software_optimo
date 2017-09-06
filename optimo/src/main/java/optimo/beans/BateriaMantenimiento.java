package optimo.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

import optimo.generales.IConstantes;

public class BateriaMantenimiento implements Serializable {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 5026264539120889899L;
	private Integer								id;
	private BigDecimal						voltaje;
	private Integer								numeroBateria;
	private InformeMantenimiento	informeMantenimiento;

	private EstructuraTabla				estructuraTabla;

	public BateriaMantenimiento() {
		this.estructuraTabla = new EstructuraTabla();
		this.informeMantenimiento = new InformeMantenimiento();
	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("baterias_mantenimiento");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("voltaje", this.voltaje);
		this.estructuraTabla.getPersistencia().put("numero_bateria", this.numeroBateria);

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

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltaje() {
		return voltaje;
	}

	public void setVoltaje(BigDecimal voltaje) {
		this.voltaje = voltaje;
	}

	@Min(value = 1, message = IConstantes.VALIDACION_MINIMO_ENTERO)
	public Integer getNumeroBateria() {
		return numeroBateria;
	}

	public void setNumeroBateria(Integer numeroBateria) {
		this.numeroBateria = numeroBateria;
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

}
