package optimo.beans;

import java.io.Serializable;

public class Personal implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7446816230779241150L;

	private Integer						id;
	private String						tipoUsuario;
	private String						nombreCompleto;
	private String						correoElectronico;
	private String						clave;

	private boolean						roles;
	private boolean						equipos;
	private boolean						informeMantenimiento;
	private boolean						reporteFallas;
	private boolean						cronograma;
	private boolean						indicadoresGestion;

	public Personal() {

	}

	public String getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public boolean isRoles() {
		return roles;
	}

	public void setRoles(boolean roles) {
		this.roles = roles;
	}

	public boolean isEquipos() {
		return equipos;
	}

	public void setEquipos(boolean equipos) {
		this.equipos = equipos;
	}

	public boolean isInformeMantenimiento() {
		return informeMantenimiento;
	}

	public void setInformeMantenimiento(boolean informeMantenimiento) {
		this.informeMantenimiento = informeMantenimiento;
	}

	public boolean isReporteFallas() {
		return reporteFallas;
	}

	public void setReporteFallas(boolean reporteFallas) {
		this.reporteFallas = reporteFallas;
	}

	public boolean isCronograma() {
		return cronograma;
	}

	public void setCronograma(boolean cronograma) {
		this.cronograma = cronograma;
	}

	public boolean isIndicadoresGestion() {
		return indicadoresGestion;
	}

	public void setIndicadoresGestion(boolean indicadoresGestion) {
		this.indicadoresGestion = indicadoresGestion;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

}
