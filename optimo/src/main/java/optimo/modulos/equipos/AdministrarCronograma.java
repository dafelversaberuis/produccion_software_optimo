package optimo.modulos.equipos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import optimo.Conexion;
import optimo.beans.ActividadMantenimiento;
import optimo.beans.ActividadNoPreventiva;
import optimo.beans.BateriaMantenimiento;
import optimo.beans.Cliente;
import optimo.beans.Cronograma;
import optimo.beans.Equipo;
import optimo.beans.InformeMantenimiento;
import optimo.beans.MaterialFotografico;
import optimo.beans.ReporteFalla;
import optimo.beans.Tecnico;
import optimo.generales.ConsultarFuncionesAPI;
import optimo.generales.IConstantes;
import optimo.modulos.IConsultasDAO;
import optimo.modulos.personal.AdministrarSesionCliente;

@ManagedBean
@ViewScoped
public class AdministrarCronograma extends ConsultarFuncionesAPI implements Serializable {

	/**
	 * 
	 */
	private static final long					serialVersionUID	= -8404356291450368745L;

	// inyecta el bean de sesion
	@ManagedProperty(value = "#{administrarSesionCliente}")
	private AdministrarSesionCliente	administrarSesionCliente;

	private Cronograma								cronograma;
	private Cronograma								cronogramaTransaccion;
	private Cronograma								cronogramaConsulta;
	private BarChartModel							graficoBarrasDisponibilidad;
	private BarChartModel							graficoBarrasConfiabilidad;
	private BarChartModel							graficoBarrasMantenibilidad;
	private BarChartModel							graficoBarrasCostos;
	private BarChartModel							graficoCumplimiento;

	private List<Cronograma>					cronogramas;

	private List<Equipo>							equipos;
	private List<SelectItem>					itemsEquiposActivos;
	private List<SelectItem>					itemsEquipos;
	private List<SelectItem>					itemsTecicosActivos;
	private List<SelectItem>					itemsTecnicos;
	private List<SelectItem>					itemsFallasDisponibles;

	// privados

