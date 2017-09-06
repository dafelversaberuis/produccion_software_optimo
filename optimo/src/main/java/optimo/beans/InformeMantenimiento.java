package optimo.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

import optimo.generales.IConstantes;

public class InformeMantenimiento implements Serializable {

	/**
	 * 
	 */
	private static final long							serialVersionUID	= -4506189570062884083L;
	private Cronograma										cronograma;
	private BigDecimal										voltajeFaseNeutro;
	private BigDecimal										voltajeFaseTierra;
	private BigDecimal										voltajeNeutroTierra;
	private BigDecimal										corrienteFase;
	private BigDecimal										voltajeFase1A2;
	private BigDecimal										voltajeFase1Tierra;
	private BigDecimal										voltajeFase2Tierra;
	private BigDecimal										corrienteFase1;
	private BigDecimal										corrienteFase2;
	private BigDecimal										voltajeFase1A3;
	private BigDecimal										voltajeFase2A3;
	private BigDecimal										corrienteFase3;
	private Integer												numeroFasesMomento;
	private String												observacionesGenerales;
	private String												responsableClienteMomento;
	private String												firmaClienteMomento;
	private String												cargoClienteMomento;
	private String												recomendaciones;
	private String												repuestosRequeridos;

	private List<BateriaMantenimiento>		tBaterias;
	private List<ActividadMantenimiento>	tActividades;
	private List<MaterialFotografico>			tMaterialesFotograficos;
	private List<ActividadNoPreventiva>		tActividadesNoPreventivas;

	private EstructuraTabla								estructuraTabla;

	public InformeMantenimiento() {
		this.estructuraTabla = new EstructuraTabla();
		this.cronograma = new Cronograma();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("informe_mantenimiento");

		this.estructuraTabla.getPersistencia().put("voltaje_fase_neutro", this.voltajeFaseNeutro);
		this.estructuraTabla.getPersistencia().put("voltaje_fase_tierra", this.voltajeFaseTierra);
		this.estructuraTabla.getPersistencia().put("voltaje_neutro_tierra", this.voltajeNeutroTierra);
		this.estructuraTabla.getPersistencia().put("corriente_fase", this.corrienteFase);
		this.estructuraTabla.getPersistencia().put("voltaje_fase_1_2", this.voltajeFase1A2);
		this.estructuraTabla.getPersistencia().put("voltaje_fase_1_Tierra", this.voltajeFase1Tierra);
		this.estructuraTabla.getPersistencia().put("voltaje_fase_2_tierra", this.voltajeFase2Tierra);
		this.estructuraTabla.getPersistencia().put("corriente_fase_1", this.corrienteFase1);
		this.estructuraTabla.getPersistencia().put("corriente_fase_2", this.corrienteFase2);
		this.estructuraTabla.getPersistencia().put("voltaje_fase_1_3", this.voltajeFase1A3);
		this.estructuraTabla.getPersistencia().put("voltaje_fase_2_3", this.voltajeFase2A3);
		this.estructuraTabla.getPersistencia().put("corriente_fase_3", this.corrienteFase3);
		this.estructuraTabla.getPersistencia().put("numero_fases_momento", this.numeroFasesMomento);
		this.estructuraTabla.getPersistencia().put("observaciones_generales", this.observacionesGenerales);
		this.estructuraTabla.getPersistencia().put("responsable_cliente_momento", this.responsableClienteMomento);
		this.estructuraTabla.getPersistencia().put("firma_cliente_momento", this.firmaClienteMomento);
		this.estructuraTabla.getPersistencia().put("cargo_cliente_momento", this.cargoClienteMomento);

		this.estructuraTabla.getPersistencia().put("recomendaciones", this.recomendaciones);
		this.estructuraTabla.getPersistencia().put("repuestos_requeridos", this.repuestosRequeridos);

		if (this.cronograma != null && this.cronograma.getId() != null) {
			this.estructuraTabla.getLlavePrimaria().put("id_cronograma", this.cronograma.getId());
			this.estructuraTabla.getPersistencia().put("id_cronograma", this.cronograma.getId());
		} else {
			this.estructuraTabla.getLlavePrimaria().put("id_cronograma", null);
			this.estructuraTabla.getPersistencia().put("id_cronograma", this.cronograma.getId());

		}

	}

	public Cronograma getCronograma() {
		return cronograma;
	}

