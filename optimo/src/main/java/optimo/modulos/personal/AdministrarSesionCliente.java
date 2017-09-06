package optimo.modulos.personal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import optimo.beans.Administrador;
import optimo.beans.Cliente;
import optimo.beans.Personal;
import optimo.beans.Tecnico;
import optimo.generales.ConsultarFuncionesAPI;
import optimo.generales.IConstantes;
import optimo.modulos.IConsultasDAO;

@ManagedBean
@SessionScoped
public class AdministrarSesionCliente extends ConsultarFuncionesAPI implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8153708434415072107L;
	private Personal					personalSesion;
	private Personal					personal;

	private String						qr;

	public void init() {

	}

	// privados

	/**
	 * Obtiene validación de rol no admitido
	 * 
	 * @return pagina
	 */
	public String getNoRolAdmitido(String aInterfaz) {
		String pagina = null;
		int sw = 2;

		if (this.personalSesion != null && this.personalSesion.getId() != null) {
			sw = 1;
			if (this.personalSesion.getTipoUsuario().equals(IConstantes.ROL_ADMINISTRADOR)) {
				sw = 0;
			} else if (this.personalSesion.getTipoUsuario().equals(IConstantes.ROL_TECNICO)) {

				if (aInterfaz != null && aInterfaz.equals("INFORME_MANTENIMIENTO")) {
					sw = 0;
				}

			} else if (this.personalSesion.getTipoUsuario().equals(IConstantes.ROL_CLIENTE)) {

				if (aInterfaz != null && aInterfaz.equals("EQUIPOS") && this.personalSesion.isEquipos()) {
					sw = 0;
				} else if (aInterfaz != null && aInterfaz.equals("CRONOGRAMA") && this.personalSesion.isCronograma()) {
					sw = 0;
				} else if (aInterfaz != null && aInterfaz.equals("REPORTE_FALLAS") && this.personalSesion.isReporteFallas()) {
					sw = 0;
				} else if (aInterfaz != null && aInterfaz.equals("INDICADORES_GESTION") && this.personalSesion.isIndicadoresGestion()) {
					sw = 0;
				}

			}

		}

		if (sw == 1) {
			pagina = IConstantes.PAGINA_HOME;

			this.mostrarMensajeGlobal("noPoseePrivilegiosSobreInterfaz", "advertencia");
			// Guarda el mensaje antes de redireccionar y lo muestra
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);

		} else if (sw == 2) {
			pagina = IConstantes.PAGINA_NO_LOGUEO;

			this.mostrarMensajeGlobal("noPoseePrivilegiosSobreInterfaz", "advertencia");
			// Guarda el mensaje antes de redireccionar y lo muestra
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);

		}

		return pagina;
	}

	/**
	 * Determina si un personal esta en sesión y lo deja o no acceder
	 * 
	 * @return pagina
	 */
	public String getNologueoPersonal() {
		String pagina = null;
		if (!(this.personalSesion != null && this.personalSesion.getId() != null)) {

			pagina = IConstantes.PAGINA_NO_LOGUEO;

			this.mostrarMensajeGlobal("noPoseePrivilegios", "advertencia");
			// Guarda el mensaje antes de redireccionar y lo muestra
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);

		}

		return pagina;
	}

	// publicos

	/**
	 * Cierra la sesión del administrador
	 */
	public String getCerrarSesion() {
		String pagina = IConstantes.PAGINA_NO_LOGUEO;
		this.personalSesion = null;

		// this.vistaLogueado = 0;

		this.mostrarMensajeGlobal("cierreSesionCorrecto", "exito");

		// Guarda el mensaje antes de redireccionar y lo muestra
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);

		return pagina;

	}

	/**
	 * Permite el acceso del personal por medio de qr
	 */
	public String accederQr() {
		String pagina = null;

		List<Administrador> administradores = null;
		List<Tecnico> tecnicos = null;
		List<Cliente> clientes = null;
		try {

			if (this.personal != null && this.personal.getCorreoElectronico() != null && !this.personal.getCorreoElectronico().trim().equals("") && this.personal.getClave() != null && !this.personal.getClave().trim().equals("")) {

				if (this.personal.getTipoUsuario() != null && this.personal.getTipoUsuario().equals(IConstantes.ROL_ADMINISTRADOR)) {

					Administrador admin = new Administrador();
					admin.setClave(this.personal.getClave());
					admin.setCorreoElectronico(this.personal.getCorreoElectronico().trim());
					admin.setEstadoVigencia(IConstantes.ACTIVO);
					administradores = IConsultasDAO.getAdministradores(admin);

					if (administradores != null && administradores.size() > 0 && administradores.get(0) != null && administradores.get(0).getId() != null) {

						this.personalSesion = new Personal();
						this.personalSesion.setCorreoElectronico(administradores.get(0).getCorreoElectronico().trim());
						this.personalSesion.setNombreCompleto(administradores.get(0).getNombres().trim());
						this.personalSesion.setTipoUsuario(this.personal.getTipoUsuario());
						this.personalSesion.setId(administradores.get(0).getId());

						// admin todos los permisos
						this.personalSesion.setEquipos(true);
						this.personalSesion.setInformeMantenimiento(true);
						this.personalSesion.setReporteFallas(true);
						this.personalSesion.setCronograma(true);
						this.personalSesion.setIndicadoresGestion(true);

						this.mostrarMensajeGlobal("ingresoCorrecto", "exito");
						this.personal = null;

						// Guarda el mensaje antes de redireccionar y lo muestra
						FacesContext context = FacesContext.getCurrentInstance();
						context.getExternalContext().getFlash().setKeepMessages(true);

					} else {
						this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");
					}

				} else if (this.personal.getTipoUsuario() != null && this.personal.getTipoUsuario().equals(IConstantes.ROL_TECNICO)) {

					Tecnico tecnico = new Tecnico();
					tecnico.setClave(this.personal.getClave());
					tecnico.setCorreoElectronico(this.personal.getCorreoElectronico().trim());
					tecnico.setEstadoVigencia(IConstantes.ACTIVO);

					tecnicos = IConsultasDAO.getTecnicos(tecnico);

					if (tecnicos != null && tecnicos.size() > 0 && tecnicos.get(0) != null && tecnicos.get(0).getId() != null) {

						this.personalSesion = new Personal();
						this.personalSesion.setCorreoElectronico(tecnicos.get(0).getCorreoElectronico().trim());
						this.personalSesion.setNombreCompleto(tecnicos.get(0).getNombres().trim());
						this.personalSesion.setTipoUsuario(this.personal.getTipoUsuario());
						this.personalSesion.setId(tecnicos.get(0).getId());

						this.personalSesion.setInformeMantenimiento(true);
						this.personalSesion.setEquipos(false);
						this.personalSesion.setReporteFallas(false);
						this.personalSesion.setCronograma(false);
						this.personalSesion.setIndicadoresGestion(false);

						this.mostrarMensajeGlobal("ingresoCorrecto", "exito");
						this.personal = null;

						// Guarda el mensaje antes de redireccionar y lo muestra
						FacesContext context = FacesContext.getCurrentInstance();
						context.getExternalContext().getFlash().setKeepMessages(true);

					} else {
						this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");
					}

				} else if (this.personal.getTipoUsuario() != null && this.personal.getTipoUsuario().equals(IConstantes.ROL_CLIENTE)) {

					Cliente cliente = new Cliente();
					cliente.setClave(this.personal.getClave());
					cliente.setCorreoElectronico(this.personal.getCorreoElectronico().trim());
					cliente.setEstadoVigencia(IConstantes.ACTIVO);

					clientes = IConsultasDAO.getClientes(cliente);

					if (clientes != null && clientes.size() > 0 && clientes.get(0) != null && clientes.get(0).getId() != null) {

						this.personalSesion = new Personal();
						this.personalSesion.setCorreoElectronico(clientes.get(0).getCorreoElectronico().trim());
						this.personalSesion.setNombreCompleto(clientes.get(0).getCliente().trim());
						this.personalSesion.setTipoUsuario(this.personal.getTipoUsuario());
						this.personalSesion.setId(clientes.get(0).getId());

						this.personalSesion.setInformeMantenimiento(false);
						this.personalSesion.setEquipos(false);
						this.personalSesion.setReporteFallas(false);
						this.personalSesion.setCronograma(false);
						this.personalSesion.setIndicadoresGestion(false);

						if (clientes.stream().anyMatch(p -> p.getHojaVida() != null && p.getHojaVida().trim().equals(IConstantes.AFIRMACION))) {
							this.personalSesion.setEquipos(true);
						}

						if (clientes.stream().anyMatch(p -> p.getReporteFallas() != null && p.getReporteFallas().trim().equals(IConstantes.AFIRMACION))) {
							this.personalSesion.setReporteFallas(true);
						}

						if (clientes.stream().anyMatch(p -> p.getCronograma() != null && p.getCronograma().trim().equals(IConstantes.AFIRMACION))) {
							this.personalSesion.setCronograma(true);
						}

						if (clientes.stream().anyMatch(p -> p.getIndicadoresGestion() != null && p.getIndicadoresGestion().trim().equals(IConstantes.AFIRMACION))) {
							this.personalSesion.setIndicadoresGestion(true);
						}

						this.mostrarMensajeGlobal("ingresoCorrecto", "exito");
						this.personal = null;

						// Guarda el mensaje antes de redireccionar y lo muestra
						FacesContext context = FacesContext.getCurrentInstance();
						context.getExternalContext().getFlash().setKeepMessages(true);

					} else {

						this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");
					}

				} else {

					this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");

				}

			} else {

				this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return pagina;
	}

	/**
	 * Permite el acceso del personal
	 */
	public String accederPersonal() {
		String pagina = null;

		List<Administrador> administradores = null;
		List<Tecnico> tecnicos = null;
		List<Cliente> clientes = null;
		try {

			if (this.personal != null && this.personal.getCorreoElectronico() != null && !this.personal.getCorreoElectronico().trim().equals("") && this.personal.getClave() != null && !this.personal.getClave().trim().equals("")) {

				if (this.personal.getTipoUsuario() != null && this.personal.getTipoUsuario().equals(IConstantes.ROL_ADMINISTRADOR)) {

					Administrador admin = new Administrador();
					admin.setClave(this.personal.getClave());
					admin.setCorreoElectronico(this.personal.getCorreoElectronico().trim());
					admin.setEstadoVigencia(IConstantes.ACTIVO);
					administradores = IConsultasDAO.getAdministradores(admin);

					if (administradores != null && administradores.size() > 0 && administradores.get(0) != null && administradores.get(0).getId() != null) {

						this.personalSesion = new Personal();
						this.personalSesion.setCorreoElectronico(administradores.get(0).getCorreoElectronico().trim());
						this.personalSesion.setNombreCompleto(administradores.get(0).getNombres().trim());
						this.personalSesion.setTipoUsuario(this.personal.getTipoUsuario());
						this.personalSesion.setId(administradores.get(0).getId());

						// admin todos los permisos
						this.personalSesion.setEquipos(true);
						this.personalSesion.setInformeMantenimiento(true);
						this.personalSesion.setReporteFallas(true);
						this.personalSesion.setCronograma(true);
						this.personalSesion.setIndicadoresGestion(true);

						this.mostrarMensajeGlobal("ingresoCorrecto", "exito");
						this.personal = null;

						pagina = IConstantes.PAGINA_HOME;

						// Guarda el mensaje antes de redireccionar y lo muestra
						FacesContext context = FacesContext.getCurrentInstance();
						context.getExternalContext().getFlash().setKeepMessages(true);

					} else {
						this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");
					}

				} else if (this.personal.getTipoUsuario() != null && this.personal.getTipoUsuario().equals(IConstantes.ROL_TECNICO)) {

					Tecnico tecnico = new Tecnico();
					tecnico.setClave(this.personal.getClave());
					tecnico.setCorreoElectronico(this.personal.getCorreoElectronico().trim());
					tecnico.setEstadoVigencia(IConstantes.ACTIVO);

					tecnicos = IConsultasDAO.getTecnicos(tecnico);

					if (tecnicos != null && tecnicos.size() > 0 && tecnicos.get(0) != null && tecnicos.get(0).getId() != null) {

						this.personalSesion = new Personal();
						this.personalSesion.setCorreoElectronico(tecnicos.get(0).getCorreoElectronico().trim());
						this.personalSesion.setNombreCompleto(tecnicos.get(0).getNombres().trim());
						this.personalSesion.setTipoUsuario(this.personal.getTipoUsuario());
						this.personalSesion.setId(tecnicos.get(0).getId());

						this.personalSesion.setInformeMantenimiento(true);
						this.personalSesion.setEquipos(false);
						this.personalSesion.setReporteFallas(false);
						this.personalSesion.setCronograma(false);
						this.personalSesion.setIndicadoresGestion(false);

						this.mostrarMensajeGlobal("ingresoCorrecto", "exito");
						this.personal = null;

						pagina = IConstantes.PAGINA_HOME;

						// Guarda el mensaje antes de redireccionar y lo muestra
						FacesContext context = FacesContext.getCurrentInstance();
						context.getExternalContext().getFlash().setKeepMessages(true);

					} else {
						this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");
					}

				} else if (this.personal.getTipoUsuario() != null && this.personal.getTipoUsuario().equals(IConstantes.ROL_CLIENTE)) {

					Cliente cliente = new Cliente();
					cliente.setClave(this.personal.getClave());
					cliente.setCorreoElectronico(this.personal.getCorreoElectronico().trim());
					cliente.setEstadoVigencia(IConstantes.ACTIVO);

					clientes = IConsultasDAO.getClientes(cliente);

					if (clientes != null && clientes.size() > 0 && clientes.get(0) != null && clientes.get(0).getId() != null) {

						this.personalSesion = new Personal();
						this.personalSesion.setCorreoElectronico(clientes.get(0).getCorreoElectronico().trim());
						this.personalSesion.setNombreCompleto(clientes.get(0).getCliente().trim());
						this.personalSesion.setTipoUsuario(this.personal.getTipoUsuario());
						this.personalSesion.setId(clientes.get(0).getId());

						this.personalSesion.setInformeMantenimiento(false);
						this.personalSesion.setEquipos(false);
						this.personalSesion.setReporteFallas(false);
						this.personalSesion.setCronograma(false);
						this.personalSesion.setIndicadoresGestion(false);

						if (clientes.stream().anyMatch(p -> p.getHojaVida() != null && p.getHojaVida().trim().equals(IConstantes.AFIRMACION))) {
							this.personalSesion.setEquipos(true);
						}

						if (clientes.stream().anyMatch(p -> p.getReporteFallas() != null && p.getReporteFallas().trim().equals(IConstantes.AFIRMACION))) {
							this.personalSesion.setReporteFallas(true);
						}

						if (clientes.stream().anyMatch(p -> p.getCronograma() != null && p.getCronograma().trim().equals(IConstantes.AFIRMACION))) {
							this.personalSesion.setCronograma(true);
						}

						if (clientes.stream().anyMatch(p -> p.getIndicadoresGestion() != null && p.getIndicadoresGestion().trim().equals(IConstantes.AFIRMACION))) {
							this.personalSesion.setIndicadoresGestion(true);
						}

						this.mostrarMensajeGlobal("ingresoCorrecto", "exito");
						this.personal = null;

						pagina = IConstantes.PAGINA_HOME;

						// Guarda el mensaje antes de redireccionar y lo muestra
						FacesContext context = FacesContext.getCurrentInstance();
						context.getExternalContext().getFlash().setKeepMessages(true);

					} else {

						this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");
					}

				} else {

					this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");

				}

			} else {

				this.mostrarMensajeGlobal("noCoincideCredenciales", "advertencia");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return pagina;
	}

	/**
	 * Obtiene los datos de sesion de un personal
	 * 
	 * @return personalSesion
	 */
	public Personal getPersonalSesion() {
		try {
			if (this.personalSesion == null) {
				this.personalSesion = new Personal();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return personalSesion;
	}

	/**
	 * Establece los datos de sesión de un personal
	 * 
	 * @param personalSesion
	 */
	public void setPersonalSesion(Personal personalSesion) {
		this.personalSesion = personalSesion;
	}

	/**
	 * Obtiene el personal que se logeua
	 * 
	 * @return personal
	 */
	public Personal getPersonal() {
		try {
			if (this.personal == null) {
				this.personal = new Personal();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return personal;
	}

	/**
	 * Establece el personal que se loguea
	 * 
	 * @param personal
	 */
	public void setPersonal(Personal personal) {
		this.personal = personal;
	}

	/**
	 * Obtiene el listado de clientes para sesión
	 * 
	 * @return itemsClientes
	 */
	public List<SelectItem> getItemsClientes() {
		List<SelectItem> itemsClientes = new ArrayList<SelectItem>();
		try {
			if (this.personalSesion != null && this.personalSesion.getId() != null && this.personalSesion.getTipoUsuario().equals(IConstantes.ROL_CLIENTE)) {
				Cliente cliente = new Cliente();
				cliente.setEstadoVigencia(IConstantes.ACTIVO);
				cliente.setCorreoElectronico(this.personalSesion.getCorreoElectronico());
				List<Cliente> clientes = IConsultasDAO.getClientes(cliente);
				if (clientes != null && clientes.size() > 0) {

					if (clientes.size() > 1) {
						itemsClientes.add(new SelectItem("", this.getMensaje("comboVacio")));
					}

					clientes.forEach(p -> itemsClientes.add(new SelectItem(p.getId(), p.getCliente() + "," + p.getUbicacion())));

				}

			} else {
				itemsClientes.add(new SelectItem("", this.getMensaje("comboVacio")));
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsClientes;
	}

	public String getQr() {
		return qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}
	
	
	

}
