package optimo.beans;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import optimo.generales.IConstantes;

public class Cliente implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1287844929551328858L;
	private Integer						id;
	private String						cliente;
	private String						nit;
	private String						ubicacion;
	private String						estadoVigencia;
	private String						direccionFisica;
	private String						representante;
	private String						correoElectronico;
	private String						clave;
	private String						telefono;
	private String						cargo;
	private String						tTipoClave;
	private String						tCopiaCorreo;
	private String						tEstado;
	private String[]					tPermisos;
	private String						informeMantenimiento;
	private String						reporteFallas;
	private String						cronograma;
	private String						hojaVida;
	private String						indicadoresGestion;

	private String						tClienteAutocompletado;
	private String						firma;

	private EstructuraTabla		estructuraTabla;

	public Cliente() {
		this.estructuraTabla = new EstructuraTabla();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("clientes");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);

		this.estructuraTabla.getPersistencia().put("cliente", this.cliente);
		this.estructuraTabla.getPersistencia().put("nit", this.nit);
		this.estructuraTabla.getPersistencia().put("representante", this.representante);
		this.estructuraTabla.getPersistencia().put("correo_electronico", this.correoElectronico);
		this.estructuraTabla.getPersistencia().put("clave", this.clave);
		this.estructuraTabla.getPersistencia().put("telefono", this.telefono);
		this.estructuraTabla.getPersistencia().put("ubicacion", this.ubicacion);
		this.estructuraTabla.getPersistencia().put("direccion_fisica", this.direccionFisica);
		this.estructuraTabla.getPersistencia().put("cargo", this.cargo);
		this.estructuraTabla.getPersistencia().put("estado_vigencia", this.estadoVigencia);
		this.estructuraTabla.getPersistencia().put("firma", this.firma);

		// hoja_vida
		// informe_mantenimiento
		// reporte_fallas
		// cronograma
		// indicadores_gestion

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getRepresentante() {
		return representante;
	}

	public void setRepresentante(String representante) {
		this.representante = representante;
	}

	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = IConstantes.VALIDACION_EMAIL_INCORRECTO)
	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
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

	public String gettCopiaCorreo() {
		return tCopiaCorreo;
	}

	public void settCopiaCorreo(String tCopiaCorreo) {
		this.tCopiaCorreo = tCopiaCorreo;
	}

	public String gettEstado() {
		return tEstado;
	}

	public void settEstado(String tEstado) {
		this.tEstado = tEstado;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	@Pattern(regexp = "[AI]", message = IConstantes.VALIDACION_ACTIVO_INACTIVO)
	public String getEstadoVigencia() {
		return estadoVigencia;
	}

	public void setEstadoVigencia(String estadoVigencia) {
		this.estadoVigencia = estadoVigencia;
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

	public String getHojaVida() {
		return hojaVida;
	}

	public void setHojaVida(String hojaVida) {
		this.hojaVida = hojaVida;
	}

	public String getIndicadoresGestion() {
		return indicadoresGestion;
	}

	public void setIndicadoresGestion(String indicadoresGestion) {
		this.indicadoresGestion = indicadoresGestion;
	}

	public String gettClienteAutocompletado() {
		return tClienteAutocompletado;
	}

	public void settClienteAutocompletado(String tClienteAutocompletado) {
		this.tClienteAutocompletado = tClienteAutocompletado;
	}

	public String getFirma() {
		return firma;
	}

	public void setFirma(String firma) {
		this.firma = firma;
	}

}
