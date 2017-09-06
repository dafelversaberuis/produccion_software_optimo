package optimo.modulos.tablasSoporte;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import optimo.Conexion;
import optimo.beans.ClaseDocumento;
import optimo.beans.ClaseSoporteBiomedico;
import optimo.beans.ClasificacionBiomedica;
import optimo.beans.ClasificacionRiesgo;
import optimo.generales.ConsultarFuncionesAPI;
import optimo.generales.IConstantes;
import optimo.modulos.IConsultasDAO;

@ManagedBean
@ViewScoped
public class HacerMantenimiento extends ConsultarFuncionesAPI implements Serializable {

	/**
	 * 
	 */
	private static final long							serialVersionUID	= 7998118824773496331L;

	private ClaseDocumento								claseDocumento;
	private ClaseDocumento								claseDocumentoTransaccion;

	private ClaseSoporteBiomedico					claseSoporteBiomedico;
	private ClaseSoporteBiomedico					claseSoporteBiomedicoTransaccion;

	private ClasificacionBiomedica				clasificacionBiomedica;
	private ClasificacionBiomedica				clasificacionBiomedicaTransaccion;

	private ClasificacionRiesgo						clasificacionRiesgo;
	private ClasificacionRiesgo						clasificacionRiesgoTransaccion;

	private List<ClaseDocumento>					clasesDocumentos;
	private List<ClaseSoporteBiomedico>		clasesSoportesBiomedicos;
	private List<ClasificacionBiomedica>	clasificacionesBiomedicas;
	private List<ClasificacionRiesgo>			clasificacionesRiesgo;

	// privados

	/**
	 * Asigna memoria y nulea depende del caso
	 * 
	 * @param aTablaSoporte
	 */
	private void asignarMemoria(String aTablaSoporte) {

		if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_DOCUMENTO")) {
			this.claseDocumentoTransaccion = null;
			this.getClaseDocumentoTransaccion();
			this.clasesDocumentos = null;
			this.getClasesDocumentos();

		} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_SOPORTE_BIOMEDICO")) {