	/**
	 * Detremina si un cronograma es válido
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isValidoCronograma(String aTransaccion) {
		boolean ok = true;
		List<Cronograma> cronogramas = null;

		try {

			if (aTransaccion.equals("C")) {

				// valida que no se haya programado un mantenimiento del mismo tipo para
				// la fecha y cliente
				// del cliente deseado

				Cronograma temp = new Cronograma();
				temp.setFechaProgramacion(this.cronograma.getFechaProgramacion());
				temp.setTipoMantenimiento(this.cronograma.getTipoMantenimiento());
				temp.getEquipo().setId(this.cronograma.getEquipo().getId());

				cronogramas = IConsultasDAO.getCronograma(temp);

				if (cronogramas != null && cronogramas.size() > 0) {
					ok = false;
					this.mostrarMensajeGlobal("programacionExistente", "advertencia");

				}

			} else {

				Cronograma temp = new Cronograma();
				temp.setFechaProgramacion(this.cronogramaTransaccion.getFechaProgramacion());
				temp.setTipoMantenimiento(this.cronogramaTransaccion.getTipoMantenimiento());
				temp.getEquipo().setId(this.cronogramaTransaccion.getEquipo().getId());

				cronogramas = IConsultasDAO.getCronograma(temp);
				if (cronogramas != null && cronogramas.size() > 0 && cronogramas.stream().anyMatch(p -> p.getId().intValue() != this.cronogramaTransaccion.getId().intValue())) {

					this.mostrarMensajeGlobal("programacionExistente", "advertencia");

				}
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return ok;
	}

	/**
	 * Obtiene el número de horas para dos fechas incluye extermos[]
	 * 
	 * @param aFechaInicial
	 * @param aFechaFinal
	 * @return horas
	 */
	private int getHorasEntreFechasIndicadores(Date aFechaInicial, Date aFechaFinal) {
		int horas = 0;
		try {
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

			aFechaInicial = formato.parse(formato.format(aFechaInicial));
			aFechaFinal = formato.parse(formato.format(aFechaFinal));

			horas = (int) ((aFechaFinal.getTime() - aFechaInicial.getTime()) / 86400000);
			horas++;
			horas = IConstantes.HORAS_DIA_INDICADORES_GESTION.intValue() * horas;

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

		return horas;
	}

	// publicos

	/**
	 * Asigna el qr de consulta de cronograma
	 */
	public String getAsignacionQrConsultarCronograma() {
		String valor = "";
		try {

			if (this.administrarSesionCliente != null && this.administrarSesionCliente.getPersonalSesion() != null && this.administrarSesionCliente.getQr() != null && !this.administrarSesionCliente.getQr().trim().equals("")) {

				this.getCronogramaConsulta();
				this.cronogramaConsulta.getEquipo().setCodigoQr(this.administrarSesionCliente.getQr().trim());

				this.administrarSesionCliente.setQr(null);
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return valor;
	}

	/**
	 * Obtiene los indicadores de cumplimiento del cronograma
	 */
	public void consultarEvaluacionCronograma() {
		try {

			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
			Equipo equipo = null;
			Cronograma cronograma = null;
			List<Cronograma> mantenimientosPreventivos = null;

			if (formato.format(this.cronogramaConsulta.gettFechaDesde()).compareTo(formato.format(this.cronogramaConsulta.gettFechaHasta())) <= 0) {

				if (formato.format(this.cronogramaConsulta.gettFechaEvaluacion()).compareTo(formato.format(this.cronogramaConsulta.gettFechaDesde())) >= 0 && formato.format(this.cronogramaConsulta.gettFechaEvaluacion()).compareTo(formato.format(this.cronogramaConsulta.gettFechaHasta())) <= 0) {

					this.cronogramaConsulta.settFechaDesde(this.getFechaCeroHoras(this.cronogramaConsulta.gettFechaDesde()));
					this.cronogramaConsulta.settFechaHasta(this.getFechaCeroHoras(this.cronogramaConsulta.gettFechaHasta()));
					this.cronogramaConsulta.settFechaEvaluacion(this.getFechaCeroHoras(this.cronogramaConsulta.gettFechaEvaluacion()));

					// EQUIPOS A MOSTRAR
					equipo = new Equipo();
					equipo.getCliente().setId(this.cronogramaConsulta.getEquipo().getCliente().getId());
					if (this.cronogramaConsulta.getEquipo() != null && this.cronogramaConsulta.getEquipo().getId() != null) {
						equipo.setId(this.cronogramaConsulta.getEquipo().getId());
					}

					this.equipos = IConsultasDAO.getEquipos(equipo);
					if (this.equipos != null && this.equipos.size() > 0) {

						for (Equipo p : this.equipos) {

							// TIP
							cronograma = new Cronograma();
							cronograma.setTipoMantenimiento(IConstantes.MANTENIMIENTO_PREVENTIVO);
							cronograma.getEquipo().setId(p.getId());
							cronograma.settFechaDesde(this.cronogramaConsulta.gettFechaDesde());
							cronograma.settFechaHasta(this.cronogramaConsulta.gettFechaHasta());
							mantenimientosPreventivos = IConsultasDAO.getCronograma(cronograma);

							p.settTotalIntervencionesProgramadas(0);
							p.settPorcentajePactado(new BigDecimal(0));
							if (mantenimientosPreventivos != null && mantenimientosPreventivos.size() > 0) {
								p.settTotalIntervencionesProgramadas(mantenimientosPreventivos.size());
								p.settPorcentajePactado(new BigDecimal(100));
							}

							// TIR, desde fecha inicio a fecha evaluación
							cronograma = new Cronograma();
							cronograma.setTipoMantenimiento(IConstantes.MANTENIMIENTO_PREVENTIVO);
							cronograma.setEstado(IConstantes.ESTADO_APROBADO);
							cronograma.getEquipo().setId(p.getId());
							cronograma.settFechaDesde(this.cronogramaConsulta.gettFechaDesde());
							cronograma.settFechaHasta(this.cronogramaConsulta.gettFechaEvaluacion());
							mantenimientosPreventivos = IConsultasDAO.getCronograma(cronograma);

							p.settTotalIntervencionesRealizadas(0);
							if (mantenimientosPreventivos != null && mantenimientosPreventivos.size() > 0) {
								p.settTotalIntervencionesRealizadas(mantenimientosPreventivos.size());
							}

							// porcentaje cumplimiento
							p.settPorcentajeCumplimiento(new BigDecimal(0));
							if (p.gettTotalIntervencionesProgramadas().intValue() > 0) {
								p.settPorcentajeCumplimiento(this.getValorRedondeado((new BigDecimal(p.gettTotalIntervencionesRealizadas()).multiply(new BigDecimal(100))).divide(new BigDecimal(p.gettTotalIntervencionesProgramadas()), IConstantes.DECIMALES_REDONDEAR, RoundingMode.HALF_UP), IConstantes.DECIMALES_REDONDEAR));
							}

						}

					}

				} else {

					this.mostrarMensajeGlobal("fechaEvaluacionIncorrecta", "advertencia");

				}

			} else {

				this.mostrarMensajeGlobal("fechasIncorrectas", "advertencia");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Obtiene los indicadores de gestión sobre los equipos analizados
	 */
	public void consultarIndicadoresGestion() {
		try {

			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
			Equipo equipo = null;
			Cronograma cronograma = null;
			ReporteFalla reporteFalla = null;
			List<ReporteFalla> reportesFallas = null;
			List<Cronograma> mantenimientosPreventivos = null;
			List<Cronograma> mantenimientosCorrectivos = null;
			List<Cronograma> mantenimientos = null;
			final List<ReporteFalla> reportesFallasSoloValidas = new ArrayList<ReporteFalla>();
			final List<ReporteFalla> reportesFallasValidasConteo = new ArrayList<ReporteFalla>();

			if (formato.format(this.cronogramaConsulta.gettFechaDesde()).compareTo(formato.format(this.cronogramaConsulta.gettFechaHasta())) <= 0) {

				this.cronogramaConsulta.settFechaDesde(this.getFechaCeroHoras(this.cronogramaConsulta.gettFechaDesde()));
				this.cronogramaConsulta.settFechaHasta(this.getFechaCeroHoras(this.cronogramaConsulta.gettFechaHasta()));

				// IA
				this.cronogramaConsulta.settIntervaloAnalizado(this.getHorasEntreFechasIndicadores(this.cronogramaConsulta.gettFechaDesde(), this.cronogramaConsulta.gettFechaHasta()));

				// EQUIPOS A MOSTRAR
				equipo = new Equipo();
				equipo.getCliente().setId(this.cronogramaConsulta.getEquipo().getCliente().getId());
				if (this.cronogramaConsulta.getEquipo() != null && this.cronogramaConsulta.getEquipo().getId() != null) {
					equipo.setId(this.cronogramaConsulta.getEquipo().getId());
				}

				this.equipos = IConsultasDAO.getEquipos(equipo);
				if (this.equipos != null && this.equipos.size() > 0) {

					for (Equipo p : this.equipos) {

						// TFS
						reporteFalla = new ReporteFalla();
						reporteFalla.getEquipo().setId(p.getId());
						reporteFalla.settFechaDesde(this.cronogramaConsulta.gettFechaDesde());
						reporteFalla.settFechaHasta(this.cronogramaConsulta.gettFechaHasta());
						reporteFalla.setEstado(IConstantes.ABREVIATURA_CERRADA);

						reportesFallas = IConsultasDAO.getReportesFallas(reporteFalla);
						if (reportesFallas != null && reportesFallas.size() > 0) {
							// solo las válidas
							reportesFallas.stream().filter(a -> !(a.getConceptoCierreManual() != null && !a.getConceptoCierreManual().trim().equals(""))).forEach(a -> reportesFallasSoloValidas.add(a));
						}

						p.settTiempoFueraServicio(0);
						if (reportesFallasSoloValidas != null && reportesFallasSoloValidas.size() > 0) {
							for (ReporteFalla r : reportesFallasSoloValidas) {
								p.settTiempoFueraServicio(p.gettTiempoFueraServicio().intValue() + this.getHorasEntreFechasIndicadores(r.getFechaFalla(), r.getFechaHoraAtencion()));
							}
						}

						// TPP
						cronograma = new Cronograma();
						cronograma.setTipoMantenimiento(IConstantes.MANTENIMIENTO_PREVENTIVO);
						cronograma.getEquipo().setId(p.getId());
						cronograma.settFechaDesde(this.cronogramaConsulta.gettFechaDesde());
						cronograma.settFechaHasta(this.cronogramaConsulta.gettFechaHasta());
						mantenimientosPreventivos = IConsultasDAO.getCronograma(cronograma);

						p.settTiempoParadasProgramadas(0);
						if (mantenimientosPreventivos != null && mantenimientosPreventivos.size() > 0) {
							for (Cronograma c : mantenimientosPreventivos) {
								p.settTiempoParadasProgramadas(p.gettTiempoParadasProgramadas().intValue() + c.getDuracion().intValue());
							}
						}

						// TPMC
						cronograma = new Cronograma();
						cronograma.setTipoMantenimiento(IConstantes.MANTENIMIENTO_CORRECTIVO);
						cronograma.getEquipo().setId(p.getId());
						cronograma.setEstado(IConstantes.ESTADO_APROBADO);
						cronograma.settFechaDesde(this.cronogramaConsulta.gettFechaDesde());
						cronograma.settFechaHasta(this.cronogramaConsulta.gettFechaHasta());
						mantenimientosCorrectivos = IConsultasDAO.getCronograma(cronograma);

						p.settTiempoProgramadoMantenimientoCorrectivo(0);
						if (mantenimientosCorrectivos != null && mantenimientosCorrectivos.size() > 0) {
							for (Cronograma c : mantenimientosCorrectivos) {
								p.settTiempoProgramadoMantenimientoCorrectivo(p.gettTiempoProgramadoMantenimientoCorrectivo().intValue() + c.getDuracion().intValue());
							}
						}

						// NF (solo reportadas válidas)
						reporteFalla = new ReporteFalla();
						reporteFalla.getEquipo().setId(p.getId());
						reporteFalla.settFechaDesde(this.cronogramaConsulta.gettFechaDesde());
						reporteFalla.settFechaHasta(this.cronogramaConsulta.gettFechaHasta());

						reportesFallas = IConsultasDAO.getReportesFallas(reporteFalla);
						if (reportesFallas != null && reportesFallas.size() > 0) {
							// solo las válidas
							reportesFallas.stream().filter(a -> !(a.getConceptoCierreManual() != null && !a.getConceptoCierreManual().trim().equals(""))).forEach(a -> reportesFallasValidasConteo.add(a));
						}

						p.settNumeroFallas(0);
						if (reportesFallasValidasConteo != null && reportesFallasValidasConteo.size() > 0) {
							p.settNumeroFallas(reportesFallasValidasConteo.size());
						}

						// disponibilidad
						p.settDisponibilidad(new BigDecimal(0));
						if (this.cronogramaConsulta.gettIntervaloAnalizado().intValue() > 0) {
							p.settDisponibilidad(this.getValorRedondeado((((new BigDecimal(this.cronogramaConsulta.gettIntervaloAnalizado()).subtract(new BigDecimal(p.gettTiempoParadasProgramadas())).subtract(new BigDecimal(p.gettTiempoFueraServicio()))).divide(new BigDecimal(this.cronogramaConsulta.gettIntervaloAnalizado()), IConstantes.DECIMALES_REDONDEAR, RoundingMode.HALF_UP)).multiply(new BigDecimal(100))), IConstantes.DECIMALES_REDONDEAR));
						}
						// mantenibilidad
						p.settMantenibilidad(new BigDecimal(0));
						if (p.gettNumeroFallas().intValue() > 0) {
							p.settMantenibilidad(this.getValorRedondeado((new BigDecimal(p.gettTiempoFueraServicio()).subtract(new BigDecimal(p.gettTiempoProgramadoMantenimientoCorrectivo()))).divide(new BigDecimal(p.gettNumeroFallas()), IConstantes.DECIMALES_REDONDEAR, RoundingMode.HALF_UP), IConstantes.DECIMALES_REDONDEAR));
						}

						// confiabilidad
						p.settConfiabilidad(new BigDecimal(0));
						if (p.gettDisponibilidad().doubleValue() != 100) {
							p.settConfiabilidad(this.getValorRedondeado((p.gettMantenibilidad().multiply(new BigDecimal(p.gettNumeroFallas()).multiply(p.gettDisponibilidad().divide(new BigDecimal(100), IConstantes.DECIMALES_REDONDEAR, RoundingMode.HALF_UP))).divide((new BigDecimal(1).subtract(p.gettDisponibilidad().divide(new BigDecimal(100), IConstantes.DECIMALES_REDONDEAR, RoundingMode.HALF_UP))), IConstantes.DECIMALES_REDONDEAR, RoundingMode.HALF_UP)), IConstantes.DECIMALES_REDONDEAR));
						}
						// costo
						cronograma = new Cronograma();
						cronograma.getEquipo().setId(p.getId());
						cronograma.settFechaDesde(this.cronogramaConsulta.gettFechaDesde());
						cronograma.settFechaHasta(this.cronogramaConsulta.gettFechaHasta());
						mantenimientos = IConsultasDAO.getCronograma(cronograma);

						p.settCosto(new BigDecimal(0));
						if (mantenimientos != null && mantenimientos.size() > 0) {
							for (Cronograma c : mantenimientos) {
								p.settCosto(p.gettCosto().add(c.getCosto()));
							}
						}
						p.settCosto(this.getValorRedondeado(p.gettCosto(), IConstantes.DECIMALES_REDONDEAR));

					}

				}

			} else {

				this.mostrarMensajeGlobal("fechasIncorrectas", "advertencia");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
	}

	/**
	 * Obtiene las horas
	 * 
	 * @return valor
	 */
	public int getHoras() {
		int horas = 0;
		if (this.cronogramas != null && cronogramas.size() > 0) {
			for (Cronograma p : this.cronogramas) {
				horas += p.getDuracion().intValue();
			}

		}

		return horas;

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
	 * Obtiene el valor en texto tipo moneda
	 * 
	 * @param aValor
	 * @return textoSalida
	 */
	public String getMoneda(BigDecimal aValor) {
		String textoSalida = "";
		DecimalFormat formato = new DecimalFormat("###,###.00");
		if (aValor != null) {
			textoSalida = formato.format(aValor);
		}
		return "$ " + textoSalida;

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

				this.cronogramaConsulta.getEquipo().setCliente(new Cliente());

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clientes;
	}

	/**
	 * Selecciona un cliente y busca su información completa
	 */
	public void seleccionarCliente() {

		try {

			if (this.cronogramaConsulta != null && this.cronogramaConsulta.getId() != null) {

				Cliente temp = new Cliente();
				temp.setId(this.cronogramaConsulta.getId());

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.cronogramaConsulta.getEquipo().getCliente().setId(cliente.getId());
					this.cronogramaConsulta.getEquipo().getCliente().setNit(cliente.getNit());
					this.cronogramaConsulta.getEquipo().getCliente().setCliente(cliente.getCliente());
					this.cronogramaConsulta.getEquipo().getCliente().setUbicacion(cliente.getUbicacion());

					this.cronogramaConsulta.getEquipo().getCliente().settClienteAutocompletado(this.cronogramaConsulta.getEquipo().getCliente().getCliente() + ", " + this.cronogramaConsulta.getEquipo().getCliente().getUbicacion());

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

			if (event != null && event.getObject() != null && !event.getObject().toString().trim().equals("") && this.cronogramaConsulta != null) {

				String[] separado = event.getObject().toString().trim().split("##id=");

				Cliente temp = new Cliente();
				temp.setId(Integer.parseInt(separado[1]));

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.cronogramaConsulta.getEquipo().getCliente().setId(cliente.getId());
					this.cronogramaConsulta.getEquipo().getCliente().setNit(cliente.getNit());
					this.cronogramaConsulta.getEquipo().getCliente().setCliente(cliente.getCliente());
					this.cronogramaConsulta.getEquipo().getCliente().setUbicacion(cliente.getUbicacion());

					this.cronogramaConsulta.getEquipo().getCliente().settClienteAutocompletado(this.cronogramaConsulta.getEquipo().getCliente().getCliente() + ", " + this.cronogramaConsulta.getEquipo().getCliente().getUbicacion());

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

			if (event != null && event.getObject() != null && !event.getObject().toString().trim().equals("") && this.cronograma != null) {

				String[] separado = event.getObject().toString().trim().split("##id=");

				Cliente temp = new Cliente();
				temp.setId(Integer.parseInt(separado[1]));

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.cronograma.getEquipo().getCliente().setId(cliente.getId());
					this.cronograma.getEquipo().getCliente().setNit(cliente.getNit());
					this.cronograma.getEquipo().getCliente().setCliente(cliente.getCliente());
					this.cronograma.getEquipo().getCliente().setUbicacion(cliente.getUbicacion());

					this.cronograma.getEquipo().getCliente().settClienteAutocompletado(this.cronograma.getEquipo().getCliente().getCliente() + ", " + this.cronograma.getEquipo().getCliente().getUbicacion());

				}

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Método que me selecciona el nombre del cliente, lo busca y llena el nit y
	 * otros para cuando se está editando un cronograma
	 * 
	 * @param event
	 */
	public void onItemSelectEditar(SelectEvent event) {

		try {

			if (event != null && event.getObject() != null && !event.getObject().toString().trim().equals("") && this.cronogramaTransaccion != null) {

				String[] separado = event.getObject().toString().trim().split("##id=");

				Cliente temp = new Cliente();
				temp.setId(Integer.parseInt(separado[1]));

				List<Cliente> clientes = IConsultasDAO.getClientes(temp);

				Cliente cliente = clientes != null && clientes.size() > 0 ? clientes.get(0) : null;

				if (cliente != null && cliente.getNit() != null) {
					this.cronogramaTransaccion.getEquipo().getCliente().setId(cliente.getId());
					this.cronogramaTransaccion.getEquipo().getCliente().setNit(cliente.getNit());
					this.cronogramaTransaccion.getEquipo().getCliente().setCliente(cliente.getCliente());
					this.cronogramaTransaccion.getEquipo().getCliente().setUbicacion(cliente.getUbicacion());

					this.cronogramaTransaccion.getEquipo().getCliente().settClienteAutocompletado(this.cronogramaTransaccion.getEquipo().getCliente().getCliente() + ", " + this.cronogramaTransaccion.getEquipo().getCliente().getUbicacion());

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
		this.cronogramaConsulta = null;
		this.getCronogramaConsulta();

		this.cronogramas = null;
		this.equipos = null;
	}

	/**
	 * Crea un cronograma
	 */
	public void crearCronograma() {
		Conexion conexion = new Conexion();
		try {
			if (isValidoCronograma("C")) {

				conexion.setAutoCommitBD(false);

				this.cronograma.setFechaDesdeHolgura(this.getFechaDiasSumados(this.cronograma.getFechaProgramacion(), -this.cronograma.getHolgura().intValue()));
				this.cronograma.setFechaHastaHolgura(this.getFechaDiasSumados(this.cronograma.getFechaProgramacion(), this.cronograma.getHolgura()));

				this.cronograma.setEstado(IConstantes.ESTADO_PROGRAMADO);
				this.cronograma.setVersionReporte(IConstantes.VERSION_ACTUAL_REPORTE);

				this.cronograma.getCamposBD();

				conexion.insertarBD(this.cronograma.getEstructuraTabla().getTabla(), this.cronograma.getEstructuraTabla().getPersistencia());

				this.cronograma.setId(conexion.getUltimoSerialTransaccion(this.cronograma.getEstructuraTabla().getTabla()));

				conexion.commitBD();

				this.mostrarMensajeGlobal("creacionExitosa", "exito");
				// reseteo de variables

				this.cronogramaConsulta = null;
				this.getCronogramaConsulta();

				this.cronogramaConsulta.setId(this.cronograma.getId());

				// reseteo de variables
				this.cronograma = null;
				this.getCronograma();
				this.cronogramas = null;
				this.consultarCronograma();

				if (this.cronogramas != null && this.cronogramas.size() > 0) {
					this.cronogramaConsulta.setId(null);
					this.cronogramaConsulta.getEquipo().setCodigoQr(this.cronogramas.get(0).getEquipo().getCodigoQr());

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
	 * Edita un cronograma
	 */
	public void editarCronograma() {
		Conexion conexion = new Conexion();

		try {
			if (isValidoCronograma("E")) {
				conexion.setAutoCommitBD(false);

				this.cronogramaTransaccion.setFechaDesdeHolgura(this.getFechaDiasSumados(this.cronogramaTransaccion.getFechaProgramacion(), -this.cronogramaTransaccion.getHolgura().intValue()));
				this.cronogramaTransaccion.setFechaHastaHolgura(this.getFechaDiasSumados(this.cronogramaTransaccion.getFechaProgramacion(), this.cronogramaTransaccion.getHolgura()));

				this.cronogramaTransaccion.getCamposBD();

				conexion.actualizarBD(this.cronogramaTransaccion.getEstructuraTabla().getTabla(), this.cronogramaTransaccion.getEstructuraTabla().getPersistencia(), this.cronogramaTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

				conexion.commitBD();
				this.mostrarMensajeGlobal("edicionExitosa", "exito");
				this.cerrarModal("panelEdicionCronograma");

				// reseteo de variables
				this.cronogramaTransaccion = null;
				this.getCronogramaTransaccion();
				this.cronogramas = null;
				this.consultarCronograma();

			}

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}

	}

	/**
	 * Aprueba un informe de mantenimiento
	 */
	public void aprobarInformeCronograma() {
		try {
			RealizarMantenimiento mantenimiento = new RealizarMantenimiento();
			mantenimiento.aprobarInformeOtraInterfaz(this.cronogramaTransaccion);

			// reseteo de variables
			this.cronogramaTransaccion = null;
			this.getCronogramaTransaccion();
			this.cronogramas = null;
			this.consultarCronograma();

			this.cerrarModal("panelAprobacionInforme");

		} catch (Exception e) {
			IConstantes.log.error(e, e);
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
		InformeMantenimiento informeMantenimiento = null;
		ActividadNoPreventiva actividadNo = null;
		MaterialFotografico material = null;

		try {
			conexion.setAutoCommitBD(false);

			condiciones = new HashMap<String, Object>();

			// borra las baterias
			bateria = new BateriaMantenimiento();
			bateria.getInformeMantenimiento().getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", bateria.getInformeMantenimiento().getCronograma().getId());
			bateria.getCamposBD();
			conexion.eliminarBD(bateria.getEstructuraTabla().getTabla(), condiciones);

			// borra las actividades
			actividad = new ActividadMantenimiento();
			actividad.getInformeMantenimiento().getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", actividad.getInformeMantenimiento().getCronograma().getId());
			actividad.getCamposBD();
			conexion.eliminarBD(actividad.getEstructuraTabla().getTabla(), condiciones);

			// borra las actividades no preventivas
			actividadNo = new ActividadNoPreventiva();
			actividadNo.getInformeMantenimiento().getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", actividadNo.getInformeMantenimiento().getCronograma().getId());
			actividadNo.getCamposBD();
			conexion.eliminarBD(actividadNo.getEstructuraTabla().getTabla(), condiciones);

			// borra material fotográfico en la nueva versión
			material = new MaterialFotografico();
			material.getInformeMantenimiento().getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", material.getInformeMantenimiento().getCronograma().getId());
			material.getCamposBD();
			conexion.eliminarBD(material.getEstructuraTabla().getTabla(), condiciones);

			// elimina el informe de mantenimeinto realizado a la fecha
			informeMantenimiento = new InformeMantenimiento();
			informeMantenimiento.getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", informeMantenimiento.getCronograma().getId());
			informeMantenimiento.getCamposBD();
			conexion.eliminarBD(informeMantenimiento.getEstructuraTabla().getTabla(), condiciones);

			// actaliza datos del cronograma

			this.cronogramaTransaccion.setEstado(IConstantes.ESTADO_PROGRAMADO);
			this.cronogramaTransaccion.setFechaHoraAtencion(null);
			this.cronogramaTransaccion.getCamposBD();
			conexion.actualizarBD(this.cronogramaTransaccion.getEstructuraTabla().getTabla(), this.cronogramaTransaccion.getEstructuraTabla().getPersistencia(), this.cronogramaTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

			conexion.commitBD();
			this.mostrarMensajeGlobal("mantenimientoEliminado", "exito");

			// reseteo de variables
			this.cronogramaTransaccion = null;
			this.getCronogramaTransaccion();
			this.cronogramas = null;
			this.consultarCronograma();

			this.cerrarModal("panelEliminacionInforme");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
		} finally {
			conexion.cerrarConexion();
		}
	}

	/**
	 * Elimina un cronograma
	 */
	public void eliminarCronograma() {
		Map<String, Object> condiciones = null;
		BateriaMantenimiento bateria = null;
		ActividadMantenimiento actividad = null;
		InformeMantenimiento informeMantenimiento = null;
		ActividadNoPreventiva actividadNo = null;
		MaterialFotografico material = null;
		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);

			// borra las baterias
			bateria = new BateriaMantenimiento();
			bateria.getInformeMantenimiento().getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", bateria.getInformeMantenimiento().getCronograma().getId());
			bateria.getCamposBD();
			conexion.eliminarBD(bateria.getEstructuraTabla().getTabla(), condiciones);

			// borra las actividades
			actividad = new ActividadMantenimiento();
			actividad.getInformeMantenimiento().getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", actividad.getInformeMantenimiento().getCronograma().getId());
			actividad.getCamposBD();
			conexion.eliminarBD(actividad.getEstructuraTabla().getTabla(), condiciones);

			// borra las actividades no preventivas
			actividadNo = new ActividadNoPreventiva();
			actividadNo.getInformeMantenimiento().getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", actividadNo.getInformeMantenimiento().getCronograma().getId());
			actividadNo.getCamposBD();
			conexion.eliminarBD(actividadNo.getEstructuraTabla().getTabla(), condiciones);

			// borra material fotográfico en la nueva versión
			material = new MaterialFotografico();
			material.getInformeMantenimiento().getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", material.getInformeMantenimiento().getCronograma().getId());
			material.getCamposBD();
			conexion.eliminarBD(material.getEstructuraTabla().getTabla(), condiciones);

			// elimina el informe de mantenimeinto realizado a la fecha
			informeMantenimiento = new InformeMantenimiento();
			informeMantenimiento.getCronograma().setId(this.cronogramaTransaccion.getId());
			condiciones = new HashMap<String, Object>();
			condiciones.put("id_cronograma", informeMantenimiento.getCronograma().getId());
			informeMantenimiento.getCamposBD();
			conexion.eliminarBD(informeMantenimiento.getEstructuraTabla().getTabla(), condiciones);

			// actaliza datos del cronograma

			this.cronogramaTransaccion.setEstado(IConstantes.ESTADO_PROGRAMADO);
			this.cronogramaTransaccion.setFechaHoraAtencion(null);
			this.cronogramaTransaccion.getCamposBD();
			conexion.actualizarBD(this.cronogramaTransaccion.getEstructuraTabla().getTabla(), this.cronogramaTransaccion.getEstructuraTabla().getPersistencia(), this.cronogramaTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

			this.cronogramaTransaccion.getCamposBD();
			conexion.eliminarBD(this.cronogramaTransaccion.getEstructuraTabla().getTabla(), this.cronogramaTransaccion.getEstructuraTabla().getLlavePrimaria());
			
			
			conexion.commitBD();
			this.mostrarMensajeGlobal("eliminacionExitosa", "exito");
			this.cerrarModal("panelEliminacionCronograma");

		} catch (Exception e) {
			conexion.rollbackBD();
			this.mostrarMensajeGlobal("transaccionFallida", "error");
			this.mostrarMensajeGlobal("eliminacionFallida", "error");

		} finally {
			conexion.cerrarConexion();
		}

		// reseteo de variables
		this.cronogramaTransaccion = null;
		this.getCronogramaTransaccion();
		this.cronogramas = null;
		this.consultarCronograma();

	}

	/**
	 * Asigna el cronograma para ser realizada una transacción
	 * 
	 * @param aCronograma
	 * @param aVista
	 */
	public void asignarCronograma(Cronograma aCronograma, String aVista) {

		try {

			this.cronogramaTransaccion = aCronograma;

			if (aVista != null && aVista.equals("MODAL_EDITAR_CRONOGRAMA")) {

				this.cronogramaTransaccion.getEquipo().getCliente().settClienteAutocompletado(this.cronogramaTransaccion.getEquipo().getCliente().getCliente() + ", " + this.cronogramaTransaccion.getEquipo().getCliente().getUbicacion());

				this.abrirModal("panelEdicionCronograma");
			} else if (aVista != null && aVista.equals("MODAL_VER_CRONOGRAMA")) {

				this.cronogramaTransaccion.getEquipo().getCliente().settClienteAutocompletado(this.cronogramaTransaccion.getEquipo().getCliente().getCliente() + ", " + this.cronogramaTransaccion.getEquipo().getCliente().getUbicacion());
				this.abrirModal("panelVerCronograma");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_INFORME")) {

				this.cronogramaTransaccion.getEquipo().getCliente().settClienteAutocompletado(this.cronogramaTransaccion.getEquipo().getCliente().getCliente() + ", " + this.cronogramaTransaccion.getEquipo().getCliente().getUbicacion());
				this.abrirModal("panelEliminacionInforme");

			} else if (aVista != null && aVista.equals("MODAL_APROBACION_INFORME")) {

				this.cronogramaTransaccion.getEquipo().getCliente().settClienteAutocompletado(this.cronogramaTransaccion.getEquipo().getCliente().getCliente() + ", " + this.cronogramaTransaccion.getEquipo().getCliente().getUbicacion());
				this.abrirModal("panelAprobacionInforme");

			} else {
				this.cronogramaTransaccion.getEquipo().getCliente().settClienteAutocompletado(this.cronogramaTransaccion.getEquipo().getCliente().getCliente() + ", " + this.cronogramaTransaccion.getEquipo().getCliente().getUbicacion());
				this.abrirModal("panelEliminacionCronograma");
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela la edición o eliminación de un cronogarama
	 * 
	 * @param aVista
	 */
	public void cancelarCronogramaTransaccion(String aVista) {
		try {

			this.cronogramaTransaccion = new Cronograma();
			this.cronogramas = null;
			this.consultarCronograma();

			if (aVista != null && aVista.equals("MODAL_EDITAR_CRONOGRAMA")) {
				this.cerrarModal("panelEdicionCronograma");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_CRONOGRAMA")) {
				this.cerrarModal("panelEliminacionCronograma");

			} else if (aVista != null && aVista.equals("MODAL_APROBACION_INFORME")) {
				this.cerrarModal("panelAprobacionInforme");

			} else if (aVista != null && aVista.equals("MODAL_VER_CRONOGRAMA")) {
				this.cerrarModal("panelVerCronograma");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR_INFORME")) {
				this.cerrarModal("panelEliminacionInforme");

			}

		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Cancela el cronogarama a crear
	 */
	public void cancelarCronograma() {

		try {
			this.cronograma = null;
			this.getCronograma();
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Consulta el cronograma por distintos criterios
	 */
	public void consultarCronograma() {

		try {

			this.cronogramas = IConsultasDAO.getCronograma(this.cronogramaConsulta);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Obtiene un listado de items de técnicos activos
	 * 
	 * @return itemsTecicosActivos
	 */
	public List<SelectItem> getItemsTecicosActivos() {
		try {
			if (this.itemsTecicosActivos == null) {
				this.itemsTecicosActivos = new ArrayList<SelectItem>();
				Tecnico tecnico = new Tecnico();
				tecnico.setEstadoVigencia(IConstantes.ACTIVO);
				List<Tecnico> tecnicos = IConsultasDAO.getTecnicos(tecnico);
				this.itemsTecicosActivos.add(new SelectItem("", this.getMensaje("comboVacio")));
				tecnicos.forEach(p -> this.itemsTecicosActivos.add(new SelectItem(p.getId(), p.getNombres() + " - " + p.getDocumento())));

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsTecicosActivos;
	}

	/**
	 * Establece items de técnicos activos
	 * 
	 * @param itemsTecicosActivos
	 */
	public void setItemsTecicosActivos(List<SelectItem> itemsTecicosActivos) {
		this.itemsTecicosActivos = itemsTecicosActivos;
	}

	/**
	 * Obtiene los tecnicos editar en una transacción
	 * 
	 * @return items
	 */
	public List<SelectItem> getItemsTecnicosEditar() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		try {

			items.add(new SelectItem("", this.getMensaje("comboVacio")));

			Tecnico tecnico = new Tecnico();
			tecnico.setEstadoVigencia(IConstantes.ACTIVO);
			List<Tecnico> tecnicos = IConsultasDAO.getTecnicos(tecnico);

			if (tecnicos != null && tecnicos.size() > 0) {
				for (Tecnico p : tecnicos) {

					items.add(new SelectItem(p.getId(), p.getNombres() + " - " + p.getDocumento()));
				}
			}

			if (this.cronogramaTransaccion != null && this.cronogramaTransaccion.getTecnico() != null && this.cronogramaTransaccion.getTecnico().getEstadoVigencia() != null && this.cronogramaTransaccion.getTecnico().getEstadoVigencia().equals(IConstantes.INACTIVO)) {
				items.add(new SelectItem(this.cronogramaTransaccion.getTecnico().getId(), this.cronogramaTransaccion.getTecnico().getNombres() + " - " + this.cronogramaTransaccion.getTecnico().getDocumento()));
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return items;
	}

	/**
	 * Obtiene items de técnicos activos
	 * 
	 * @return itemsTecnicos
	 */
	public List<SelectItem> getItemsTecnicos() {
		try {
			if (this.itemsTecnicos == null) {
				this.itemsTecnicos = new ArrayList<SelectItem>();
				List<Tecnico> tecnicos = IConsultasDAO.getTecnicos(null);
				this.itemsTecnicos.add(new SelectItem("", this.getMensaje("comboVacio")));
				tecnicos.forEach(p -> this.itemsTecnicos.add(new SelectItem(p.getId(), p.getNombres() + " - " + p.getDocumento())));

			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsTecnicos;
	}

	/**
	 * Establece items de técnicos
	 * 
	 * @param itemsTecnicos
	 */
	public void setItemsTecnicos(List<SelectItem> itemsTecnicos) {
		this.itemsTecnicos = itemsTecnicos;
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

			if (this.cronogramaTransaccion != null && this.cronogramaTransaccion.getEquipo() != null && this.cronogramaTransaccion.getEquipo().getCliente() != null && this.cronogramaTransaccion.getEquipo().getCliente().getId() != null) {
				Equipo equipo = new Equipo();
				equipo.getCliente().setId(this.cronogramaTransaccion.getEquipo().getCliente().getId());
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

			if (this.cronograma != null && this.cronograma.getEquipo() != null && this.cronograma.getEquipo().getCliente() != null && this.cronograma.getEquipo().getCliente().getId() != null) {
				Equipo equipo = new Equipo();
				equipo.getCliente().setId(this.cronograma.getEquipo().getCliente().getId());
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
	 * Obtiene un listado de las fallas disponibles
	 * 
	 * @return itemsFallasDisponibles
	 */
	public List<SelectItem> getItemsFallasDisponibles() {
		try {

			this.itemsFallasDisponibles = new ArrayList<SelectItem>();
			this.itemsFallasDisponibles.add(new SelectItem("", this.getMensaje("comboVacio")));
			if (this.cronograma != null && this.cronograma.getEquipo() != null && this.cronograma.getEquipo().getId() != null) {
				ReporteFalla reporteFalla = new ReporteFalla();
				reporteFalla.getEquipo().setId(this.cronograma.getEquipo().getId());
				List<ReporteFalla> reportes = IConsultasDAO.getReportesFallasDisponibles(reporteFalla);
				reportes.forEach(p -> this.itemsFallasDisponibles.add(new SelectItem(p.getId(), this.getFechaColombia(p.getFechaFalla()) + ", " + p.getDescripcionFalla())));
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return itemsFallasDisponibles;
	}

	/**
	 * Establece un listado de las fallas disponibles
	 * 
	 * @param itemsFallasDisponibles
	 */
	public void setItemsFallasDisponibles(List<SelectItem> itemsFallasDisponibles) {
		this.itemsFallasDisponibles = itemsFallasDisponibles;
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

			if (this.cronogramaConsulta != null && this.cronogramaConsulta.getEquipo() != null && this.cronogramaConsulta.getEquipo().getCliente() != null && this.cronogramaConsulta.getEquipo().getCliente().getId() != null) {
				Equipo equipo = new Equipo();
				equipo.getCliente().setId(this.cronogramaConsulta.getEquipo().getCliente().getId());

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
	 * Arma gráfico de barras cumplimiento de cronograma
	 * 
	 * @return graficoCumplimiento
	 */
	public BarChartModel getGraficoCumplimiento() {
		ChartSeries porcentajeTotal = new ChartSeries();
		ChartSeries porcentajeCumplido = new ChartSeries();

		this.graficoCumplimiento = null;

		if (this.equipos != null && this.equipos.size() > 0) {

			this.graficoCumplimiento = new BarChartModel();

			porcentajeTotal.setLabel(this.getMensaje("porcentajeAlcanzar"));
			porcentajeCumplido.setLabel(this.getMensaje("porcentajeCumplimiento"));

			for (Equipo e : this.equipos) {

				porcentajeTotal.set("" + e.getNombreEquipo() + "," + e.getCodigoQr(), e.gettPorcentajePactado());
				porcentajeCumplido.set("" + e.getNombreEquipo() + "," + e.getCodigoQr(), e.gettPorcentajeCumplimiento());

			}

			this.graficoCumplimiento.addSeries(porcentajeTotal);
			this.graficoCumplimiento.addSeries(porcentajeCumplido);

			this.graficoCumplimiento.setTitle(this.getMensaje("porcentajeCumplimientoCronograma"));
			this.graficoCumplimiento.setAnimate(true);
			this.graficoCumplimiento.setLegendPosition("nw");
			this.graficoCumplimiento.getAxis(AxisType.X).setTickAngle(-30);

		}
		return graficoCumplimiento;
	}

	/**
	 * Establece gráfico de cumplimiento
	 * 
	 * @param graficoCumplimiento
	 */
	public void setGraficoCumplimiento(BarChartModel graficoCumplimiento) {
		this.graficoCumplimiento = graficoCumplimiento;
	}

	/**
	 * Arma los gráficos de barras pero en un sólo método llena los demas
	 * 
	 * @return graficoBarrasDisponibilidad
	 */
	public BarChartModel getGraficoBarrasDisponibilidad() {
		ChartSeries disponibilidad = new ChartSeries();
		ChartSeries mantenibilidad = new ChartSeries();
		ChartSeries confiabilidad = new ChartSeries();
		ChartSeries costos = new ChartSeries();
		this.graficoBarrasDisponibilidad = null;
		this.graficoBarrasMantenibilidad = null;
		this.graficoBarrasConfiabilidad = null;
		this.graficoBarrasCostos = null;

		if (this.equipos != null && this.equipos.size() > 0) {

			this.graficoBarrasDisponibilidad = new BarChartModel();
			this.graficoBarrasMantenibilidad = new BarChartModel();
			this.graficoBarrasConfiabilidad = new BarChartModel();
			this.graficoBarrasCostos = new BarChartModel();

			disponibilidad.setLabel(this.getMensaje("disponibilidad"));
			mantenibilidad.setLabel(this.getMensaje("mantenibilidad"));
			confiabilidad.setLabel(this.getMensaje("confiabilidad"));
			costos.setLabel(this.getMensaje("costos"));

			for (Equipo e : this.equipos) {
				disponibilidad.set("" + e.getNombreEquipo() + "," + e.getCodigoQr(), e.gettDisponibilidad());
				mantenibilidad.set("" + e.getNombreEquipo() + "," + e.getCodigoQr(), e.gettMantenibilidad());
				confiabilidad.set("" + e.getNombreEquipo() + "," + e.getCodigoQr(), e.gettConfiabilidad());
				costos.set("" + e.getNombreEquipo() + "," + e.getCodigoQr(), e.gettCosto());
			}

			this.graficoBarrasDisponibilidad.addSeries(disponibilidad);
			this.graficoBarrasMantenibilidad.addSeries(mantenibilidad);
			this.graficoBarrasConfiabilidad.addSeries(confiabilidad);
			this.graficoBarrasCostos.addSeries(costos);

			this.graficoBarrasDisponibilidad.setTitle(this.getMensaje("indicadoresGestion") + " " + this.getMensaje("disponibilidad"));
			this.graficoBarrasDisponibilidad.setAnimate(true);
			this.graficoBarrasDisponibilidad.setLegendPosition("nw");
			this.graficoBarrasDisponibilidad.getAxis(AxisType.X).setTickAngle(-30);

			this.graficoBarrasMantenibilidad.setTitle(this.getMensaje("indicadoresGestion") + " " + this.getMensaje("mantenibilidad"));
			this.graficoBarrasMantenibilidad.setAnimate(true);
			this.graficoBarrasMantenibilidad.setLegendPosition("nw");
			this.graficoBarrasMantenibilidad.getAxis(AxisType.X).setTickAngle(-30);

			this.graficoBarrasConfiabilidad.setTitle(this.getMensaje("indicadoresGestion") + " " + this.getMensaje("confiabilidad"));
			this.graficoBarrasConfiabilidad.setAnimate(true);
			this.graficoBarrasConfiabilidad.setLegendPosition("nw");
			this.graficoBarrasConfiabilidad.getAxis(AxisType.X).setTickAngle(-30);

			this.graficoBarrasCostos.setTitle(this.getMensaje("indicadoresGestion") + " " + this.getMensaje("costos"));
			this.graficoBarrasCostos.setAnimate(true);
			this.graficoBarrasCostos.setLegendPosition("nw");
			this.graficoBarrasCostos.getAxis(AxisType.X).setTickAngle(-30);

		}
		return graficoBarrasDisponibilidad;
	}

	/**
	 * Establece gráfico indicador
	 * 
	 * @param graficoBarrasDisponibilidad
	 */
	public void setGraficoBarrasDisponibilidad(BarChartModel graficoBarrasDisponibilidad) {
		this.graficoBarrasDisponibilidad = graficoBarrasDisponibilidad;
	}

	public BarChartModel getGraficoBarrasConfiabilidad() {
		return graficoBarrasConfiabilidad;
	}

	public void setGraficoBarrasConfiabilidad(BarChartModel graficoBarrasConfiabilidad) {
		this.graficoBarrasConfiabilidad = graficoBarrasConfiabilidad;
	}

	public BarChartModel getGraficoBarrasMantenibilidad() {
		return graficoBarrasMantenibilidad;
	}

	public void setGraficoBarrasMantenibilidad(BarChartModel graficoBarrasMantenibilidad) {
		this.graficoBarrasMantenibilidad = graficoBarrasMantenibilidad;
	}

	public BarChartModel getGraficoBarrasCostos() {
		return graficoBarrasCostos;
	}

	public void setGraficoBarrasCostos(BarChartModel graficoBarrasCostos) {
		this.graficoBarrasCostos = graficoBarrasCostos;
	}

	public void setCronograma(Cronograma cronograma) {
		this.cronograma = cronograma;
	}

	public Cronograma getCronogramaTransaccion() {
		try {
			if (this.cronogramaTransaccion == null) {
				this.cronogramaTransaccion = new Cronograma();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return cronogramaTransaccion;
	}

	public void setCronogramaTransaccion(Cronograma cronogramaTransaccion) {
		this.cronogramaTransaccion = cronogramaTransaccion;
	}

	public Cronograma getCronogramaConsulta() {
		try {
			if (this.cronogramaConsulta == null) {
				this.cronogramaConsulta = new Cronograma();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return cronogramaConsulta;
	}

	public void setCronogramaConsulta(Cronograma cronogramaConsulta) {
		this.cronogramaConsulta = cronogramaConsulta;
	}

	public List<Cronograma> getCronogramas() {
		return cronogramas;
	}

	public void setCronogramas(List<Cronograma> cronogramas) {
		this.cronogramas = cronogramas;
	}

	public List<Equipo> getEquipos() {
		return equipos;
	}

	public void setEquipos(List<Equipo> equipos) {
		this.equipos = equipos;
	}

	public AdministrarSesionCliente getAdministrarSesionCliente() {
		return administrarSesionCliente;
	}

	public void setAdministrarSesionCliente(AdministrarSesionCliente administrarSesionCliente) {
		this.administrarSesionCliente = administrarSesionCliente;
	}

}
