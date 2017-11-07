package optimo.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import optimo.generales.IConstantes;

public class Equipo implements Serializable {

	/**
	 * 
	 */
	private static final long				serialVersionUID	= -7753739856495852166L;
	private Integer									id;
	private Integer									numeroBaterias;
	private Integer									numeroFases;
	private String									nombreEquipo;
	private String									numeroInventario;
	private String									marca;
	private String									modelo;
	private String									numeroSerie;
	private String									estado;
	private String									codigoQr;
	private String									descripcionEquipo;
	private String									servicio;
	private String									equipo;
	private String									registroInvima;
	private String									datosTecnicos;
	private String									mediciones;
	private String									requiereCalibracion;
	private String									contieneBaterias;
	private String									proveedor;
	private String									telefono;
	private String									correoElectronico;
	private String									equipoBiomedico;
	private String									potencia;
	private String									voltaje;
	private String									corriente;
	private String									frecuencia;
	private String									minimoPresion;
	private String									maximoPresion;
	private String									minimoTemperatura;
	private String									maximoTemperatura;

	private Cliente									cliente;
	private ClasificacionBiomedica	clasificacionBiomedica;
	private ClasificacionRiesgo			clasificacionRiesgo;
	private ClaseSoporteBiomedico		claseSoporteBiomedico;

	private EstructuraTabla					estructuraTabla;

	private String									tCopiaNumeroInventario;
	private String									tCopiaNumeroSerie;

	// private Integer tTiempoFueraServicio; // TFS
	private BigDecimal							tTiempoFueraServicio;
	private Integer									tTiempoParadasProgramadas;								// TPP
	private Integer									tTiempoProgramadoMantenimientoCorrectivo;	// TPMC
	private Integer									tNumeroFallas;														// NF
	private BigDecimal							tDisponibilidad;													// D
	private BigDecimal							tMantenibilidad;													// M
	private BigDecimal							tConfiabilidad;														// C
	private BigDecimal							tCosto;

	private Integer									tTotalIntervencionesProgramadas;
	private Integer									tTotalIntervencionesRealizadas;
	private BigDecimal							tPorcentajeCumplimiento;
	private BigDecimal							tPorcentajePactado;

	private List<Equipo>						tEquiposNuevos;

	public Equipo() {
		this.estructuraTabla = new EstructuraTabla();
		this.cliente = new Cliente();
		this.claseSoporteBiomedico = new ClaseSoporteBiomedico();
		this.clasificacionBiomedica = new ClasificacionBiomedica();
		this.clasificacionRiesgo = new ClasificacionRiesgo();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("equipos");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);

		this.estructuraTabla.getPersistencia().put("nombre_equipo", this.nombreEquipo);
		this.estructuraTabla.getPersistencia().put("numero_inventario", this.numeroInventario);
		this.estructuraTabla.getPersistencia().put("marca", this.marca);
		this.estructuraTabla.getPersistencia().put("modelo", this.modelo);
		this.estructuraTabla.getPersistencia().put("numero_serie", this.numeroSerie);
		this.estructuraTabla.getPersistencia().put("estado", this.estado);
		this.estructuraTabla.getPersistencia().put("codigo_qr", this.codigoQr);
		this.estructuraTabla.getPersistencia().put("descripcion_equipo", this.descripcionEquipo);
		this.estructuraTabla.getPersistencia().put("servicio", this.servicio);
		this.estructuraTabla.getPersistencia().put("equipo", this.equipo);
		this.estructuraTabla.getPersistencia().put("registro_invima", this.registroInvima);
		this.estructuraTabla.getPersistencia().put("datos_tecnicos", this.datosTecnicos);
		this.estructuraTabla.getPersistencia().put("potencia", this.potencia);
		this.estructuraTabla.getPersistencia().put("voltaje", this.voltaje);
		this.estructuraTabla.getPersistencia().put("corriente", this.corriente);
		this.estructuraTabla.getPersistencia().put("frecuencia", this.frecuencia);
		this.estructuraTabla.getPersistencia().put("numero_fases", this.numeroFases);
		this.estructuraTabla.getPersistencia().put("minimo_presion", this.minimoPresion);
		this.estructuraTabla.getPersistencia().put("maximo_presion", this.maximoPresion);
		this.estructuraTabla.getPersistencia().put("minimo_temperatura", this.minimoTemperatura);
		this.estructuraTabla.getPersistencia().put("maximo_temperatura", this.maximoTemperatura);
		this.estructuraTabla.getPersistencia().put("mediciones", this.mediciones);
		this.estructuraTabla.getPersistencia().put("requiere_calibracion", this.requiereCalibracion);
		this.estructuraTabla.getPersistencia().put("contiene_baterias", this.contieneBaterias);
		this.estructuraTabla.getPersistencia().put("numero_baterias", this.numeroBaterias);
		this.estructuraTabla.getPersistencia().put("proveedor", this.proveedor);
		this.estructuraTabla.getPersistencia().put("telefono", this.telefono);
		this.estructuraTabla.getPersistencia().put("correo_electronico", this.correoElectronico);
		this.estructuraTabla.getPersistencia().put("equipo_biomedico", this.equipoBiomedico);

