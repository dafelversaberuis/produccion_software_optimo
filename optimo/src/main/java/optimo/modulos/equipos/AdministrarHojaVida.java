package optimo.modulos.equipos;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.imgscalr.Scalr;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.SelectEvent;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import optimo.Conexion;
import optimo.beans.ActividadInformeEquipo;
import optimo.beans.Calibracion;
import optimo.beans.ClaseDocumento;
import optimo.beans.ClaseSoporteBiomedico;
import optimo.beans.ClasificacionBiomedica;
import optimo.beans.ClasificacionRiesgo;
import optimo.beans.Cliente;
import optimo.beans.DocumentoEquipo;
import optimo.beans.Equipo;
import optimo.beans.FotoEquipo;
import optimo.beans.RepuestoEquipo;
import optimo.generales.ConsultarFuncionesAPI;
import optimo.generales.IConstantes;
import optimo.modulos.IConsultasDAO;
import optimo.modulos.personal.AdministrarSesionCliente;

@ManagedBean
@ViewScoped
public class AdministrarHojaVida extends ConsultarFuncionesAPI implements Serializable {

	/**
	 *     
	 */
	private static final long							serialVersionUID	= -3686800987553054211L;

	// inyecta el bean de sesion
	@ManagedProperty(value = "#{administrarSesionCliente}")
	private AdministrarSesionCliente			administrarSesionCliente;

	private Equipo												equipo;
	private Equipo												equipoTransaccion;
	private Equipo												equipoConsulta;

	private RepuestoEquipo								repuestoEquipo;
	private RepuestoEquipo								repuestoTransaccion;

	private DocumentoEquipo								documentoEquipo;
	private DocumentoEquipo								documentoEquipoTransaccion;

	private Calibracion										calibracion;
	private Calibracion										calibracionTransaccion;

	private ActividadInformeEquipo				actividad;
	private ActividadInformeEquipo				actividadTransaccion;

	private FotoEquipo										fotoEquipo;
	private FotoEquipo										fotoEquipoTransaccion;

	private List<ActividadInformeEquipo>	actividades;
	private List<Equipo>									equipos;
	private List<DocumentoEquipo>					documentos;
	private List<Calibracion>							calibraciones;
	private List<FotoEquipo>							fotosEquipos;
	private List<RepuestoEquipo>					repuestosEquipos;

