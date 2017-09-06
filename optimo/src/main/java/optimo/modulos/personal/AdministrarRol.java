package optimo.modulos.personal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import optimo.Conexion;
import optimo.beans.Administrador;
import optimo.beans.Cliente;
import optimo.beans.Tecnico;
import optimo.generales.ConsultarFuncionesAPI;
import optimo.generales.IConstantes;
import optimo.generales.IEmail;
import optimo.modulos.IConsultasDAO;

@ManagedBean
@ViewScoped
public class AdministrarRol extends ConsultarFuncionesAPI implements Serializable {

	/**   
	 * 
	 */
	private static final long		serialVersionUID	= 3335261820496774072L;
	private Administrador				administrador;
	private Administrador				administradorTransaccion;
	private Tecnico							tecnico;
	private Tecnico							tecnicoTransaccion;
	private Cliente							clienteConsulta;
	private Cliente							cliente;
	private Cliente							clienteTransaccion;
	private List<Administrador>	administradores;
	private List<Tecnico>				tecnicos;
	private List<Cliente>				clientes;

	// privados
	
	


	/**
	 * Obtiene una clave aleatoria numérica de n dígitos
	 * 
	 * @return clave
	 */
	public String getClaveAleatoria() {
		String clave = "";
		int n = 0;
		try {
			for (int i = 1; i <= IConstantes.NUMERO_DIGITOS_CLAVE_ALEATORIA.intValue(); i++) {
				n = (int) (10.0 * Math.random()) + 0;
				clave = clave + String.valueOf(n);

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clave;

	}

	/**
	 * Valida un administrador
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isValidoAdministrador(String aTransaccion) {
		boolean ok = true;

		if (aTransaccion.equals("C")) {
			if (this.administradores != null && this.administradores.size() > 0 && this.administradores.stream().anyMatch(i -> i.getCorreoElectronico().trim().toLowerCase().equals(this.administrador.getCorreoElectronico().trim().toLowerCase()))) {
				ok = false;
				this.mostrarMensajeGlobal("correoExistenteAdministrador", "advertencia");
			}
			if (this.isVacio(this.administrador.getNombres())) {
				ok = false;
				this.mostrarMensajeGlobal("nombresVacios", "advertencia");
			}

			if (this.isVacio(this.administrador.getCorreoElectronico())) {
				ok = false;
				this.mostrarMensajeGlobal("correoVacio", "advertencia");
			}

		} else {

			if (this.administradores != null && this.administradores.size() > 0 && this.administradores.stream().anyMatch(i -> i.getId() != this.administradorTransaccion.getId() && i.getCorreoElectronico().trim().toLowerCase().equals(this.administradorTransaccion.getCorreoElectronico().trim().toLowerCase()))) {
				ok = false;
				this.mostrarMensajeGlobal("correoExistenteAdministrador", "advertencia");
			}

			if (this.isVacio(this.administradorTransaccion.getNombres())) {
				ok = false;
				this.mostrarMensajeGlobal("nombresVacios", "advertencia");
			}

			if (this.isVacio(this.administradorTransaccion.getCorreoElectronico())) {
				ok = false;
				this.mostrarMensajeGlobal("correoVacio", "advertencia");
			}

		}

		return ok;
	}

	/**
	 * Detremina si un cliente es válido
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isValidoCliente(String aTransaccion) {
		boolean ok = true;

		try {

			boolean clienteExistente = false;
			String parteA = null;
			String parteB = null;
			List<Cliente> clientes = null;

			if (aTransaccion.equals("C")) {

				// analiza nit
				if (!this.isVacio(this.cliente.getNit())) {
					String[] partes = this.cliente.getNit().split("-");

					if (partes != null && partes.length == 1) {
						parteA = this.cliente.getNit().toUpperCase().trim();
						this.cliente.setNit(parteA);

					} else {
						parteA = partes[0].toUpperCase().trim();
						parteB = partes[0].toUpperCase().trim() + "-" + partes[1].toUpperCase().trim();

						this.cliente.setNit(parteB);
					}

					clienteExistente = IConsultasDAO.getClienteExistente(parteA);

				}

				if (this.isVacio(this.cliente.getCorreoElectronico())) {
					ok = false;
					this.mostrarMensajeGlobal("correoVacio", "advertencia");
				} else {
					Cliente clienteTemp = new Cliente();
					clienteTemp.setCorreoElectronico(this.cliente.getCorreoElectronico().trim());
					clientes = IConsultasDAO.getClientes(clienteTemp);

					if (clientes != null && clientes.size() > 0 && clientes.stream().anyMatch(i -> i.getCorreoElectronico().trim().toLowerCase().equals(this.cliente.getCorreoElectronico().trim().toLowerCase()))) {
						if (!clienteExistente) {
							ok = false;
							this.mostrarMensajeGlobal("correoExistenteCliente", "advertencia");
						}
					}

				}

				if (this.isVacio(this.cliente.getCliente())) {
					ok = false;
					this.mostrarMensajeGlobal("clienteVacio", "advertencia");
				}

				if (this.isVacio(this.cliente.getNit())) {
					ok = false;
					this.mostrarMensajeGlobal("nitVacio", "advertencia");
				} else {
					this.cliente.setNit(this.cliente.getNit().trim());
				}

				if (this.isVacio(this.cliente.getRepresentante())) {
					ok = false;
					this.mostrarMensajeGlobal("representanteVacio", "advertencia");
				}

				if (this.isVacio(this.cliente.getTelefono())) {
					ok = false;
					this.mostrarMensajeGlobal("telefonoVacio", "advertencia");
				}

				if (this.isVacio(this.cliente.getDireccionFisica())) {
					ok = false;
					this.mostrarMensajeGlobal("direccionVacio", "advertencia");
				}
				if (this.isVacio(this.cliente.getCargo())) {
					ok = false;
					this.mostrarMensajeGlobal("cargoVacio", "advertencia");
				}

			} else {

				if (this.isVacio(this.clienteTransaccion.getCorreoElectronico())) {
					ok = false;
					this.mostrarMensajeGlobal("correoVacio", "advertencia");
				} else {

					Cliente clienteTemp = new Cliente();
					clienteTemp.setCorreoElectronico(this.clienteTransaccion.getCorreoElectronico().trim());
					clientes = IConsultasDAO.getClientes(clienteTemp);

					if (!this.clienteTransaccion.getCorreoElectronico().trim().toLowerCase().equals(this.clienteTransaccion.gettCopiaCorreo().trim().toLowerCase()) && clientes != null && clientes.size() > 0 && clientes.stream().anyMatch(i -> i.getId().intValue() != this.clienteTransaccion.getId().intValue() && i.getCorreoElectronico().trim().toLowerCase().equals(this.clienteTransaccion.getCorreoElectronico().trim().toLowerCase()))) {
						ok = false;
						this.mostrarMensajeGlobal("correoExistenteCliente", "advertencia");
					}

				}

				if (this.isVacio(this.clienteTransaccion.getCliente())) {
					ok = false;
					this.mostrarMensajeGlobal("clienteVacio", "advertencia");
				}

				if (this.isVacio(this.clienteTransaccion.getNit())) {
					ok = false;
					this.mostrarMensajeGlobal("nitVacio", "advertencia");
				}

				if (this.isVacio(this.clienteTransaccion.getRepresentante())) {
					ok = false;
					this.mostrarMensajeGlobal("representanteVacio", "advertencia");
				}

				if (this.isVacio(this.clienteTransaccion.getTelefono())) {
					ok = false;
					this.mostrarMensajeGlobal("telefonoVacio", "advertencia");
				}

				if (this.isVacio(this.clienteTransaccion.getDireccionFisica())) {
					ok = false;
					this.mostrarMensajeGlobal("direccionVacio", "advertencia");
				}
				if (this.isVacio(this.clienteTransaccion.getCargo())) {
					ok = false;
					this.mostrarMensajeGlobal("cargoVacio", "advertencia");
				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return ok;
	}

	/**
	 * Valida un tecnico
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isValidoTecnico(String aTransaccion) {
		boolean ok = true;

		if (aTransaccion.equals("C")) {
			if (this.tecnicos != null && this.tecnicos.size() > 0 && this.tecnicos.stream().anyMatch(i -> i.getCorreoElectronico().trim().toLowerCase().equals(this.tecnico.getCorreoElectronico().trim().toLowerCase()))) {
				ok = false;
				this.mostrarMensajeGlobal("correoExistenteTecnico", "advertencia");
			}
			if (this.isVacio(this.tecnico.getNombres())) {
				ok = false;
				this.mostrarMensajeGlobal("nombresVacios", "advertencia");
			}

			if (this.isVacio(this.tecnico.getCorreoElectronico())) {
				ok = false;
				this.mostrarMensajeGlobal("correoVacio", "advertencia");
			}

			if (this.isVacio(this.tecnico.getTelefono())) {
				ok = false;
				this.mostrarMensajeGlobal("telefonoVacio", "advertencia");
			}

			if (this.isVacio(this.tecnico.getCargo())) {
				ok = false;
				this.mostrarMensajeGlobal("cargoVacio", "advertencia");
			}

		} else {

			if (this.tecnicos != null && this.tecnicos.size() > 0 && this.tecnicos.stream().anyMatch(i -> i.getId() != this.tecnicoTransaccion.getId() && i.getCorreoElectronico().trim().toLowerCase().equals(this.tecnicoTransaccion.getCorreoElectronico().trim().toLowerCase()))) {
				ok = false;
				this.mostrarMensajeGlobal("correoExistenteTecnico", "advertencia");
			}
			if (this.isVacio(this.tecnicoTransaccion.getNombres())) {
				ok = false;
				this.mostrarMensajeGlobal("nombresVacios", "advertencia");
			}

			if (this.isVacio(this.tecnicoTransaccion.getCorreoElectronico())) {
				ok = false;
				this.mostrarMensajeGlobal("correoVacio", "advertencia");
			}

			if (this.isVacio(this.tecnicoTransaccion.getTelefono())) {
				ok = false;
				this.mostrarMensajeGlobal("telefonoVacio", "advertencia");
			}
			if (this.isVacio(this.tecnicoTransaccion.getCargo())) {
				ok = false;
				this.mostrarMensajeGlobal("cargoVacio", "advertencia");
			}

		}

		return ok;
	}

	// publicos

	/**
	 * Consulta los clientes
	 */
	public void consultarClientes() {
		Conexion conexion = new Conexion();
		try {

			this.clientes = IConsultasDAO.getClientes(this.clienteConsulta);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Permite crear un cliente
	 */
	public void crearCliente() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoCliente("C")) {

				Cliente clienteTemp = new Cliente();
				clienteTemp.setCorreoElectronico(this.cliente.getCorreoElectronico().trim());
				List<Cliente> clientes = IConsultasDAO.getClientes(clienteTemp);

				conexion.setAutoCommitBD(false);

				this.cliente.setCliente(this.getSinEspacios(this.cliente.getCliente()));
				this.cliente.setNit(this.getSinEspacios(this.cliente.getNit()));
				this.cliente.setRepresentante(this.getSinEspacios(this.cliente.getRepresentante()));
				this.cliente.setCorreoElectronico(this.cliente.getCorreoElectronico().trim());
				this.cliente.setTelefono(this.cliente.getTelefono().trim());
				this.cliente.setEstadoVigencia(IConstantes.ACTIVO);

				if (clientes != null && clientes.size() > 0) {
					this.cliente.setClave(clientes.get(0).getClave());
				} else {
					this.cliente.setClave(this.getClaveAleatoria());
				}

				this.cliente.getCamposBD();

				this.cliente.getEstructuraTabla().getPersistencia().put("informe_mantenimiento", IConstantes.NEGACION);
				this.cliente.getEstructuraTabla().getPersistencia().put("reporte_fallas", IConstantes.NEGACION);
				this.cliente.getEstructuraTabla().getPersistencia().put("cronograma", IConstantes.NEGACION);
				this.cliente.getEstructuraTabla().getPersistencia().put("indicadores_gestion", IConstantes.NEGACION);
				this.cliente.getEstructuraTabla().getPersistencia().put("hoja_vida", IConstantes.NEGACION);

				conexion.insertarBD(this.cliente.getEstructuraTabla().getTabla(), this.cliente.getEstructuraTabla().getPersistencia());

				this.cliente.setId(conexion.getUltimoSerialTransaccion(this.cliente.getEstructuraTabla().getTabla()));

				this.cliente.getCamposBD();

				Map<String, Object> permisos = new HashMap<String, Object>();
				if (this.cliente.gettPermisos() != null && this.cliente.gettPermisos().length > 0) {

					for (String p : this.cliente.gettPermisos()) {
						permisos.put(p, IConstantes.AFIRMACION);
					}

					conexion.actualizarBD(this.cliente.getEstructuraTabla().getTabla(), permisos, this.cliente.getEstructuraTabla().getLlavePrimaria(), null);
				}

				conexion.commitBD();

				this.mostrarMensajeGlobal("creacionExitosa", "exito");
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveAleatoria", this.cliente.getClave()), "exito");

				IEmail.enviarCorreo(this.getMensaje("mensajeClaveAleatoria", this.cliente.getCliente(), this.cliente.getClave()), this.getMensaje("asuntoClaveAleatoria"), this.cliente.getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveCorreoExitoso", this.cliente.getCorreoElectronico()), "exito");

				// reseteo de variables

				this.clienteConsulta = null;
				this.getClienteConsulta();
				this.clienteConsulta.setNit(this.cliente.getNit());

				// reseteo de variables
				this.cliente = null;
				this.getCliente();
				this.clientes = null;
				this.consultarClientes();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Cancela y resetea criterios de consulta y resultados antes cargados
	 */
	public void cancelarConsulta() {
		this.clienteConsulta = null;
		this.getClienteConsulta();

		this.clientes = null;
	}

	/**
	 * Limpia la firma del cliente
	 */
	public void limpiarFirmaEdicionCliente() {
		this.clienteTransaccion.setFirma(null);
	}

	/**
	 * Limpia la firma del técnico
	 */
	public void limpiarFirmaCreacionCliente() {
		this.cliente.setFirma(null);
	}

	/**
	 * Limpia la firma del técnico
	 */
	public void limpiarFirmaEdicionTecnico() {
		this.tecnicoTransaccion.setFirma(null);
	}

	/**
	 * Limpia la firma del técnico
	 */
	public void limpiarFirmaCreacionTecnico() {
		this.tecnico.setFirma(null);
	}

	/**
	 * Permite crear un tecnico
	 */
	public void crearTecnico() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoTecnico("C")) {
				conexion.setAutoCommitBD(false);

				this.tecnico.setEstadoVigencia(null);

				this.tecnico.setNombres(this.getSinEspacios(this.tecnico.getNombres()));

				this.tecnico.setCorreoElectronico(this.tecnico.getCorreoElectronico().trim());
				this.tecnico.setTelefono(this.tecnico.getTelefono().trim());
				this.tecnico.setCargo(this.tecnico.getCargo().trim());
				this.tecnico.setClave(this.getClaveAleatoria());
				this.tecnico.setEstadoVigencia(IConstantes.ACTIVO);

				this.tecnico.getCamposBD();

				this.tecnico.getEstructuraTabla().getPersistencia().put("informe_mantenimiento", IConstantes.NEGACION);
				this.tecnico.getEstructuraTabla().getPersistencia().put("reporte_fallas", IConstantes.NEGACION);
				this.tecnico.getEstructuraTabla().getPersistencia().put("cronograma", IConstantes.NEGACION);

				conexion.insertarBD(this.tecnico.getEstructuraTabla().getTabla(), this.tecnico.getEstructuraTabla().getPersistencia());

				this.tecnico.setId(conexion.getUltimoSerialTransaccion(this.tecnico.getEstructuraTabla().getTabla()));

				this.tecnico.getCamposBD();

				conexion.commitBD();

				this.mostrarMensajeGlobal("creacionExitosa", "exito");
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveAleatoria", this.tecnico.getClave()), "exito");

				IEmail.enviarCorreo(this.getMensaje("mensajeClaveAleatoria", this.tecnico.getNombres(), this.tecnico.getClave()), this.getMensaje("asuntoClaveAleatoria"), this.tecnico.getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveCorreoExitoso", this.tecnico.getCorreoElectronico()), "exito");

				// reseteo de variables
				this.tecnico = null;
				this.getTecnico();
				this.tecnicos = null;
				this.getTecnicos();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Crea un nuevo administrador del software
	 */
	public void crearAdministrador() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoAdministrador("C")) {
				conexion.setAutoCommitBD(false);

				this.administrador.setEstadoVigencia(IConstantes.ACTIVO);
				this.administrador.setNombres(this.getSinEspacios(this.administrador.getNombres()));

				this.administrador.setCorreoElectronico(this.administrador.getCorreoElectronico().trim());
				this.administrador.setClave(this.getClaveAleatoria());

				this.administrador.getCamposBD();

				conexion.insertarBD(this.administrador.getEstructuraTabla().getTabla(), this.administrador.getEstructuraTabla().getPersistencia());
				conexion.commitBD();

				this.mostrarMensajeGlobal("creacionExitosa", "exito");
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveAleatoria", this.administrador.getClave()), "exito");

				IEmail.enviarCorreo(this.getMensaje("mensajeClaveAleatoria", this.administrador.getNombres(), this.administrador.getClave()), this.getMensaje("asuntoClaveAleatoria"), this.administrador.getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveCorreoExitoso", this.administrador.getCorreoElectronico()), "exito");

				// reseteo de variables
				this.administrador = null;
				this.getAdministrador();
				this.administradores = null;
				this.getAdministradores();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Genera una nueva clave aleatoria para el administrador
	 */
	public void generarClaveAdministrador() {

		Conexion conexion = new Conexion();

		try {
			boolean ok = true;
			if (this.administradorTransaccion != null && this.administradorTransaccion.gettTipoClave() != null && this.administradorTransaccion.gettTipoClave().equals("A")) {

				this.administradorTransaccion.setClave(this.getClaveAleatoria());

			} else {

				if (this.isVacio(this.administradorTransaccion.getClave())) {
					ok = false;
				}

			}
			if (ok) {
				conexion.setAutoCommitBD(false);

				this.administradorTransaccion.getCamposBD();

				conexion.actualizarBD(this.administradorTransaccion.getEstructuraTabla().getTabla(), this.administradorTransaccion.getEstructuraTabla().getPersistencia(), this.administradorTransaccion.getEstructuraTabla().getLlavePrimaria(), null);
				conexion.commitBD();

				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveAleatoria", this.administradorTransaccion.getClave()), "exito");

				IEmail.enviarCorreo(this.getMensaje("mensajeClaveAleatoria", this.administradorTransaccion.getNombres(), this.administradorTransaccion.getClave()), this.getMensaje("asuntoClaveAleatoria"), this.administradorTransaccion.getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveCorreoExitoso", this.administradorTransaccion.getCorreoElectronico()), "exito");

				this.cerrarModal("panelClaveAdministrador");

				// reseteo de variables
				this.administradorTransaccion = null;
				this.getAdministradorTransaccion();
				this.administradores = null;
				this.getAdministradores();

			} else {

				this.mostrarMensajeGlobal("claveEnBlanco", "error");
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Genera la clave del cliente
	 */
	public void generarClaveCliente() {

		Conexion conexion = new Conexion();

		try {
			boolean ok = true;
			if (this.clienteTransaccion != null && this.clienteTransaccion.gettTipoClave() != null && this.clienteTransaccion.gettTipoClave().equals("A")) {

				this.clienteTransaccion.setClave(this.getClaveAleatoria());

			} else {

				if (this.isVacio(this.clienteTransaccion.getClave())) {
					ok = false;
				}

			}
			if (ok) {
				conexion.setAutoCommitBD(false);

				this.clienteTransaccion.getCamposBD();

				Map<String, Object> camposActualizar = new HashMap<String, Object>();
				Map<String, Object> condiciones = new HashMap<String, Object>();

				camposActualizar.put("clave", this.clienteTransaccion.getClave());
				condiciones.put("correo_electronico", this.clienteTransaccion.getCorreoElectronico());

				conexion.actualizarBD(this.clienteTransaccion.getEstructuraTabla().getTabla(), camposActualizar, condiciones, null);
				conexion.commitBD();

				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveAleatoria", this.clienteTransaccion.getClave()), "exito");

				IEmail.enviarCorreo(this.getMensaje("mensajeClaveAleatoria", this.clienteTransaccion.getCliente(), this.clienteTransaccion.getClave()), this.getMensaje("asuntoClaveAleatoria"), this.clienteTransaccion.getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveCorreoExitoso", this.clienteTransaccion.getCorreoElectronico()), "exito");

				this.cerrarModal("panelClaveCliente");

				// reseteo de variables
				this.clienteTransaccion = null;
				this.getClienteTransaccion();
				this.clientes = null;
				consultarClientes();

			} else {

				this.mostrarMensajeGlobal("claveEnBlanco", "error");
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Genera una nueva clave para el técnico
	 */
	public void generarClaveTecnico() {

		Conexion conexion = new Conexion();

		try {
			boolean ok = true;
			if (this.tecnicoTransaccion != null && this.tecnicoTransaccion.gettTipoClave() != null && this.tecnicoTransaccion.gettTipoClave().equals("A")) {

				this.tecnicoTransaccion.setClave(this.getClaveAleatoria());

			} else {

				if (this.isVacio(this.tecnicoTransaccion.getClave())) {
					ok = false;
				}

			}
			if (ok) {
				conexion.setAutoCommitBD(false);

				this.tecnicoTransaccion.getCamposBD();

				conexion.actualizarBD(this.tecnicoTransaccion.getEstructuraTabla().getTabla(), this.tecnicoTransaccion.getEstructuraTabla().getPersistencia(), this.tecnicoTransaccion.getEstructuraTabla().getLlavePrimaria(), null);
				conexion.commitBD();

				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveAleatoria", this.tecnicoTransaccion.getClave()), "exito");

				IEmail.enviarCorreo(this.getMensaje("mensajeClaveAleatoria", this.tecnicoTransaccion.getNombres(), this.tecnicoTransaccion.getClave()), this.getMensaje("asuntoClaveAleatoria"), this.tecnicoTransaccion.getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("claveCorreoExitoso", this.tecnicoTransaccion.getCorreoElectronico()), "exito");

				this.cerrarModal("panelClaveTecnico");

				// reseteo de variables
				this.tecnicoTransaccion = null;
				this.getTecnicoTransaccion();
				this.tecnicos = null;
				this.getTecnicos();

			} else {

				this.mostrarMensajeGlobal("claveEnBlanco", "error");
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Edita un registro de administrador de software
	 */
	public void editarAdministrador() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoAdministrador("E")) {
				conexion.setAutoCommitBD(false);

				this.administradorTransaccion.setNombres(this.getSinEspacios(this.administradorTransaccion.getNombres()));

				this.administradorTransaccion.getCamposBD();
				conexion.actualizarBD(this.administradorTransaccion.getEstructuraTabla().getTabla(), this.administradorTransaccion.getEstructuraTabla().getPersistencia(), this.administradorTransaccion.getEstructuraTabla().getLlavePrimaria(), null);
				conexion.commitBD();
				this.mostrarMensajeGlobal("edicionExitosa", "exito");
				this.mostrarMensajeGlobal("algunosCambios", "advertencia");
				this.cerrarModal("panelEdicionAdministrador");

				// reseteo de variables
				this.administradorTransaccion = null;
				this.getAdministradorTransaccion();
				this.administradores = null;
				this.getAdministradores();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Edita los datos de un cliente
	 */
	public void editarCliente() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoCliente("E")) {
				conexion.setAutoCommitBD(false);

				this.clienteTransaccion.setCliente(this.getSinEspacios(this.clienteTransaccion.getCliente()));
				this.clienteTransaccion.setNit(this.getSinEspacios(this.clienteTransaccion.getNit()));
				this.clienteTransaccion.setRepresentante(this.getSinEspacios(this.clienteTransaccion.getRepresentante()));
				this.clienteTransaccion.setCorreoElectronico(this.getSinEspacios(this.clienteTransaccion.getCorreoElectronico()));
				this.clienteTransaccion.setTelefono(this.getSinEspacios(this.clienteTransaccion.getTelefono()));

				this.clienteTransaccion.getCamposBD();

				this.clienteTransaccion.getEstructuraTabla().getPersistencia().put("informe_mantenimiento", IConstantes.NEGACION);
				this.clienteTransaccion.getEstructuraTabla().getPersistencia().put("reporte_fallas", IConstantes.NEGACION);
				this.clienteTransaccion.getEstructuraTabla().getPersistencia().put("cronograma", IConstantes.NEGACION);
				this.clienteTransaccion.getEstructuraTabla().getPersistencia().put("indicadores_gestion", IConstantes.NEGACION);
				this.clienteTransaccion.getEstructuraTabla().getPersistencia().put("hoja_vida", IConstantes.NEGACION);

				conexion.actualizarBD(this.clienteTransaccion.getEstructuraTabla().getTabla(), this.clienteTransaccion.getEstructuraTabla().getPersistencia(), this.clienteTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

				Map<String, Object> permisos = new HashMap<String, Object>();
				if (this.clienteTransaccion.gettPermisos() != null && this.clienteTransaccion.gettPermisos().length > 0) {

					for (String p : this.clienteTransaccion.gettPermisos()) {
						permisos.put(p, IConstantes.AFIRMACION);
					}

					conexion.actualizarBD(this.clienteTransaccion.getEstructuraTabla().getTabla(), permisos, this.clienteTransaccion.getEstructuraTabla().getLlavePrimaria(), null);
				}

				conexion.commitBD();
				this.mostrarMensajeGlobal("edicionExitosa", "exito");
				this.mostrarMensajeGlobal("algunosCambios", "advertencia");
				this.cerrarModal("panelEdicionCliente");

				// reseteo de variables
				this.clienteTransaccion = null;
				this.getClienteTransaccion();
				this.clientes = null;
				this.consultarClientes();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Edita los datos de un técnico
	 */
	public void editarTecnico() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoTecnico("E")) {
				conexion.setAutoCommitBD(false);

				this.tecnicoTransaccion.setNombres(this.getSinEspacios(this.tecnicoTransaccion.getNombres()));
				this.tecnicoTransaccion.setTelefono(this.getSinEspacios(this.tecnicoTransaccion.getTelefono()));
				this.tecnicoTransaccion.setCargo(this.getSinEspacios(this.tecnicoTransaccion.getCargo()));

				this.tecnicoTransaccion.getCamposBD();

				this.tecnicoTransaccion.getEstructuraTabla().getPersistencia().put("informe_mantenimiento", IConstantes.NEGACION);
				this.tecnicoTransaccion.getEstructuraTabla().getPersistencia().put("reporte_fallas", IConstantes.NEGACION);
				this.tecnico.getEstructuraTabla().getPersistencia().put("cronograma", IConstantes.NEGACION);

				conexion.actualizarBD(this.tecnicoTransaccion.getEstructuraTabla().getTabla(), this.tecnicoTransaccion.getEstructuraTabla().getPersistencia(), this.tecnicoTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

				conexion.commitBD();

				this.mostrarMensajeGlobal("edicionExitosa", "exito");
				this.mostrarMensajeGlobal("algunosCambios", "advertencia");
				this.cerrarModal("panelEdicionTecnico");

				// reseteo de variables
				this.tecnicoTransaccion = null;
				this.getTecnicoTransaccion();
				this.tecnicos = null;
				this.getTecnicos();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Elimina un cliente
	 */
	public void eliminarCliente() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.clienteTransaccion.getCamposBD();
			conexion.eliminarBD(this.clienteTransaccion.getEstructuraTabla().getTabla(), this.clienteTransaccion.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.clienteTransaccion = null;
		this.getClienteTransaccion();
		this.clientes = null;
		this.consultarClientes();

	}

	/**
	 * Elimina el técnico
	 */
	public void eliminarTecnico() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.tecnicoTransaccion.getCamposBD();
			conexion.eliminarBD(this.tecnicoTransaccion.getEstructuraTabla().getTabla(), this.tecnicoTransaccion.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.tecnicoTransaccion = null;
		this.tecnicos = null;
		this.getTecnicos();

	}

	/**
	 * Elimina un registro de adminiistador
	 */
	public void eliminarAdministrador() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.administradorTransaccion.getCamposBD();
			conexion.eliminarBD(this.administradorTransaccion.getEstructuraTabla().getTabla(), this.administradorTransaccion.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.administradorTransaccion = null;
		this.administradores = null;
		this.getAdministradores();

	}

	/**
	 * Este método borra el formulario de creación de un administrador
	 */
	public void cancelarAdministrador() {

		try {
			this.administrador = new Administrador();

			this.administradores = null;
			this.getAdministradores();
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela la creación de un cliente
	 */
	public void cancelarCliente() {

		try {
			this.cliente = null;
			this.getCliente();
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Este método cancela la información de un técnico
	 */
	public void cancelarTecnico() {

		try {
			this.tecnico = new Tecnico();

			this.tecnicos = null;
			this.getTecnicos();
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela un cliente en transacción
	 * 
	 * @param aVista
	 */
	public void cancelarClienteTransaccion(String aVista) {
		try {

			this.clienteTransaccion = new Cliente();
			this.clientes = null;
			this.consultarClientes();

			if (aVista != null && aVista.equals("MODAL_EDITAR_CLIENTE")) {
				this.cerrarModal("panelEdicionCliente");

			} else if (aVista != null && aVista.equals("MODAL_CLAVE_CLIENTE")) {
				this.cerrarModal("panelClaveCliente");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_CLIENTE")) {
				this.cerrarModal("panelEliminacionCliente");

			} else if (aVista != null && aVista.equals("MODAL_VER_CLIENTE")) {
				this.cerrarModal("panelVerCliente");

			}

		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Cancela la edición de un técnico
	 * 
	 * @param aVista
	 */
	public void cancelarTecnicoTransaccion(String aVista) {
		try {

			this.tecnicoTransaccion = new Tecnico();
			this.tecnicos = null;
			this.getTecnicos();

			if (aVista != null && aVista.equals("MODAL_EDITAR_TECNICO")) {
				this.cerrarModal("panelEdicionTecnico");

			} else if (aVista != null && aVista.equals("MODAL_CLAVE_TECNICO")) {
				this.cerrarModal("panelClaveTecnico");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_TECNICO")) {
				this.cerrarModal("panelEliminacionTecnico");

			}
		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Este método borra el formulario de edición de un administrador en
	 * transacción
	 */
	public void cancelarAdministradorTransaccion(String aVista) {
		try {

			this.administradorTransaccion = new Administrador();
			this.administradores = null;
			this.getAdministradores();

			if (aVista != null && aVista.equals("MODAL_EDITAR_ADMINISTRADOR")) {
				this.cerrarModal("panelEdicionAdministrador");

			} else if (aVista != null && aVista.equals("MODAL_CLAVE_ADMINISTRADOR")) {
				this.cerrarModal("panelClaveAdministrador");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_ADMINISTRADOR")) {
				this.cerrarModal("panelEliminacionAdministrador");

			}

		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Asigna un cliente para realizar una transacción
	 * 
	 * @param aCliente
	 * @param aVista
	 */
	public void asignarCliente(Cliente aCliente, String aVista) {

		try {

			this.clienteTransaccion = aCliente;

			List<String> temp = new ArrayList<String>();
			this.clienteTransaccion.settPermisos(null);
			if (this.clienteTransaccion.getCronograma() != null && this.clienteTransaccion.getCronograma().equals(IConstantes.AFIRMACION)) {
				temp.add("cronograma");
			}
			if (this.clienteTransaccion.getInformeMantenimiento() != null && this.clienteTransaccion.getInformeMantenimiento().equals(IConstantes.AFIRMACION)) {
				temp.add("informe_mantenimiento");
			}
			if (this.clienteTransaccion.getReporteFallas() != null && this.clienteTransaccion.getReporteFallas().equals(IConstantes.AFIRMACION)) {
				temp.add("reporte_fallas");
			}
			if (this.clienteTransaccion.getIndicadoresGestion() != null && this.clienteTransaccion.getIndicadoresGestion().equals(IConstantes.AFIRMACION)) {
				temp.add("indicadores_gestion");
			}
			if (this.clienteTransaccion.getHojaVida() != null && this.clienteTransaccion.getHojaVida().equals(IConstantes.AFIRMACION)) {
				temp.add("hoja_vida");
			}

			if (temp.size() > 0) {
				this.clienteTransaccion.settPermisos(temp.toArray(new String[temp.size()]));
			}

			if (aVista != null && aVista.equals("MODAL_EDITAR_CLIENTE")) {
				// que en realidad es copia de correo
				this.clienteTransaccion.settCopiaCorreo(aCliente.getCorreoElectronico());

				this.abrirModal("panelEdicionCliente");
			} else if (aVista != null && aVista.equals("MODAL_CLAVE_CLIENTE")) {
				this.abrirModal("panelClaveCliente");
			} else if (aVista != null && aVista.equals("MODAL_VER_CLIENTE")) {
				this.abrirModal("panelVerCliente");
			} else {
				this.abrirModal("panelEliminacionCliente");
			}

		} catch (

		Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna un técnico para realizar alguna operación sobre el mismo
	 * 
	 * @param aTecnico
	 * @param aVista
	 */
	public void asignarTecnico(Tecnico aTecnico, String aVista) {

		try {

			this.tecnicoTransaccion = aTecnico;
			List<String> temp = new ArrayList<String>();
			this.tecnicoTransaccion.settPermisos(null);
			if (this.tecnicoTransaccion.getCronograma() != null && this.tecnicoTransaccion.getCronograma().equals(IConstantes.AFIRMACION)) {
				temp.add("cronograma");
			}
			if (this.tecnicoTransaccion.getInformeMantenimiento() != null && this.tecnicoTransaccion.getInformeMantenimiento().equals(IConstantes.AFIRMACION)) {
				temp.add("informe_mantenimiento");
			}
			if (this.tecnicoTransaccion.getReporteFallas() != null && this.tecnicoTransaccion.getReporteFallas().equals(IConstantes.AFIRMACION)) {
				temp.add("reporte_fallas");
			}
			if (temp.size() > 0) {
				this.tecnicoTransaccion.settPermisos(temp.toArray(new String[temp.size()]));
			}

			if (aVista != null && aVista.equals("MODAL_EDITAR_TECNICO")) {

				this.abrirModal("panelEdicionTecnico");

			} else if (aVista != null && aVista.equals("MODAL_CLAVE_TECNICO")) {
				this.abrirModal("panelClaveTecnico");

			} else if (aVista != null && aVista.equals("MODAL_VER_TECNICO")) {

				this.abrirModal("panelVerTecnico");

			} else {

				this.abrirModal("panelEliminacionTecnico");

			}

		} catch (

		Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna un administrador para realizar una acción
	 * 
	 * @param aAgrupador
	 * @param aVista
	 */
	public void asignarAdministrador(Administrador aAdministrador, String aVista) {

		try {

			this.administradorTransaccion = aAdministrador;

			if (aVista != null && aVista.equals("MODAL_EDITAR_ADMINISTRADOR")) {
				this.abrirModal("panelEdicionAdministrador");

			} else if (aVista != null && aVista.equals("MODAL_CLAVE_ADMINISTRADOR")) {
				this.abrirModal("panelClaveAdministrador");

			} else {

				this.abrirModal("panelEliminacionAdministrador");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	public List<Administrador> getAdministradores() {
		try {
			if (this.administradores == null) {

				this.administradores = IConsultasDAO.getAdministradores(null);
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return administradores;
	}

	public void setAdministradores(List<Administrador> administradores) {
		this.administradores = administradores;
	}

	public List<Tecnico> getTecnicos() {
		try {
			if (this.tecnicos == null) {

				this.tecnicos = IConsultasDAO.getTecnicos(null);

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return tecnicos;
	}

	public void setTecnicos(List<Tecnico> tecnicos) {
		this.tecnicos = tecnicos;
	}

	/**
	 * Obtiene los clientes
	 * 
	 * @return clientes
	 */
	public List<Cliente> getClientes() {
		return clientes;
	}

	/**
	 * Establece los clientes
	 * 
	 * @param clientes
	 */
	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	// get/sets

	public Cliente getCliente() {
		try {
			if (this.cliente == null) {
				this.cliente = new Cliente();

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Cliente getClienteTransaccion() {
		try {
			if (this.clienteTransaccion == null) {
				this.clienteTransaccion = new Cliente();

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clienteTransaccion;
	}

	public void setClienteTransaccion(Cliente clienteTransaccion) {
		this.clienteTransaccion = clienteTransaccion;
	}

	public Administrador getAdministrador() {
		try {
			if (this.administrador == null) {
				this.administrador = new Administrador();

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return administrador;
	}

	public void setAdministrador(Administrador administrador) {
		this.administrador = administrador;
	}

	public Tecnico getTecnico() {
		try {
			if (this.tecnico == null) {
				this.tecnico = new Tecnico();

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return tecnico;
	}

	public void setTecnico(Tecnico tecnico) {
		this.tecnico = tecnico;
	}

	public Administrador getAdministradorTransaccion() {
		return administradorTransaccion;
	}

	public void setAdministradorTransaccion(Administrador administradorTransaccion) {
		this.administradorTransaccion = administradorTransaccion;
	}

	public Tecnico getTecnicoTransaccion() {
		return tecnicoTransaccion;
	}

	public void setTecnicoTransaccion(Tecnico tecnicoTransaccion) {
		this.tecnicoTransaccion = tecnicoTransaccion;
	}

	public Cliente getClienteConsulta() {
		try {
			if (this.clienteConsulta == null) {
				this.clienteConsulta = new Cliente();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clienteConsulta;
	}

	public void setClienteConsulta(Cliente clienteConsulta) {
		this.clienteConsulta = clienteConsulta;
	}

	/**
	 * Arama el selecitems con los permisos
	 * 
	 * @param aTecnico
	 * @return itemsPermisos
	 */
	public List<SelectItem> getItemsPermisosTecnicoRegistro(Tecnico aTecnico) {
		List<SelectItem> itemsPermisos = new ArrayList<SelectItem>();

		itemsPermisos.add(new SelectItem("", "--"));

		if (aTecnico.getCronograma() != null && aTecnico.getCronograma().equals(IConstantes.AFIRMACION)) {
			itemsPermisos.add(new SelectItem("cronograma", this.getMensaje("cronograma")));
		}
		if (aTecnico.getInformeMantenimiento() != null && aTecnico.getInformeMantenimiento().equals(IConstantes.AFIRMACION)) {
			itemsPermisos.add(new SelectItem("informe_mantenimiento", this.getMensaje("informeMantenimiento")));
		}
		if (aTecnico.getReporteFallas() != null && aTecnico.getReporteFallas().equals(IConstantes.AFIRMACION)) {
			itemsPermisos.add(new SelectItem("reporte_fallas", this.getMensaje("reporteFallas")));
		}

		return itemsPermisos;
	}

	/**
	 * Obtiene fselectitems de cliente
	 * 
	 * @param aCliente
	 * @return itemsPermisos
	 */
	public List<SelectItem> getItemsPermisosClienteRegistro(Cliente aCliente) {
		List<SelectItem> itemsPermisos = new ArrayList<SelectItem>();

		itemsPermisos.add(new SelectItem("", "--"));

		if (aCliente.getCronograma() != null && aCliente.getCronograma().equals(IConstantes.AFIRMACION)) {
			itemsPermisos.add(new SelectItem("cronograma", this.getMensaje("cronograma")));
		}
		if (aCliente.getInformeMantenimiento() != null && aCliente.getInformeMantenimiento().equals(IConstantes.AFIRMACION)) {
			itemsPermisos.add(new SelectItem("informe_mantenimiento", this.getMensaje("informeMantenimiento")));
		}
		if (aCliente.getReporteFallas() != null && aCliente.getReporteFallas().equals(IConstantes.AFIRMACION)) {
			itemsPermisos.add(new SelectItem("reporte_fallas", this.getMensaje("reporteFallas")));
		}
		if (aCliente.getHojaVida() != null && aCliente.getHojaVida().equals(IConstantes.AFIRMACION)) {
			itemsPermisos.add(new SelectItem("hoja_vida", this.getMensaje("hojaVida")));
		}
		if (aCliente.getIndicadoresGestion() != null && aCliente.getIndicadoresGestion().equals(IConstantes.AFIRMACION)) {
			itemsPermisos.add(new SelectItem("indicadores_gestion", this.getMensaje("indicadoresGestion")));
		}

		return itemsPermisos;
	}

	/**
	 * Obtiene fselecitems de permisos de técnico
	 * 
	 * @return itemsPermisos
	 */
	public List<SelectItem> getItemsPermisosTecnico() {
		List<SelectItem> itemsPermisos = new ArrayList<SelectItem>();

		itemsPermisos.add(new SelectItem("informe_mantenimiento", this.getMensaje("informeMantenimiento")));
		itemsPermisos.add(new SelectItem("reporte_fallas", this.getMensaje("reporteFallas")));
		itemsPermisos.add(new SelectItem("cronograma", this.getMensaje("cronograma")));

		return itemsPermisos;
	}

	/**
	 * Obtiene fselecitems de permisos de clientes
	 * 
	 * @return itemsPermisos
	 */
	public List<SelectItem> getItemsPermisosCliente() {
		List<SelectItem> itemsPermisos = new ArrayList<SelectItem>();

		itemsPermisos.add(new SelectItem("hoja_vida", this.getMensaje("hojaVida")));
		itemsPermisos.add(new SelectItem("informe_mantenimiento", this.getMensaje("informeMantenimiento")));
		itemsPermisos.add(new SelectItem("reporte_fallas", this.getMensaje("reporteFallas")));
		itemsPermisos.add(new SelectItem("cronograma", this.getMensaje("cronograma")));
		itemsPermisos.add(new SelectItem("indicadores_gestion", this.getMensaje("indicadoresGestion")));

		return itemsPermisos;
	}

}