		if (this.cliente != null && this.cliente.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_cliente", this.cliente.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_cliente", this.cliente.getId());
		}

		if (this.claseSoporteBiomedico != null && this.claseSoporteBiomedico.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_clase_soporte_biomedico", this.claseSoporteBiomedico.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_clase_soporte_biomedico", this.claseSoporteBiomedico.getId());
		}

		if (this.clasificacionBiomedica != null && this.clasificacionBiomedica.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_clasificacion_biomedica", this.clasificacionBiomedica.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_clasificacion_biomedica", this.clasificacionBiomedica.getId());
		}

		if (this.clasificacionRiesgo != null && this.clasificacionRiesgo.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_clasificacion_riesgo", this.clasificacionRiesgo.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_clasificacion_riesgo", this.clasificacionRiesgo.getId());
		}

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Min(value = 1, message = IConstantes.VALIDACION_MINIMO_ENTERO)
	public Integer getNumeroBaterias() {
		return numeroBaterias;
	}

	public void setNumeroBaterias(Integer numeroBaterias) {
		this.numeroBaterias = numeroBaterias;
	}

	@Min(value = 0, message = IConstantes.VALIDACION_MINIMO_ENTERO)
	@Max(value = 3, message = IConstantes.VALIDACION_MAXIMO_ENTERO)
	public Integer getNumeroFases() {
		return numeroFases;
	}

	public void setNumeroFases(Integer numeroFases) {
		this.numeroFases = numeroFases;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getNombreEquipo() {
		return nombreEquipo;
	}

	public void setNombreEquipo(String nombreEquipo) {
		this.nombreEquipo = nombreEquipo;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getNumeroInventario() {
		return numeroInventario;
	}

	public void setNumeroInventario(String numeroInventario) {
		this.numeroInventario = numeroInventario;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getCodigoQr() {
		return codigoQr;
	}

	public void setCodigoQr(String codigoQr) {
		this.codigoQr = codigoQr;
	}

	public String getDescripcionEquipo() {
		return descripcionEquipo;
	}

	public void setDescripcionEquipo(String descripcionEquipo) {
		this.descripcionEquipo = descripcionEquipo;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public String getEquipo() {
		return equipo;
	}

	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}

	public String getRegistroInvima() {
		return registroInvima;
	}

	public void setRegistroInvima(String registroInvima) {
		this.registroInvima = registroInvima;
	}

	public String getDatosTecnicos() {
		return datosTecnicos;
	}

	public void setDatosTecnicos(String datosTecnicos) {
		this.datosTecnicos = datosTecnicos;
	}

	public String getMediciones() {
		return mediciones;
	}

	public void setMediciones(String mediciones) {
		this.mediciones = mediciones;
	}

	public String getRequiereCalibracion() {
		return requiereCalibracion;
	}

	public void setRequiereCalibracion(String requiereCalibracion) {
		this.requiereCalibracion = requiereCalibracion;
	}

	public String getContieneBaterias() {
		return contieneBaterias;
	}

	public void setContieneBaterias(String contieneBaterias) {
		this.contieneBaterias = contieneBaterias;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = IConstantes.VALIDACION_EMAIL_INCORRECTO)
	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getEquipoBiomedico() {
		return equipoBiomedico;
	}

	public void setEquipoBiomedico(String equipoBiomedico) {
		this.equipoBiomedico = equipoBiomedico;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getPotencia() {
		return potencia;
	}

	public void setPotencia(String potencia) {
		this.potencia = potencia;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getVoltaje() {
		return voltaje;
	}

	public void setVoltaje(String voltaje) {
		this.voltaje = voltaje;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getCorriente() {
		return corriente;
	}

	public void setCorriente(String corriente) {
		this.corriente = corriente;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getMinimoPresion() {
		return minimoPresion;
	}

	public void setMinimoPresion(String minimoPresion) {
		this.minimoPresion = minimoPresion;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getMaximoPresion() {
		return maximoPresion;
	}

	public void setMaximoPresion(String maximoPresion) {
		this.maximoPresion = maximoPresion;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getMinimoTemperatura() {
		return minimoTemperatura;
	}

	public void setMinimoTemperatura(String minimoTemperatura) {
		this.minimoTemperatura = minimoTemperatura;
	}

	@Size(max = 10, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getMaximoTemperatura() {
		return maximoTemperatura;
	}

	public void setMaximoTemperatura(String maximoTemperatura) {
		this.maximoTemperatura = maximoTemperatura;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public ClasificacionBiomedica getClasificacionBiomedica() {
		return clasificacionBiomedica;
	}

	public void setClasificacionBiomedica(ClasificacionBiomedica clasificacionBiomedica) {
		this.clasificacionBiomedica = clasificacionBiomedica;
	}

	public ClasificacionRiesgo getClasificacionRiesgo() {
		return clasificacionRiesgo;
	}

	public void setClasificacionRiesgo(ClasificacionRiesgo clasificacionRiesgo) {
		this.clasificacionRiesgo = clasificacionRiesgo;
	}

	public ClaseSoporteBiomedico getClaseSoporteBiomedico() {
		return claseSoporteBiomedico;
	}

	public void setClaseSoporteBiomedico(ClaseSoporteBiomedico claseSoporteBiomedico) {
		this.claseSoporteBiomedico = claseSoporteBiomedico;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public String gettCopiaNumeroInventario() {
		return tCopiaNumeroInventario;
	}

	public void settCopiaNumeroInventario(String tCopiaNumeroInventario) {
		this.tCopiaNumeroInventario = tCopiaNumeroInventario;
	}

	public String gettCopiaNumeroSerie() {
		return tCopiaNumeroSerie;
	}

	public void settCopiaNumeroSerie(String tCopiaNumeroSerie) {
		this.tCopiaNumeroSerie = tCopiaNumeroSerie;
	}

	public BigDecimal gettTiempoFueraServicio() {
		return tTiempoFueraServicio;
	}

	public void settTiempoFueraServicio(BigDecimal tTiempoFueraServicio) {
		this.tTiempoFueraServicio = tTiempoFueraServicio;
	}

	public Integer gettTiempoParadasProgramadas() {
		return tTiempoParadasProgramadas;
	}

	public void settTiempoParadasProgramadas(Integer tTiempoParadasProgramadas) {
		this.tTiempoParadasProgramadas = tTiempoParadasProgramadas;
	}

	public Integer gettTiempoProgramadoMantenimientoCorrectivo() {
		return tTiempoProgramadoMantenimientoCorrectivo;
	}

	public void settTiempoProgramadoMantenimientoCorrectivo(Integer tTiempoProgramadoMantenimientoCorrectivo) {
		this.tTiempoProgramadoMantenimientoCorrectivo = tTiempoProgramadoMantenimientoCorrectivo;
	}

	public Integer gettNumeroFallas() {
		return tNumeroFallas;
	}

	public void settNumeroFallas(Integer tNumeroFallas) {
		this.tNumeroFallas = tNumeroFallas;
	}

	public BigDecimal gettDisponibilidad() {
		return tDisponibilidad;
	}

	public void settDisponibilidad(BigDecimal tDisponibilidad) {
		this.tDisponibilidad = tDisponibilidad;
	}

	public BigDecimal gettMantenibilidad() {
		return tMantenibilidad;
	}

	public void settMantenibilidad(BigDecimal tMantenibilidad) {
		this.tMantenibilidad = tMantenibilidad;
	}

	public BigDecimal gettConfiabilidad() {
		return tConfiabilidad;
	}

	public void settConfiabilidad(BigDecimal tConfiabilidad) {
		this.tConfiabilidad = tConfiabilidad;
	}

	public BigDecimal gettCosto() {
		return tCosto;
	}

	public void settCosto(BigDecimal tCosto) {
		this.tCosto = tCosto;
	}

	public Integer gettTotalIntervencionesProgramadas() {
		return tTotalIntervencionesProgramadas;
	}

	public void settTotalIntervencionesProgramadas(Integer tTotalIntervencionesProgramadas) {
		this.tTotalIntervencionesProgramadas = tTotalIntervencionesProgramadas;
	}

	public Integer gettTotalIntervencionesRealizadas() {
		return tTotalIntervencionesRealizadas;
	}

	public void settTotalIntervencionesRealizadas(Integer tTotalIntervencionesRealizadas) {
		this.tTotalIntervencionesRealizadas = tTotalIntervencionesRealizadas;
	}

	public BigDecimal gettPorcentajeCumplimiento() {
		return tPorcentajeCumplimiento;
	}

	public void settPorcentajeCumplimiento(BigDecimal tPorcentajeCumplimiento) {
		this.tPorcentajeCumplimiento = tPorcentajeCumplimiento;
	}

	public BigDecimal gettPorcentajePactado() {
		return tPorcentajePactado;
	}

	public void settPorcentajePactado(BigDecimal tPorcentajePactado) {
		this.tPorcentajePactado = tPorcentajePactado;
	}

	public List<Equipo> gettEquiposNuevos() {
		return tEquiposNuevos;
	}

	public void settEquiposNuevos(List<Equipo> tEquiposNuevos) {
		this.tEquiposNuevos = tEquiposNuevos;
	}

}