	private List<SelectItem>							itemsClasesDocumentosActivos;
	private List<SelectItem>							itemsClasesSoporteActivos;
	private List<SelectItem>							itemsClasificacionesBiomedicasActivas;
	private List<SelectItem>							itemsClasificacionesRiesgoActivas;
	private List<SelectItem>							itemsEquipos;

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
	 * Método que me selecciona el nombre del cliente, lo busca y llena el nit y
	 * otros
	 * 
	 * @param event
	 */
	public void onItemSelectConsulta(SelectEvent event) {

		try {

			if (event != null && event.getObject() != null && !event.getObject().toString().trim().equals("") && this.equipoConsulta != null) {

				String[] separado = event.getObject().toString().trim().split("##id=");

				Cliente temp = new Cliente();
				temp.setId(Integer.parseInt(separado[1]));

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.equipoConsulta.getCliente().setId(cliente.getId());
					this.equipoConsulta.getCliente().setNit(cliente.getNit());
					this.equipoConsulta.getCliente().setCliente(cliente.getCliente());
					this.equipoConsulta.getCliente().setUbicacion(cliente.getUbicacion());

					this.equipoConsulta.getCliente().settClienteAutocompletado(this.equipoConsulta.getCliente().getCliente() + ", " + this.equipoConsulta.getCliente().getUbicacion());

				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

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

			if (this.equipoConsulta != null  && this.equipoConsulta.getCliente() != null && this.equipoConsulta.getCliente().getId() != null) {
				Equipo equipo = new Equipo();
				equipo.getCliente().setId(this.equipoConsulta.getCliente().getId());

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

	/**
	 * Obtiene un método de autocompletar para el nombre cuando es consulta
	 * 
	 * @param aTexto
	 * @return clientes
	 */
	public List<String> usarAutocompletarConsulta(String aTexto) {
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

				this.equipoConsulta.setCliente(new Cliente());

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clientes;
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
	 * Descarga el archivo requerido
	 * 
	 * @param aArchivo
	 * @param aExtension
	 * @param aContentType
	 */
	private void descargarAdjunto(byte[] aArchivo, String aExtension, String aContentType) {

		try {

			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext ext = context.getExternalContext();
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

			HttpServletResponse response = (HttpServletResponse) ext.getResponse();
			response.setContentType(aContentType);
			response.setHeader("Content-Disposition", "attachment; filename=" + formato.format(getFechaHoraMinutoActualGmtColombia()) + "." + aExtension.toLowerCase());
			response.setContentLength(aArchivo.length);
			ServletOutputStream servletOutputStream = response.getOutputStream();

			servletOutputStream.write(aArchivo, 0, aArchivo.length);

			servletOutputStream.flush();
			servletOutputStream.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Obtiene el tipo de archivo
	 * 
	 * @param aNombre
	 * @return tipo
	 */
	private String getTipoArchivo(String aNombre) {
		String tipo = "";
		int ultimoPunto = 0;
		try {

			ultimoPunto = aNombre.lastIndexOf('.');
			tipo = aNombre.substring(ultimoPunto + 1);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return tipo;
	}

	/**
	 * Obtiene un ID para un qr de forma aleatoria
	 * 
	 * @return clave
	 */
	private String getQRAleatorio() {
		String clave = "";
		int numero = 0;
		int numeroAscii = 0;
		int carcaterNumero = 0;
		try {
			for (int i = 1; i <= IConstantes.NUMERO_CARACTERES_QR.intValue(); i++) {

				// priemro decidimos si es caracter o número, si es 0->numero,
				// 1->caracter
				carcaterNumero = (int) (2.0 * Math.random());

				if (carcaterNumero == 0) {
					// si es número buscamos un aletorio de 0-9
					numero = (int) (10.0 * Math.random());
					clave += "" + numero;

				} else {

					// si es caracter buscamos una letra aleatoria entre a y z
					// para eso buscamos su número ascii entre 0 y 25 y le sumamos 97 ya
					// que a-z es (97-122)
					numeroAscii = (int) (26.0 * Math.random()) + 97;

					clave += (char) numeroAscii;

				}

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clave;

	}

	/**
	 * Validar la foto
	 * 
	 * @return ok
	 */
	private boolean isValidaFoto() {
		boolean ok = true;

		if (this.isVacio(this.fotoEquipo.getLeyenda())) {
			ok = false;
			this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
		} else {
			this.fotoEquipo.setLeyenda(this.fotoEquipo.getLeyenda().trim());
		}

		if (this.fotoEquipo.getArchivo() == null) {

			ok = false;
			this.mostrarMensajeGlobal("fotoRequerida", "advertencia");

		} else {

			String imagenOriginal64 = Base64.getEncoder().encodeToString(this.fotoEquipo.getArchivo());
			BufferedImage imagenOriginalMemoria = base64StringToImg(imagenOriginal64);

			BufferedImage imagenEscala = Scalr.resize(imagenOriginalMemoria, 200);
			this.fotoEquipo.setArchivo(getByteData(imagenEscala));

		}

		return ok;
	}

	/**
	 * Detremina si es válida o no una calibración a crear
	 * 
	 * @return ok
	 */
	private boolean isValidaCalibracion() {
		boolean ok = true;

		if (this.calibracion.getArchivo() == null) {
			ok = false;
			this.mostrarMensajeGlobal("archivoAdjuntoRequerido", "advertencia");
		}

		return ok;
	}

	/**
	 * Valida la creación de un documento
	 * 
	 * @return ok
	 */
	private boolean isValidoDocumento() {
		boolean ok = true;

		if (this.isVacio(this.documentoEquipo.getNombre())) {
			ok = false;
			this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
		} else {
			this.documentoEquipo.setNombre(this.documentoEquipo.getNombre().trim());
		}

		if (this.documentoEquipo.getPoseeDocumento().equals(IConstantes.AFIRMACION)) {

			if (this.documentoEquipo.getArchivo() == null) {
				ok = false;
				this.mostrarMensajeGlobal("archivoAdjuntoRequerido", "advertencia");
			}

		} else {
			this.documentoEquipo.setArchivo(null);
			this.documentoEquipo.setContentType(null);
			this.documentoEquipo.setExtensionArchivo(null);
		}

		return ok;
	}

	/**
	 * Valida una actividad a crear
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isValidaActividad(String aTransaccion) {
		boolean ok = true;

		if (aTransaccion.equals("C")) {

			if (this.isVacio(this.actividad.getActividad())) {
				ok = false;
			} else {
				this.actividad.setActividad(this.actividad.getActividad().trim());
			}

		} else {

			if (this.isVacio(this.actividadTransaccion.getActividad())) {
				ok = false;
			} else {
				this.actividadTransaccion.setActividad(this.actividadTransaccion.getActividad().trim());
			}

		}

		if (!ok) {

			this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
		}

		return ok;
	}

	/**
	 * Valida un repuesto
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isValidoRepuesto(String aTransaccion) {
		boolean ok = true;

		if (aTransaccion.equals("C")) {

			if (this.isVacio(this.repuestoEquipo.getNombre())) {
				ok = false;
			} else {
				this.repuestoEquipo.setNombre(this.repuestoEquipo.getNombre().trim());
			}

			if (this.isVacio(this.repuestoEquipo.getMarca())) {
				ok = false;
			} else {
				this.repuestoEquipo.setMarca(this.repuestoEquipo.getMarca().trim());
			}

			if (this.isVacio(this.repuestoEquipo.getModelo())) {
				ok = false;
			} else {
				this.repuestoEquipo.setModelo(this.repuestoEquipo.getModelo().trim());
			}

			if (this.isVacio(this.repuestoEquipo.getDescripcion())) {
				ok = false;
			} else {
				this.repuestoEquipo.setDescripcion(this.repuestoEquipo.getDescripcion().trim());
			}

		} else {

			if (this.isVacio(this.repuestoTransaccion.getNombre())) {
				ok = false;
			} else {
				this.repuestoTransaccion.setNombre(this.repuestoTransaccion.getNombre().trim());
			}

			if (this.isVacio(this.repuestoTransaccion.getMarca())) {
				ok = false;
			} else {
				this.repuestoTransaccion.setMarca(this.repuestoTransaccion.getMarca().trim());
			}

			if (this.isVacio(this.repuestoTransaccion.getModelo())) {
				ok = false;
			} else {
				this.repuestoTransaccion.setModelo(this.repuestoTransaccion.getModelo().trim());
			}

			if (this.isVacio(this.repuestoTransaccion.getDescripcion())) {
				ok = false;
			} else {
				this.repuestoTransaccion.setDescripcion(this.repuestoTransaccion.getDescripcion().trim());
			}

		}

		if (!ok) {

			this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
		}

		return ok;
	}

	/**
	 * Valida la información de un equipo clonado
	 * 
	 * @param conexion
	 * @return equipos
	 * @throws Exception
	 */
	private boolean isValidoEquiposClonado(Conexion conexion) throws Exception {
		boolean ok = true;
		List<Equipo> equipos = null;

		try {

			Equipo equipoTemp = new Equipo();
			equipoTemp.setNumeroInventario(this.equipoTransaccion.getNumeroInventario().trim());
			equipoTemp.getCliente().setId(this.equipoTransaccion.getCliente().getId());
			equipos = IConsultasDAO.getEquipos(equipoTemp);

			if (equipos != null && equipos.size() > 0 && equipos.stream().anyMatch(i -> i.getNumeroInventario().trim().toLowerCase().equals(this.equipoTransaccion.getNumeroInventario().trim().toLowerCase()))) {
				ok = false;
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("numeroInventarioExistente", "" + this.equipoTransaccion.getNumeroInventario()), "advertencia");

			}

			equipoTemp = new Equipo();
			equipoTemp.setNumeroSerie(this.equipoTransaccion.getNumeroSerie().trim());
			equipoTemp.getCliente().setId(this.equipoTransaccion.getCliente().getId());
			equipos = IConsultasDAO.getEquipos(equipoTemp);

			if (equipos != null && equipos.size() > 0 && equipos.stream().anyMatch(i -> i.getNumeroSerie().trim().toLowerCase().equals(this.equipoTransaccion.getNumeroSerie().trim().toLowerCase()))) {
				ok = false;
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("numeroSerieExistente", "" + this.equipoTransaccion.getNumeroSerie()), "advertencia");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
			throw new Exception(e);
		}

		return ok;
	}

	/**
	 * Detremina si un cliente es válido
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isValidoEquipo(String aTransaccion) {
		boolean ok = true;
		List<Equipo> equipos = null;

		try {

			if (aTransaccion.equals("C")) {

				if (this.equipo.getEquipoBiomedico().equals(IConstantes.AFIRMACION)) {

					if (this.isVacio(this.equipo.getDescripcionEquipo())) {
						ok = false;
					} else {
						this.equipo.setDescripcionEquipo(this.equipo.getDescripcionEquipo().trim());
					}

					if (this.isVacio(this.equipo.getProveedor())) {
						ok = false;
					} else {
						this.equipo.setProveedor(this.equipo.getProveedor().trim());
					}

					if (this.isVacio(this.equipo.getRegistroInvima())) {
						ok = false;
					} else {
						this.equipo.setRegistroInvima(this.equipo.getRegistroInvima().trim());
					}

					if (this.isVacio(this.equipo.getServicio())) {
						ok = false;
					} else {
						this.equipo.setServicio(this.equipo.getServicio().trim());
					}

					if (this.isVacio(this.equipo.getTelefono())) {
						ok = false;
					} else {
						this.equipo.setTelefono(this.equipo.getTelefono().trim());
					}

					if (this.isVacio(this.equipo.getCorreoElectronico())) {
						ok = false;
					} else {
						this.equipo.setCorreoElectronico(this.equipo.getCorreoElectronico().trim());
					}

				} else {

					this.equipo.setDescripcionEquipo(null);
					this.equipo.setProveedor(null);
					this.equipo.setRegistroInvima(null);
					this.equipo.setServicio(null);
					this.equipo.setTelefono(null);
					this.equipo.setCorreoElectronico(null);

				}

				if (this.isVacio(this.equipo.getMarca())) {
					ok = false;
				} else {
					this.equipo.setMarca(this.equipo.getMarca().trim());
				}

				if (this.isVacio(this.equipo.getModelo())) {
					ok = false;
				} else {
					this.equipo.setModelo(this.equipo.getModelo().trim());
				}

				if (this.isVacio(this.equipo.getNombreEquipo())) {
					ok = false;
				} else {
					this.equipo.setNombreEquipo(this.equipo.getNombreEquipo().trim());
				}

				if (this.isVacio(this.equipo.getNumeroInventario())) {
					ok = false;
				} else {
					this.equipo.setNumeroInventario(this.equipo.getNumeroInventario().trim());
				}

				if (this.isVacio(this.equipo.getNumeroSerie())) {
					ok = false;
				} else {
					this.equipo.setNumeroSerie(this.equipo.getNumeroSerie().trim());
				}

				// se bajó por nuevo requerimiento

				// if (!ok) {
				//
				// this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
				// }

				if (this.equipo.getContieneBaterias().equals(IConstantes.NEGACION)) {
					this.equipo.setNumeroBaterias(null);

				}

				if (this.equipo.getDatosTecnicos().equals(IConstantes.NEGACION)) {

					this.equipo.setPotencia(null);
					this.equipo.setVoltaje(null);
					this.equipo.setCorriente(null);
					this.equipo.setNumeroFases(null);
					this.equipo.setFrecuencia(null);

					this.equipo.setMaximoPresion(null);
					this.equipo.setMaximoTemperatura(null);
					this.equipo.setMinimoPresion(null);
					this.equipo.setMinimoTemperatura(null);

				} else {

					if (this.equipo.getEquipoBiomedico().equals(IConstantes.AFIRMACION)) {

						if (this.isVacio(this.equipo.getMinimoPresion())) {
							ok = false;
						} else {
							this.equipo.setMinimoPresion(this.equipo.getMinimoPresion().trim());
						}

						if (this.isVacio(this.equipo.getMaximoPresion())) {
							ok = false;
						} else {
							this.equipo.setMaximoPresion(this.equipo.getMaximoPresion().trim());

						}

						if (this.isVacio(this.equipo.getFrecuencia())) {
							ok = false;
						} else {
							this.equipo.setFrecuencia(this.equipo.getFrecuencia().trim());

						}

					}

					if (this.isVacio(this.equipo.getPotencia())) {
						ok = false;
					} else {
						this.equipo.setPotencia(this.equipo.getPotencia().trim());

					}

					if (this.isVacio(this.equipo.getVoltaje())) {
						ok = false;
					} else {
						this.equipo.setVoltaje(this.equipo.getVoltaje().trim());

					}

					if (this.isVacio(this.equipo.getCorriente())) {
						ok = false;
					} else {
						this.equipo.setCorriente(this.equipo.getCorriente().trim());

					}

				}

				if (!ok) {

					this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
				}

				if (this.equipo.getEquipoBiomedico().equals(IConstantes.AFIRMACION)) {
					if (this.equipo.getDatosTecnicos().equals(IConstantes.AFIRMACION)) {

						// nuevo requerimiento se quita

						// if
						// (this.equipo.getMaximoPresion().compareTo(this.equipo.getMinimoPresion())
						// < 0) {
						// ok = false;
						// this.mostrarMensajeGlobal("presionIncorrecta", "advertencia");
						// }

						// if
						// (this.equipo.getMaximoTemperatura().compareTo(this.equipo.getMinimoTemperatura())
						// < 0) {
						// ok = false;
						// this.mostrarMensajeGlobal("temperaturaIncorrecta",
						// "advertencia");
						// }
					}
				} else {

					this.equipo.setMaximoPresion(null);
					this.equipo.setMaximoTemperatura(null);
					this.equipo.setMinimoPresion(null);
					this.equipo.setMinimoTemperatura(null);
					this.equipo.setFrecuencia(null);

				}

				// valida que no se haya hecho el numero de inventario para un equipo
				// del cliente deseado
				if (!this.isVacio(this.equipo.getNumeroInventario())) {
					Equipo equipoTemp = new Equipo();
					equipoTemp.setNumeroInventario(this.equipo.getNumeroInventario().trim());
					equipoTemp.getCliente().setId(this.equipo.getCliente().getId());
					equipos = IConsultasDAO.getEquipos(equipoTemp);

					if (equipos != null && equipos.size() > 0 && equipos.stream().anyMatch(i -> i.getNumeroInventario().trim().toLowerCase().equals(this.equipo.getNumeroInventario().trim().toLowerCase()))) {
						ok = false;
						this.mostrarMensajeGlobal("inventarioExistente", "advertencia");

					}
				}

				// valida el número de serie
				if (!this.isVacio(this.equipo.getNumeroSerie())) {
					Equipo equipoTemp = new Equipo();
					equipoTemp.setNumeroSerie(this.equipo.getNumeroSerie().trim());
					equipoTemp.getCliente().setId(this.equipo.getCliente().getId());
					equipos = IConsultasDAO.getEquipos(equipoTemp);

					if (equipos != null && equipos.size() > 0 && equipos.stream().anyMatch(i -> i.getNumeroSerie().trim().toLowerCase().equals(this.equipo.getNumeroSerie().trim().toLowerCase()))) {
						ok = false;
						this.mostrarMensajeGlobal("serieExistente", "advertencia");

					}
				}

			} else {

				if (this.equipoTransaccion.getEquipoBiomedico().equals(IConstantes.AFIRMACION)) {

					if (this.isVacio(this.equipoTransaccion.getDescripcionEquipo())) {
						ok = false;
					} else {
						this.equipoTransaccion.setDescripcionEquipo(this.equipoTransaccion.getDescripcionEquipo().trim());
					}

					if (this.isVacio(this.equipoTransaccion.getProveedor())) {
						ok = false;
					} else {
						this.equipoTransaccion.setProveedor(this.equipoTransaccion.getProveedor().trim());
					}

					if (this.isVacio(this.equipoTransaccion.getRegistroInvima())) {
						ok = false;
					} else {
						this.equipoTransaccion.setRegistroInvima(this.equipoTransaccion.getRegistroInvima().trim());
					}

					if (this.isVacio(this.equipoTransaccion.getServicio())) {
						ok = false;
					} else {
						this.equipoTransaccion.setServicio(this.equipoTransaccion.getServicio().trim());
					}

					if (this.isVacio(this.equipoTransaccion.getTelefono())) {
						ok = false;
					} else {
						this.equipoTransaccion.setTelefono(this.equipoTransaccion.getTelefono().trim());
					}

					if (this.isVacio(this.equipoTransaccion.getCorreoElectronico())) {
						ok = false;
					} else {
						this.equipoTransaccion.setCorreoElectronico(this.equipoTransaccion.getCorreoElectronico().trim());
					}

				} else {

					this.equipoTransaccion.setDescripcionEquipo(null);
					this.equipoTransaccion.setProveedor(null);
					this.equipoTransaccion.setRegistroInvima(null);
					this.equipoTransaccion.setServicio(null);
					this.equipoTransaccion.setTelefono(null);
					this.equipoTransaccion.setCorreoElectronico(null);

				}

				if (this.isVacio(this.equipoTransaccion.getMarca())) {
					ok = false;
				} else {
					this.equipoTransaccion.setMarca(this.equipoTransaccion.getMarca().trim());
				}

				if (this.isVacio(this.equipoTransaccion.getModelo())) {
					ok = false;
				} else {
					this.equipoTransaccion.setModelo(this.equipoTransaccion.getModelo().trim());
				}

				if (this.isVacio(this.equipoTransaccion.getNombreEquipo())) {
					ok = false;
				} else {
					this.equipoTransaccion.setNombreEquipo(this.equipoTransaccion.getNombreEquipo().trim());
				}

				if (this.isVacio(this.equipoTransaccion.getNumeroInventario())) {
					ok = false;
				} else {

					this.equipoTransaccion.setNumeroInventario(this.equipoTransaccion.getNumeroInventario().trim());

				}

				if (this.isVacio(this.equipoTransaccion.getNumeroSerie())) {
					ok = false;
				} else {
					this.equipoTransaccion.setNumeroSerie(this.equipoTransaccion.getNumeroSerie().trim());
				}

				// lo bajamos por requisito nuevo

				// if (!ok) {
				//
				// this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
				// }

				if (this.equipoTransaccion.getContieneBaterias().equals(IConstantes.NEGACION)) {
					this.equipoTransaccion.setNumeroBaterias(null);

				}

				if (this.equipoTransaccion.getDatosTecnicos().equals(IConstantes.NEGACION)) {

					this.equipoTransaccion.setPotencia(null);
					this.equipoTransaccion.setVoltaje(null);
					this.equipoTransaccion.setCorriente(null);
					this.equipoTransaccion.setNumeroFases(null);
					this.equipoTransaccion.setFrecuencia(null);

					this.equipoTransaccion.setMaximoPresion(null);
					this.equipoTransaccion.setMaximoTemperatura(null);
					this.equipoTransaccion.setMinimoPresion(null);
					this.equipoTransaccion.setMinimoTemperatura(null);

				} else {

					if (this.equipoTransaccion.getEquipoBiomedico().equals(IConstantes.AFIRMACION)) {

						if (this.isVacio(this.equipoTransaccion.getMinimoPresion())) {
							ok = false;
						} else {
							this.equipoTransaccion.setMinimoPresion(this.equipoTransaccion.getMinimoPresion().trim());
						}

						if (this.isVacio(this.equipoTransaccion.getMaximoPresion())) {
							ok = false;
						} else {
							this.equipoTransaccion.setMaximoPresion(this.equipoTransaccion.getMaximoPresion().trim());

						}

						if (this.isVacio(this.equipoTransaccion.getFrecuencia())) {
							ok = false;
						} else {
							this.equipoTransaccion.setFrecuencia(this.equipoTransaccion.getFrecuencia().trim());

						}

					}

					if (this.isVacio(this.equipoTransaccion.getPotencia())) {
						ok = false;
					} else {
						this.equipoTransaccion.setPotencia(this.equipoTransaccion.getPotencia().trim());

					}

					if (this.isVacio(this.equipoTransaccion.getVoltaje())) {
						ok = false;
					} else {
						this.equipoTransaccion.setVoltaje(this.equipoTransaccion.getVoltaje().trim());

					}

					if (this.isVacio(this.equipoTransaccion.getCorriente())) {
						ok = false;
					} else {
						this.equipoTransaccion.setCorriente(this.equipoTransaccion.getCorriente().trim());

					}

				}

				if (!ok) {

					this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
				}

				if (this.equipoTransaccion.getEquipoBiomedico().equals(IConstantes.AFIRMACION)) {
					if (this.equipoTransaccion.getDatosTecnicos().equals(IConstantes.AFIRMACION)) {

						// se quita requerimiento

						// if
						// (this.equipoTransaccion.getMaximoPresion().compareTo(this.equipoTransaccion.getMinimoPresion())
						// < 0) {
						// ok = false;
						// this.mostrarMensajeGlobal("presionIncorrecta", "advertencia");
						// }
						//
						// if
						// (this.equipoTransaccion.getMaximoTemperatura().compareTo(this.equipoTransaccion.getMinimoTemperatura())
						// < 0) {
						// ok = false;
						// this.mostrarMensajeGlobal("temperaturaIncorrecta",
						// "advertencia");
						// }
					}
				} else {

					this.equipoTransaccion.setMaximoPresion(null);
					this.equipoTransaccion.setMaximoTemperatura(null);
					this.equipoTransaccion.setMinimoPresion(null);
					this.equipoTransaccion.setMinimoTemperatura(null);
					this.equipoTransaccion.setFrecuencia(null);

				}

				// valida que no se haya hecho el numero de inventario para un equipo
				// del cliente deseado
				if (!this.isVacio(this.equipoTransaccion.getNumeroInventario())) {

					Equipo equipoTemp = new Equipo();
					equipoTemp.setNumeroInventario(this.equipoTransaccion.getNumeroInventario().trim());
					equipoTemp.getCliente().setId(this.equipoTransaccion.getCliente().getId());
					equipos = IConsultasDAO.getEquipos(equipoTemp);

					if (!this.equipoTransaccion.getNumeroInventario().trim().toLowerCase().equals(this.equipoTransaccion.gettCopiaNumeroInventario().trim().toLowerCase()) && equipos != null && equipos.size() > 0 && equipos.stream().anyMatch(i -> i.getId().intValue() != this.equipoTransaccion.getId().intValue() && i.getNumeroInventario().trim().toLowerCase().equals(this.equipoTransaccion.getNumeroInventario().trim().toLowerCase()))) {
						ok = false;
						this.mostrarMensajeGlobal("inventarioExistente", "advertencia");
					}
				}

				// valida existencia de serie
				if (!this.isVacio(this.equipoTransaccion.getNumeroSerie())) {

					Equipo equipoTemp = new Equipo();
					equipoTemp.setNumeroSerie(this.equipoTransaccion.getNumeroSerie().trim());
					equipoTemp.getCliente().setId(this.equipoTransaccion.getCliente().getId());
					equipos = IConsultasDAO.getEquipos(equipoTemp);

					if (!this.equipoTransaccion.getNumeroSerie().trim().toLowerCase().equals(this.equipoTransaccion.gettCopiaNumeroSerie().trim().toLowerCase()) && equipos != null && equipos.size() > 0 && equipos.stream().anyMatch(i -> i.getId().intValue() != this.equipoTransaccion.getId().intValue() && i.getNumeroSerie().trim().toLowerCase().equals(this.equipoTransaccion.getNumeroSerie().trim().toLowerCase()))) {
						ok = false;
						this.mostrarMensajeGlobal("serieExistente", "advertencia");
					}
				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return ok;
	}

	// publicos

	/**
	 * Asigna el qr de consulta de equipo
	 */
	public String getAsignacionQrConsultarHojaVida() {
		String valor = "";
		try {

			if (this.administrarSesionCliente != null && this.administrarSesionCliente.getPersonalSesion() != null && this.administrarSesionCliente.getQr() != null && !this.administrarSesionCliente.getQr().trim().equals("")) {

				this.getEquipoConsulta();
				this.equipoConsulta.setCodigoQr(this.administrarSesionCliente.getQr().trim());

				this.administrarSesionCliente.setQr(null);
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return valor;
	}

	/**
	 * Imprime el reporte de tarjetas con cósigos qr
	 */
	public void imprimirQrs() {
		try {
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Map<String, Object> parametros = new HashMap<String, Object>();

			parametros.put("pUrl", "" + this.getMensaje("urlSistema"));
			parametros.put("pUrlTarjeta", "" + this.getMensaje("urlTarjeta"));
			parametros.put("pLogo", this.getPath(IConstantes.PAQUETE_IMAGENES) + "/");
			this.generarListado(new JRBeanCollectionDataSource(this.equipos), IConstantes.REPORTE_QR, formato.format(getFechaHoraMinutoActualGmtColombia()), "pdf", parametros);

		} catch (Exception e) {

		}

	}

	/**
	 * Descarga el adjunto de calibracion
	 */
	public void descargarCalibracion(Calibracion aCalibracion) {
		try {
			Calibracion temp = IConsultasDAO.getCalibracionAdjunta(aCalibracion);
			if (temp != null) {
				descargarAdjunto(temp.getArchivo(), temp.getExtensionArchivo(), temp.getContentType());
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Descarga el adjunto
	 */
	public void descargarDocumento(DocumentoEquipo aDocumentoEquipo) {
		try {
			DocumentoEquipo temp = IConsultasDAO.getAdjuntoDocumento(aDocumentoEquipo);
			if (temp != null) {
				descargarAdjunto(temp.getArchivo(), temp.getExtensionArchivo(), temp.getContentType());
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Limpia la foto cargada para cargar otra
	 */
	public void limpiarFotoCargada() {
		this.fotoEquipo.settFile(null);
		this.fotoEquipo.setArchivo(null);
	}

	/**
	 * Limpia el archivo de calibración para volver a ingresar otro
	 */
	public void limpiarArchivoCalibracion() {
		this.calibracion.settFile(null);
		this.calibracion.setArchivo(null);
	}

	/**
	 * Limpia el archivo cargado para volver a ingresar otro
	 */
	public void limpiarArchivoCargado() {
		this.documentoEquipo.settFile(null);
		this.documentoEquipo.setArchivo(null);
	}

	/**
	 * Recibir foto y asignara aobjeto
	 * 
	 * @param event
	 */
	public void recibirFoto(FileUploadEvent event) {

		try {
			this.fotoEquipo.settFile(event.getFile());
			this.fotoEquipo.setArchivo(event.getFile().getContents());

			this.mostrarMensajeGlobal("archivoRecibido", "advertencia");
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Recibir el archivo y lo asigna al objeto
	 * 
	 * @param event
	 */
	public void recibirArchivoCalibracion(FileUploadEvent event) {

		try {
			this.calibracion.settFile(event.getFile());
			this.calibracion.setArchivo(event.getFile().getContents());
			this.calibracion.setContentType(event.getFile().getContentType());
			this.calibracion.setExtensionArchivo(this.getTipoArchivo(event.getFile().getFileName()));
			this.mostrarMensajeGlobal("archivoRecibido", "advertencia");
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Recibir el archivo y lo asigna al objeto
	 * 
	 * @param event
	 */
	public void recibirArchivo(FileUploadEvent event) {

		try {
			this.documentoEquipo.settFile(event.getFile());
			this.documentoEquipo.setArchivo(event.getFile().getContents());
			this.documentoEquipo.setContentType(event.getFile().getContentType());
			this.documentoEquipo.setExtensionArchivo(this.getTipoArchivo(event.getFile().getFileName()));
			this.mostrarMensajeGlobal("archivoRecibido", "advertencia");
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Permite crear una foto
	 */
	public void crearFoto() {
		Conexion conexion = new Conexion();

		try {
			if (isValidaFoto()) {
				conexion.setAutoCommitBD(false);

				this.fotoEquipo.getCamposBD();
				conexion.insertarBD(this.fotoEquipo.getEstructuraTabla().getTabla(), this.fotoEquipo.getEstructuraTabla().getPersistencia());
				conexion.commitBD();
				this.mostrarMensajeGlobal("creacionExitosa", "exito");

				// reseteo de variables
				this.fotoEquipo = null;
				this.getFotoEquipo();
				this.fotosEquipos = null;
				this.getFotosEquipos();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Crea un reporte de calibración
	 */
	public void crearCalibracion() {
		List<Cliente> clientes = null;
		Conexion conexion = new Conexion();

		try {
			if (isValidaCalibracion()) {
				Cliente cliente = new Cliente();
				cliente.setId(this.equipoTransaccion.getCliente().getId());
				clientes = IConsultasDAO.getClientes(cliente);

				conexion.setAutoCommitBD(false);

				this.calibracion.setFirmaClienteMomento(clientes.get(0).getFirma());

				this.calibracion.getCamposBD();
				conexion.insertarBD(this.calibracion.getEstructuraTabla().getTabla(), this.calibracion.getEstructuraTabla().getPersistencia());
				conexion.commitBD();
				this.mostrarMensajeGlobal("creacionExitosa", "exito");

				// reseteo de variables
				this.calibracion = null;
				this.getCalibracion();
				this.calibraciones = null;
				this.getCalibraciones();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Permite crear un documento
	 */
	public void crearDocumento() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoDocumento()) {
				conexion.setAutoCommitBD(false);

				this.documentoEquipo.getCamposBD();
				conexion.insertarBD(this.documentoEquipo.getEstructuraTabla().getTabla(), this.documentoEquipo.getEstructuraTabla().getPersistencia());
				conexion.commitBD();
				this.mostrarMensajeGlobal("creacionExitosa", "exito");

				// reseteo de variables
				this.documentoEquipo = null;
				this.getDocumentoEquipo();
				this.documentos = null;
				this.getDocumentos();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Obtiene la periodicidad en etiqueta
	 * 
	 * @param aAbreviatura
	 * @return etiqueta
	 */
	public String getPeriocidad(String aAbreviatura) {
		String etiqueta = "";

		if (aAbreviatura != null && aAbreviatura.equals("D")) {
			etiqueta = "" + this.getMensaje("dias");

		} else if (aAbreviatura != null && aAbreviatura.equals("M")) {
			etiqueta = "" + this.getMensaje("meses");

		} else if (aAbreviatura != null && aAbreviatura.equals("A")) {

			etiqueta = "" + this.getMensaje("anos");
		}

		return etiqueta;
	}

	/**
	 * Elimina la foto indicada
	 */
	public void eliminarFoto() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.fotoEquipoTransaccion.getCamposBD();
			conexion.eliminarBD(this.fotoEquipoTransaccion.getEstructuraTabla().getTabla(), this.fotoEquipoTransaccion.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

			this.eliminarFoto(this.fotoEquipoTransaccion.getId(), "fotosOriginalesEquipos");

			this.cerrarModal("panelEliminacionFoto");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.fotoEquipoTransaccion = null;
		this.getFotoEquipoTransaccion();
		this.fotosEquipos = null;
		this.getFotosEquipos();

	}

	/**
	 * Elimina la calibración elegida
	 */
	public void eliminarCalibracion() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.calibracionTransaccion.getCamposBD();
			conexion.eliminarBD(this.calibracionTransaccion.getEstructuraTabla().getTabla(), this.calibracionTransaccion.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

			this.cerrarModal("panelEliminacionCalibracion");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.calibracionTransaccion = null;
		this.getCalibracionTransaccion();
		this.calibraciones = null;
		this.getCalibraciones();

	}

	/**
	 * Elimina el documento elegido
	 */
	public void eliminarDocumento() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.documentoEquipoTransaccion.getCamposBD();
			conexion.eliminarBD(this.documentoEquipoTransaccion.getEstructuraTabla().getTabla(), this.documentoEquipoTransaccion.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

			this.cerrarModal("panelEliminacionDocumento");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.documentoEquipoTransaccion = null;
		this.getDocumentoEquipoTransaccion();
		this.documentos = null;
		this.getDocumentos();

	}

	/**
	 * Reordena las filas de las actividades
	 * 
	 * @param event
	 */
	public void onRowReorder(ReorderEvent event) {
		Conexion conexion = new Conexion();
		conexion.setAutoCommitBD(false);

		try {
			int i = 0;
			// if (event.getFromIndex() != event.getToIndex() && this.actividades !=
			// null && this.actividades.size() > 0) {
			if (this.actividades != null && this.actividades.size() > 0) {

				for (ActividadInformeEquipo p : this.actividades) {
					i++;
					p.setPosicion(i);
					p.getCamposBD();
					conexion.actualizarBD(p.getEstructuraTabla().getTabla(), p.getEstructuraTabla().getPersistencia(), p.getEstructuraTabla().getLlavePrimaria(), null);
					conexion.commitBD();
				}
				this.actividades = null;
				this.getActividades();

				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("actividadMovida", "" + (event.getFromIndex() + 1), "" + (event.getToIndex() + 1)), "exito");
			}

		} catch (Exception e) {
			conexion.rollbackBD();

			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {

			conexion.cerrarConexion();
		}

	}

	/**
	 * Elimina una actividad
	 */
	public void eliminarActividad() {

		Conexion conexion = new Conexion();
		List<ActividadInformeEquipo> acts = null;
		int i = 0;
		try {

			conexion.setAutoCommitBD(false);
			this.actividadTransaccion.getCamposBD();
			conexion.eliminarBD(this.actividadTransaccion.getEstructuraTabla().getTabla(), this.actividadTransaccion.getEstructuraTabla().getLlavePrimaria());

			ActividadInformeEquipo act = new ActividadInformeEquipo();
			act.getEquipo().setId(this.equipoTransaccion.getId());
			acts = IConsultasDAO.getActividades(conexion, act);
			if (acts != null && acts.size() > 0) {
				for (ActividadInformeEquipo p : acts) {
					i++;
					p.setPosicion(i);
					p.getCamposBD();
					conexion.actualizarBD(p.getEstructuraTabla().getTabla(), p.getEstructuraTabla().getPersistencia(), p.getEstructuraTabla().getLlavePrimaria(), null);

				}
			}

			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

			this.cerrarModal("panelEliminacionActividad");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.actividadTransaccion = null;
		this.getActividadTransaccion();
		this.actividades = null;
		this.getActividades();

	}

	/**
	 * Elimina un repuesto
	 */
	public void eliminarRepuesto() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.repuestoTransaccion.getCamposBD();
			conexion.eliminarBD(this.repuestoTransaccion.getEstructuraTabla().getTabla(), this.repuestoTransaccion.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

			this.cerrarModal("panelEliminacionRepuesto");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.repuestoTransaccion = null;
		this.getRepuestoTransaccion();
		this.repuestosEquipos = null;
		this.getRepuestosEquipos();

	}

	/**
	 * Edita los datos de una actividad
	 */
	public void editarActividad() {
		Conexion conexion = new Conexion();

		try {
			if (isValidaActividad("E")) {
				conexion.setAutoCommitBD(false);

				this.actividadTransaccion.getCamposBD();

				conexion.actualizarBD(this.actividadTransaccion.getEstructuraTabla().getTabla(), this.actividadTransaccion.getEstructuraTabla().getPersistencia(), this.actividadTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

				conexion.commitBD();

				this.mostrarMensajeGlobal("edicionExitosa", "exito");
				this.cerrarModal("panelEdicionActividad");

				// reseteo de variables
				this.actividadTransaccion = null;
				this.getActividadTransaccion();
				this.actividades = null;
				this.getActividades();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Edita los datos de un repuesto
	 */
	public void editarRepuesto() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoRepuesto("E")) {
				conexion.setAutoCommitBD(false);

				this.repuestoTransaccion.getCamposBD();

				conexion.actualizarBD(this.repuestoTransaccion.getEstructuraTabla().getTabla(), this.repuestoTransaccion.getEstructuraTabla().getPersistencia(), this.repuestoTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

				conexion.commitBD();

				this.mostrarMensajeGlobal("edicionExitosa", "exito");
				this.cerrarModal("panelEdicionRepuesto");

				// reseteo de variables
				this.repuestoTransaccion = null;
				this.getRepuestoTransaccion();
				this.repuestosEquipos = null;
				this.getRepuestosEquipos();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Permite crear una actividad para un equipo
	 */
	public void crearActividad() {
		Conexion conexion = new Conexion();

		try {
			if (isValidaActividad("C")) {
				conexion.setAutoCommitBD(false);

				if (this.actividades != null && this.actividades.size() > 0) {
					this.actividad.setPosicion(this.actividades.size() + 1);
				} else {

					this.actividad.setPosicion(1);
				}
				this.actividad.setIndicativoVigencia(IConstantes.ACTIVO);
				this.actividad.getCamposBD();
				conexion.insertarBD(this.actividad.getEstructuraTabla().getTabla(), this.actividad.getEstructuraTabla().getPersistencia());
				conexion.commitBD();
				this.mostrarMensajeGlobal("creacionExitosa", "exito");

				// reseteo de variables
				this.actividad = null;
				this.getActividad();
				this.actividades = null;
				this.getActividades();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Permite crear un repuesto
	 */
	public void crearRepuesto() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoRepuesto("C")) {
				conexion.setAutoCommitBD(false);

				this.repuestoEquipo.getCamposBD();
				conexion.insertarBD(this.repuestoEquipo.getEstructuraTabla().getTabla(), this.repuestoEquipo.getEstructuraTabla().getPersistencia());
				conexion.commitBD();
				this.mostrarMensajeGlobal("creacionExitosa", "exito");

				// reseteo de variables
				this.repuestoEquipo = null;
				this.getRepuestoEquipo();
				this.repuestosEquipos = null;
				this.getRepuestosEquipos();
			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Cancela la eliminación de una foto
	 * 
	 * @param aVista
	 */
	public void cancelarFotoTransaccion(String aVista) {
		try {

			this.fotoEquipoTransaccion = null;
			this.getFotoEquipoTransaccion();
			this.fotosEquipos = null;
			this.getFotosEquipos();

			if (aVista != null && aVista.equals("MODAL_ELIMINAR_FOTO")) {
				this.cerrarModal("panelEliminacionFoto");

			}
		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Cancela la eliminacion de una calibración
	 * 
	 * @param aVista
	 */
	public void cancelarCalibracionTransaccion(String aVista) {
		try {

			this.calibracionTransaccion = null;
			this.getCalibracionTransaccion();
			this.calibraciones = null;
			this.getCalibraciones();

			if (aVista != null && aVista.equals("MODAL_ELIMINAR_CALIBRACION")) {
				this.cerrarModal("panelEliminacionCalibracion");

			}
		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Cancela la eliminacion de un docuemnto en transacción
	 * 
	 * @param aVista
	 */
	public void cancelarDocumentoTransaccion(String aVista) {
		try {

			this.documentoEquipoTransaccion = null;
			this.getDocumentoEquipoTransaccion();
			this.documentos = null;
			this.getDocumentos();

			if (aVista != null && aVista.equals("MODAL_ELIMINAR_DOCUMENTO")) {
				this.cerrarModal("panelEliminacionDocumento");

			}
		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Cancela la edición de una actividad
	 * 
	 * @param aVista
	 */
	public void cancelarActividadTransaccion(String aVista) {
		try {

			this.actividadTransaccion = null;
			this.getActividadTransaccion();
			this.actividades = null;
			this.getActividades();

			if (aVista != null && aVista.equals("MODAL_EDITAR_ACTIVIDAD")) {
				this.cerrarModal("panelEdicionActividad");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_ACTIVIDAD")) {
				this.cerrarModal("panelEliminacionActividad");

			}
		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Cancela la edición de un repuesto en transacción
	 * 
	 * @param aVista
	 */
	public void cancelarRepuestoTransaccion(String aVista) {
		try {

			this.repuestoTransaccion = null;
			this.getRepuestoTransaccion();
			this.repuestosEquipos = null;
			this.getRepuestosEquipos();

			if (aVista != null && aVista.equals("MODAL_EDITAR_REPUESTO")) {
				this.cerrarModal("panelEdicionRepuesto");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_REPUESTO")) {
				this.cerrarModal("panelEliminacionRepuesto");

			}
		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Cancela la creación de una foto
	 */
	public void cancelarFoto() {

		try {
			this.fotoEquipo = null;
			this.getFotoEquipo();
			this.fotoEquipoTransaccion = null;
			this.getFotoEquipoTransaccion();
			this.fotosEquipos = null;
			this.getFotosEquipos();

			this.cerrarModal("panelVerFoto");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela la creación de una calibracion
	 */
	public void cancelarCalibracion() {

		try {
			this.calibracion = null;
			this.getCalibracion();
			this.calibracionTransaccion = null;
			this.getCalibracionTransaccion();
			this.calibraciones = null;
			this.getCalibraciones();

			this.cerrarModal("panelVerCalibracion");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela la creación de un documento
	 */
	public void cancelarDocumento() {

		try {
			this.documentoEquipo = null;
			this.getDocumentoEquipo();
			this.documentoEquipoTransaccion = null;
			this.getDocumentoEquipoTransaccion();
			this.documentos = null;
			this.getDocumentos();

			this.cerrarModal("panelVerDocumento");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela una actividad
	 */
	public void cancelarActividad() {

		try {
			this.actividad = null;
			this.getActividad();
			this.actividadTransaccion = null;
			this.getActividadTransaccion();
			this.actividades = null;
			this.getActividades();

			this.cerrarModal("panelVerActividad");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Este método cancela la información de un repuesto
	 */
	public void cancelarRepuesto() {

		try {
			this.repuestoEquipo = null;
			this.getRepuestoEquipo();
			this.repuestoTransaccion = null;
			this.getRepuestoTransaccion();
			this.repuestosEquipos = null;
			this.getRepuestosEquipos();

			this.cerrarModal("panelVerRepuesto");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna una foto para ser eliminada
	 * 
	 * @param aFotoEquipo
	 * @param aVista
	 */
	public void asignarFotoEquipo(FotoEquipo aFotoEquipo, String aVista) {

		try {

			this.fotoEquipoTransaccion = aFotoEquipo;

			this.abrirModal("panelEliminacionFoto");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna una calibración
	 * 
	 * @param aCalibracion
	 * @param aVista
	 */
	public void asignarCalibracion(Calibracion aCalibracion, String aVista) {

		try {

			this.calibracionTransaccion = aCalibracion;
			if (aVista.equals("MODAL_FIRMA")) {
				this.abrirModal("panelFirmaCalibracion");
			} else {
				this.abrirModal("panelEliminacionCalibracion");
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna un documento
	 * 
	 * @param aDocumentoEquipo
	 * @param aVista
	 */
	public void asignarDocumento(DocumentoEquipo aDocumentoEquipo, String aVista) {

		try {

			this.documentoEquipoTransaccion = aDocumentoEquipo;

			this.abrirModal("panelEliminacionDocumento");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna una actividad para ser editada o eliminada
	 * 
	 * @param aActividad
	 * @param aVista
	 */
	public void asignarActividad(ActividadInformeEquipo aActividad, String aVista) {

		try {

			this.actividadTransaccion = aActividad;

			if (aVista != null && aVista.equals("MODAL_EDITAR_ACTIVIDAD")) {

				this.abrirModal("panelEdicionActividad");

			} else {

				this.abrirModal("panelEliminacionActividad");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna un repuesto
	 * 
	 * @param aRepuesto
	 * @param aVista
	 */
	public void asignarRepuesto(RepuestoEquipo aRepuesto, String aVista) {

		try {

			this.repuestoTransaccion = aRepuesto;

			if (aVista != null && aVista.equals("MODAL_EDITAR_REPUESTO")) {

				this.abrirModal("panelEdicionRepuesto");

			} else {

				this.abrirModal("panelEliminacionRepuesto");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Obtiene los paneles técnicos
	 * 
	 * @return paneles
	 */
	public String getPanelesUpdateDatosTecnicosEditar() {

		String paneles = "@this";
		for (int i = 1; i <= IConstantes.NUMERO_PANELES_TECNICOS_NO_BIOMEDICOS.intValue(); i++) {
			paneles += ",pnlTecnicos" + i;
		}

		for (int i = IConstantes.NUMERO_DESDE_BIOMEDICO_TECNICO_EDITAR.intValue(); i <= IConstantes.NUMERO_PANELES_EQUIPOS_BIOMEDICOS_EDITAR.intValue(); i++) {
			paneles += ",pnlBiomedico" + i;
		}

		return paneles;
	}

	/**
	 * Obtiene los paneles técnicos
	 * 
	 * @return paneles
	 */
	public String getPanelesUpdateDatosTecnicos() {

		String paneles = "@this";
		for (int i = 1; i <= IConstantes.NUMERO_PANELES_TECNICOS_NO_BIOMEDICOS.intValue(); i++) {
			paneles += ",pnlTecnicos" + i;
		}

		for (int i = IConstantes.NUMERO_DESDE_BIOMEDICO_TECNICO.intValue(); i <= IConstantes.NUMERO_PANELES_EQUIPOS_BIOMEDICOS.intValue(); i++) {
			paneles += ",pnlBiomedico" + i;
		}

		return paneles;
	}

	/**
	 * Obtiene los paneles a los que debe hacer update
	 * 
	 * @return paneles
	 */
	public String getPanelesUpdateEquipoBiomedicoEditar() {

		String paneles = "@this";
		for (int i = 1; i <= IConstantes.NUMERO_PANELES_EQUIPOS_BIOMEDICOS_EDITAR.intValue(); i++) {
			paneles += ",pnlBiomedico" + i;
		}

		return paneles;
	}

	/**
	 * Obtiene los paneles a los que debe hacer update
	 * 
	 * @return paneles
	 */
	public String getPanelesUpdateEquipoBiomedico() {

		String paneles = "@this";
		for (int i = 1; i <= IConstantes.NUMERO_PANELES_EQUIPOS_BIOMEDICOS.intValue(); i++) {
			paneles += ",pnlBiomedico" + i;
		}

		return paneles;
	}

	/**
	 * Método que me selecciona el nombre del cliente, lo busca y llena el nit y
	 * otros
	 * 
	 * @param event
	 */
	public void onItemSelectEditar(SelectEvent event) {

		try {

			if (event != null && event.getObject() != null && !event.getObject().toString().trim().equals("") && this.equipoTransaccion != null) {

				String[] separado = event.getObject().toString().trim().split("##id=");

				Cliente temp = new Cliente();
				temp.setId(Integer.parseInt(separado[1]));

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.equipoTransaccion.getCliente().setId(cliente.getId());
					this.equipoTransaccion.getCliente().setNit(cliente.getNit());
					this.equipoTransaccion.getCliente().setCliente(cliente.getCliente());
					this.equipoTransaccion.getCliente().setUbicacion(cliente.getUbicacion());

					this.equipoTransaccion.getCliente().settClienteAutocompletado(this.equipoTransaccion.getCliente().getCliente() + ", " + this.equipoTransaccion.getCliente().getUbicacion());

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

			if (event != null && event.getObject() != null && !event.getObject().toString().trim().equals("") && this.equipo != null) {

				String[] separado = event.getObject().toString().trim().split("##id=");

				Cliente temp = new Cliente();
				temp.setId(Integer.parseInt(separado[1]));
				// nuevo
				// temp.setEstadoVigencia(IConstantes.ACTIVO);

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.equipo.getCliente().setId(cliente.getId());
					this.equipo.getCliente().setNit(cliente.getNit());
					this.equipo.getCliente().setCliente(cliente.getCliente());
					this.equipo.getCliente().setUbicacion(cliente.getUbicacion());

					this.equipo.getCliente().settClienteAutocompletado(this.equipo.getCliente().getCliente() + ", " + this.equipo.getCliente().getUbicacion());

				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Obtiene un método de autocompletar para el nombre del cliente
	 * 
	 * @param aTexto
	 * @return clientes
	 */
	public List<String> usarAutocompletarEditar(String aTexto) {
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

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clientes;
	}

	/**
	 * Obtiene un método de autocompletar para el nombre del cliente
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

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clientes;
	}

	/**
	 * Consulta los equipos
	 */
	public void consultarEquipos() {

		try {

			this.equipos = IConsultasDAO.getEquipos(this.equipoConsulta);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Crea un registro de los equipos clonados
	 */
	public void crearEquiposClonados() {
		Conexion conexion = new Conexion();
		Equipo equipoTemp = null;
		boolean ok = true;
		int i = 0;
		try {

			int contadorInventario = 0;
			int contadorSerie = 0;

			boolean validarServicio = false;

			if (this.equipoTransaccion.getEquipoBiomedico() != null && this.equipoTransaccion.getEquipoBiomedico().equals(IConstantes.AFIRMACION)) {
				validarServicio = true;
			}

			if (validarServicio) {
				if (this.equipoTransaccion.gettEquiposNuevos().stream().anyMatch(p -> !(p.getNumeroInventario() != null && !p.getNumeroInventario().trim().equals("")) || !(p.getNumeroSerie() != null && !p.getNumeroSerie().trim().equals("")) || !(p.getModelo() != null && !p.getModelo().trim().equals("") || !(p.getServicio() != null && !p.getServicio().trim().equals(""))))) {
					ok = false;
					this.mostrarMensajeGlobal("equiposConCamposVacios", "advertencia");

				}

			} else {
				if (this.equipoTransaccion.gettEquiposNuevos().stream().anyMatch(p -> !(p.getNumeroInventario() != null && !p.getNumeroInventario().trim().equals("")) || !(p.getNumeroSerie() != null && !p.getNumeroSerie().trim().equals("")) || !(p.getModelo() != null && !p.getModelo().trim().equals("")))) {
					ok = false;
					this.mostrarMensajeGlobal("equiposConCamposVacios", "advertencia");

				}

			}

			if (ok) {
				for (Equipo e : this.equipoTransaccion.gettEquiposNuevos()) {

					contadorInventario = 0;
					contadorSerie = 0;

					for (Equipo e2 : this.equipoTransaccion.gettEquiposNuevos()) {
						if (e2.getNumeroInventario() != null && e2.getNumeroInventario().trim().equals(e.getNumeroInventario())) {
							contadorInventario++;
						}
						if (e2.getNumeroSerie() != null && e2.getNumeroSerie().trim().equals(e.getNumeroSerie())) {
							contadorSerie++;
						}
					}
					if (contadorInventario > 1 || contadorSerie > 1) {
						ok = false;
						this.mostrarMensajeGlobal("camposRepetidos", "advertencia");
						break;
					}

				}

			}

			if (ok) {

				conexion.setAutoCommitBD(false);
				for (Equipo e : this.equipoTransaccion.gettEquiposNuevos()) {

					this.equipoTransaccion.setNumeroInventario(e.getNumeroInventario());
					this.equipoTransaccion.setModelo(e.getModelo());
					this.equipoTransaccion.setNumeroSerie(e.getNumeroSerie());
					if (this.equipoTransaccion.getEquipoBiomedico() != null && this.equipoTransaccion.getEquipoBiomedico().equals(IConstantes.AFIRMACION)) {

						this.equipoTransaccion.setServicio(e.getServicio());
					}

					if (isValidoEquiposClonado(conexion)) {
						// realiza validación de qr
						do {
							this.equipoTransaccion.setCodigoQr(this.getQRAleatorio());

							equipoTemp = new Equipo();
							equipoTemp.setCodigoQr(this.equipoTransaccion.getCodigoQr());

						} while (IConsultasDAO.isExistenteQR(conexion, equipoTemp));

						this.equipoTransaccion.getCamposBD();

						conexion.insertarBD(this.equipoTransaccion.getEstructuraTabla().getTabla(), this.equipoTransaccion.getEstructuraTabla().getPersistencia());

						this.equipoTransaccion.setId(conexion.getUltimoSerialTransaccion(this.equipoTransaccion.getEstructuraTabla().getTabla()));

						if (this.actividades != null && this.actividades.size() > 0) {
							i = 0;
							for (ActividadInformeEquipo a : this.actividades) {
								if (a.istSeleccionado()) {
									i++;
									a.setPosicion(i);
									a.getEquipo().setId(this.equipoTransaccion.getId());
									a.getCamposBD();
									conexion.insertarBD(a.getEstructuraTabla().getTabla(), a.getEstructuraTabla().getPersistencia());
								}

							}
						}

					} else {

						ok = false;
					}

				}

				if (ok) {
					conexion.commitBD();

					this.mostrarMensajeGlobalPersonalizado(this.getMensaje("clonacionExitosa", "" + this.equipoTransaccion.gettEquiposNuevos().size()), "exito");
					// reseteo de variables

					this.cerrarModal("panelClonarEquipo");

					// reseteo de variables
					this.equipoTransaccion = null;
					this.getEquipoTransaccion();
					this.equipos = null;
					this.consultarEquipos();

				} else {
					conexion.rollbackBD();
				}

			}

		} catch (

		Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Permite crear un equipo
	 */
	public void crearEquipo() {
		Conexion conexion = new Conexion();
		Equipo equipoTemp = null;
		try {
			if (isValidoEquipo("C")) {

				conexion.setAutoCommitBD(false);
				// realiza validación de qr
				do {
					this.equipo.setCodigoQr(this.getQRAleatorio());

					equipoTemp = new Equipo();
					equipoTemp.setCodigoQr(this.equipo.getCodigoQr());

				} while (IConsultasDAO.isExistenteQR(conexion, equipoTemp));

				this.equipo.getCamposBD();

				conexion.insertarBD(this.equipo.getEstructuraTabla().getTabla(), this.equipo.getEstructuraTabla().getPersistencia());

				conexion.commitBD();

				this.mostrarMensajeGlobal("creacionExitosa", "exito");
				// reseteo de variables

				this.equipoConsulta = null;
				this.getEquipoConsulta();
				this.equipoConsulta.setCodigoQr(this.equipo.getCodigoQr());

				// reseteo de variables
				this.equipo = null;
				this.getEquipo();
				this.equipos = null;
				this.consultarEquipos();
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
		this.equipoConsulta = null;
		this.getEquipoConsulta();

		this.equipos = null;
	}

	/**
	 * Edita los datos de un equipo
	 */
	public void editarEquipo() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoEquipo("E")) {
				conexion.setAutoCommitBD(false);
				this.equipoTransaccion.getCamposBD();

				conexion.actualizarBD(this.equipoTransaccion.getEstructuraTabla().getTabla(), this.equipoTransaccion.getEstructuraTabla().getPersistencia(), this.equipoTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

				conexion.commitBD();
				this.mostrarMensajeGlobal("edicionExitosa", "exito");
				this.cerrarModal("panelEdicionEquipo");

				// reseteo de variables
				this.equipoTransaccion = null;
				this.getEquipoTransaccion();
				this.equipos = null;
				this.consultarEquipos();

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Elimina un equipo
	 */
	public void eliminarEquipo() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.equipoTransaccion.getCamposBD();
			conexion.eliminarBD(this.equipoTransaccion.getEstructuraTabla().getTabla(), this.equipoTransaccion.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");
			this.cerrarModal("panelEliminacionEquipo");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.equipoTransaccion = null;
		this.getEquipoTransaccion();
		this.equipos = null;
		this.consultarEquipos();

	}

	/**
	 * Cancela la creación de un equipo
	 */
	public void cancelarEquipo() {

		try {
			this.equipo = null;
			this.getEquipo();
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Quita un equipo de clonación
	 */
	public void quitarEquipoClonacion(Equipo aEquipo) {

		this.equipoTransaccion.gettEquiposNuevos().remove(aEquipo);
		this.mostrarMensajeGlobal("removioRegistro", "advertencia");
	}

	/**
	 * Sgrega otro equipo a clonación
	 */
	public void agregarOtroEquipoClonacion() {
		Equipo eq = new Equipo();
		this.equipoTransaccion.gettEquiposNuevos().add(eq);
		this.mostrarMensajeGlobal("agregoEquipo", "advertencia");
	}

	/**
	 * Cancela un equipo en transacción
	 * 
	 * @param aVista
	 */
	public void cancelarEquipoTransaccion(String aVista) {
		try {

			this.equipoTransaccion = new Equipo();
			this.equipos = null;
			this.consultarEquipos();

			if (aVista != null && aVista.equals("MODAL_EDITAR_EQUIPO")) {
				this.cerrarModal("panelEdicionEquipo");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_EQUIPO")) {
				this.cerrarModal("panelEliminacionEquipo");

			} else if (aVista != null && aVista.equals("MODAL_VER_EQUIPO")) {
				this.cerrarModal("panelVerEquipo");

			} else if (aVista != null && aVista.equals("MODAL_CLONAR")) {
				this.cerrarModal("panelClonarEquipo");

			}

		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Asigna un equipo para realizar una transacción
	 * 
	 * @param aEquipo
	 * @param aVista
	 */
	public void asignarEquipo(Equipo aEquipo, String aVista) {

		try {

			this.equipoTransaccion = aEquipo;
			if (aVista != null && aVista.equals("MODAL_CLONAR")) {
				// que en realidad es copia de correo
				this.equipoTransaccion.settCopiaNumeroInventario(this.equipoTransaccion.getNumeroInventario());
				this.equipoTransaccion.settCopiaNumeroSerie(this.equipoTransaccion.getNumeroSerie());
				this.equipoTransaccion.getCliente().settClienteAutocompletado(this.equipoTransaccion.getCliente().getCliente() + ", " + this.equipoTransaccion.getCliente().getUbicacion());

				this.calibraciones = null;
				this.getCalibraciones();

				this.actividades = null;
				this.getActividades();
				if (this.actividades != null && this.actividades.size() > 0) {
					for (ActividadInformeEquipo a : this.actividades) {
						a.settSeleccionado(true);
					}
				}

				this.equipoTransaccion.settEquiposNuevos(new ArrayList<Equipo>());

				Equipo eq = new Equipo();
				this.equipoTransaccion.gettEquiposNuevos().add(eq);

				this.abrirModal("panelClonarEquipo");

			} else if (aVista != null && aVista.equals("MODAL_EDITAR_EQUIPO")) {
				// que en realidad es copia de correo
				this.equipoTransaccion.settCopiaNumeroInventario(this.equipoTransaccion.getNumeroInventario());
				this.equipoTransaccion.settCopiaNumeroSerie(this.equipoTransaccion.getNumeroSerie());
				this.equipoTransaccion.getCliente().settClienteAutocompletado(this.equipoTransaccion.getCliente().getCliente() + ", " + this.equipoTransaccion.getCliente().getUbicacion());

				this.calibraciones = null;
				this.getCalibraciones();

				this.abrirModal("panelEdicionEquipo");
			} else if (aVista != null && aVista.equals("MODAL_VER_EQUIPO")) {

				this.equipoTransaccion.getCliente().settClienteAutocompletado(this.equipoTransaccion.getCliente().getCliente() + ", " + this.equipoTransaccion.getCliente().getUbicacion());
				this.abrirModal("panelVerEquipo");

			} else if (aVista != null && aVista.equals("MODAL_VER_EQUIPO_INFORME")) {

				this.equipoTransaccion.getCliente().settClienteAutocompletado(this.equipoTransaccion.getCliente().getCliente() + ", " + this.equipoTransaccion.getCliente().getUbicacion());

				this.fotosEquipos = null;
				this.getFotosEquipos();

				this.abrirModal("panelVerEquipo");

			} else if (aVista != null && aVista.equals("MODAL_VER_REPUESTOS")) {

				this.repuestoEquipo = null;
				this.getRepuestoEquipo();
				this.repuestoTransaccion = null;
				this.getRepuestoTransaccion();
				this.repuestosEquipos = null;
				this.getRepuestosEquipos();

				this.abrirModal("panelVerRepuesto");

			} else if (aVista != null && aVista.equals("MODAL_VER_ACTIVIDADES")) {

				this.actividad = null;
				this.getActividad();
				this.actividadTransaccion = null;
				this.getActividadTransaccion();
				this.actividades = null;
				this.getActividades();

				this.abrirModal("panelVerActividad");

			} else if (aVista != null && aVista.equals("MODAL_VER_DOCUMENTOS")) {

				this.documentoEquipo = null;
				this.getDocumentoEquipo();
				this.documentoEquipoTransaccion = null;
				this.getDocumentoEquipoTransaccion();
				this.documentos = null;
				this.getDocumentos();

				this.abrirModal("panelVerDocumento");

			} else if (aVista != null && aVista.equals("MODAL_VER_CALIBRACIONES")) {

				this.calibracion = null;
				this.getCalibracion();
				this.calibracionTransaccion = null;
				this.getCalibracionTransaccion();
				this.calibraciones = null;
				this.getCalibraciones();

				this.abrirModal("panelVerCalibracion");

			} else if (aVista != null && aVista.equals("MODAL_VER_FOTOS")) {

				this.fotoEquipo = null;
				this.getFotoEquipo();
				this.fotoEquipoTransaccion = null;
				this.getFotoEquipoTransaccion();
				this.fotosEquipos = null;
				this.getFotosEquipos();

				this.abrirModal("panelVerFoto");

			}

			else {
				this.equipoTransaccion.getCliente().settClienteAutocompletado(this.equipoTransaccion.getCliente().getCliente() + ", " + this.equipoTransaccion.getCliente().getUbicacion());
				this.abrirModal("panelEliminacionEquipo");
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Obtiene clases de soporte rápido
	 * 
	 * @return items
	 */
	public List<SelectItem> getItemsClasesSoporteEditar() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		try {

			items.add(new SelectItem("", this.getMensaje("comboVacio")));

			ClaseSoporteBiomedico clasificacion = new ClaseSoporteBiomedico();
			clasificacion.setIndicativoVigencia(IConstantes.ACTIVO);
			List<ClaseSoporteBiomedico> clasificaciones = IConsultasDAO.getClasesSoportesBiomedicos(clasificacion);

			if (clasificaciones != null && clasificaciones.size() > 0) {
				for (ClaseSoporteBiomedico p : clasificaciones) {

					items.add(new SelectItem(p.getId(), p.getNombre()));
				}
			}

			if (this.equipoTransaccion != null && this.equipoTransaccion.getClaseSoporteBiomedico() != null && this.equipoTransaccion.getClaseSoporteBiomedico().getIndicativoVigencia() != null && this.equipoTransaccion.getClaseSoporteBiomedico().getIndicativoVigencia().equals(IConstantes.INACTIVO)) {
				items.add(new SelectItem(this.equipoTransaccion.getClaseSoporteBiomedico().getId(), this.equipoTransaccion.getClaseSoporteBiomedico().getNombre()));
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return items;
	}

	/**
	 * Obtiene items de clases soporte activos
	 * 
	 * @return itemsClasesSoporteActivos
	 */
	public List<SelectItem> getItemsClasesSoporteActivos() {
		try {
			if (this.itemsClasesSoporteActivos == null) {
				this.itemsClasesSoporteActivos = new ArrayList<SelectItem>();
				ClaseSoporteBiomedico clase = new ClaseSoporteBiomedico();
				clase.setIndicativoVigencia(IConstantes.ACTIVO);
				List<ClaseSoporteBiomedico> clases = IConsultasDAO.getClasesSoportesBiomedicos(clase);
				this.itemsClasesSoporteActivos.add(new SelectItem("", this.getMensaje("comboVacio")));
				clases.forEach(p -> this.itemsClasesSoporteActivos.add(new SelectItem(p.getId(), p.getNombre())));

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsClasesSoporteActivos;
	}

	/**
	 * Establece listado
	 * 
	 * @param itemsClasesSoporteActivos
	 */
	public void setItemsClasesSoporteActivos(List<SelectItem> itemsClasesSoporteActivos) {
		this.itemsClasesSoporteActivos = itemsClasesSoporteActivos;
	}

	/**
	 * Obtiene las clasificaciones biomédicas a editar
	 * 
	 * @return items
	 */
	public List<SelectItem> getItemsClasificacionesBiomedicasEditar() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		try {

			items.add(new SelectItem("", this.getMensaje("comboVacio")));

			ClasificacionBiomedica clasificacion = new ClasificacionBiomedica();
			clasificacion.setIndicativoVigencia(IConstantes.ACTIVO);
			List<ClasificacionBiomedica> clasificaciones = IConsultasDAO.getClasificacionesBiomedicas(clasificacion);

			if (clasificaciones != null && clasificaciones.size() > 0) {
				for (ClasificacionBiomedica p : clasificaciones) {

					items.add(new SelectItem(p.getId(), p.getNombre()));
				}
			}

			if (this.equipoTransaccion != null && this.equipoTransaccion.getClasificacionBiomedica() != null && this.equipoTransaccion.getClasificacionBiomedica().getIndicativoVigencia() != null && this.equipoTransaccion.getClasificacionBiomedica().getIndicativoVigencia().equals(IConstantes.INACTIVO)) {
				items.add(new SelectItem(this.equipoTransaccion.getClasificacionBiomedica().getId(), this.equipoTransaccion.getClasificacionBiomedica().getNombre()));
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return items;
	}

	/**
	 * Obtiene items de clasificaciones biomédicas
	 * 
	 * @return itemsClasificacionesBiomedicasActivas
	 */
	public List<SelectItem> getItemsClasificacionesBiomedicasActivas() {
		try {
			if (this.itemsClasificacionesBiomedicasActivas == null) {
				this.itemsClasificacionesBiomedicasActivas = new ArrayList<SelectItem>();
				ClasificacionBiomedica clasificacion = new ClasificacionBiomedica();
				clasificacion.setIndicativoVigencia(IConstantes.ACTIVO);
				List<ClasificacionBiomedica> clasificaciones = IConsultasDAO.getClasificacionesBiomedicas(clasificacion);
				this.itemsClasificacionesBiomedicasActivas.add(new SelectItem("", this.getMensaje("comboVacio")));
				clasificaciones.forEach(p -> this.itemsClasificacionesBiomedicasActivas.add(new SelectItem(p.getId(), p.getNombre())));

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsClasificacionesBiomedicasActivas;
	}

	/**
	 * Establece items de clasificaciones biomédicaas
	 * 
	 * @param itemsClasificacionesBiomedicasActivas
	 */
	public void setItemsClasificacionesBiomedicasActivas(List<SelectItem> itemsClasificacionesBiomedicasActivas) {
		this.itemsClasificacionesBiomedicasActivas = itemsClasificacionesBiomedicasActivas;
	}

	/**
	 * Obtiene las clasificaciones riesgo a editar en una transacción
	 * 
	 * @return items
	 */
	public List<SelectItem> getItemsClasificacionesRiesgoEditar() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		try {

			items.add(new SelectItem("", this.getMensaje("comboVacio")));

			ClasificacionRiesgo clasificacionRiesgo = new ClasificacionRiesgo();
			clasificacionRiesgo.setIndicativoVigencia(IConstantes.ACTIVO);
			List<ClasificacionRiesgo> clasificaciones = IConsultasDAO.getClasificacionesRiesgo(clasificacionRiesgo);

			if (clasificaciones != null && clasificaciones.size() > 0) {
				for (ClasificacionRiesgo p : clasificaciones) {

					items.add(new SelectItem(p.getId(), p.getNombre()));
				}
			}

			if (this.equipoTransaccion != null && this.equipoTransaccion.getClasificacionRiesgo() != null && this.equipoTransaccion.getClasificacionRiesgo().getIndicativoVigencia() != null && this.equipoTransaccion.getClasificacionRiesgo().getIndicativoVigencia().equals(IConstantes.INACTIVO)) {
				items.add(new SelectItem(this.equipoTransaccion.getClasificacionRiesgo().getId(), this.equipoTransaccion.getClasificacionRiesgo().getNombre()));
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return items;
	}

	/**
	 * Obtiene items de clasificaciones riesgo activas
	 * 
	 * @return itemsClasificacionesRiesgoActivas
	 */
	public List<SelectItem> getItemsClasificacionesRiesgoActivas() {
		try {
			if (this.itemsClasificacionesRiesgoActivas == null) {
				this.itemsClasificacionesRiesgoActivas = new ArrayList<SelectItem>();
				ClasificacionRiesgo clasificacion = new ClasificacionRiesgo();
				clasificacion.setIndicativoVigencia(IConstantes.ACTIVO);
				List<ClasificacionRiesgo> clasificaciones = IConsultasDAO.getClasificacionesRiesgo(clasificacion);
				this.itemsClasificacionesRiesgoActivas.add(new SelectItem("", this.getMensaje("comboVacio")));
				clasificaciones.forEach(p -> this.itemsClasificacionesRiesgoActivas.add(new SelectItem(p.getId(), p.getNombre())));

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsClasificacionesRiesgoActivas;
	}

	/**
	 * Establece items de clasificaciones riesgo
	 * 
	 * @param itemsClasificacionesRiesgoActivas
	 */
	public void setItemsClasificacionesRiesgoActivas(List<SelectItem> itemsClasificacionesRiesgoActivas) {
		this.itemsClasificacionesRiesgoActivas = itemsClasificacionesRiesgoActivas;
	}

	/**
	 * Obtiene las clases de documentos activos
	 * 
	 * @return itemsClasesDocumentosActivos
	 */
	public List<SelectItem> getItemsClasesDocumentosActivos() {
		try {

			this.itemsClasesDocumentosActivos = new ArrayList<SelectItem>();
			ClaseDocumento clase = new ClaseDocumento();
			clase.setIndicativoVigencia(IConstantes.ACTIVO);
			List<ClaseDocumento> clases = IConsultasDAO.getClasesDocumentos((clase));
			this.itemsClasesDocumentosActivos.add(new SelectItem("", this.getMensaje("comboVacio")));
			if (this.equipoTransaccion != null && this.equipoTransaccion.getId() != null && this.equipoTransaccion.getEquipoBiomedico().equals(IConstantes.NEGACION)) {
				clases.stream().filter(p -> !p.getEuipoBiomedico().equals(IConstantes.AFIRMACION)).forEach(p -> this.itemsClasesDocumentosActivos.add(new SelectItem(p.getId(), p.getNombre())));
			} else {
				clases.forEach(p -> this.itemsClasesDocumentosActivos.add(new SelectItem(p.getId(), p.getNombre())));
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsClasesDocumentosActivos;
	}

	/**
	 * Establece las clases de documentos activos
	 * 
	 * @param itemsClasesDocumentosActivos
	 */
	public void setItemsClasesDocumentosActivos(List<SelectItem> itemsClasesDocumentosActivos) {
		this.itemsClasesDocumentosActivos = itemsClasesDocumentosActivos;
	}

	// get/sets

	public Equipo getEquipo() {
		try {
			if (this.equipo == null) {
				this.equipo = new Equipo();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public Equipo getEquipoTransaccion() {
		try {
			if (this.equipoTransaccion == null) {
				this.equipoTransaccion = new Equipo();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return equipoTransaccion;
	}

	public void setEquipoTransaccion(Equipo equipoTransaccion) {
		this.equipoTransaccion = equipoTransaccion;
	}

	public RepuestoEquipo getRepuestoEquipo() {
		try {
			if (this.repuestoEquipo == null) {
				this.repuestoEquipo = new RepuestoEquipo();
				this.repuestoEquipo.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return repuestoEquipo;
	}

	public void setRepuestoEquipo(RepuestoEquipo repuestoEquipo) {
		this.repuestoEquipo = repuestoEquipo;
	}

	public RepuestoEquipo getRepuestoTransaccion() {
		try {
			if (this.repuestoTransaccion == null) {
				this.repuestoTransaccion = new RepuestoEquipo();
				this.repuestoTransaccion.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return repuestoTransaccion;
	}

	public void setRepuestoTransaccion(RepuestoEquipo repuestoTransaccion) {
		this.repuestoTransaccion = repuestoTransaccion;
	}

	public Equipo getEquipoConsulta() {
		try {
			if (this.equipoConsulta == null) {
				this.equipoConsulta = new Equipo();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return equipoConsulta;
	}

	public void setEquipoConsulta(Equipo equipoConsulta) {
		this.equipoConsulta = equipoConsulta;
	}

	public List<Equipo> getEquipos() {

		return equipos;
	}

	public void setEquipos(List<Equipo> equipos) {
		this.equipos = equipos;
	}

	public List<RepuestoEquipo> getRepuestosEquipos() {
		try {
			if (this.repuestosEquipos == null && this.equipoTransaccion != null && this.equipoTransaccion.getId() != null) {
				RepuestoEquipo repuesto = new RepuestoEquipo();
				repuesto.getEquipo().setId(this.equipoTransaccion.getId());
				this.repuestosEquipos = IConsultasDAO.getRepuestos(repuesto);

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return repuestosEquipos;
	}

	public void setRepuestosEquipos(List<RepuestoEquipo> repuestosEquipos) {
		this.repuestosEquipos = repuestosEquipos;
	}

	public DocumentoEquipo getDocumentoEquipo() {
		try {
			if (this.documentoEquipo == null) {
				this.documentoEquipo = new DocumentoEquipo();
				this.documentoEquipo.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return documentoEquipo;
	}

	public void setDocumentoEquipo(DocumentoEquipo documentoEquipo) {
		this.documentoEquipo = documentoEquipo;
	}

	public DocumentoEquipo getDocumentoEquipoTransaccion() {
		try {
			if (this.documentoEquipoTransaccion == null) {
				this.documentoEquipoTransaccion = new DocumentoEquipo();
				this.documentoEquipoTransaccion.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return documentoEquipoTransaccion;
	}

	public void setDocumentoEquipoTransaccion(DocumentoEquipo documentoEquipoTransaccion) {
		this.documentoEquipoTransaccion = documentoEquipoTransaccion;
	}

	public Calibracion getCalibracion() {
		try {
			if (this.calibracion == null) {
				this.calibracion = new Calibracion();
				this.calibracion.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return calibracion;
	}

	public void setCalibracion(Calibracion calibracion) {
		this.calibracion = calibracion;
	}

	public Calibracion getCalibracionTransaccion() {
		try {
			if (this.calibracionTransaccion == null) {
				this.calibracionTransaccion = new Calibracion();
				this.calibracionTransaccion.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return calibracionTransaccion;
	}

	public void setCalibracionTransaccion(Calibracion calibracionTransaccion) {
		this.calibracionTransaccion = calibracionTransaccion;
	}

	public List<DocumentoEquipo> getDocumentos() {
		try {
			if (this.documentos == null && this.equipoTransaccion != null && this.equipoTransaccion.getId() != null) {
				DocumentoEquipo doc = new DocumentoEquipo();
				doc.getEquipo().setId(this.equipoTransaccion.getId());
				this.documentos = IConsultasDAO.getDocumentos(doc);

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return documentos;
	}

	public void setDocumentos(List<DocumentoEquipo> documentos) {
		this.documentos = documentos;
	}

	public List<Calibracion> getCalibraciones() {
		try {
			if (this.calibraciones == null && this.equipoTransaccion != null && this.equipoTransaccion.getId() != null) {
				Calibracion cal = new Calibracion();
				cal.getEquipo().setId(this.equipoTransaccion.getId());
				this.calibraciones = IConsultasDAO.getCalibraciones(cal);

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return calibraciones;
	}

	public void setCalibraciones(List<Calibracion> calibraciones) {
		this.calibraciones = calibraciones;
	}

	public ActividadInformeEquipo getActividad() {
		try {
			if (this.actividad == null) {
				this.actividad = new ActividadInformeEquipo();
				this.actividad.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return actividad;
	}

	public void setActividad(ActividadInformeEquipo actividad) {
		this.actividad = actividad;
	}

	public ActividadInformeEquipo getActividadTransaccion() {
		try {
			if (this.actividadTransaccion == null) {
				this.actividadTransaccion = new ActividadInformeEquipo();
				this.actividadTransaccion.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return actividadTransaccion;
	}

	public void setActividadTransaccion(ActividadInformeEquipo actividadTransaccion) {
		this.actividadTransaccion = actividadTransaccion;
	}

	public List<ActividadInformeEquipo> getActividades() {
		try {
			if (this.actividades == null && this.equipoTransaccion != null && this.equipoTransaccion.getId() != null) {
				ActividadInformeEquipo actividad = new ActividadInformeEquipo();
				actividad.getEquipo().setId(this.equipoTransaccion.getId());
				this.actividades = IConsultasDAO.getActividades(actividad);

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return actividades;
	}

	public void setActividades(List<ActividadInformeEquipo> actividades) {
		this.actividades = actividades;
	}

	public FotoEquipo getFotoEquipo() {
		try {
			if (this.fotoEquipo == null) {
				this.fotoEquipo = new FotoEquipo();
				this.fotoEquipo.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return fotoEquipo;
	}

	public void setFotoEquipo(FotoEquipo fotoEquipo) {
		this.fotoEquipo = fotoEquipo;
	}

	public FotoEquipo getFotoEquipoTransaccion() {
		try {
			if (this.fotoEquipoTransaccion == null) {
				this.fotoEquipoTransaccion = new FotoEquipo();
				this.fotoEquipoTransaccion.getEquipo().setId(this.equipoTransaccion.getId());
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return fotoEquipoTransaccion;
	}

	public void setFotoEquipoTransaccion(FotoEquipo fotoEquipoTransaccion) {
		this.fotoEquipoTransaccion = fotoEquipoTransaccion;
	}

	public List<FotoEquipo> getFotosEquipos() {
		try {
			if (this.fotosEquipos == null && this.equipoTransaccion != null && this.equipoTransaccion.getId() != null) {
				FotoEquipo temp = new FotoEquipo();
				temp.getEquipo().setId(this.equipoTransaccion.getId());
				this.fotosEquipos = IConsultasDAO.getFotosEquipos(temp);

				if (this.fotosEquipos != null && this.fotosEquipos.size() > 0) {
					for (FotoEquipo f : this.fotosEquipos) {
						this.guardarImagenEnDisco(f.getId(), f.gettFotoDecodificada(), "fotosOriginalesEquipos");
						f.settNombreFoto("equipo" + f.getId() + ".png");

					}
				}

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return fotosEquipos;
	}

	public void setFotosEquipos(List<FotoEquipo> fotosEquipos) {
		this.fotosEquipos = fotosEquipos;
	}

	public AdministrarSesionCliente getAdministrarSesionCliente() {
		return administrarSesionCliente;
	}

	public void setAdministrarSesionCliente(AdministrarSesionCliente administrarSesionCliente) {
		this.administrarSesionCliente = administrarSesionCliente;
	}

}
