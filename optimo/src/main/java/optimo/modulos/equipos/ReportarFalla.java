package optimo.modulos.equipos;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

import optimo.Conexion;
import optimo.beans.Administrador;
import optimo.beans.Cliente;
import optimo.beans.Equipo;
import optimo.beans.ReporteFalla;
import optimo.generales.ConsultarFuncionesAPI;
import optimo.generales.IConstantes;
import optimo.generales.IEmail;
import optimo.modulos.IConsultasDAO;
import optimo.modulos.personal.AdministrarSesionCliente;

@ManagedBean
@ViewScoped
public class ReportarFalla extends ConsultarFuncionesAPI implements Serializable {

	/**
	 * 
	 */
	private static final long					serialVersionUID	= 956898290178458397L;

	// inyecta el bean de sesion
	@ManagedProperty(value = "#{administrarSesionCliente}")
	private AdministrarSesionCliente	administrarSesionCliente;

	private ReporteFalla							reporteFalla;
	private ReporteFalla							reporteFallaTransaccion;
	private ReporteFalla							reporteFallaConsulta;

	private List<ReporteFalla>				reportesFallas;

	private List<SelectItem>					itemsEquiposActivos;
	private List<SelectItem>					itemsEquipos;

	// privados

	/**
	 * Base 64 a bufferedimagen
	 * 
	 * @param base64String
	 * @return ok
	 */
	public BufferedImage base64StringToImg(String base64String) {
		try {
			return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	/**
	 * Convierte buffered image a byte
	 * 
	 * @param userSpaceImage
	 * @return ok
	 */
	public byte[] getByteData(BufferedImage userSpaceImage) {

		byte[] bytes = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(userSpaceImage, "jpg", baos);
			bytes = baos.toByteArray();
		} catch (Exception e) {

		}
		return bytes;
	}

	/**
	 * Detremina si un reporte es válido
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isValidoReporte(String aTransaccion) {
		boolean ok = true;
		List<ReporteFalla> reportes = null;

		try {

			if (aTransaccion.equals("C")) {

				// this.reporteFalla.setFechaFalla(this.getFechaCeroHoras(getFechaHoraMinutoActualGmtColombia()));

				this.reporteFalla.setFechaFalla(getFechaHoraMinutoActualGmtColombia());

				if (this.isVacio(this.reporteFalla.getDescripcionFalla())) {
					ok = false;
					this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
				} else {
					this.reporteFalla.setDescripcionFalla(this.reporteFalla.getDescripcionFalla().trim());
				}

				// valida que no se haya programado un reporte de falla del mismo tipo
				// para
				// la fecha y cliente abierto

				ReporteFalla temp = new ReporteFalla();
				temp.setFechaFalla(this.reporteFalla.getFechaFalla());
				temp.setEstado(IConstantes.ESTADO_ABIERTO);
				temp.getEquipo().setId(this.reporteFalla.getEquipo().getId());

				reportes = IConsultasDAO.getReportesFallas(temp);

				if (reportes != null && reportes.size() > 0) {
					ok = false;
					this.mostrarMensajeGlobal("reporteFallaExistente", "advertencia");

				}

				if (this.reporteFalla.getArchivo() == null) {
					ok = false;
					this.mostrarMensajeGlobal("fotoRequerida", "advertencia");
				} else {

					String imagenOriginal64 = Base64.getEncoder().encodeToString(this.reporteFalla.getArchivo());
					BufferedImage imagenOriginalMemoria = base64StringToImg(imagenOriginal64);

					BufferedImage imagenEscala = Scalr.resize(imagenOriginalMemoria, 200);
					this.reporteFalla.setArchivo(getByteData(imagenEscala));

				}

			} else {

				if (this.isVacio(this.reporteFallaTransaccion.getConceptoCierreManual())) {
					ok = false;
					this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
				} else {
					this.reporteFallaTransaccion.setConceptoCierreManual(this.reporteFallaTransaccion.getConceptoCierreManual().trim());
				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return ok;
	}

	// publicos

	/**
	 * Limpia la foto cargada para cargar otra
	 */
	public void limpiarFotoCargada() {
		this.reporteFalla.settFile(null);
		this.reporteFalla.setArchivo(null);
	}

	/**
	 * Recibir foto y asignara aobjeto
	 * 
	 * @param event
	 */
	public void recibirFoto(FileUploadEvent event) {

		try {
			this.reporteFalla.settFile(event.getFile());
			this.reporteFalla.setArchivo(event.getFile().getContents());

			this.mostrarMensajeGlobal("archivoRecibido", "advertencia");
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Obtiene un método de autocompletar para el nombre cuando es consulta
	 * 
	 * @param aTexto
	 * @return clientes
	 */
	public List<String> usarAutocompletar(String aTexto) {
		final List<String> clientes = new ArrayList<String>();
		try {

			if (aTexto != null && !aTexto.equals("")) {
				Cliente cliente = new Cliente();

				// texto buscado en nit, cliente, ubicación
				cliente.setCliente(aTexto.trim().toUpperCase());

				List<Cliente> listadoClientes = IConsultasDAO.getClientesLimitados(cliente);

				if (listadoClientes != null && listadoClientes.size() > 0) {

					listadoClientes.forEach(p -> clientes.add(p.getCliente() + ", " + p.getUbicacion() + " ##id=" + p.getId()));
				}

			} else {

				this.reporteFallaConsulta.getEquipo().setCliente(new Cliente());

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clientes;
	}

	/**
	 * Selecciona una falla para consulta
	 */
	public void seleccionarFallaConsulta() {

		try {

			if (this.reporteFallaConsulta != null && this.reporteFallaConsulta.getId() != null) {

				Cliente temp = new Cliente();
				temp.setId(this.reporteFallaConsulta.getId());

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.reporteFallaConsulta.getEquipo().getCliente().setId(cliente.getId());
					this.reporteFallaConsulta.getEquipo().getCliente().setNit(cliente.getNit());
					this.reporteFallaConsulta.getEquipo().getCliente().setCliente(cliente.getCliente());
					this.reporteFallaConsulta.getEquipo().getCliente().setUbicacion(cliente.getUbicacion());

					this.reporteFallaConsulta.getEquipo().getCliente().settClienteAutocompletado(this.reporteFallaConsulta.getEquipo().getCliente().getCliente() + ", " + this.reporteFallaConsulta.getEquipo().getCliente().getUbicacion());

				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Método que me selecciona el nombre del cliente, lo busca y llena el nit y
	 * otros
	 * 
	 * @param event
	 */
	public void onItemSelectConsulta(SelectEvent event) {

		try {

			if (event != null && event.getObject() != null && !event.getObject().toString().trim().equals("") && this.reporteFallaConsulta != null) {

				String[] separado = event.getObject().toString().trim().split("##id=");

				Cliente temp = new Cliente();
				temp.setId(Integer.parseInt(separado[1]));

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.reporteFallaConsulta.getEquipo().getCliente().setId(cliente.getId());
					this.reporteFallaConsulta.getEquipo().getCliente().setNit(cliente.getNit());
					this.reporteFallaConsulta.getEquipo().getCliente().setCliente(cliente.getCliente());
					this.reporteFallaConsulta.getEquipo().getCliente().setUbicacion(cliente.getUbicacion());

					this.reporteFallaConsulta.getEquipo().getCliente().settClienteAutocompletado(this.reporteFallaConsulta.getEquipo().getCliente().getCliente() + ", " + this.reporteFallaConsulta.getEquipo().getCliente().getUbicacion());

				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Selecciona una falla
	 */
	public void seleccionarFalla() {

		try {

			if (this.reporteFalla != null && this.reporteFalla.getId() != null) {

				Cliente temp = new Cliente();
				temp.setId(this.reporteFalla.getId());

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.reporteFalla.getEquipo().getCliente().setId(cliente.getId());
					this.reporteFalla.getEquipo().getCliente().setNit(cliente.getNit());
					this.reporteFalla.getEquipo().getCliente().setCliente(cliente.getCliente());
					this.reporteFalla.getEquipo().getCliente().setUbicacion(cliente.getUbicacion());

					this.reporteFalla.getEquipo().getCliente().settClienteAutocompletado(this.reporteFalla.getEquipo().getCliente().getCliente() + ", " + this.reporteFalla.getEquipo().getCliente().getUbicacion());

				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Método que me selecciona el nombre del cliente, lo busca y llena el nit y
	 * otros
	 * 
	 * @param event
	 */
	public void onItemSelect(SelectEvent event) {

		try {

			if (event != null && event.getObject() != null && !event.getObject().toString().trim().equals("") && this.reporteFalla != null) {

				String[] separado = event.getObject().toString().trim().split("##id=");

				Cliente temp = new Cliente();
				temp.setId(Integer.parseInt(separado[1]));

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.reporteFalla.getEquipo().getCliente().setId(cliente.getId());
					this.reporteFalla.getEquipo().getCliente().setNit(cliente.getNit());
					this.reporteFalla.getEquipo().getCliente().setCliente(cliente.getCliente());
					this.reporteFalla.getEquipo().getCliente().setUbicacion(cliente.getUbicacion());

					this.reporteFalla.getEquipo().getCliente().settClienteAutocompletado(this.reporteFalla.getEquipo().getCliente().getCliente() + ", " + this.reporteFalla.getEquipo().getCliente().getUbicacion());

				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela y resetea criterios de consulta y resultados antes cargados
	 */
	public void cancelarConsulta() {
		this.reporteFallaConsulta = null;
		this.getReporteFallaConsulta();

		this.reportesFallas = null;
	}

	/**
	 * Asigna el qr de creación de falla
	 */
	public String getAsignacionQrReportarFalla() {
		String valor = "";
		try {

			if (this.administrarSesionCliente != null && this.administrarSesionCliente.getPersonalSesion() != null && this.administrarSesionCliente.getQr() != null && !this.administrarSesionCliente.getQr().trim().equals("")) {

				this.getReporteFalla();
				this.reporteFalla.getEquipo().setCodigoQr(this.administrarSesionCliente.getQr().trim());

				this.administrarSesionCliente.setQr(null);
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return valor;
	}

	/**
	 * Crea un reporte de falla
	 */
	public void crearReporteQr() {

		Conexion conexion = new Conexion();
		List<Administrador> administradores = null;
		List<String> correos = null;
		Administrador administrador = null;

		Equipo equipo2 = new Equipo();
		equipo2.setCodigoQr(this.reporteFalla.getEquipo().getCodigoQr());
		try {

			final List<Equipo> equiposValidos = IConsultasDAO.getEquipos(equipo2);

			if (isValidoReporte("C")) {

				if (equiposValidos != null && equiposValidos.size() > 0 && this.administrarSesionCliente != null && this.administrarSesionCliente.getPersonalSesion() != null && this.administrarSesionCliente.getPersonalSesion().getId() != null) {

					if (this.administrarSesionCliente.getPersonalSesion().getTipoUsuario().equals(IConstantes.ROL_CLIENTE)) {

						Cliente cliente = new Cliente();
						cliente.setEstadoVigencia(IConstantes.ACTIVO);
						cliente.setCorreoElectronico(this.administrarSesionCliente.getPersonalSesion().getCorreoElectronico());
						List<Cliente> clientes = IConsultasDAO.getClientes(cliente);
						if (clientes != null && clientes.size() > 0) {

							if (equiposValidos != null && equiposValidos.size() > 0 && clientes.stream().anyMatch(p -> p.getId().intValue() == equiposValidos.get(0).getCliente().getId().intValue())) {

								this.reporteFalla.setEquipo(equiposValidos.get(0));

							} else {
								this.mostrarMensajeGlobal("codigoQRIncorrecto", "exito");
								return;

							}
						}

					} else {

						this.reporteFalla.setEquipo(equiposValidos.get(0));

					}

				} else {
					this.mostrarMensajeGlobal("codigoQRIncorrecto", "exito");
					return;

				}

				conexion.setAutoCommitBD(false);
				this.reporteFalla.setFechaFalla(getFechaHoraMinutoActualGmtColombia());
				this.reporteFalla.setEstado(IConstantes.ESTADO_ABIERTO);

				this.reporteFalla.getCamposBD();

				conexion.insertarBD(this.reporteFalla.getEstructuraTabla().getTabla(), this.reporteFalla.getEstructuraTabla().getPersistencia());

				this.reporteFalla.setId(conexion.getUltimoSerialTransaccion(this.reporteFalla.getEstructuraTabla().getTabla()));

				conexion.commitBD();

				this.mostrarMensajeGlobal("creacionExitosa", "exito");

				administrador = new Administrador();
				administrador.setEstadoVigencia(IConstantes.ACTIVO);

				administradores = IConsultasDAO.getAdministradores(administrador);
				int i = 0;
				if (administradores != null && administradores.size() > 0) {
					correos = new ArrayList<String>();
					for (Administrador a : administradores) {
						correos.add(a.getCorreoElectronico());
						i++;
						if (i >= 100) {
							break;
						}

					}

					IEmail.enviarCorreoMasivo(this.getMensaje("mensajeReporteFallas", this.reporteFalla.getEquipo().getCliente().getCliente() + ", " + this.reporteFalla.getEquipo().getCliente().getUbicacion(), this.reporteFalla.getEquipo().getNombreEquipo() + "," + this.reporteFalla.getEquipo().getCodigoQr()), this.getMensaje("asuntoReporteFallas", this.reporteFalla.getEquipo().getCodigoQr()), correos);
					this.mostrarMensajeGlobal("mailReporteFallas", "exito");
				}

				// reseteo de variables

				this.reporteFallaConsulta = null;
				this.getReporteFallaConsulta();

				this.reporteFallaConsulta.setId(this.reporteFalla.getId());

				// reseteo de variables
				this.reporteFalla = null;
				this.getReporteFalla();
				this.reportesFallas = null;
				this.consultarReporteFalla();

				if (this.reportesFallas != null && this.reportesFallas.size() > 0) {
					this.reporteFallaConsulta.setId(null);
					this.reporteFallaConsulta.getEquipo().setCodigoQr(this.reportesFallas.get(0).getEquipo().getCodigoQr());

				}

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Crea un reporte de falla
	 */
	public void crearReporte() {

		Conexion conexion = new Conexion();
		List<Administrador> administradores = null;
		List<String> correos = null;
		Administrador administrador = null;

		try {
			if (isValidoReporte("C")) {
				Equipo equipo = new Equipo();
				equipo.setId(this.reporteFalla.getEquipo().getId());
				this.reporteFalla.setEquipo(IConsultasDAO.getEquipos(equipo).get(0));

				conexion.setAutoCommitBD(false);
				this.reporteFalla.setFechaFalla(getFechaHoraMinutoActualGmtColombia());
				this.reporteFalla.setEstado(IConstantes.ESTADO_ABIERTO);

				this.reporteFalla.getCamposBD();

				conexion.insertarBD(this.reporteFalla.getEstructuraTabla().getTabla(), this.reporteFalla.getEstructuraTabla().getPersistencia());

				this.reporteFalla.setId(conexion.getUltimoSerialTransaccion(this.reporteFalla.getEstructuraTabla().getTabla()));

				conexion.commitBD();

				this.mostrarMensajeGlobal("creacionExitosa", "exito");

				administrador = new Administrador();
				administrador.setEstadoVigencia(IConstantes.ACTIVO);

				administradores = IConsultasDAO.getAdministradores(administrador);
				int i = 0;
				if (administradores != null && administradores.size() > 0) {
					correos = new ArrayList<String>();
					for (Administrador a : administradores) {
						correos.add(a.getCorreoElectronico());
						i++;
						if (i >= 100) {
							break;
						}

					}

					IEmail.enviarCorreoMasivo(this.getMensaje("mensajeReporteFallas", this.reporteFalla.getEquipo().getCliente().getCliente() + ", " + this.reporteFalla.getEquipo().getCliente().getUbicacion(), this.reporteFalla.getEquipo().getNombreEquipo() + "," + this.reporteFalla.getEquipo().getCodigoQr()), this.getMensaje("asuntoReporteFallas", this.reporteFalla.getEquipo().getCodigoQr()), correos);
					this.mostrarMensajeGlobal("mailReporteFallas", "exito");
				}

				// reseteo de variables

				this.reporteFallaConsulta = null;
				this.getReporteFallaConsulta();

				this.reporteFallaConsulta.setId(this.reporteFalla.getId());

				// reseteo de variables
				this.reporteFalla = null;
				this.getReporteFalla();
				this.reportesFallas = null;
				this.consultarReporteFalla();

				if (this.reportesFallas != null && this.reportesFallas.size() > 0) {
					this.reporteFallaConsulta.setId(null);
					this.reporteFallaConsulta.getEquipo().setCodigoQr(this.reportesFallas.get(0).getEquipo().getCodigoQr());

				}

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Edita un reporte de falla
	 */
	public void editarReporte() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoReporte("E")) {
				conexion.setAutoCommitBD(false);

				this.reporteFallaTransaccion.setEstado(IConstantes.ESTADO_CERRADO);
				this.reporteFallaTransaccion.setFechaHoraAtencion(getFechaHoraMinutoActualGmtColombia());
				// campos que va actualizar

				Map<String, Object> campos = new HashMap<String, Object>();
				campos.put("estado", this.reporteFallaTransaccion.getEstado());
				campos.put("fecha_atencion", this.reporteFallaTransaccion.getFechaHoraAtencion());
				campos.put("concepto_cierre_manual", this.reporteFallaTransaccion.getConceptoCierreManual());
				this.reporteFallaTransaccion.getCamposBD();
				conexion.actualizarBD(this.reporteFallaTransaccion.getEstructuraTabla().getTabla(), campos, this.reporteFallaTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

				conexion.commitBD();
				this.mostrarMensajeGlobal("fallaCerrada", "exito");
				this.cerrarModal("panelEdicionReporte");

				// reseteo de variables
				this.reporteFallaTransaccion = null;
				this.getReporteFallaTransaccion();
				this.reportesFallas = null;
				this.consultarReporteFalla();

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Obtiene el reporte de falla a visualizar
	 * 
	 * @param aReporteFalla
	 */
	public void verReporteFalla(ReporteFalla aReporteFalla) {
		try {
			this.reporteFalla = null;
			if (aReporteFalla != null && aReporteFalla.getId() != null) {
				ReporteFalla reporte = new ReporteFalla();
				reporte.setId(aReporteFalla.getId());
				this.reporteFalla = IConsultasDAO.getReportesFallas(reporte).get(0);

				this.guardarImagenEnDisco(this.reporteFalla.getId(), this.reporteFalla.gettFotoDecodificada(), "fotosFallas");
				this.reporteFalla.settNombreFoto("equipo" + this.reporteFalla.getId() + ".png");

				this.abrirModal("panelInformacionFalla");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Asigna el reporte para ser realizada una transacción
	 * 
	 * @param aReporteFalla
	 * @param aVista
	 */
	public void asignarReporte(ReporteFalla aReporteFalla, String aVista) {

		try {

			this.reporteFallaTransaccion = aReporteFalla;

			if (aVista != null && aVista.equals("MODAL_EDITAR_REPORTE")) {
				this.mostrarMensajeGlobal("messagesCerrarFalla", "advertencia");
				this.reporteFallaTransaccion.getEquipo().getCliente().settClienteAutocompletado(this.reporteFallaTransaccion.getEquipo().getCliente().getCliente() + ", " + this.reporteFallaTransaccion.getEquipo().getCliente().getUbicacion());

				this.abrirModal("panelEdicionReporte");
			} else if (aVista != null && aVista.equals("MODAL_VER_REPORTE")) {

				this.guardarImagenEnDisco(this.reporteFallaTransaccion.getId(), this.reporteFallaTransaccion.gettFotoDecodificada(), "fotosFallas");
				this.reporteFallaTransaccion.settNombreFoto("equipo" + this.reporteFallaTransaccion.getId() + ".png");

				this.abrirModal("panelFotoFalla");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela la edición de un reporte de falla
	 * 
	 * @param aVista
	 */
	public void cancelarReporteFallaTransaccion(String aVista) {
		try {

			this.reporteFallaTransaccion = new ReporteFalla();
			this.reportesFallas = null;
			this.consultarReporteFalla();

			if (aVista != null && aVista.equals("MODAL_EDITAR_REPORTE")) {
				this.cerrarModal("panelEdicionReporte");

			} else if (aVista != null && aVista.equals("MODAL_VER_REPORTE")) {
				this.cerrarModal("panelFotoFalla");

			}

		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Cancela el reporte de falla a crear
	 */
	public void cancelarReporte() {

		try {
			this.reporteFalla = null;
			this.getReporteFalla();
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Consulta lso reportews de fallas por distintos criterios
	 */
	public void consultarReporteFalla() {

		try {

			this.reportesFallas = IConsultasDAO.getReportesFallas(this.reporteFallaConsulta);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Obtiene un listado de items de equipos activos para editar
	 * 
	 * @return itemsEquiposActivos
	 */
	public List<SelectItem> getItemsEquiposEditar() {
		List<SelectItem> itemsEquiposActivos2 = new ArrayList<SelectItem>();
		try {

			itemsEquiposActivos2.add(new SelectItem("", this.getMensaje("comboVacio")));

			if (this.reporteFallaTransaccion != null && this.reporteFallaTransaccion.getEquipo() != null && this.reporteFallaTransaccion.getEquipo().getCliente() != null && this.reporteFallaTransaccion.getEquipo().getCliente().getId() != null) {
				Equipo equipo = new Equipo();
				equipo.getCliente().setId(this.reporteFallaTransaccion.getEquipo().getCliente().getId());
				equipo.setEstado(IConstantes.ACTIVO);
				List<Equipo> equipos = IConsultasDAO.getEquipos(equipo);
				equipos.forEach(p -> itemsEquiposActivos2.add(new SelectItem(p.getId(), p.getNombreEquipo() + " - " + p.getCodigoQr())));

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsEquiposActivos2;
	}

	/**
	 * Obtiene un listado de items de equipos activos
	 * 
	 * @return itemsEquiposActivos
	 */
	public List<SelectItem> getItemsEquiposActivos() {
		try {

			this.itemsEquiposActivos = new ArrayList<SelectItem>();
			this.itemsEquiposActivos.add(new SelectItem("", this.getMensaje("comboVacio")));

			if (this.reporteFalla != null && this.reporteFalla.getEquipo() != null && this.reporteFalla.getEquipo().getCliente() != null && this.reporteFalla.getEquipo().getCliente().getId() != null) {
				Equipo equipo = new Equipo();
				equipo.getCliente().setId(this.reporteFalla.getEquipo().getCliente().getId());
				equipo.setEstado(IConstantes.ACTIVO);
				List<Equipo> equipos = IConsultasDAO.getEquipos(equipo);
				equipos.forEach(p -> this.itemsEquiposActivos.add(new SelectItem(p.getId(), p.getNombreEquipo() + " - " + p.getCodigoQr())));

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsEquiposActivos;
	}

	/**
	 * Establece un listado de equipos activos
	 * 
	 * @param itemsEquiposActivos
	 */
	public void setItemsEquiposActivos(List<SelectItem> itemsEquiposActivos) {
		this.itemsEquiposActivos = itemsEquiposActivos;
	}

	/**
	 * Obtiene un listado de los equipos consulta
	 * 
	 * @return itemsEquipos
	 */
	public List<SelectItem> getItemsEquipos() {
		try {

			this.itemsEquipos = new ArrayList<SelectItem>();
			this.itemsEquipos.add(new SelectItem("", this.getMensaje("comboVacio")));

			if (this.reporteFallaConsulta != null && this.reporteFallaConsulta.getEquipo() != null && this.reporteFallaConsulta.getEquipo().getCliente() != null && this.reporteFallaConsulta.getEquipo().getCliente().getId() != null) {
				Equipo equipo = new Equipo();
				equipo.getCliente().setId(this.reporteFallaConsulta.getEquipo().getCliente().getId());

				List<Equipo> equipos = IConsultasDAO.getEquipos(equipo);
				equipos.forEach(p -> this.itemsEquipos.add(new SelectItem(p.getId(), p.getNombreEquipo() + " - " + p.getCodigoQr())));

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsEquipos;
	}

	/**
	 * Establece los equipos según cliente seleccionado
	 * 
	 * @param itemsEquipos
	 */
	public void setItemsEquipos(List<SelectItem> itemsEquipos) {
		this.itemsEquipos = itemsEquipos;
	}

	// get/sets

	public ReporteFalla getReporteFalla() {
		try {
			if (this.reporteFalla == null) {
				this.reporteFalla = new ReporteFalla();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return reporteFalla;
	}

	public void setReporteFalla(ReporteFalla reporteFalla) {
		this.reporteFalla = reporteFalla;
	}

	public ReporteFalla getReporteFallaTransaccion() {
		try {
			if (this.reporteFallaTransaccion == null) {
				this.reporteFallaTransaccion = new ReporteFalla();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return reporteFallaTransaccion;
	}

	public void setReporteFallaTransaccion(ReporteFalla reporteFallaTransaccion) {
		this.reporteFallaTransaccion = reporteFallaTransaccion;
	}

	public ReporteFalla getReporteFallaConsulta() {
		try {
			if (this.reporteFallaConsulta == null) {
				this.reporteFallaConsulta = new ReporteFalla();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return reporteFallaConsulta;
	}

	public void setReporteFallaConsulta(ReporteFalla reporteFallaConsulta) {
		this.reporteFallaConsulta = reporteFallaConsulta;
	}

	public List<ReporteFalla> getReportesFallas() {
		return reportesFallas;
	}

	public void setReportesFallas(List<ReporteFalla> reportesFallas) {
		this.reportesFallas = reportesFallas;
	}

	public AdministrarSesionCliente getAdministrarSesionCliente() {
		return administrarSesionCliente;
	}

	public void setAdministrarSesionCliente(AdministrarSesionCliente administrarSesionCliente) {
		this.administrarSesionCliente = administrarSesionCliente;
	}

}