	public void setCronograma(Cronograma cronograma) {
		this.cronograma = cronograma;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltajeFaseNeutro() {
		return voltajeFaseNeutro;
	}

	public void setVoltajeFaseNeutro(BigDecimal voltajeFaseNeutro) {
		this.voltajeFaseNeutro = voltajeFaseNeutro;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltajeFaseTierra() {
		return voltajeFaseTierra;
	}

	public void setVoltajeFaseTierra(BigDecimal voltajeFaseTierra) {
		this.voltajeFaseTierra = voltajeFaseTierra;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltajeNeutroTierra() {
		return voltajeNeutroTierra;
	}

	public void setVoltajeNeutroTierra(BigDecimal voltajeNeutroTierra) {
		this.voltajeNeutroTierra = voltajeNeutroTierra;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getCorrienteFase() {
		return corrienteFase;
	}

	public void setCorrienteFase(BigDecimal corrienteFase) {
		this.corrienteFase = corrienteFase;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltajeFase1A2() {
		return voltajeFase1A2;
	}

	public void setVoltajeFase1A2(BigDecimal voltajeFase1A2) {
		this.voltajeFase1A2 = voltajeFase1A2;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltajeFase1Tierra() {
		return voltajeFase1Tierra;
	}

	public void setVoltajeFase1Tierra(BigDecimal voltajeFase1Tierra) {
		this.voltajeFase1Tierra = voltajeFase1Tierra;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltajeFase2Tierra() {
		return voltajeFase2Tierra;
	}

	public void setVoltajeFase2Tierra(BigDecimal voltajeFase2Tierra) {
		this.voltajeFase2Tierra = voltajeFase2Tierra;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getCorrienteFase1() {
		return corrienteFase1;
	}

	public void setCorrienteFase1(BigDecimal corrienteFase1) {
		this.corrienteFase1 = corrienteFase1;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getCorrienteFase2() {
		return corrienteFase2;
	}

	public void setCorrienteFase2(BigDecimal corrienteFase2) {
		this.corrienteFase2 = corrienteFase2;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltajeFase1A3() {
		return voltajeFase1A3;
	}

	public void setVoltajeFase1A3(BigDecimal voltajeFase1A3) {
		this.voltajeFase1A3 = voltajeFase1A3;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getVoltajeFase2A3() {
		return voltajeFase2A3;
	}

	public void setVoltajeFase2A3(BigDecimal voltajeFase2A3) {
		this.voltajeFase2A3 = voltajeFase2A3;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	public BigDecimal getCorrienteFase3() {
		return corrienteFase3;
	}

	public void setCorrienteFase3(BigDecimal corrienteFase3) {
		this.corrienteFase3 = corrienteFase3;
	}

	@Min(value = 0, message = IConstantes.VALIDACION_MINIMO_ENTERO)
	public Integer getNumeroFasesMomento() {
		return numeroFasesMomento;
	}

	public void setNumeroFasesMomento(Integer numeroFasesMomento) {
		this.numeroFasesMomento = numeroFasesMomento;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public List<BateriaMantenimiento> gettBaterias() {
		return tBaterias;
	}

	public void settBaterias(List<BateriaMantenimiento> tBaterias) {
		this.tBaterias = tBaterias;
	}

	public List<ActividadMantenimiento> gettActividades() {
		return tActividades;
	}

	public void settActividades(List<ActividadMantenimiento> tActividades) {
		this.tActividades = tActividades;
	}

	public List<ActividadNoPreventiva> gettActividadesNoPreventivas() {
		return tActividadesNoPreventivas;
	}

	public void settActividadesNoPreventivas(List<ActividadNoPreventiva> tActividadesNoPreventivas) {
		this.tActividadesNoPreventivas = tActividadesNoPreventivas;
	}

	public String getObservacionesGenerales() {
		return observacionesGenerales;
	}

	public void setObservacionesGenerales(String observacionesGenerales) {
		this.observacionesGenerales = observacionesGenerales;
	}

	public String getResponsableClienteMomento() {
		return responsableClienteMomento;
	}

	public void setResponsableClienteMomento(String responsableClienteMomento) {
		this.responsableClienteMomento = responsableClienteMomento;
	}

	public String getFirmaClienteMomento() {
		return firmaClienteMomento;
	}

	public void setFirmaClienteMomento(String firmaClienteMomento) {
		this.firmaClienteMomento = firmaClienteMomento;
	}

	public String getCargoClienteMomento() {
		return cargoClienteMomento;
	}

	public void setCargoClienteMomento(String cargoClienteMomento) {
		this.cargoClienteMomento = cargoClienteMomento;
	}

	public String getRecomendaciones() {
		return recomendaciones;
	}

	public void setRecomendaciones(String recomendaciones) {
		this.recomendaciones = recomendaciones;
	}

	public String getRepuestosRequeridos() {
		return repuestosRequeridos;
	}

	public void setRepuestosRequeridos(String repuestosRequeridos) {
		this.repuestosRequeridos = repuestosRequeridos;
	}

	public List<MaterialFotografico> gettMaterialesFotograficos() {
		return tMaterialesFotograficos;
	}

	public void settMaterialesFotograficos(List<MaterialFotografico> tMaterialesFotograficos) {
		this.tMaterialesFotograficos = tMaterialesFotograficos;
	}

}
