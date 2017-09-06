package optimo.beans;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import optimo.generales.IConstantes;

public class Tecnico implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1052534353071703576L;
	private Integer						id;
	private String						nombres;
	private String						correoElectronico;
	private String						estadoVigencia;
	private String						clave;
	private String						documento;
	private String						telefono;
	private String						cargo;
	private String						tTipoClave;
	private String[]					tPermisos;
	private String						informeMantenimiento;
	private String						reporteFallas;
	private String						cronograma;
	private String						firma;

	private EstructuraTabla		estructuraTabla;

	public Tecnico() {
		this.estructuraTabla = new EstructuraTabla();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("tecnicos");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("nombres", this.nombres);
		this.estructuraTabla.getPersistencia().put("correo_electronico", this.correoElectronico);
		this.estructuraTabla.getPersistencia().put("estado_vigencia", this.estadoVigencia);
		this.estructuraTabla.getPersistencia().put("clave", this.clave);
		this.estructuraTabla.getPersistencia().put("telefono", this.telefono);
		this.estructuraTabla.getPersistencia().put("documento", this.documento);
		this.estructuraTabla.getPersistencia().put("cargo", this.cargo);
		this.estructuraTabla.getPersistencia().put("firma", this.firma);

		// el resto de campos de permisos no los coloco porque se actualizan de otra
		// manera
		// informe_mantenimiento
		// reporte_fallas
		// cronograma

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = IConstantes.VALIDACION_EMAIL_INCORRECTO)
	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getEstadoVigencia() {
		return estadoVigencia;
	}

	public void setEstadoVigencia(String estadoVigencia) {
		this.estadoVigencia = estadoVigencia;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public String gettTipoClave() {
		return tTipoClave;
	}

	public void settTipoClave(String tTipoClave) {
		this.tTipoClave = tTipoClave;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Size(max = 30, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String[] gettPermisos() {
		return tPermisos;
	}

	public void settPermisos(String[] tPermisos) {
		this.tPermisos = tPermisos;
	}

	public String getInformeMantenimiento() {
		return informeMantenimiento;
	}

	public void setInformeMantenimiento(String informeMantenimiento) {
		this.informeMantenimiento = informeMantenimiento;
	}

	public String getReporteFallas() {
		return reporteFallas;
	}

	public void setReporteFallas(String reporteFallas) {
		this.reporteFallas = reporteFallas;
	}

	public String getCronograma() {
		return cronograma;
	}

	public void setCronograma(String cronograma) {
		this.cronograma = cronograma;
	}

	public String getFirma() {
		return firma;
	}

	public void setFirma(String firma) {
		this.firma = firma;
	}

}
