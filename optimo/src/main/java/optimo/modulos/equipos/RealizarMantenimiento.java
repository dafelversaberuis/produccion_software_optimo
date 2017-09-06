package optimo.modulos.equipos;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
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
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.primefaces.event.FileUploadEvent;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import optimo.Conexion;
import optimo.beans.ActividadInformeEquipo;
import optimo.beans.ActividadMantenimiento;
import optimo.beans.ActividadNoPreventiva;
import optimo.beans.BateriaMantenimiento;
import optimo.beans.Cliente;
import optimo.beans.Cronograma;
import optimo.beans.Equipo;
import optimo.beans.FotoEquipo;
import optimo.beans.InformeMantenimiento;
import optimo.beans.MaterialFotografico;
import optimo.beans.ReporteFalla;
import optimo.beans.Tecnico;
import optimo.generales.Basico;
import optimo.generales.ConsultarFuncionesAPI;
import optimo.generales.IConstantes;
import optimo.generales.IEmail;
import optimo.modulos.IConsultasDAO;
import optimo.modulos.personal.AdministrarSesionCliente;

@ManagedBean
@ViewScoped
public class RealizarMantenimiento extends ConsultarFuncionesAPI implements Serializable {

	/**
	 * 
	 */
	private static final long					serialVersionUID	= -8328495759534252256L;

	// inyecta el bean de sesion
	@ManagedProperty(value = "#{administrarSesionCliente}")
	private AdministrarSesionCliente	administrarSesionCliente;

	private Cliente										cliente;
	private Equipo										equipo;
	private Cronograma								cronograma;
	private InformeMantenimiento			informeMantenimiento;
	private ActividadMantenimiento		actividadMantenimiento;
	private MaterialFotografico				materialFotografico;
	private ActividadNoPreventiva			actividadNoPreventiva;

	private List<Cronograma>					cronogramas;
	private List<SelectItem>					itemsClientes;
	private List<SelectItem>					itemsEquipos;
	private List<SelectItem>					itemsCronogramas;

	private Integer										inicio;
	private Integer										fin;

	// método especial para redimensionar

	/**
	 * Aplica los cambios un o a uno
	 * 
	 * @param aTabla
	 * @param aId
	 * @param aBase64
	 */
	private void aplicarCambiosFotos(String aTabla, Integer aId, String aBase64, Conexion conexion, byte[] aArchivo) throws Exception {
		byte[] imageBytes = null;
		try {

			try {
				byte[] uno = javax.xml.bind.DatatypeConverter.parseBase64Binary(aBase64);
				imageBytes = uno;

			} catch (Exception e) {
				byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(aBase64.getBytes());

				imageBytes = decoded;
			}

			if (imageBytes != null) {
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

				// BufferedImage imagenOriginalMemoria = base64StringToImg(aBase64);

				BufferedImage imagenEscala = Scalr.resize(img, 200);

				byte[] bytes = getByteData(imagenEscala);

				Map<String, Object> llave = new HashMap<String, Object>();
				llave.put("id", aId);

				Map<String, Object> campos = new HashMap<String, Object>();
				campos.put("archivo", bytes);

				conexion.actualizarBD(aTabla, campos, llave, null);

			} else {

				Map<String, Object> llave = new HashMap<String, Object>();
				llave.put("id", aId);

				Map<String, Object> campos = new HashMap<String, Object>();
				campos.put("archivo", null);

				conexion.actualizarBD(aTabla, campos, llave, null);

			}

		} catch (Exception e) {
			// no lo hace
			// throw new Exception(e);
			this.mostrarMensajeGlobalPersonalizado("PROBLEMAS, en: " + aId, "advertencia");
			System.out.println(aId);
		}

	}

	/**
	 * Redimensiona las fotos existentes
	 * 
	 * @param aTabla
	 */
	public void redimensionarExistentes(String aTabla) {
		int id = 0;
		Conexion conexion = new Conexion();
		try {
			conexion.setAutoCommitBD(false);

			if (aTabla != null && !aTabla.trim().equals("") && aTabla.trim().equals("actividades_mantenimiento")) {

				List<ActividadMantenimiento> registros = IConsultasDAO.getActividadesMantenimiento(this.inicio, this.fin);

				if (registros != null && registros.size() > 0) {
					for (ActividadMantenimiento r : registros) {
						id = r.getId();
						aplicarCambiosFotos(aTabla, r.getId(), r.gettFotoDecodificada(), conexion, r.getArchivo());
					}
				}

			} else if (aTabla != null && !aTabla.trim().equals("") && aTabla.trim().equals("fotos_equipos")) {

				List<FotoEquipo> registros = IConsultasDAO.getFotosEquipos();
				if (registros != null && registros.size() > 0) {
					for (FotoEquipo r : registros) {
						id = r.getId();
						aplicarCambiosFotos(aTabla, r.getId(), r.gettFotoDecodificada(), conexion, r.getArchivo());
					}
				}

			} else if (aTabla != null && !aTabla.trim().equals("") && aTabla.trim().equals("reporte_fallas")) {

				List<ReporteFalla> registros = IConsultasDAO.getFallas();
				if (registros != null && registros.size() > 0) {
					for (ReporteFalla r : registros) {
						id = r.getId();
						aplicarCambiosFotos(aTabla, r.getId(), r.gettFotoDecodificada(), conexion, r.getArchivo());
					}
				}
			}

			conexion.commitBD();
			this.mostrarMensajeGlobalPersonalizado("Exitoso", "exito");

		} catch (Exception e) {
			this.mostrarMensajeGlobalPersonalizado("PROBLEMAS, iba cuando error: " + id, "advertencia");
			conexion.rollbackBD();

		} finally {
			conexion.cerrarConexion();
		}
	}

	// privados