			this.claseSoporteBiomedicoTransaccion = null;
			this.getClaseSoporteBiomedicoTransaccion();
			this.clasesSoportesBiomedicos = null;
			this.getClasesSoportesBiomedicos();

		} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_BIOMEDICA")) {

			this.clasificacionBiomedicaTransaccion = null;
			this.getClasificacionBiomedicaTransaccion();
			this.clasificacionesBiomedicas = null;
			this.getClasificacionesBiomedicas();

		} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_RIESGO")) {
			this.clasificacionRiesgoTransaccion = null;
			this.getClasificacionRiesgoTransaccion();
			this.clasificacionesRiesgo = null;
			this.getClasificacionesRiesgo();
		}
	}

	/**
	 * Valida datos llenos de clase documento
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isOkClaseDocumento(String aTransaccion) {
		boolean ok = true;

		if (aTransaccion.equals("C")) {
			if (this.isVacio(this.claseDocumento.getNombre())) {
				ok = false;
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			} else {
				this.claseDocumento.setNombre(this.claseDocumento.getNombre().trim());
			}

		} else {
			if (this.isVacio(this.claseDocumentoTransaccion.getNombre())) {
				ok = false;
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			} else {
				this.claseDocumentoTransaccion.setNombre(this.claseDocumentoTransaccion.getNombre().trim());
			}

		}

		return ok;

	}

	/**
	 * Valida datos llenos de clase soporte biomedico
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isOkClaseSoporteBiomedico(String aTransaccion) {
		boolean ok = true;

		if (aTransaccion.equals("C")) {
			if (this.isVacio(this.claseSoporteBiomedico.getNombre())) {
				ok = false;
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			} else {
				this.claseSoporteBiomedico.setNombre(this.claseSoporteBiomedico.getNombre().trim());
			}

		} else {
			if (this.isVacio(this.claseSoporteBiomedicoTransaccion.getNombre())) {
				ok = false;
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			} else {
				this.claseSoporteBiomedicoTransaccion.setNombre(this.claseSoporteBiomedicoTransaccion.getNombre().trim());
			}

		}

		return ok;

	}

	/**
	 * Valida datos llenos de clasificación biomédica
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isOkClasificacionBiomedica(String aTransaccion) {
		boolean ok = true;

		if (aTransaccion.equals("C")) {
			if (this.isVacio(this.clasificacionBiomedica.getNombre())) {
				ok = false;
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			} else {
				this.clasificacionBiomedica.setNombre(this.clasificacionBiomedica.getNombre().trim());
			}

		} else {
			if (this.isVacio(this.clasificacionBiomedicaTransaccion.getNombre())) {
				ok = false;
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			} else {
				this.clasificacionBiomedicaTransaccion.setNombre(this.clasificacionBiomedicaTransaccion.getNombre().trim());
			}

		}

		return ok;

	}

	/**
	 * Valida datos llenos de clasificación riesgo
	 * 
	 * @param aTransaccion
	 * @return ok
	 */
	private boolean isOkClasificacionRiesgo(String aTransaccion) {
		boolean ok = true;

		if (aTransaccion.equals("C")) {
			if (this.isVacio(this.clasificacionRiesgo.getNombre())) {
				ok = false;
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			} else {
				this.clasificacionRiesgo.setNombre(this.clasificacionRiesgo.getNombre().trim());
			}

		} else {
			if (this.isVacio(this.clasificacionRiesgoTransaccion.getNombre())) {
				ok = false;
				this.mostrarMensajeGlobal("campoEstaVacio", "advertencia");
			} else {
				this.clasificacionRiesgoTransaccion.setNombre(this.clasificacionRiesgoTransaccion.getNombre().trim());
			}

		}

		return ok;

	}

	// publicos

	/**
	 * Asigna una clase de documento
	 * 
	 * @param aClaseDocumento
	 * @param aVista
	 */
	public void asignarClaseDocumento(ClaseDocumento aClaseDocumento, String aVista) {

		try {

			this.claseDocumentoTransaccion = aClaseDocumento;

			if (aVista != null && aVista.equals("MODAL_EDITAR")) {
				this.abrirModal("panelEdicion");

			} else {

				this.abrirModal("panelEliminacion");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna clase de soporte biomédico
	 * 
	 * @param aClaseSoporteBiomedico
	 * @param aVista
	 */
	public void asignarClaseSoporteBiomedico(ClaseSoporteBiomedico aClaseSoporteBiomedico, String aVista) {

		try {

			this.claseSoporteBiomedicoTransaccion = aClaseSoporteBiomedico;

			if (aVista != null && aVista.equals("MODAL_EDITAR")) {
				this.abrirModal("panelEdicion");

			} else {

				this.abrirModal("panelEliminacion");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna clasificación biomédica
	 * 
	 * @param aClasificacionBiomedica
	 * @param aVista
	 */
	public void asignarClasificacionBiomedica(ClasificacionBiomedica aClasificacionBiomedica, String aVista) {

		try {

			this.clasificacionBiomedicaTransaccion = aClasificacionBiomedica;

			if (aVista != null && aVista.equals("MODAL_EDITAR")) {
				this.abrirModal("panelEdicion");

			} else {

				this.abrirModal("panelEliminacion");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Asigna la clasificación riesgo
	 * 
	 * @param aClasificacionRiesgo
	 * @param aVista
	 */
	public void asignarClasificacionRiesgo(ClasificacionRiesgo aClasificacionRiesgo, String aVista) {

		try {

			this.clasificacionRiesgoTransaccion = aClasificacionRiesgo;

			if (aVista != null && aVista.equals("MODAL_EDITAR")) {
				this.abrirModal("panelEdicion");

			} else {

				this.abrirModal("panelEliminacion");

			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	/**
	 * Cancela la edición o eliminación de un objeto de tabla de soporte original
	 * 
	 * @param aVista
	 * @param aTablaSoporte
	 */
	public void cancelarTransaccion(String aVista, String aTablaSoporte) {
		try {

			asignarMemoria(aTablaSoporte);

			if (aVista != null && aVista.equals("MODAL_EDITAR")) {
				this.cerrarModal("panelEdicion");

			} else if (aVista != null && aVista.equals("MODAL_ELIMINAR")) {
				this.cerrarModal("panelEliminacion");

			}

		} catch (Exception e) {

			IConstantes.log.error(e, e);

		}

	}

	/**
	 * Elimina un registro de la tabla soporte
	 * 
	 * @param aTalaSoporte
	 */
	public void eliminarRegistro(String aTablaSoporte) {

		Conexion conexion = new Conexion();
		try {

			conexion.setAutoCommitBD(false);

			if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_DOCUMENTO")) {
				this.claseDocumentoTransaccion.getCamposBD();
				conexion.eliminarBD(this.claseDocumentoTransaccion.getEstructuraTabla().getTabla(), this.claseDocumentoTransaccion.getEstructuraTabla().getLlavePrimaria());

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_SOPORTE_BIOMEDICO")) {

				this.claseSoporteBiomedicoTransaccion.getCamposBD();
				conexion.eliminarBD(this.claseSoporteBiomedicoTransaccion.getEstructuraTabla().getTabla(), this.claseSoporteBiomedicoTransaccion.getEstructuraTabla().getLlavePrimaria());

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_BIOMEDICA")) {

				this.clasificacionBiomedicaTransaccion.getCamposBD();
				conexion.eliminarBD(this.clasificacionBiomedicaTransaccion.getEstructuraTabla().getTabla(), this.clasificacionBiomedicaTransaccion.getEstructuraTabla().getLlavePrimaria());

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_RIESGO")) {
				this.clasificacionRiesgoTransaccion.getCamposBD();
				conexion.eliminarBD(this.clasificacionRiesgoTransaccion.getEstructuraTabla().getTabla(), this.clasificacionRiesgoTransaccion.getEstructuraTabla().getLlavePrimaria());
			}

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
		asignarMemoria(aTablaSoporte);

	}

	/**
	 * Edita un registro de tabla de soporte
	 */
	public void editarRegistro(String aTablaSoporte) {
		Conexion conexion = new Conexion();

		try {

			if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_DOCUMENTO")) {

				if (isOkClaseDocumento("E")) {

					conexion.setAutoCommitBD(false);
					this.claseDocumentoTransaccion.getCamposBD();
					conexion.actualizarBD(this.claseDocumentoTransaccion.getEstructuraTabla().getTabla(), this.claseDocumentoTransaccion.getEstructuraTabla().getPersistencia(), this.claseDocumentoTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

					conexion.commitBD();

					this.mostrarMensajeGlobal("edicionExitosa", "exito");
					this.cerrarModal("panelEdicion");

					// reseteo de variables
					asignarMemoria(aTablaSoporte);
				}

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_SOPORTE_BIOMEDICO")) {

				if (isOkClaseSoporteBiomedico("E")) {
					conexion.setAutoCommitBD(false);
					this.claseSoporteBiomedicoTransaccion.getCamposBD();
					conexion.actualizarBD(this.claseSoporteBiomedicoTransaccion.getEstructuraTabla().getTabla(), this.claseSoporteBiomedicoTransaccion.getEstructuraTabla().getPersistencia(), this.claseSoporteBiomedicoTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

					conexion.commitBD();

					this.mostrarMensajeGlobal("edicionExitosa", "exito");
					this.cerrarModal("panelEdicion");

					// reseteo de variables
					asignarMemoria(aTablaSoporte);

				}

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_BIOMEDICA")) {

				if (isOkClasificacionBiomedica("E")) {
					conexion.setAutoCommitBD(false);
					this.clasificacionBiomedicaTransaccion.getCamposBD();
					conexion.actualizarBD(this.clasificacionBiomedicaTransaccion.getEstructuraTabla().getTabla(), this.clasificacionBiomedicaTransaccion.getEstructuraTabla().getPersistencia(), this.clasificacionBiomedicaTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

					conexion.commitBD();

					this.mostrarMensajeGlobal("edicionExitosa", "exito");
					this.cerrarModal("panelEdicion");

					// reseteo de variables
					asignarMemoria(aTablaSoporte);

				}

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_RIESGO")) {

				if (isOkClasificacionRiesgo("E")) {
					conexion.setAutoCommitBD(false);
					this.clasificacionRiesgoTransaccion.getCamposBD();
					conexion.actualizarBD(this.clasificacionRiesgoTransaccion.getEstructuraTabla().getTabla(), this.clasificacionRiesgoTransaccion.getEstructuraTabla().getPersistencia(), this.clasificacionRiesgoTransaccion.getEstructuraTabla().getLlavePrimaria(), null);

					conexion.commitBD();

					this.mostrarMensajeGlobal("edicionExitosa", "exito");
					this.cerrarModal("panelEdicion");

					// reseteo de variables
					asignarMemoria(aTablaSoporte);

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
	 * Crea un nuevo registro de tabla de soporte
	 */
	public void crearEstado(String aTablaSoporte) {
		Conexion conexion = new Conexion();

		try {

			if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_DOCUMENTO")) {
				if (isOkClaseDocumento("C")) {
					conexion.setAutoCommitBD(false);

					this.claseDocumento.setIndicativoVigencia(IConstantes.ACTIVO);

					this.claseDocumento.getCamposBD();
					conexion.insertarBD(this.claseDocumento.getEstructuraTabla().getTabla(), this.claseDocumento.getEstructuraTabla().getPersistencia());
					conexion.commitBD();

					this.mostrarMensajeGlobal("creacionExitosa", "exito");

					// reseteo de variables
					cancelarRegistro(aTablaSoporte);

				}

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_SOPORTE_BIOMEDICO")) {
				if (isOkClaseSoporteBiomedico("C")) {
					conexion.setAutoCommitBD(false);

					this.claseSoporteBiomedico.setIndicativoVigencia(IConstantes.ACTIVO);

					this.claseSoporteBiomedico.getCamposBD();
					conexion.insertarBD(this.claseSoporteBiomedico.getEstructuraTabla().getTabla(), this.claseSoporteBiomedico.getEstructuraTabla().getPersistencia());
					conexion.commitBD();

					this.mostrarMensajeGlobal("creacionExitosa", "exito");

					// reseteo de variables
					cancelarRegistro(aTablaSoporte);

				}

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_BIOMEDICA")) {
				if (isOkClasificacionBiomedica("C")) {
					conexion.setAutoCommitBD(false);

					this.clasificacionBiomedica.setIndicativoVigencia(IConstantes.ACTIVO);

					this.clasificacionBiomedica.getCamposBD();
					conexion.insertarBD(this.clasificacionBiomedica.getEstructuraTabla().getTabla(), this.clasificacionBiomedica.getEstructuraTabla().getPersistencia());
					conexion.commitBD();

					this.mostrarMensajeGlobal("creacionExitosa", "exito");

					// reseteo de variables
					cancelarRegistro(aTablaSoporte);

				}

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_RIESGO")) {

				if (isOkClasificacionRiesgo("C")) {
					conexion.setAutoCommitBD(false);

					this.clasificacionRiesgo.setIndicativoVigencia(IConstantes.ACTIVO);

					this.clasificacionRiesgo.getCamposBD();
					conexion.insertarBD(this.clasificacionRiesgo.getEstructuraTabla().getTabla(), this.clasificacionRiesgo.getEstructuraTabla().getPersistencia());
					conexion.commitBD();

					this.mostrarMensajeGlobal("creacionExitosa", "exito");

					// reseteo de variables
					cancelarRegistro(aTablaSoporte);

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
	 * Cancela el registro de creación
	 * 
	 * @param aTablaSoporte
	 */
	public void cancelarRegistro(String aTablaSoporte) {

		try {

			if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_DOCUMENTO")) {
				this.claseDocumento = null;
				this.getClaseDocumento();
				this.clasesDocumentos = null;
				this.getClasesDocumentos();

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASE_SOPORTE_BIOMEDICO")) {

				this.claseSoporteBiomedico = null;
				this.getClaseSoporteBiomedico();
				this.clasesSoportesBiomedicos = null;
				this.getClasesSoportesBiomedicos();

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_BIOMEDICA")) {

				this.clasificacionBiomedica = null;
				this.getClasificacionBiomedica();
				this.clasificacionesBiomedicas = null;
				this.getClasificacionesBiomedicas();

			} else if (aTablaSoporte != null && aTablaSoporte.equals("CLASIFICACION_RIESGO")) {
				this.clasificacionRiesgo = null;
				this.getClasificacionRiesgo();
				this.clasificacionesRiesgo = null;
				this.getClasificacionesRiesgo();
			}

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}

	}

	// listados y etructuras

	// gets/sets
	/**
	 * Obtiene objeto para crear un nuevo registro
	 * 
	 * @return claseDocumento
	 */
	public ClaseDocumento getClaseDocumento() {
		try {
			if (this.claseDocumento == null) {
				this.claseDocumento = new ClaseDocumento();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return claseDocumento;
	}

	/**
	 * Establece objeto de la clase documento
	 * 
	 * @param claseDocumento
	 */
	public void setClaseDocumento(ClaseDocumento claseDocumento) {
		this.claseDocumento = claseDocumento;
	}

	/**
	 * Obtiene objeto para realziarle transacciones
	 * 
	 * @return claseDocumentoTransaccion
	 */
	public ClaseDocumento getClaseDocumentoTransaccion() {
		return claseDocumentoTransaccion;
	}

	/**
	 * Establece el objeto
	 * 
	 * @param claseDocumentoTransaccion
	 */
	public void setClaseDocumentoTransaccion(ClaseDocumento claseDocumentoTransaccion) {
		this.claseDocumentoTransaccion = claseDocumentoTransaccion;
	}

	/**
	 * Obtiene objeto para crear registro del mismo
	 * 
	 * @return claseSoporteBiomedico
	 */
	public ClaseSoporteBiomedico getClaseSoporteBiomedico() {
		try {
			if (this.claseSoporteBiomedico == null) {
				this.claseSoporteBiomedico = new ClaseSoporteBiomedico();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return claseSoporteBiomedico;
	}

	/**
	 * Establece objeto
	 * 
	 * @param claseSoporteBiomedico
	 */
	public void setClaseSoporteBiomedico(ClaseSoporteBiomedico claseSoporteBiomedico) {
		this.claseSoporteBiomedico = claseSoporteBiomedico;
	}

	/**
	 * Obtiene objeto para realizar operaciones
	 * 
	 * @return claseSoporteBiomedicoTransaccion
	 */
	public ClaseSoporteBiomedico getClaseSoporteBiomedicoTransaccion() {
		return claseSoporteBiomedicoTransaccion;
	}

	/**
	 * 
	 * @param claseSoporteBiomedicoTransaccion
	 */
	public void setClaseSoporteBiomedicoTransaccion(ClaseSoporteBiomedico claseSoporteBiomedicoTransaccion) {
		this.claseSoporteBiomedicoTransaccion = claseSoporteBiomedicoTransaccion;
	}

	public ClasificacionBiomedica getClasificacionBiomedica() {
		try {
			if (this.clasificacionBiomedica == null) {
				this.clasificacionBiomedica = new ClasificacionBiomedica();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clasificacionBiomedica;
	}

	/**
	 * Establece clasificación biomédica
	 * 
	 * @param clasificacionBiomedica
	 */
	public void setClasificacionBiomedica(ClasificacionBiomedica clasificacionBiomedica) {
		this.clasificacionBiomedica = clasificacionBiomedica;
	}

	/**
	 * Obtiene clasificación biomédica, un objeto para crear
	 * 
	 * @return clasificacionBiomedicaTransaccion
	 */
	public ClasificacionBiomedica getClasificacionBiomedicaTransaccion() {
		return clasificacionBiomedicaTransaccion;
	}

	/**
	 * Establece el objeto en transacción
	 * 
	 * @param clasificacionBiomedicaTransaccion
	 */
	public void setClasificacionBiomedicaTransaccion(ClasificacionBiomedica clasificacionBiomedicaTransaccion) {
		this.clasificacionBiomedicaTransaccion = clasificacionBiomedicaTransaccion;
	}

	/**
	 * Obtiene el objeto para operación de crear
	 * 
	 * @return clasificacionRiesgo
	 */
	public ClasificacionRiesgo getClasificacionRiesgo() {
		try {
			if (this.clasificacionRiesgo == null) {
				this.clasificacionRiesgo = new ClasificacionRiesgo();
			}
		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clasificacionRiesgo;
	}

	/**
	 * Establece el objeto
	 * 
	 * @param clasificacionRiesgo
	 */
	public void setClasificacionRiesgo(ClasificacionRiesgo clasificacionRiesgo) {
		this.clasificacionRiesgo = clasificacionRiesgo;
	}

	/**
	 * Obtiene objeto para operaciones de crud
	 * 
	 * @return clasificacionRiesgoTransaccion
	 */
	public ClasificacionRiesgo getClasificacionRiesgoTransaccion() {
		return clasificacionRiesgoTransaccion;
	}

	/**
	 * Establece objeto para operaciones
	 * 
	 * @param clasificacionRiesgoTransaccion
	 */
	public void setClasificacionRiesgoTransaccion(ClasificacionRiesgo clasificacionRiesgoTransaccion) {
		this.clasificacionRiesgoTransaccion = clasificacionRiesgoTransaccion;
	}

	/**
	 * Obtiene un listado de la tabla soporte indicada
	 * 
	 * @return clasesDocumentos
	 */
	public List<ClaseDocumento> getClasesDocumentos() {
		try {
			this.clasesDocumentos = IConsultasDAO.getClasesDocumentos(null);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clasesDocumentos;
	}

	public void setClasesDocumentos(List<ClaseDocumento> clasesDocumentos) {
		this.clasesDocumentos = clasesDocumentos;
	}

	/**
	 * Obtiene un listado de la tabla soporte indicada
	 * 
	 * @return clasesSoportesBiomedicos
	 */
	public List<ClaseSoporteBiomedico> getClasesSoportesBiomedicos() {
		try {
			this.clasesSoportesBiomedicos = IConsultasDAO.getClasesSoportesBiomedicos(null);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clasesSoportesBiomedicos;
	}

	/**
	 * Establece un listado de la tabla soporte indicada
	 * 
	 * @param clasesSoportesBiomedicos
	 */
	public void setClasesSoportesBiomedicos(List<ClaseSoporteBiomedico> clasesSoportesBiomedicos) {
		this.clasesSoportesBiomedicos = clasesSoportesBiomedicos;
	}

	/**
	 * Obtiene un listado de la tabla soporte indicada
	 * 
	 * @return clasificacionesBiomedicas
	 */
	public List<ClasificacionBiomedica> getClasificacionesBiomedicas() {
		try {
			this.clasificacionesBiomedicas = IConsultasDAO.getClasificacionesBiomedicas(null);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clasificacionesBiomedicas;
	}

	/**
	 * Establece un listado de la tabla soporte indicada
	 * 
	 * @param clasificacionesBiomedicas
	 */
	public void setClasificacionesBiomedicas(List<ClasificacionBiomedica> clasificacionesBiomedicas) {
		this.clasificacionesBiomedicas = clasificacionesBiomedicas;
	}

	/**
	 * Obtiene un listado de la tabla soporte indicada
	 * 
	 * @return clasificacionesRiesgo
	 */
	public List<ClasificacionRiesgo> getClasificacionesRiesgo() {
		try {
			this.clasificacionesRiesgo = IConsultasDAO.getClasificacionesRiesgo(null);

		} catch (Exception e) {
			IConstantes.log.error(e, e);
		}
		return clasificacionesRiesgo;
	}

	/**
	 * Establece un listado de la tabla soporte indicada
	 * 
	 * @param clasificacionesRiesgo
	 */
	public void setClasificacionesRiesgo(List<ClasificacionRiesgo> clasificacionesRiesgo) {
		this.clasificacionesRiesgo = clasificacionesRiesgo;
	}

}
