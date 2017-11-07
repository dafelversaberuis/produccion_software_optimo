package optimo.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import optimo.generales.IConstantes;

public class Cronograma implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2787139370726580477L;
	private Integer						id;
	private Date							fechaProgramacion;
	private Integer						holgura;
	private String						tipoMantenimiento;
	private Integer						duracion;
	private BigDecimal				costo;
	private String						estado;
	private Date							fechaHoraAtencion;
	private Date							fechaHoraAprobacionCliente;
	private Date							fechaDesdeHolgura;
	private Date							fechaHastaHolgura;
	private Equipo						equipo;
	private Tecnico						tecnico;
	private ReporteFalla			reporteFalla;
	private Integer						versionReporte;

	private Date							tFechaDesde;
	private Date							tFechaHasta;
	private Date							tFechaEvaluacion;
	private String						tObservaciones;
	private String						tCopiaEstado;

	private EstructuraTabla		estructuraTabla;

	// private Integer tIntervaloAnalizado;
	private BigDecimal				tIntervaloAnalizado;

	public Cronograma() {
		this.estructuraTabla = new EstructuraTabla();
		this.tecnico = new Tecnico();
		this.equipo = new Equipo();
		this.reporteFalla = new ReporteFalla();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("cronograma");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);

		this.estructuraTabla.getPersistencia().put("fecha_programacion", this.fechaProgramacion);
		this.estructuraTabla.getPersistencia().put("holgura", this.holgura);
		this.estructuraTabla.getPersistencia().put("tipo_mantenimiento", this.tipoMantenimiento);
		this.estructuraTabla.getPersistencia().put("duracion", this.duracion);
		this.estructuraTabla.getPersistencia().put("costo", this.costo);
		this.estructuraTabla.getPersistencia().put("estado", this.estado);
		this.estructuraTabla.getPersistencia().put("fecha_hora_atencion", this.fechaHoraAtencion);
		this.estructuraTabla.getPersistencia().put("fecha_hora_aprobacion_cliente", this.fechaHoraAprobacionCliente);
		this.estructuraTabla.getPersistencia().put("fecha_desde_holgura", this.fechaDesdeHolgura);
		this.estructuraTabla.getPersistencia().put("fecha_hasta_holgura", this.fechaHastaHolgura);

		this.estructuraTabla.getPersistencia().put("version_reporte", this.versionReporte);

		if (this.equipo != null && this.equipo.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_equipo", this.equipo.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_equipo", this.equipo.getId());
		}

		if (this.tecnico != null && this.tecnico.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_tecnico", this.tecnico.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_tecnico", this.tecnico.getId());
		}

		if (this.reporteFalla != null && this.reporteFalla.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_reporte_falla", this.reporteFalla.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_reporte_falla", null);
		}

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getFechaProgramacion() {
		return fechaProgramacion;
	}

	public void setFechaProgramacion(Date fechaProgramacion) {
		this.fechaProgramacion = fechaProgramacion;
	}

	@Min(value = 0, message = IConstantes.VALIDACION_MINIMO_ENTERO)
	@Max(value = 30, message = IConstantes.VALIDACION_MAXIMO_ENTERO)
	public Integer getHolgura() {
		return holgura;
	}

	public void setHolgura(Integer holgura) {
		this.holgura = holgura;
	}

	public String getTipoMantenimiento() {
		return tipoMantenimiento;
	}

	public void setTipoMantenimiento(String tipoMantenimiento) {
		this.tipoMantenimiento = tipoMantenimiento;
	}

	@Min(value = 1, message = IConstantes.VALIDACION_MINIMO_ENTERO)
	public Integer getDuracion() {
		return duracion;
	}

	public void setDuracion(Integer duracion) {
		this.duracion = duracion;
	}

	@Digits(integer = 10, fraction = 2, message = IConstantes.VALIDACION_MAXIMO_DECIMAL)
	@DecimalMin("0.00")
	public BigDecimal getCosto() {
		return costo;
	}

	public void setCosto(BigDecimal costo) {
		this.costo = costo;
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

	public Date getFechaHoraAprobacionCliente() {
		return fechaHoraAprobacionCliente;
	}

	public void setFechaHoraAprobacionCliente(Date fechaHoraAprobacionCliente) {
		this.fechaHoraAprobacionCliente = fechaHoraAprobacionCliente;
	}

	public Date getFechaDesdeHolgura() {
		return fechaDesdeHolgura;
	}

	public void setFechaDesdeHolgura(Date fechaDesdeHolgura) {
		this.fechaDesdeHolgura = fechaDesdeHolgura;
	}

	public Date getFechaHastaHolgura() {
		return fechaHastaHolgura;
	}

	public void setFechaHastaHolgura(Date fechaHastaHolgura) {
		this.fechaHastaHolgura = fechaHastaHolgura;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public Tecnico getTecnico() {
		return tecnico;
	}

	public void setTecnico(Tecnico tecnico) {
		this.tecnico = tecnico;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
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

	public ReporteFalla getReporteFalla() {
		return reporteFalla;
	}

	public void setReporteFalla(ReporteFalla reporteFalla) {
		this.reporteFalla = reporteFalla;
	}

	public String gettObservaciones() {
		return tObservaciones;
	}

	public void settObservaciones(String tObservaciones) {
		this.tObservaciones = tObservaciones;
	}

	public BigDecimal gettIntervaloAnalizado() {
		return tIntervaloAnalizado;
	}

	public void settIntervaloAnalizado(BigDecimal tIntervaloAnalizado) {
		this.tIntervaloAnalizado = tIntervaloAnalizado;
	}

	public Date gettFechaEvaluacion() {
		return tFechaEvaluacion;
	}

	public void settFechaEvaluacion(Date tFechaEvaluacion) {
		this.tFechaEvaluacion = tFechaEvaluacion;
	}

	public Integer getVersionReporte() {
		return versionReporte;
	}

	public void setVersionReporte(Integer versionReporte) {
		this.versionReporte = versionReporte;
	}

	public String gettCopiaEstado() {
		return tCopiaEstado;
	}

	public void settCopiaEstado(String tCopiaEstado) {
		this.tCopiaEstado = tCopiaEstado;
	}

}