	/**
	 * Genera los datos básicos de un informe de mantenimiento para realizarle
	 * operaciones desde otra interfaz o clase
	 */
	private void generarDatosInforme() {
		try {
			if (this.cronograma != null && this.cronograma.getId() != null) {
				Cronograma cron = new Cronograma();
				cron.setId(this.cronograma.getId());
				this.cronograma = IConsultasDAO.getCronograma(cron).get(0);

				Equipo equipo = new Equipo();
				equipo.setId(this.cronograma.getEquipo().getId());

				this.cronograma.setEquipo(IConsultasDAO.getEquipos(equipo).get(0));

				// si tiene asociado reporte de falla lo busca
				if (this.cronograma.getReporteFalla() != null && this.cronograma.getReporteFalla().getId() != null) {
					ReporteFalla reporte = new ReporteFalla();
					reporte.setId(this.cronograma.getReporteFalla().getId());
					this.cronograma.setReporteFalla(IConsultasDAO.getReportesFallas(reporte).get(0));

				}
				this.armarEstructuraMantenimiento();

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Valida si un informe de mantenimiento está lleno para ello cada una debe
	 * contar con un id de actividad
	 * 
	 * @return ok
	 */
	private boolean isValidoInforme() {
		boolean ok = true;
		boolean ok2 = false;
		if (this.informeMantenimiento.getCronograma().getTipoMantenimiento() != null && this.informeMantenimiento.getCronograma().getTipoMantenimiento().equals(IConstantes.MANTENIMIENTO_PREVENTIVO)) {
			if (this.informeMantenimiento.gettActividades() != null && this.informeMantenimiento.gettActividades().size() > 0) {

				for (ActividadMantenimiento loc : this.informeMantenimiento.gettActividades()) {
					if (!(loc.getEstadoInicial() != null && !loc.getEstadoInicial().trim().equals("") && loc.getEstadoFinal() != null && !loc.getEstadoFinal().trim().equals(""))) {
						ok2 = true;
						break;
						// existen actividades vacías
					}

				}
			}

		} else {

			if (this.informeMantenimiento.gettActividadesNoPreventivas() != null && this.informeMantenimiento.gettActividadesNoPreventivas().size() > 0) {
				for (ActividadNoPreventiva loc : this.informeMantenimiento.gettActividadesNoPreventivas()) {
					if (!(loc.getEstadoInicial() != null && !loc.getEstadoInicial().trim().equals("") && loc.getEstadoFinal() != null && !loc.getEstadoFinal().trim().equals("") && loc.getDescripcionActividad() != null && !loc.getDescripcionActividad().trim().equals(""))) {
						ok2 = true;
						break;
						// existen actividades vacías
					}

				}
			}

		}

		if (ok2) {
			// existen vacias
			ok = false;
			this.mostrarMensajeGlobal("actividadesVacias", "advertencia");
		}

		if (!this.isVacio(this.informeMantenimiento.getRecomendaciones())) {
			this.informeMantenimiento.setRecomendaciones(this.informeMantenimiento.getRecomendaciones().trim());
		}

		if (!this.isVacio(this.informeMantenimiento.getRepuestosRequeridos())) {
			this.informeMantenimiento.setRepuestosRequeridos(this.informeMantenimiento.getRepuestosRequeridos().trim());
		}

		if (this.isVacio(this.informeMantenimiento.getObservacionesGenerales())) {
			ok = false;
			this.mostrarMensajeGlobal("observacionesVacias", "advertencia");
		} else {

			this.informeMantenimiento.setObservacionesGenerales(this.informeMantenimiento.getObservacionesGenerales().trim());
		}

		return ok;

	}

	/**
	 * Valida una actividad NO preventiva
	 * 
	 * @return ok
	 */
	private boolean isValidaActividadNoPreventiva() {
		boolean ok = true;

		// obligatorio
		if (this.isVacio(this.actividadNoPreventiva.getDescripcionActividad())) {

			ok = false;

		} else {
			this.actividadNoPreventiva.setDescripcionActividad(this.actividadNoPreventiva.getDescripcionActividad().trim());
		}

		if (!this.isVacio(this.actividadNoPreventiva.getObservaciones())) {
			this.actividadNoPreventiva.setObservaciones(this.actividadNoPreventiva.getObservaciones().trim());
		}

		if (!ok) {
			this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
		}

		return ok;

	}

	/**
	 * Determina si es válida una foto
	 * 
	 * @return ok
	 */
	private boolean isValidaFoto() {
		boolean ok = true;
		try {
			if (this.isVacio(this.materialFotografico.getTitulo())) {
				ok = false;
			} else {
				this.materialFotografico.setTitulo(this.materialFotografico.getTitulo().trim());
			}

			if (!ok) {
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			}

			// el archivo no existe en esta versión
			if (this.materialFotografico.getId() == null && this.materialFotografico.getArchivo() == null) {
				ok = false;
				this.mostrarMensajeGlobal("fotoRequerida", "advertencia");
			} else {

				if (this.materialFotografico.getId() == null) {

					String imagenOriginal64 = Base64.getEncoder().encodeToString(this.materialFotografico.getArchivo());
					BufferedImage imagenOriginalMemoria = base64StringToImg(imagenOriginal64);

					BufferedImage imagenEscala = Scalr.resize(imagenOriginalMemoria, 200);
					this.materialFotografico.setArchivo(getByteData(imagenEscala));

				}

			}

		} catch (Exception e) {
			ok = false;
			IConstantes.log.error(e, e);
		}

		return ok;

	}

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
	 * Determina si es válida una actividad
	 * 
	 * @return ok
	 */
	private boolean isValidaActividad() {
		boolean ok = true;

		// las ahora llamadas observaciones no son obligatorias, antes
		// descripcion
		if (!this.isVacio(this.actividadMantenimiento.getDescripcion())) {
			this.actividadMantenimiento.setDescripcion(this.actividadMantenimiento.getDescripcion().trim());
		}

		// las recomendaciones se eliminan

		// if (this.isVacio(this.actividadMantenimiento.getRecomendaciones())) {
		// ok = false;
		//
		// } else {
		// this.actividadMantenimiento.setRecomendaciones(this.actividadMantenimiento.getRecomendaciones().trim());
		// }

		if (!ok) {
			this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
		}

		// el archivo no existe en esta versión
		// if (this.actividadMantenimiento.getArchivo() == null) {
		// ok = false;
		// this.mostrarMensajeGlobal("fotoRequerida", "advertencia");
		// }

		return ok;

	}

	/**
	 * Obtiene el tipo de mantenimiento
	 * 
	 * @param aTipo
	 * @return tipo
	 */
	private String getTipoMantenimiento(String aTipo) {
		String tipo = "";
		if (aTipo != null && aTipo.equals("P")) {
			tipo = "" + this.getMensaje("preventivo");
		} else if (aTipo != null && aTipo.equals("C")) {
			tipo = "" + this.getMensaje("correctivo");
		} else if (aTipo != null && aTipo.equals("D")) {
			tipo = "" + this.getMensaje("diagnostico");
		} else if (aTipo != null && aTipo.equals("I")) {
			tipo = "" + this.getMensaje("instalacion");
		}

		return tipo;
	}

	// publicos

	/**
	 * Imprime el reporte de tarjetas con cósigos qr
	 */
	public void imprimirFormatoDiagnostico() {
		try {
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Map<String, Object> parametros = new HashMap<String, Object>();
			List<BateriaMantenimiento> bateriasReporte = null;
			BateriaMantenimiento bateria = null;
			List<Basico> recomendaciones = null;
			List<Basico> repuestos = null;
			List<Basico> observaciones = null;
			List<Basico> materiales = null;
			Basico basico = null;

			parametros.put("informeMantenimiento", this.informeMantenimiento);
			parametros.put("SUBREPORT_DIR", this.getPath(IConstantes.PAQUETE_MODULO_REPORTES) + "/");
			parametros.put("rutaFotosEquipos", this.getPath(IConstantes.PAQUETE_IMAGENES) + "/fotosMantenimientos/");
			parametros.put("rutaLogoEmpresa", this.getPath(IConstantes.PAQUETE_IMAGENES) + "/fotoLogo/");
			parametros.put("rutaFirmas", this.getPath(IConstantes.PAQUETE_IMAGENES) + "/fotosFirmas/");

			try {

				Tecnico temp1 = new Tecnico();
				temp1.setId(this.informeMantenimiento.getCronograma().getTecnico().getId());
				List<Tecnico> temps1 = IConsultasDAO.getTecnicos(temp1);

				if (temps1 != null && temps1.size() > 0) {
					if (temps1.get(0).getFirma() != null && !temps1.get(0).getFirma().trim().equals("")) {
						// firma del tecnico
						this.guardarFirmaComoImagen(temps1.get(0).getFirma(), "consultor" + this.informeMantenimiento.getCronograma().getTecnico().getId());
					}

				}

				Cliente temp2 = new Cliente();
				temp2.setId(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getId());
				List<Cliente> temp2s = IConsultasDAO.getClientes(temp2);

				if (temp2s != null && temp2s.size() > 0) {
					if (temp2s.get(0).getFirma() != null && !temp2s.get(0).getFirma().trim().equals("")) {
						// firma del tecnico
						// firma del representante del cliente
						this.guardarFirmaComoImagen(temp2s.get(0).getFirma(), "cliente" + this.informeMantenimiento.getCronograma().getEquipo().getCliente().getId());
					}

				}
			} catch (Exception e) {

			}

			if (this.informeMantenimiento.gettBaterias() != null && this.informeMantenimiento.gettBaterias().size() > 0) {
				bateriasReporte = new ArrayList<BateriaMantenimiento>();
				bateriasReporte.addAll(this.informeMantenimiento.gettBaterias());

				// mira si es menor que 3
				if (this.informeMantenimiento.gettBaterias().size() < 3) {
					for (int i = 1; i <= 3 - this.informeMantenimiento.gettBaterias().size(); i++) {
						bateria = new BateriaMantenimiento();
						bateria.setNumeroBateria(null);
						bateriasReporte.add(bateria);
					}
				} else {

					// si es mayor que 3 preguntamos si no es múltiplo de 3 esto con el
					// fin
					// de armar el reporte
					if (this.informeMantenimiento.gettBaterias().size() % 3 != 0) {
						// ahora bsucamos el multiplo de 3 más cercano y mayor que el número
						// de baterias

						int multiploAnalizado = 3;
						while (multiploAnalizado < this.informeMantenimiento.gettBaterias().size()) {
							multiploAnalizado = multiploAnalizado + 3;
						}

						for (int i = 1; i <= multiploAnalizado - this.informeMantenimiento.gettBaterias().size(); i++) {
							bateria = new BateriaMantenimiento();
							bateria.setNumeroBateria(null);
							bateriasReporte.add(bateria);
						}

					}

				}

			}

			parametros.put("baterias", bateriasReporte);

			if (this.informeMantenimiento.getCronograma() != null && this.informeMantenimiento.getCronograma().getVersionReporte() != null && this.informeMantenimiento.getCronograma().getVersionReporte().intValue() == IConstantes.VERSION_1.intValue()) {
				// v1 preventivo y no preventivo
				ActividadMantenimiento act = new ActividadMantenimiento();
				act.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
				this.informeMantenimiento.settActividades(new ArrayList<ActividadMantenimiento>());
				this.informeMantenimiento.settActividades(IConsultasDAO.getActividadesMantenimiento(act));

				if (this.informeMantenimiento.gettActividades() != null && this.informeMantenimiento.gettActividades().size() > 0) {
					for (ActividadMantenimiento a : this.informeMantenimiento.gettActividades()) {

						a.setObjeto(null);
						try {
							if (a.gettFotoDecodificada() != null) {
								//ByteBuffer outputBuffer =  Charset.forName("UTF-8").encode(a.gettFotoDecodificada());
								byte[] imageBytes =  javax.xml.bind.DatatypeConverter.parseBase64Binary(a.gettFotoDecodificada());
								BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
								a.setObjeto(new Object());
								a.setObjeto(img);
							}

						} catch (Exception e) {
							a.setObjeto(null);
						}
					}
				}
				this.generarListado(new JRBeanCollectionDataSource(this.informeMantenimiento.gettActividades()), IConstantes.REPORTE_MANTENIMIENTO, "IM-" + formato.format(new Date()), "pdf", parametros);

			} else {

				// *********** versión 2 o nula *********/

				if (this.informeMantenimiento.getRecomendaciones() != null && !this.informeMantenimiento.getRecomendaciones().trim().equals("")) {
					basico = new Basico();
					recomendaciones = new ArrayList<Basico>();
					basico.setDescripcion(this.informeMantenimiento.getRecomendaciones());
					recomendaciones.add(basico);
					parametros.put("recomendaciones", recomendaciones);
				}

				if (this.informeMantenimiento.getRepuestosRequeridos() != null && !this.informeMantenimiento.getRepuestosRequeridos().trim().equals("")) {
					basico = new Basico();
					repuestos = new ArrayList<Basico>();
					basico.setDescripcion(this.informeMantenimiento.getRepuestosRequeridos());
					repuestos.add(basico);
					parametros.put("repuestos", repuestos);
				}

				if (this.informeMantenimiento.getObservacionesGenerales() != null && !this.informeMantenimiento.getObservacionesGenerales().trim().equals("")) {
					basico = new Basico();
					observaciones = new ArrayList<Basico>();
					basico.setDescripcion(this.informeMantenimiento.getObservacionesGenerales());
					observaciones.add(basico);
					parametros.put("observaciones", observaciones);
				}

				if (this.informeMantenimiento.gettMaterialesFotograficos() != null && this.informeMantenimiento.gettMaterialesFotograficos().size() > 0) {

					int contador = 0;
					materiales = new ArrayList<Basico>();

					for (int i = 0; i <= this.informeMantenimiento.gettMaterialesFotograficos().size() - 1; i++) {
						contador++;
						if (contador == 1) {
							basico = new Basico();
						}

						if (contador == 1) {

							basico.getMaterial1().setTitulo(this.informeMantenimiento.gettMaterialesFotograficos().get(i).getTitulo());
							basico.getMaterial1().settFotoDecodificada(this.informeMantenimiento.gettMaterialesFotograficos().get(i).gettFotoDecodificada());

						} else if (contador == 2) {

							basico.getMaterial2().setTitulo(this.informeMantenimiento.gettMaterialesFotograficos().get(i).getTitulo());
							basico.getMaterial2().settFotoDecodificada(this.informeMantenimiento.gettMaterialesFotograficos().get(i).gettFotoDecodificada());

						} else {

							basico.getMaterial3().setTitulo(this.informeMantenimiento.gettMaterialesFotograficos().get(i).getTitulo());
							basico.getMaterial3().settFotoDecodificada(this.informeMantenimiento.gettMaterialesFotograficos().get(i).gettFotoDecodificada());

							contador = 0;
							materiales.add(basico);

						}

						if (i == this.informeMantenimiento.gettMaterialesFotograficos().size() - 1 && contador != 0) {
							materiales.add(basico);
						}

					}
					parametros.put("materiales", materiales);

				}

				if (!(this.informeMantenimiento.getCronograma().getTipoMantenimiento() != null && this.informeMantenimiento.getCronograma().getTipoMantenimiento().equals(IConstantes.MANTENIMIENTO_PREVENTIVO))) {
					// si no es preventivo
					List<ActividadMantenimiento> actividadesNoPreventivas = new ArrayList<ActividadMantenimiento>();
					ActividadMantenimiento actividadTemp = null;
					for (ActividadNoPreventiva a : this.informeMantenimiento.gettActividadesNoPreventivas()) {
						actividadTemp = new ActividadMantenimiento();
						actividadTemp.setEstadoInicial(a.getEstadoInicial());
						actividadTemp.setEstadoFinal(a.getEstadoFinal());
						actividadTemp.getActividadInformeEquipo().setActividad(a.getDescripcionActividad());
						actividadTemp.setDescripcion(a.getObservaciones());
						actividadesNoPreventivas.add(actividadTemp);
					}
					this.generarListado(new JRBeanCollectionDataSource(actividadesNoPreventivas), IConstantes.REPORTE_MANTENIMIENTO_V2, "IM-V2-" + formato.format(new Date()), "pdf", parametros);

				} else {
					// preventivo
					ActividadMantenimiento act = new ActividadMantenimiento();
					act.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
					this.informeMantenimiento.settActividades(new ArrayList<ActividadMantenimiento>());
					this.informeMantenimiento.settActividades(IConsultasDAO.getActividadesMantenimiento(act));

					this.generarListado(new JRBeanCollectionDataSource(this.informeMantenimiento.gettActividades()), IConstantes.REPORTE_MANTENIMIENTO_V2, "IM-V2-" + formato.format(new Date()), "pdf", parametros);

				}

			}

		} catch (Exception e) {

		}
	}

	/**
	 * Obtiene el costo redondeado
	 * 
	 * @return valor
	 */
	public BigDecimal getCostoRedondeado() {
		BigDecimal valor = new BigDecimal(0);
		if (this.cronogramas != null && cronogramas.size() > 0) {
			for (Cronograma p : this.cronogramas) {
				valor = valor.add(p.getCosto());
			}
			valor = this.getValorRedondeado(valor, IConstantes.DECIMALES_REDONDEAR);

		}

		return valor;

	}

	/**
	 * Consulta el historial de mantenimiento de un equipo
	 * 
	 * @param aEquipo
	 */
	public void verHistorialMantenimiento(Equipo aEquipo) {
		try {
			Cronograma cronograma = new Cronograma();
			InformeMantenimiento informe = null;
			cronograma.getEquipo().setId(aEquipo.getId());

			this.cronogramas = IConsultasDAO.getCronograma(cronograma);

			if (this.cronogramas != null && this.cronogramas.size() > 0) {

				for (Cronograma c : this.cronogramas) {
					informe = new InformeMantenimiento();
					informe.getCronograma().setId(c.getId());
					informe = IConsultasDAO.getInformeMantenimiento(informe);
					if (informe != null && informe.getObservacionesGenerales() != null && !informe.getObservacionesGenerales().trim().equals("")) {
						c.settObservaciones(informe.getObservacionesGenerales());
					} else {
						c.settObservaciones(null);
					}
				}
			}
			this.abrirModal("panelHistorial");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Obtiene el update de los campos del informe de mantenimeinto cuando se
	 * selecciona el cronograma
	 * 
	 * @return campos
	 */
	public String getUpdateCamposMantenimiento() {
		String campos = ":form:pnlEstructuraMantenimiento, :form:messages";
		for (int i = 1; i <= 24; i++) {
			campos += ", pnlInformacion" + i;
		}

		return campos;
	}

	/**
	 * Cambia y reseat cronograma
	 */
	public void cambiarEquipo() {
		this.cronograma = null;
		this.getCronograma();
		this.informeMantenimiento = null;
		this.getInformeMantenimiento();
		this.actividadMantenimiento = null;
		this.getActividadMantenimiento();

		this.actividadNoPreventiva = null;
		this.getActividadNoPreventiva();

	}

	/**
	 * Cambia y resetea combos de cronograma y cliente
	 */
	public void cambiarCliente() {
		this.cronograma = null;
		this.actividadMantenimiento = null;
		this.getActividadMantenimiento();

		this.actividadNoPreventiva = null;
		this.getActividadNoPreventiva();

		this.equipo = null;
		this.getCronograma();
		this.getEquipo();
		this.informeMantenimiento = null;
		this.getInformeMantenimiento();
	}

	/**
	 * Detremina si un mantenimiento está registrado
	 * 
	 * @return true:false
	 */
	public boolean isExistenteMantenimiento() {
		InformeMantenimiento mantenimiento = null;
		try {
			InformeMantenimiento mantenimientoConsulta = new InformeMantenimiento();
			mantenimientoConsulta.getCronograma().setId(this.cronograma.getId());
			mantenimiento = IConsultasDAO.getInformeMantenimiento(mantenimientoConsulta);
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return mantenimiento != null && mantenimiento.getCronograma() != null && mantenimiento.getCronograma().getId() != null ? true : false;
	}

	/**
	 * Elimina una actividad no preventiva
	 */
	public void eliminarActividadNoPreventiva() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.actividadNoPreventiva.getCamposBD();
			conexion.eliminarBD(this.actividadNoPreventiva.getEstructuraTabla().getTabla(), this.actividadNoPreventiva.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

			// reseteo de variables
			this.actividadNoPreventiva = null;
			this.getActividadNoPreventiva();

			ActividadNoPreventiva actividad = new ActividadNoPreventiva();
			actividad.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			this.informeMantenimiento.settActividadesNoPreventivas(IConsultasDAO.getActividadesMantenimientoNoPreventiva(actividad));

			this.cerrarModal("panelEliminacionActividadNoPreventiva");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Elimina un cronograma
	 */
	public void eliminarFoto() {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);
			this.materialFotografico.getCamposBD();
			conexion.eliminarBD(this.materialFotografico.getEstructuraTabla().getTabla(), this.materialFotografico.getEstructuraTabla().getLlavePrimaria());
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");

			// reseteo de variables
			this.materialFotografico = null;
			this.getMaterialFotografico();

			MaterialFotografico material = new MaterialFotografico();
			material.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			this.informeMantenimiento.settMaterialesFotograficos(IConsultasDAO.getMaterialesFotograficos(material));

			this.cerrarModal("panelEliminacionMaterialFotografico");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Elimina un mantenimiento realizado
	 */
	public void eliminarMantenimiento() {

		Conexion conexion = new Conexion();
		Map<String, Object> condiciones = null;
		BateriaMantenimiento bateria = null;
		ActividadMantenimiento actividad = null;
		MaterialFotografico material = null;
		ActividadNoPreventiva actividadNo = null;

		try {
			conexion.setAutoCommitBD(false);

			condiciones = new HashMap<String, Object>();

			// borra las baterias
			bateria = new BateriaMantenimiento();
			bateria.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", bateria.getInformeMantenimiento().getCronograma().getId());
			bateria.getCamposBD();
			conexion.eliminarBD(bateria.getEstructuraTabla().getTabla(), condiciones);

			// borra las actividades
			actividad = new ActividadMantenimiento();
			actividad.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", actividad.getInformeMantenimiento().getCronograma().getId());
			actividad.getCamposBD();
			conexion.eliminarBD(actividad.getEstructuraTabla().getTabla(), condiciones);

			// borra las actividades no preventivas
			actividadNo = new ActividadNoPreventiva();
			actividadNo.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", actividadNo.getInformeMantenimiento().getCronograma().getId());
			actividadNo.getCamposBD();
			conexion.eliminarBD(actividadNo.getEstructuraTabla().getTabla(), condiciones);

			// borra material fotográfico en la nueva versión
			material = new MaterialFotografico();
			material.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", material.getInformeMantenimiento().getCronograma().getId());
			material.getCamposBD();
			conexion.eliminarBD(material.getEstructuraTabla().getTabla(), condiciones);

			// elimina el informe de mantenimeinto realizado a la fecha
			this.informeMantenimiento.getCamposBD();
			conexion.eliminarBD(this.informeMantenimiento.getEstructuraTabla().getTabla(), this.informeMantenimiento.getEstructuraTabla().getLlavePrimaria());

			conexion.commitBD();
			this.mostrarMensajeGlobal("mantenimientoEliminado", "exito");

			this.informeMantenimiento = null;
			this.armarEstructuraMantenimiento();

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}
	}

	/**
	 * Cancela una actividad no preventiva
	 */
	public void cancelarEdicionActividadNoPreventiva(String aTipo) {
		try {

			ActividadNoPreventiva actividad = null;
			InformeMantenimiento temp = new InformeMantenimiento();
			temp.getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			InformeMantenimiento informe = IConsultasDAO.getInformeMantenimiento(temp);
			ActividadInformeEquipo actividadInforme = new ActividadInformeEquipo();

			if (informe != null && informe.getCronograma().getId() != null) {

				this.informeMantenimiento.settActividades(new ArrayList<ActividadMantenimiento>());

				actividad = new ActividadNoPreventiva();
				actividad.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
				this.informeMantenimiento.settActividadesNoPreventivas(IConsultasDAO.getActividadesMantenimientoNoPreventiva(actividad));

			} else {

				// armamos las actividades nuevamente
				this.informeMantenimiento.settActividadesNoPreventivas(new ArrayList<ActividadNoPreventiva>());
				actividadInforme = new ActividadInformeEquipo();
				actividadInforme.getEquipo().setId(this.informeMantenimiento.getCronograma().getEquipo().getId());

			}
			if (aTipo != null && aTipo.equals("edicion")) {
				this.cerrarModal("panelEdicionActividadNoPreventiva");
			} else {
				this.cerrarModal("panelEliminacionActividadNoPreventiva");
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela la edición de un material fotográfico
	 */
	public void cancelarEdicionFoto(String aTipo) {
		try {
			InformeMantenimiento temp = new InformeMantenimiento();
			temp.getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			InformeMantenimiento informe = IConsultasDAO.getInformeMantenimiento(temp);
			MaterialFotografico material = new MaterialFotografico();

			if (!(informe != null && informe.getCronograma().getId() != null)) {

				this.informeMantenimiento.settMaterialesFotograficos(new ArrayList<MaterialFotografico>());

			} else {

				material.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());

				this.informeMantenimiento.settMaterialesFotograficos(IConsultasDAO.getMaterialesFotograficos(material));

			}

			if (aTipo != null && aTipo.equals("edicion")) {
				this.cerrarModal("panelEdicionMaterialFotografico");
			} else {
				this.cerrarModal("panelEliminacionMaterialFotografico");
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela la edición de la actividad y la vuelve al estado anterior según
	 * esté o no guardada
	 */
	public void cancelarEdicionActividad() {
		try {
			ActividadMantenimiento actividad = null;
			InformeMantenimiento temp = new InformeMantenimiento();
			temp.getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			InformeMantenimiento informe = IConsultasDAO.getInformeMantenimiento(temp);
			ActividadInformeEquipo actividadInforme = new ActividadInformeEquipo();
			List<ActividadInformeEquipo> actividades = null;

			if (informe != null && informe.getCronograma().getId() != null) {

				this.informeMantenimiento.settActividades(new ArrayList<ActividadMantenimiento>());

				actividad = new ActividadMantenimiento();
				actividad.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
				this.informeMantenimiento.settActividades(IConsultasDAO.getActividadesMantenimiento(actividad));

			} else {

				// armamos las actividades nuevamente
				this.informeMantenimiento.settActividades(new ArrayList<ActividadMantenimiento>());
				actividadInforme = new ActividadInformeEquipo();
				actividadInforme.getEquipo().setId(this.informeMantenimiento.getCronograma().getEquipo().getId());
				actividades = IConsultasDAO.getActividades(actividadInforme);

				if (actividades != null && actividades.size() > 0) {

					for (ActividadInformeEquipo a : actividades) {
						actividad = new ActividadMantenimiento();
						actividad.setActividadInformeEquipo(a);
						actividad.setInformeMantenimiento(this.informeMantenimiento);
						this.informeMantenimiento.gettActividades().add(actividad);
					}
				}

			}
			this.cerrarModal("panelEdicionActividad");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Limpia la foto cargada para cargar otra
	 */
	public void limpiarFotoCargadaV2() {
		this.materialFotografico.settFile(null);
		this.materialFotografico.setArchivo(null);

	}

	/**
	 * Limpia la foto cargada para cargar otra
	 */
	public void limpiarFotoCargada() {
		this.actividadMantenimiento.settFile(null);
		this.actividadMantenimiento.setArchivo(null);

	}

	/**
	 * Recibir foto y asignara aobjeto
	 * 
	 * @param event
	 */
	public void recibirFotoV2(FileUploadEvent event) {

		try {
			this.materialFotografico.settFile(event.getFile());
			this.materialFotografico.setArchivo(event.getFile().getContents());

			this.mostrarMensajeGlobal("archivoRecibido", "advertencia");
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Recibir foto y asignara aobjeto
	 * 
	 * @param event
	 */
	public void recibirFoto(FileUploadEvent event) {

		try {
			this.actividadMantenimiento.settFile(event.getFile());
			this.actividadMantenimiento.setArchivo(event.getFile().getContents());

			this.mostrarMensajeGlobal("archivoRecibido", "advertencia");
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Decide que transacción realizar
	 * 
	 * @param aDecision
	 */
	public void decidirPostGuardado(String aDecision) {
		try {
			if (aDecision != null && aDecision.equals("CERRAR")) {

				this.cerrarModal("panelResumen");

			} else if (aDecision != null && aDecision.equals("CERRAR_APROBADO_CLIENTE")) {
				this.cambiarCliente();
				this.cambiarEquipo();
				this.cerrarModal("panelResumenAprobado");

			} else if (aDecision != null && aDecision.equals("CERRAR_ELABORAR_NUEVO")) {

				this.cambiarCliente();
				this.cambiarEquipo();
				this.cerrarModal("panelResumen");

			} else if (aDecision != null && aDecision.equals("APROBAR_INFORME")) {

				aprobarInformeMantenimiento();

				this.abrirModal("panelResumenAprobado");
				this.cerrarModal("panelResumen");

			} else if (aDecision != null && aDecision.equals("AVISAR_CLIENTE")) {

				IEmail.enviarCorreo(this.getMensaje("mensajeAprobacionDisponible", this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCliente() + ", " + this.informeMantenimiento.getCronograma().getEquipo().getCliente().getUbicacion(), this.informeMantenimiento.getCronograma().getEquipo().getNombreEquipo() + "," + this.informeMantenimiento.getCronograma().getEquipo().getCodigoQr(), "" + this.informeMantenimiento.getCronograma().getId()), this.getMensaje("asuntoDisponibleAprobacion", this.informeMantenimiento.getCronograma().getEquipo().getCodigoQr()), this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("mailAprobacionCliente", this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCorreoElectronico()), "exito");

				this.cambiarCliente();
				this.cambiarEquipo();

				this.cerrarModal("panelResumen");

			} else if (aDecision != null && aDecision.equals("IMPRIMIR_NO_APROBADO")) {

				imprimirFormatoDiagnostico();

				// Imprimir
			} else if (aDecision != null && aDecision.equals("IMPRIMIR_APROBADO")) {
				imprimirFormatoDiagnostico();
				// Imprimir
			} else if (aDecision != null && aDecision.equals("ELIMINAR_INFORME")) {
				this.abrirModal("panelEliminarInforme");
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Aprueba el informe de mantenimiento
	 */
	public void aprobarInformeMantenimiento() {
		Conexion conexion = new Conexion();
		boolean correctivo = false;
		try {

			conexion.setAutoCommitBD(false);

			// guarda el informe
			this.informeMantenimiento.setResponsableClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getRepresentante());
			this.informeMantenimiento.setFirmaClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getFirma());
			this.informeMantenimiento.setCargoClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCargo());

			this.informeMantenimiento.getCamposBD();
			conexion.actualizarBD(this.informeMantenimiento.getEstructuraTabla().getTabla(), this.informeMantenimiento.getEstructuraTabla().getPersistencia(), this.informeMantenimiento.getEstructuraTabla().getLlavePrimaria(), null);

			// cambiamos el estado del cronograma a atendido por el técnico y su
			// fecha

			this.informeMantenimiento.getCronograma().setFechaHoraAprobacionCliente(new Date());
			this.informeMantenimiento.getCronograma().setEstado(IConstantes.ESTADO_APROBADO);
			this.informeMantenimiento.getCronograma().getCamposBD();
			conexion.actualizarBD(this.informeMantenimiento.getCronograma().getEstructuraTabla().getTabla(), this.informeMantenimiento.getCronograma().getEstructuraTabla().getPersistencia(), this.informeMantenimiento.getCronograma().getEstructuraTabla().getLlavePrimaria(), null);

			// revisa si es un informe correctivo y tiene asociado un reporte de falla
			// y la cierra

			if (this.informeMantenimiento.getCronograma().getReporteFalla() != null && this.informeMantenimiento.getCronograma().getReporteFalla().getId() != null) {

				Map<String, Object> camposActualizar = new HashMap<String, Object>();

				this.informeMantenimiento.getCronograma().getReporteFalla().setEstado(IConstantes.ESTADO_CERRADO);
				this.informeMantenimiento.getCronograma().getReporteFalla().setFechaHoraAtencion(this.informeMantenimiento.getCronograma().getFechaHoraAtencion());

				camposActualizar.put("estado", IConstantes.ESTADO_CERRADO);
				camposActualizar.put("fecha_atencion", this.informeMantenimiento.getCronograma().getFechaHoraAtencion());

				this.informeMantenimiento.getCronograma().getReporteFalla().getCamposBD();
				conexion.actualizarBD(this.informeMantenimiento.getCronograma().getReporteFalla().getEstructuraTabla().getTabla(), camposActualizar, this.informeMantenimiento.getCronograma().getReporteFalla().getEstructuraTabla().getLlavePrimaria(), null);
				correctivo = true;
			}

			conexion.commitBD();

			this.mostrarMensajeGlobal("aprobacionInformeExitosa", "exito");

			if (correctivo) {

				this.mostrarMensajeGlobal("tambienCerradaFalla", "exito");

				IEmail.enviarCorreo(this.getMensaje("mensajeInformeMantenimientoAprobadoFalla", this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCliente() + ", " + this.informeMantenimiento.getCronograma().getEquipo().getCliente().getUbicacion(), this.informeMantenimiento.getCronograma().getEquipo().getNombreEquipo() + "," + this.informeMantenimiento.getCronograma().getEquipo().getCodigoQr(), "" + this.informeMantenimiento.getCronograma().getId()), this.getMensaje("asuntoInformeMantenimiento", this.informeMantenimiento.getCronograma().getEquipo().getCodigoQr()), this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("tambienMailCliente", this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCorreoElectronico()), "exito");

			} else {

				IEmail.enviarCorreo(this.getMensaje("mensajeInformeMantenimientoAprobado", this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCliente() + ", " + this.informeMantenimiento.getCronograma().getEquipo().getCliente().getUbicacion(), this.informeMantenimiento.getCronograma().getEquipo().getNombreEquipo() + "," + this.informeMantenimiento.getCronograma().getEquipo().getCodigoQr(), "" + this.informeMantenimiento.getCronograma().getId()), this.getMensaje("asuntoInformeMantenimiento", this.informeMantenimiento.getCronograma().getEquipo().getCodigoQr()), this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCorreoElectronico());
				this.mostrarMensajeGlobalPersonalizado(this.getMensaje("tambienMailCliente", this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCorreoElectronico()), "exito");

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Guarda el informe de mantenimiento
	 */
	public void guardarInformeMantenimiento() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoInforme()) {
				conexion.setAutoCommitBD(false);

				// guarda el informe
				this.informeMantenimiento.setResponsableClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getRepresentante());
				this.informeMantenimiento.setFirmaClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getFirma());
				this.informeMantenimiento.setCargoClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCargo());

				this.informeMantenimiento.getCamposBD();
				conexion.actualizarBD(this.informeMantenimiento.getEstructuraTabla().getTabla(), this.informeMantenimiento.getEstructuraTabla().getPersistencia(), this.informeMantenimiento.getEstructuraTabla().getLlavePrimaria(), null);

				// las actividades se supone que ya están guardadas las omitimos

				// si posee baterias las guardamos, su actualización
				if (this.informeMantenimiento.gettBaterias() != null && this.informeMantenimiento.gettBaterias().size() > 0) {
					for (BateriaMantenimiento b : this.informeMantenimiento.gettBaterias()) {
						b.getCamposBD();
						conexion.actualizarBD(b.getEstructuraTabla().getTabla(), b.getEstructuraTabla().getPersistencia(), b.getEstructuraTabla().getLlavePrimaria(), null);

					}
				}

				// cambiamos el estado del croograma a atendido por el técnico y su
				// fecha

				this.informeMantenimiento.getCronograma().setFechaHoraAtencion(new Date());
				this.informeMantenimiento.getCronograma().setEstado(IConstantes.ESTADO_ATENDIDO);
				this.informeMantenimiento.getCronograma().getCamposBD();
				conexion.actualizarBD(this.informeMantenimiento.getCronograma().getEstructuraTabla().getTabla(), this.informeMantenimiento.getCronograma().getEstructuraTabla().getPersistencia(), this.informeMantenimiento.getCronograma().getEstructuraTabla().getLlavePrimaria(), null);

				conexion.commitBD();

				this.mostrarMensajeGlobal("informeMantenimientoGuardado", "exito");
				this.abrirModal("panelResumen");

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Determinamos si se crea o actualiza parcialmente para actividad NO
	 * preventiva
	 */
	public void guardarParcialmenteNoPreventiva() {
		try {
			InformeMantenimiento temp = new InformeMantenimiento();
			temp.getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			InformeMantenimiento informe = IConsultasDAO.getInformeMantenimiento(temp);
			if (informe != null && informe.getCronograma().getId() != null) {
				atualizarMantenimientoParcialmenteNoPreventiva("E");
			} else {

				atualizarMantenimientoParcialmenteNoPreventiva("C");
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Determinamos si se crea o actualiza parcialmente para actividad preventiva
	 */
	public void guardarParcialmente() {
		try {
			InformeMantenimiento temp = new InformeMantenimiento();
			temp.getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
			InformeMantenimiento informe = IConsultasDAO.getInformeMantenimiento(temp);
			if (informe != null && informe.getCronograma().getId() != null) {
				atualizarMantenimientoParcialmente("E");
			} else {

				atualizarMantenimientoParcialmente("C");
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Actualiza de forma NO preventiva una actividad
	 * 
	 * @param aTransaccion
	 */
	public void atualizarMantenimientoParcialmenteNoPreventiva(String aTransaccion) {
		Conexion conexion = new Conexion();
		ActividadNoPreventiva actividadNo = null;
		try {
			if (isValidaActividadNoPreventiva()) {
				conexion.setAutoCommitBD(false);
				if (aTransaccion.equals("C")) {

					this.informeMantenimiento.setResponsableClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getRepresentante());
					this.informeMantenimiento.setFirmaClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getFirma());
					this.informeMantenimiento.setCargoClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCargo());

					// guarda el informe como valla
					this.informeMantenimiento.getCamposBD();
					conexion.insertarBD(this.informeMantenimiento.getEstructuraTabla().getTabla(), this.informeMantenimiento.getEstructuraTabla().getPersistencia());

					// revisa si la actividad es nueva o no
					if (this.actividadNoPreventiva != null && this.actividadNoPreventiva.getId() != null) {

						this.actividadNoPreventiva.getCamposBD();
						conexion.actualizarBD(this.actividadNoPreventiva.getEstructuraTabla().getTabla(), this.actividadNoPreventiva.getEstructuraTabla().getPersistencia(), this.actividadNoPreventiva.getEstructuraTabla().getLlavePrimaria(), null);

					} else {

						this.actividadNoPreventiva.getCamposBD();
						conexion.insertarBD(this.actividadNoPreventiva.getEstructuraTabla().getTabla(), this.actividadNoPreventiva.getEstructuraTabla().getPersistencia());
						this.actividadNoPreventiva.setId(conexion.getUltimoSerialTransaccion(this.actividadNoPreventiva.getEstructuraTabla().getTabla()));

					}

					// si posee baterias las guarda hasta donde esté lleno en el momento
					if (this.informeMantenimiento.gettBaterias() != null && this.informeMantenimiento.gettBaterias().size() > 0) {
						for (BateriaMantenimiento b : this.informeMantenimiento.gettBaterias()) {
							b.getCamposBD();
							conexion.insertarBD(b.getEstructuraTabla().getTabla(), b.getEstructuraTabla().getPersistencia());
							b.setId(conexion.getUltimoSerialTransaccion(b.getEstructuraTabla().getTabla()));
						}
					}

					// como es la primera vez actualiza el estado es programado

				} else {

					// revisa si la actividad es nueva o no
					if (this.actividadNoPreventiva != null && this.actividadNoPreventiva.getId() != null) {

						this.actividadNoPreventiva.getCamposBD();
						conexion.actualizarBD(this.actividadNoPreventiva.getEstructuraTabla().getTabla(), this.actividadNoPreventiva.getEstructuraTabla().getPersistencia(), this.actividadNoPreventiva.getEstructuraTabla().getLlavePrimaria(), null);

					} else {

						this.actividadNoPreventiva.getCamposBD();
						conexion.insertarBD(this.actividadNoPreventiva.getEstructuraTabla().getTabla(), this.actividadNoPreventiva.getEstructuraTabla().getPersistencia());
						this.actividadNoPreventiva.setId(conexion.getUltimoSerialTransaccion(this.actividadNoPreventiva.getEstructuraTabla().getTabla()));

					}

					this.informeMantenimiento.setResponsableClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getRepresentante());
					this.informeMantenimiento.setFirmaClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getFirma());
					this.informeMantenimiento.setCargoClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCargo());

					// actualizamos los datos de quien hizo cambios por si cambian al
					// responsable en el cliente
					Map<String, Object> campos = new HashMap<String, Object>();
					campos.put("firma_cliente_momento", this.informeMantenimiento.getFirmaClienteMomento());
					campos.put("cargo_cliente_momento", this.informeMantenimiento.getCargoClienteMomento());
					campos.put("responsable_cliente_momento", this.informeMantenimiento.getResponsableClienteMomento());

					this.informeMantenimiento.getCamposBD();
					conexion.actualizarBD(this.informeMantenimiento.getEstructuraTabla().getTabla(), campos, this.informeMantenimiento.getEstructuraTabla().getLlavePrimaria(), null);

				}

				conexion.commitBD();

				actividadNo = new ActividadNoPreventiva();
				actividadNo.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());

				this.informeMantenimiento.settActividadesNoPreventivas(new ArrayList<ActividadNoPreventiva>());
				this.informeMantenimiento.settActividadesNoPreventivas(IConsultasDAO.getActividadesMantenimientoNoPreventiva(actividadNo));

				if (!(this.informeMantenimiento.gettActividadesNoPreventivas() != null && this.informeMantenimiento.gettActividadesNoPreventivas().size() > 0)) {
					this.informeMantenimiento.settActividadesNoPreventivas(new ArrayList<ActividadNoPreventiva>());
				}

				this.mostrarMensajeGlobal("actividadActualizada", "exito");

				this.cerrarModal("panelEdicionActividadNoPreventiva");

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}
	}

	/**
	 * Actualizar foto
	 */
	public void atualizarFoto() {
		Conexion conexion = new Conexion();

		try {
			if (isValidaFoto()) {
				conexion.setAutoCommitBD(false);

				if (this.materialFotografico != null && this.materialFotografico.getId() != null) {

					// como al actualizar las fotos tiene problemas el archivo solo
					// actualizo el nombre
					Map<String, Object> actualizar = new HashMap<String, Object>();
					actualizar.put("titulo", this.materialFotografico.getTitulo());
					this.materialFotografico.getCamposBD();
					conexion.actualizarBD(this.materialFotografico.getEstructuraTabla().getTabla(), actualizar, this.materialFotografico.getEstructuraTabla().getLlavePrimaria(), null);

				} else {

					this.materialFotografico.getCamposBD();
					conexion.insertarBD(this.materialFotografico.getEstructuraTabla().getTabla(), this.materialFotografico.getEstructuraTabla().getPersistencia());
					this.materialFotografico.setId(conexion.getUltimoSerialTransaccion(this.materialFotografico.getEstructuraTabla().getTabla()));
				}

				conexion.commitBD();

				this.mostrarMensajeGlobal("materialFotograficoActualizado", "exito");
				this.cerrarModal("panelEdicionMaterialFotografico");
				MaterialFotografico material = new MaterialFotografico();
				material.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
				this.informeMantenimiento.settMaterialesFotograficos(IConsultasDAO.getMaterialesFotograficos(material));

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}
	}

	/**
	 * Actualiza el informe de mantenimiento a como vaya en el momento
	 * 
	 * @param aTransaccion
	 */
	public void atualizarMantenimientoParcialmente(String aTransaccion) {
		Conexion conexion = new Conexion();

		try {
			if (isValidaActividad()) {
				conexion.setAutoCommitBD(false);
				if (aTransaccion.equals("C")) {

					this.informeMantenimiento.setResponsableClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getRepresentante());
					this.informeMantenimiento.setFirmaClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getFirma());
					this.informeMantenimiento.setCargoClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCargo());

					// guarda el informe como valla
					this.informeMantenimiento.getCamposBD();
					conexion.insertarBD(this.informeMantenimiento.getEstructuraTabla().getTabla(), this.informeMantenimiento.getEstructuraTabla().getPersistencia());

					// guarda las actividades, la actual llena y las demas nulas
					for (ActividadMantenimiento a : this.informeMantenimiento.gettActividades()) {
						a.getCamposBD();
						conexion.insertarBD(a.getEstructuraTabla().getTabla(), a.getEstructuraTabla().getPersistencia());
						a.setId(conexion.getUltimoSerialTransaccion(a.getEstructuraTabla().getTabla()));

					}

					// si posee baterias las guarda hasta donde esté lleno en el momento
					if (this.informeMantenimiento.gettBaterias() != null && this.informeMantenimiento.gettBaterias().size() > 0) {
						for (BateriaMantenimiento b : this.informeMantenimiento.gettBaterias()) {
							b.getCamposBD();
							conexion.insertarBD(b.getEstructuraTabla().getTabla(), b.getEstructuraTabla().getPersistencia());
							b.setId(conexion.getUltimoSerialTransaccion(b.getEstructuraTabla().getTabla()));
						}
					}

					// como es la primera vez actualiza el estado es programado

				} else {

					this.actividadMantenimiento.getCamposBD();
					conexion.actualizarBD(this.actividadMantenimiento.getEstructuraTabla().getTabla(), this.actividadMantenimiento.getEstructuraTabla().getPersistencia(), this.actividadMantenimiento.getEstructuraTabla().getLlavePrimaria(), null);

					this.informeMantenimiento.setResponsableClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getRepresentante());
					this.informeMantenimiento.setFirmaClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getFirma());
					this.informeMantenimiento.setCargoClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCargo());

					// actualizamos los datos de quien hizo cambios por si cambian al
					// responsable en el cliente
					Map<String, Object> campos = new HashMap<String, Object>();
					campos.put("firma_cliente_momento", this.informeMantenimiento.getFirmaClienteMomento());
					campos.put("cargo_cliente_momento", this.informeMantenimiento.getCargoClienteMomento());
					campos.put("responsable_cliente_momento", this.informeMantenimiento.getResponsableClienteMomento());

					this.informeMantenimiento.getCamposBD();
					conexion.actualizarBD(this.informeMantenimiento.getEstructuraTabla().getTabla(), campos, this.informeMantenimiento.getEstructuraTabla().getLlavePrimaria(), null);

				}

				conexion.commitBD();

				this.mostrarMensajeGlobal("actividadActualizada", "exito");
				this.cerrarModal("panelEdicionActividad");

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}
	}

	/**
	 * Asigna una actividad de mantenimiento para editarla o guardarla
	 * 
	 * @param aActividadMantenimiento
	 */
	public void asignarAcividadMantenimiento(ActividadMantenimiento aActividadMantenimiento) {
		this.actividadMantenimiento = aActividadMantenimiento;

		this.abrirModal("panelEdicionActividad");

	}

	/**
	 * Retorna el nombre de una actividad
	 * 
	 * @return actividadNoPreventiva
	 */
	public String getNombreActividad(String aTipo) {

		if (aTipo == null || aTipo.equals("null") || aTipo.equals("")) {
			// cabecera
			return this.actividadNoPreventiva != null && this.actividadNoPreventiva.getId() != null ? this.getMensaje("editarActividad") : this.getMensaje("crearActividad");
		} else {
			// botón guardar
			return this.actividadNoPreventiva != null && this.actividadNoPreventiva.getId() != null ? this.getMensaje("editar") : this.getMensaje("crear");
		}

	}

	/**
	 * Retorna el nombre de un material
	 * 
	 * @return actividadNoPreventiva
	 */
	public String getNombreMaterial(String aTipo) {

		if (aTipo == null || aTipo.equals("null") || aTipo.equals("")) {
			// cabecera
			return this.materialFotografico != null && this.materialFotografico.getId() != null ? this.getMensaje("editarMaterialFotografico") : this.getMensaje("crearMaterialFotografico");
		} else {
			// botón guardar
			return this.materialFotografico != null && this.materialFotografico.getId() != null ? this.getMensaje("editar") : this.getMensaje("crear");
		}

	}

	/**
	 * Crea una nuevo material fotografico
	 */
	public void asignarCreacionMaterialFotografico() {

		boolean ok2 = false;
		if (this.informeMantenimiento.getCronograma().getTipoMantenimiento() != null && this.informeMantenimiento.getCronograma().getTipoMantenimiento().equals(IConstantes.MANTENIMIENTO_PREVENTIVO)) {

			if (this.informeMantenimiento.gettActividades() != null && this.informeMantenimiento.gettActividades().size() > 0) {

				for (ActividadMantenimiento loc : this.informeMantenimiento.gettActividades()) {
					if ((loc.getEstadoInicial() != null && !loc.getEstadoInicial().trim().equals("") && loc.getEstadoFinal() != null && !loc.getEstadoFinal().trim().equals(""))) {
						ok2 = true;
						break;
						// si existe al menos una llena
					}

				}
			}

		} else {
			if (this.informeMantenimiento.gettActividadesNoPreventivas() != null && this.informeMantenimiento.gettActividadesNoPreventivas().size() > 0) {
				for (ActividadNoPreventiva loc : this.informeMantenimiento.gettActividadesNoPreventivas()) {
					if ((loc.getEstadoInicial() != null && !loc.getEstadoInicial().trim().equals("") && loc.getEstadoFinal() != null && !loc.getEstadoFinal().trim().equals("") && loc.getDescripcionActividad() != null && !loc.getDescripcionActividad().trim().equals(""))) {
						ok2 = true;
						break;
						// si existe al menos una llena
					}

				}
			}
		}

		if (ok2) {
			// si al menos guardo una actividad puede seguir
			this.materialFotografico = null;
			this.getMaterialFotografico();
			this.materialFotografico.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());

			this.abrirModal("panelEdicionMaterialFotografico");
		} else {
			this.mostrarMensajeGlobal("guardePrimeroActividad", "advertencia");
		}
	}

	/**
	 * Asigna panel de edicion material fotográfico
	 * 
	 * @param aMaterialFotografico
	 */
	public void asignarMaterialFotografico(MaterialFotografico aMaterialFotografico, String aTipo) {
		this.materialFotografico = aMaterialFotografico;

		if (aTipo != null && aTipo.equals("edicion")) {
			this.abrirModal("panelEdicionMaterialFotografico");
		} else {
			this.abrirModal("panelEliminacionMaterialFotografico");
		}
	}

	/**
	 * Crea una nueva actividad no preventiva
	 */
	public void asignarCreacionNuevaActividadNoPreventiva() {
		this.actividadNoPreventiva = null;
		this.getActividadNoPreventiva();
		this.actividadNoPreventiva.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());

		this.abrirModal("panelEdicionActividadNoPreventiva");
	}

	/**
	 * Asigna una actividad de mantenimiento no preventiva para editarla o
	 * guardarla
	 * 
	 * @param aActividadMantenimiento
	 */
	public void asignarAcividadMantenimientoNoPreventiva(ActividadNoPreventiva aActividadMantenimiento, String aTipo) {
		this.actividadNoPreventiva = aActividadMantenimiento;
		if (aTipo != null && aTipo.equals("edicion")) {
			this.abrirModal("panelEdicionActividadNoPreventiva");
		} else {
			this.abrirModal("panelEliminacionActividadNoPreventiva");
		}
	}

	/**
	 * Asigna una foto para visualizarla
	 * 
	 * @param aActividadMantenimiento
	 */
	public void asignarAcividadFotoV2(MaterialFotografico aMaterialFotografico) {
		this.materialFotografico = aMaterialFotografico;
		// String base64DataString =
		// Base64.getEncoder().encodeToString(this.materialFotografico.getArchivo());
		// this.materialFotografico.settFotoDecodificada(base64DataString);
		this.abrirModal("panelFotoActividad");
	}

	/**
	 * Asigna una foto para visualizarla
	 * 
	 * @param aActividadMantenimiento
	 */
	public void asignarAcividadFoto(ActividadMantenimiento aActividadMantenimiento) {
		this.actividadMantenimiento = aActividadMantenimiento;
		this.guardarImagenEnDisco(this.actividadMantenimiento.getId(), this.actividadMantenimiento.gettFotoDecodificada(), "fotosMantenimientos");
		this.actividadMantenimiento.settNombreFoto("equipo" + this.actividadMantenimiento.getId() + ".png");

		this.abrirModal("panelFotoActividad");
	}

	/**
	 * Arma la estructura de un mantenimiento
	 */
	public void armarEstructuraMantenimiento() {
		try {

			List<ActividadInformeEquipo> actividades = null;
			ActividadInformeEquipo actividadInforme = null;
			BateriaMantenimiento bateria = null;
			ActividadMantenimiento actividad = null;
			ActividadNoPreventiva actividadNo = null;
			MaterialFotografico material = null;

			InformeMantenimiento informe = new InformeMantenimiento();
			informe.getCronograma().setId(this.cronograma.getId());

			this.informeMantenimiento = IConsultasDAO.getInformeMantenimiento(informe);

			// si no existe el informe
			if (!(this.informeMantenimiento != null && this.informeMantenimiento.getCronograma() != null && this.informeMantenimiento.getCronograma().getId() != null)) {

				// le asignamos todos los valores del cronograma
				this.informeMantenimiento = new InformeMantenimiento();
				this.informeMantenimiento.setCronograma(this.cronograma);

				this.informeMantenimiento.setResponsableClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getRepresentante());
				this.informeMantenimiento.setFirmaClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getFirma());
				this.informeMantenimiento.setCargoClienteMomento(this.informeMantenimiento.getCronograma().getEquipo().getCliente().getCargo());

				// armamos el material fotográfico
				this.informeMantenimiento.settMaterialesFotograficos(new ArrayList<MaterialFotografico>());

				// armamos las actividades
				this.informeMantenimiento.settActividades(new ArrayList<ActividadMantenimiento>());
				actividadInforme = new ActividadInformeEquipo();
				actividadInforme.getEquipo().setId(this.informeMantenimiento.getCronograma().getEquipo().getId());

				this.informeMantenimiento.settBaterias(new ArrayList<BateriaMantenimiento>());

				if (this.informeMantenimiento.getCronograma().getTipoMantenimiento() != null && this.informeMantenimiento.getCronograma().getTipoMantenimiento().equals(IConstantes.MANTENIMIENTO_PREVENTIVO)) {

					actividades = IConsultasDAO.getActividades(actividadInforme);
					if (actividades != null && actividades.size() > 0) {

						for (ActividadInformeEquipo a : actividades) {
							actividad = new ActividadMantenimiento();
							actividad.setActividadInformeEquipo(a);
							actividad.setInformeMantenimiento(this.informeMantenimiento);
							this.informeMantenimiento.gettActividades().add(actividad);
						}

						if (this.informeMantenimiento.getCronograma().getEquipo().getMediciones().equals(IConstantes.AFIRMACION) && this.informeMantenimiento.getCronograma().getEquipo().getNumeroFases() != null && this.informeMantenimiento.getCronograma().getEquipo().getNumeroFases().intValue() > 0) {
							this.informeMantenimiento.setNumeroFasesMomento(this.informeMantenimiento.getCronograma().getEquipo().getNumeroFases());
						} else {
							this.informeMantenimiento.setNumeroFasesMomento(0);
						}

						if (this.informeMantenimiento.getCronograma().getEquipo().getContieneBaterias().equals(IConstantes.AFIRMACION) && this.informeMantenimiento.getCronograma().getEquipo().getNumeroBaterias() != null && this.informeMantenimiento.getCronograma().getEquipo().getNumeroBaterias().intValue() > 0) {
							for (int i = 1; i <= this.informeMantenimiento.getCronograma().getEquipo().getNumeroBaterias().intValue(); i++) {
								bateria = new BateriaMantenimiento();
								bateria.setNumeroBateria(i);
								bateria.setInformeMantenimiento(this.informeMantenimiento);
								this.informeMantenimiento.gettBaterias().add(bateria);
							}

						}
					} else {
						this.mostrarMensajeGlobal("actividadesActivasEquipo", "advertencia");
						return;
					}

				} else {

					this.informeMantenimiento.settActividadesNoPreventivas(new ArrayList<ActividadNoPreventiva>());

					if (this.informeMantenimiento.getCronograma().getEquipo().getMediciones().equals(IConstantes.AFIRMACION) && this.informeMantenimiento.getCronograma().getEquipo().getNumeroFases() != null && this.informeMantenimiento.getCronograma().getEquipo().getNumeroFases().intValue() > 0) {
						this.informeMantenimiento.setNumeroFasesMomento(this.informeMantenimiento.getCronograma().getEquipo().getNumeroFases());
					} else {
						this.informeMantenimiento.setNumeroFasesMomento(0);
					}

					if (this.informeMantenimiento.getCronograma().getEquipo().getContieneBaterias().equals(IConstantes.AFIRMACION) && this.informeMantenimiento.getCronograma().getEquipo().getNumeroBaterias() != null && this.informeMantenimiento.getCronograma().getEquipo().getNumeroBaterias().intValue() > 0) {
						for (int i = 1; i <= this.informeMantenimiento.getCronograma().getEquipo().getNumeroBaterias().intValue(); i++) {
							bateria = new BateriaMantenimiento();
							bateria.setNumeroBateria(i);
							bateria.setInformeMantenimiento(this.informeMantenimiento);
							this.informeMantenimiento.gettBaterias().add(bateria);
						}

					}

				}

			} else {

				// le asignamos todos los valores del cronograma
				this.informeMantenimiento.setCronograma(this.cronograma);
				this.informeMantenimiento.settBaterias(new ArrayList<BateriaMantenimiento>());
				this.informeMantenimiento.settActividades(new ArrayList<ActividadMantenimiento>());

				this.informeMantenimiento.settMaterialesFotograficos(new ArrayList<MaterialFotografico>());

				bateria = new BateriaMantenimiento();
				bateria.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
				this.informeMantenimiento.settBaterias(IConsultasDAO.getBateriasMantenimiento(bateria));

				actividad = new ActividadMantenimiento();
				actividad.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());

				actividadNo = new ActividadNoPreventiva();
				actividadNo.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());

				if (this.informeMantenimiento.getCronograma().getTipoMantenimiento() != null && this.informeMantenimiento.getCronograma().getTipoMantenimiento().equals(IConstantes.MANTENIMIENTO_PREVENTIVO)) {

					this.informeMantenimiento.settActividades(IConsultasDAO.getActividadesMantenimiento(actividad));

				} else {

					this.informeMantenimiento.settActividadesNoPreventivas(new ArrayList<ActividadNoPreventiva>());
					this.informeMantenimiento.settActividadesNoPreventivas(IConsultasDAO.getActividadesMantenimientoNoPreventiva(actividadNo));

					if (!(this.informeMantenimiento.gettActividadesNoPreventivas() != null && this.informeMantenimiento.gettActividadesNoPreventivas().size() > 0)) {
						this.informeMantenimiento.settActividadesNoPreventivas(new ArrayList<ActividadNoPreventiva>());
					}

				}

				material = new MaterialFotografico();
				material.getInformeMantenimiento().getCronograma().setId(this.informeMantenimiento.getCronograma().getId());
				// consultamos las fotos que lleven en el momento
				this.informeMantenimiento.settMaterialesFotograficos(IConsultasDAO.getMaterialesFotograficos(material));

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Aprueba un informe desde otra interfaz o clase
	 * 
	 * @param aCronograma
	 */
	public void aprobarInformeOtraInterfaz(Cronograma aCronograma) {
		this.cronograma = aCronograma;
		generarDatosInforme();
		aprobarInformeMantenimiento();
	}

	public String mostrarcodigo(Cronograma aCronograma) {
		this.cronograma = aCronograma;
		generarDatosInforme();
		return this.informeMantenimiento.gettActividades().get(0).gettFotoDecodificada();

	}

	/**
	 * Imprimir informe desde cualquier interfaz
	 * 
	 * @param aCronograma
	 */
	public void diligenciarImprimirInforme(Cronograma aCronograma) {
		try {
			this.cronograma = aCronograma;
			generarDatosInforme();
			imprimirFormatoDiagnostico();
		} catch (Exception e) {

		}
	}

	/**
	 * Consulta todos los datos para el informe de mantenimiento
	 */
	public void consultarDatosParaMantenimiento() {

		try {
			if (this.cronograma != null && this.cronograma.getId() != null) {
				Cronograma cron = new Cronograma();
				cron.setId(this.cronograma.getId());
				this.cronograma = IConsultasDAO.getCronograma(cron).get(0);

				Equipo equipo = new Equipo();
				equipo.setId(this.cronograma.getEquipo().getId());

				this.cronograma.setEquipo(IConsultasDAO.getEquipos(equipo).get(0));

				// si tiene asociado reporte de falla lo busca
				if (this.cronograma.getReporteFalla() != null && this.cronograma.getReporteFalla().getId() != null) {
					ReporteFalla reporte = new ReporteFalla();
					reporte.setId(this.cronograma.getReporteFalla().getId());
					this.cronograma.setReporteFalla(IConsultasDAO.getReportesFallas(reporte).get(0));

					// correctivo
					if (this.cronograma.getTipoMantenimiento().equals("C")) {
						this.mostrarMensajeGlobal("tieneReporteFallaAsociado", "advertencia");
					}
				}

				this.armarEstructuraMantenimiento();
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Obtiene el cliente analizado
	 * 
	 * @return cliente
	 */
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

	/**
	 * Establece el cliente analizado
	 * 
	 * @param cliente
	 */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	/**
	 * Obtiene el equipo analizado
	 * 
	 * @return equipo
	 */
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

	/**
	 * Establece el equipo analizado
	 * 
	 * @param equipo
	 */
	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	/**
	 * Obtiene el cronograma analziado
	 * 
	 * @return cronograma
	 */
	public Cronograma getCronograma() {
		try {
			if (this.cronograma == null) {
				this.cronograma = new Cronograma();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return cronograma;
	}

	/**
	 * Establece el cronograma analizado
	 * 
	 * @param cronograma
	 */
	public void setCronograma(Cronograma cronograma) {
		this.cronograma = cronograma;
	}

	/**
	 * Obtiene el informe de mantenimiento analizado
	 * 
	 * @return informeMantenimiento
	 */
	public InformeMantenimiento getInformeMantenimiento() {
		try {
			if (this.informeMantenimiento == null) {
				this.informeMantenimiento = new InformeMantenimiento();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return informeMantenimiento;
	}

	/**
	 * Establece el informe de mantenimiento analizado
	 * 
	 * @param informeMantenimiento
	 */
	public void setInformeMantenimiento(InformeMantenimiento informeMantenimiento) {
		this.informeMantenimiento = informeMantenimiento;
	}

	/**
	 * Obtiene la actividad de mantenimiento a editar
	 * 
	 * @return actividadMantenimiento
	 */
	public ActividadMantenimiento getActividadMantenimiento() {
		try {
			if (this.actividadMantenimiento == null) {
				this.actividadMantenimiento = new ActividadMantenimiento();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return actividadMantenimiento;
	}

	/**
	 * Establece la actividad de mantenimiento a editar
	 * 
	 * @param actividadMantenimiento
	 */
	public void setActividadMantenimiento(ActividadMantenimiento actividadMantenimiento) {
		this.actividadMantenimiento = actividadMantenimiento;
	}

	/**
	 * Obtiene una actividad no preventiva
	 * 
	 * @return actividadNoPreventiva
	 */
	public ActividadNoPreventiva getActividadNoPreventiva() {
		try {
			if (this.actividadNoPreventiva == null) {
				this.actividadNoPreventiva = new ActividadNoPreventiva();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return actividadNoPreventiva;
	}

	/**
	 * Establece una actividad no preventiva
	 * 
	 * @param actividadNoPreventiva
	 */
	public void setActividadNoPreventiva(ActividadNoPreventiva actividadNoPreventiva) {
		this.actividadNoPreventiva = actividadNoPreventiva;
	}

	/**
	 * Obtiene el material fotográfico
	 * 
	 * @return materialFotografico
	 */
	public MaterialFotografico getMaterialFotografico() {
		try {
			if (this.materialFotografico == null) {
				this.materialFotografico = new MaterialFotografico();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return materialFotografico;
	}

	/**
	 * Establece el material fotográfico
	 * 
	 * @param materialFotografico
	 */
	public void setMaterialFotografico(MaterialFotografico materialFotografico) {
		this.materialFotografico = materialFotografico;
	}

	// listas
	/**
	 * Obtiene el listado de clientes que puede manipular el diagnóstico
	 * 
	 * @return itemsClientes
	 */
	public List<SelectItem> getItemsClientes() {
		try {

			this.itemsClientes = new ArrayList<SelectItem>();
			this.itemsClientes.add(new SelectItem("", this.getMensaje("comboVacio")));

			Cronograma cron = new Cronograma();

			if (this.administrarSesionCliente != null && this.administrarSesionCliente.getPersonalSesion() != null && administrarSesionCliente.getPersonalSesion().getId() != null && administrarSesionCliente.getPersonalSesion().getTipoUsuario() != null && administrarSesionCliente.getPersonalSesion().getTipoUsuario().equals(IConstantes.ROL_TECNICO)) {
				cron.getTecnico().setId(this.administrarSesionCliente.getPersonalSesion().getId());
			}

			List<Cliente> clientes = IConsultasDAO.getClientesCronograma(cron);

			if (clientes != null && clientes.size() > 0) {
				clientes.forEach(p -> this.itemsClientes.add(new SelectItem(p.getId(), p.getCliente() + "," + p.getUbicacion())));
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsClientes;
	}

	/**
	 * Establece el listado de clientes
	 * 
	 * @param itemsClientes
	 */
	public void setItemsClientes(List<SelectItem> itemsClientes) {
		this.itemsClientes = itemsClientes;
	}

	/**
	 * Obtiene los items de los equipos disponibles
	 * 
	 * @return itemsEquipos
	 */
	public List<SelectItem> getItemsEquipos() {
		try {

			this.itemsEquipos = new ArrayList<SelectItem>();
			this.itemsEquipos.add(new SelectItem("", this.getMensaje("comboVacio")));
			if (this.cliente != null && this.cliente.getId() != null) {
				Cronograma cron = new Cronograma();
				cron.getEquipo().getCliente().setId(this.cliente.getId());

				if (this.administrarSesionCliente != null && this.administrarSesionCliente.getPersonalSesion() != null && administrarSesionCliente.getPersonalSesion().getId() != null && administrarSesionCliente.getPersonalSesion().getTipoUsuario() != null && administrarSesionCliente.getPersonalSesion().getTipoUsuario().equals(IConstantes.ROL_TECNICO)) {
					cron.getTecnico().setId(this.administrarSesionCliente.getPersonalSesion().getId());
				}

				List<Equipo> equipos = IConsultasDAO.getEquiposCronograma(cron);

				if (equipos != null && equipos.size() > 0) {
					equipos.forEach(p -> this.itemsEquipos.add(new SelectItem(p.getId(), p.getNombreEquipo() + "," + p.getCodigoQr())));
				}
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsEquipos;
	}

	/**
	 * Establece los items de los equipos disponibles
	 * 
	 * @param itemsEquipos
	 */
	public void setItemsEquipos(List<SelectItem> itemsEquipos) {
		this.itemsEquipos = itemsEquipos;
	}

	/**
	 * Obtiene los items de los cronogramas
	 * 
	 * @return itemsCronogramas
	 */
	public List<SelectItem> getItemsCronogramas() {
		try {

			this.itemsCronogramas = new ArrayList<SelectItem>();
			this.itemsCronogramas.add(new SelectItem("", this.getMensaje("comboVacio")));
			if (this.equipo != null && this.equipo.getId() != null && this.cliente != null && this.cliente.getId() != null) {

				Cronograma cron = new Cronograma();
				cron.getEquipo().getCliente().setId(this.cliente.getId());
				cron.getEquipo().setId(this.equipo.getId());

				if (this.administrarSesionCliente != null && this.administrarSesionCliente.getPersonalSesion() != null && administrarSesionCliente.getPersonalSesion().getId() != null && administrarSesionCliente.getPersonalSesion().getTipoUsuario() != null && administrarSesionCliente.getPersonalSesion().getTipoUsuario().equals(IConstantes.ROL_TECNICO)) {
					cron.getTecnico().setId(this.administrarSesionCliente.getPersonalSesion().getId());
				}

				List<Cronograma> cronos = IConsultasDAO.getCronogramasMantenimientos(cron);

				if (cronos != null && cronos.size() > 0) {

					if (cronos.stream().anyMatch(p -> p.getEstado() != null && p.getEstado().equals(IConstantes.ESTADO_PROGRAMADO))) {

						SelectItemGroup g1 = new SelectItemGroup(this.getMensaje("programado"));
						List<SelectItem> itemsG1 = new ArrayList<SelectItem>();
						cronos.stream().filter(p -> p.getEstado() != null && p.getEstado().equals(IConstantes.ESTADO_PROGRAMADO)).forEach(p -> itemsG1.add(new SelectItem(p.getId(), this.getFechaColombia(p.getFechaProgramacion()) + " +/- " + p.getHolgura() + ",(" + this.getTipoMantenimiento(p.getTipoMantenimiento()) + ")")));
						g1.setSelectItems(itemsG1.stream().toArray(SelectItem[]::new));
						this.itemsCronogramas.add(g1);
					}

					if (cronos.stream().anyMatch(p -> p.getEstado() != null && p.getEstado().equals(IConstantes.ESTADO_ATENDIDO))) {

						SelectItemGroup g2 = new SelectItemGroup(this.getMensaje("atendido"));
						List<SelectItem> itemsG2 = new ArrayList<SelectItem>();
						cronos.stream().filter(p -> p.getEstado() != null && p.getEstado().equals(IConstantes.ESTADO_ATENDIDO)).forEach(p -> itemsG2.add(new SelectItem(p.getId(), this.getFechaColombia(p.getFechaProgramacion()) + " +/- " + p.getHolgura() + ",(" + this.getTipoMantenimiento(p.getTipoMantenimiento()) + ")")));
						g2.setSelectItems(itemsG2.stream().toArray(SelectItem[]::new));
						this.itemsCronogramas.add(g2);
					}

					if (cronos.stream().anyMatch(p -> p.getEstado() != null && p.getEstado().equals(IConstantes.ESTADO_APROBADO))) {

						SelectItemGroup g3 = new SelectItemGroup(this.getMensaje("aprobado"));
						List<SelectItem> itemsG3 = new ArrayList<SelectItem>();
						cronos.stream().filter(p -> p.getEstado() != null && p.getEstado().equals(IConstantes.ESTADO_APROBADO)).forEach(p -> itemsG3.add(new SelectItem(p.getId(), this.getFechaColombia(p.getFechaProgramacion()) + " +/- " + p.getHolgura() + ",(" + this.getTipoMantenimiento(p.getTipoMantenimiento()) + ")")));
						g3.setSelectItems(itemsG3.stream().toArray(SelectItem[]::new));
						this.itemsCronogramas.add(g3);
					}

				}
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsCronogramas;
	}

	/**
	 * Establece los items de los cronogramas
	 * 
	 * @param itemsCronogramas
	 */
	public void setItemsCronogramas(List<SelectItem> itemsCronogramas) {
		this.itemsCronogramas = itemsCronogramas;
	}

	/**
	 * Obtiene un listado de cronogramas
	 * 
	 * @return cronogramas
	 */
	public List<Cronograma> getCronogramas() {
		return cronogramas;
	}

	/**
	 * Establece un listado de cronogramas
	 * 
	 * @param cronogramas
	 */
	public void setCronogramas(List<Cronograma> cronogramas) {
		this.cronogramas = cronogramas;
	}

	public AdministrarSesionCliente getAdministrarSesionCliente() {
		return administrarSesionCliente;
	}

	public void setAdministrarSesionCliente(AdministrarSesionCliente administrarSesionCliente) {
		this.administrarSesionCliente = administrarSesionCliente;
	}

	public Integer getInicio() {
		return inicio;
	}

	public void setInicio(Integer inicio) {
		this.inicio = inicio;
	}

	public Integer getFin() {
		return fin;
	}

	public void setFin(Integer fin) {
		this.fin = fin;
	}

}
