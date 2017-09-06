package optimo.modulos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import optimo.Conexion;
import optimo.beans.ActividadInformeEquipo;
import optimo.beans.ActividadMantenimiento;
import optimo.beans.ActividadNoPreventiva;
import optimo.beans.Administrador;
import optimo.beans.BateriaMantenimiento;
import optimo.beans.Calibracion;
import optimo.beans.ClaseDocumento;
import optimo.beans.ClaseSoporteBiomedico;
import optimo.beans.ClasificacionBiomedica;
import optimo.beans.ClasificacionRiesgo;
import optimo.beans.Cliente;
import optimo.beans.Cronograma;
import optimo.beans.DocumentoEquipo;
import optimo.beans.Equipo;
import optimo.beans.FotoEquipo;
import optimo.beans.InformeMantenimiento;
import optimo.beans.MaterialFotografico;
import optimo.beans.ReporteFalla;
import optimo.beans.RepuestoEquipo;
import optimo.beans.Tecnico;

public interface IConsultasDAO {

	/**
	 * Obtiene un listado de fotos
	 * 
	 * @return fotos
	 * @throws Exception
	 */
	public static List<FotoEquipo> getFotosEquipos() throws Exception {
		List<FotoEquipo> fotos = new ArrayList<FotoEquipo>();
		List<Object> prametros = new ArrayList<Object>();
		FotoEquipo foto = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.id, encode(archivo::bytea, 'base64') foto_decodificada");
			sql.append("  FROM fotos_equipos p");
			sql.append("  ORDER BY p.id");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				foto = new FotoEquipo();
				foto.setId((Integer) rs.getObject("id"));
				foto.settFotoDecodificada((String) rs.getObject("foto_decodificada"));
				// foto.setArchivo(rs.getBytes("archivo"));
				fotos.add(foto);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return fotos;

	}

	/**
	 * Obtiene un informe de reportes de fall
	 * 
	 * @return reportes
	 * @throws Exception
	 */
	public static List<ReporteFalla> getFallas() throws Exception {
		List<ReporteFalla> reportes = new ArrayList<ReporteFalla>();
		List<Object> prametros = new ArrayList<Object>();
		ReporteFalla reporte = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.id, encode(archivo::bytea, 'base64') foto_decodificada");
			sql.append("  FROM reporte_fallas p");
			sql.append("  ORDER BY p.id");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				reporte = new ReporteFalla();
				reporte.setId((Integer) rs.getObject("id"));
				reporte.settFotoDecodificada((String) rs.getObject("foto_decodificada"));
				// reporte.setArchivo(rs.getBytes("archivo"));
				reportes.add(reporte);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return reportes;

	}

	public static byte[] getArchivo(Integer id) throws Exception {
		byte[] archivo = null;
		List<Object> prametros = new ArrayList<Object>();

		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT archivo");
			sql.append("  FROM actividades_mantenimiento p");
			sql.append("  WHERE p.id =" + id);

			rs = conexion.consultarBD(sql.toString(), prametros);

			if (rs.next()) {

				archivo = rs.getBytes("archivo");

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return archivo;

	}

	/**
	 * Todos las actividades manteniento ordenadas para redimensionar
	 * 
	 * @param aActividadMantenimiento
	 * @return actividades
	 * @throws Exception
	 */
	public static List<ActividadMantenimiento> getActividadesMantenimiento(Integer inicio, Integer fin) throws Exception {
		List<ActividadMantenimiento> actividades = new ArrayList<ActividadMantenimiento>();
		List<Object> prametros = new ArrayList<Object>();
		ActividadMantenimiento actividad = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.id, encode(archivo::bytea, 'base64') foto_decodificada");
			sql.append("  FROM actividades_mantenimiento p");
			sql.append("  WHERE p.id >=" + inicio + " AND p.id <=" + fin);
			sql.append("  ORDER BY p.id");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				actividad = new ActividadMantenimiento();
				actividad.setId((Integer) rs.getObject("id"));
				actividad.settFotoDecodificada((String) rs.getObject("foto_decodificada"));
				// actividad.setArchivo(rs.getBytes("archivo"));
				actividades.add(actividad);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return actividades;

	}

	/**
	 * Obtiene las fotos o material fotografico de un mantenimiento
	 * 
	 * @param aMaterialFotografico
	 * @return materiales
	 * @throws Exception
	 */
	public static List<MaterialFotografico> getMaterialesFotograficos(MaterialFotografico aMaterialFotografico) throws Exception {
		List<MaterialFotografico> materiales = new ArrayList<MaterialFotografico>();
		List<Object> prametros = new ArrayList<Object>();
		MaterialFotografico material = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.titulo, encode(archivo::bytea, 'base64') foto_decodificada , p.id, p.archivo, p.id_cronograma");
			sql.append("  FROM material_fotografico p");
			sql.append("  WHERE 1=1 ");

			if (aMaterialFotografico != null && aMaterialFotografico.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aMaterialFotografico.getId());
			}

			if (aMaterialFotografico != null && aMaterialFotografico.getInformeMantenimiento() != null && aMaterialFotografico.getInformeMantenimiento().getCronograma() != null && aMaterialFotografico.getInformeMantenimiento().getCronograma().getId() != null) {
				sql.append("  AND p.id_cronograma =  ? ");
				prametros.add(aMaterialFotografico.getInformeMantenimiento().getCronograma().getId());
			}

			sql.append("  ORDER BY p.id");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				material = new MaterialFotografico();
				material.setId((Integer) rs.getObject("id"));
				material.setTitulo((String) rs.getObject("titulo"));
				material.settFotoDecodificada((String) rs.getObject("foto_decodificada"));
				material.setArchivo(rs.getBytes("archivo"));
				material.getInformeMantenimiento().getCronograma().setId((Integer) rs.getObject("id_cronograma"));
				materiales.add(material);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return materiales;

	}

	/**
	 * Obtiene de forma no preventiva las actividades creadas
	 * 
	 * @param aActividadMantenimiento
	 * @return actividades
	 * @throws Exception
	 */
	public static List<ActividadNoPreventiva> getActividadesMantenimientoNoPreventiva(ActividadNoPreventiva aActividadMantenimiento) throws Exception {
		List<ActividadNoPreventiva> actividades = new ArrayList<ActividadNoPreventiva>();
		List<Object> prametros = new ArrayList<Object>();
		ActividadNoPreventiva actividad = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.*");
			sql.append("  FROM actividades_no_preventivas p");
			sql.append("  WHERE 1=1 ");

			if (aActividadMantenimiento != null && aActividadMantenimiento.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aActividadMantenimiento.getId());
			}

			if (aActividadMantenimiento != null && aActividadMantenimiento.getInformeMantenimiento() != null && aActividadMantenimiento.getInformeMantenimiento().getCronograma() != null && aActividadMantenimiento.getInformeMantenimiento().getCronograma().getId() != null) {
				sql.append("  AND p.id_cronograma =  ? ");
				prametros.add(aActividadMantenimiento.getInformeMantenimiento().getCronograma().getId());
			}

			sql.append("  ORDER BY p.id");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				actividad = new ActividadNoPreventiva();
				actividad.setId((Integer) rs.getObject("id"));
				actividad.setDescripcionActividad((String) rs.getObject("descripcion_actividad"));
				actividad.setEstadoFinal((String) rs.getObject("estado_final"));
				actividad.setEstadoInicial((String) rs.getObject("estado_inicial"));
				actividad.setObservaciones((String) rs.getObject("observaciones"));
				actividad.getInformeMantenimiento().getCronograma().setId((Integer) rs.getObject("id_cronograma"));

				actividades.add(actividad);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return actividades;

	}

	/**
	 * Obtiene las actividades del mantenimiento
	 * 
	 * @param aActividadMantenimiento
	 * @return actividades
	 * @throws Exception
	 */
	public static List<ActividadMantenimiento> getActividadesMantenimiento(ActividadMantenimiento aActividadMantenimiento) throws Exception {
		List<ActividadMantenimiento> actividades = new ArrayList<ActividadMantenimiento>();
		List<Object> prametros = new ArrayList<Object>();
		ActividadMantenimiento actividad = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.id, p.id_actividad, p.id_cronograma, p.estado_inicial, p.estado_final,p.descripcion, p.recomendaciones, encode(archivo::bytea, 'base64') foto_decodificada , a.actividad, a.posicion, p.archivo");
			sql.append("  FROM actividades_mantenimiento p");
			sql.append("  INNER JOIN actividades_informe_equipo a ON a.id = p.id_actividad");
			sql.append("  WHERE 1=1 ");

			if (aActividadMantenimiento != null && aActividadMantenimiento.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aActividadMantenimiento.getId());
			}

			if (aActividadMantenimiento != null && aActividadMantenimiento.getInformeMantenimiento() != null && aActividadMantenimiento.getInformeMantenimiento().getCronograma() != null && aActividadMantenimiento.getInformeMantenimiento().getCronograma().getId() != null) {
				sql.append("  AND p.id_cronograma =  ? ");
				prametros.add(aActividadMantenimiento.getInformeMantenimiento().getCronograma().getId());
			}

			sql.append("  ORDER BY a.posicion");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				actividad = new ActividadMantenimiento();
				actividad.setId((Integer) rs.getObject("id"));

				actividad.setDescripcion((String) rs.getObject("descripcion"));
				actividad.setEstadoFinal((String) rs.getObject("estado_final"));
				actividad.setEstadoInicial((String) rs.getObject("estado_inicial"));

				actividad.setRecomendaciones((String) rs.getObject("recomendaciones"));
				actividad.settFotoDecodificada((String) rs.getObject("foto_decodificada"));
				actividad.setArchivo(rs.getBytes("archivo"));
				//actividad.setArchivoDecodificado((String) rs.getObject("archivo_decodificado"));

				actividad.getActividadInformeEquipo().setId((Integer) rs.getObject("id_actividad"));
				actividad.getActividadInformeEquipo().setActividad((String) rs.getObject("actividad"));
				actividad.getActividadInformeEquipo().setPosicion((Integer) rs.getObject("posicion"));

				actividad.getInformeMantenimiento().getCronograma().setId((Integer) rs.getObject("id_cronograma"));

				actividades.add(actividad);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return actividades;

	}

	/**
	 * Obtiene las baterias de un mantenimiento
	 * 
	 * @param aBateriaMantenimiento
	 * @return baterias
	 * @throws Exception
	 */
	public static List<BateriaMantenimiento> getBateriasMantenimiento(BateriaMantenimiento aBateriaMantenimiento) throws Exception {
		List<BateriaMantenimiento> baterias = new ArrayList<BateriaMantenimiento>();
		List<Object> prametros = new ArrayList<Object>();
		BateriaMantenimiento bateria = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.*");
			sql.append("  FROM baterias_mantenimiento p");
			sql.append("  WHERE 1=1 ");

			if (aBateriaMantenimiento != null && aBateriaMantenimiento.getInformeMantenimiento() != null && aBateriaMantenimiento.getInformeMantenimiento().getCronograma() != null && aBateriaMantenimiento.getInformeMantenimiento().getCronograma().getId() != null) {
				sql.append("  AND p.id_cronograma =  ? ");
				prametros.add(aBateriaMantenimiento.getInformeMantenimiento().getCronograma().getId());
			}

			sql.append("  ORDER BY numero_bateria");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				bateria = new BateriaMantenimiento();
				bateria.setId((Integer) rs.getObject("id"));
				bateria.setNumeroBateria((Integer) rs.getObject("numero_bateria"));
				bateria.getInformeMantenimiento().getCronograma().setId((Integer) rs.getObject("id_cronograma"));
				bateria.setVoltaje((BigDecimal) rs.getObject("voltaje"));

				baterias.add(bateria);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return baterias;

	}

	/**
	 * Obtiene el informe de mantenimeinto
	 * 
	 * @param aInformeMantenimiento
	 * @return informeMantenimiento
	 * @throws Exception
	 */
	public static InformeMantenimiento getInformeMantenimiento(InformeMantenimiento aInformeMantenimiento) throws Exception {

		List<Object> prametros = new ArrayList<Object>();
		InformeMantenimiento informeMantenimiento = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.*");
			sql.append("  FROM informe_mantenimiento p");
			sql.append("  WHERE 1=1 ");

			if (aInformeMantenimiento != null && aInformeMantenimiento.getCronograma() != null && aInformeMantenimiento.getCronograma().getId() != null) {
				sql.append("  AND p.id_cronograma =  ? ");
				prametros.add(aInformeMantenimiento.getCronograma().getId());
			}

			rs = conexion.consultarBD(sql.toString(), prametros);

			if (rs.next()) {

				informeMantenimiento = new InformeMantenimiento();
				informeMantenimiento.getCronograma().setId((Integer) rs.getObject("id_cronograma"));
				informeMantenimiento.setCorrienteFase((BigDecimal) rs.getObject("corriente_fase"));
				informeMantenimiento.setCorrienteFase1((BigDecimal) rs.getObject("corriente_fase_1"));
				informeMantenimiento.setCorrienteFase2((BigDecimal) rs.getObject("corriente_fase_2"));
				informeMantenimiento.setCorrienteFase3((BigDecimal) rs.getObject("corriente_fase_3"));
				informeMantenimiento.setNumeroFasesMomento((Integer) rs.getObject("numero_fases_momento"));
				informeMantenimiento.setVoltajeFase1A2((BigDecimal) rs.getObject("voltaje_fase_1_2"));
				informeMantenimiento.setVoltajeFase1A3((BigDecimal) rs.getObject("voltaje_fase_1_3"));
				informeMantenimiento.setVoltajeFase1Tierra((BigDecimal) rs.getObject("voltaje_fase_1_tierra"));
				informeMantenimiento.setVoltajeFase2A3((BigDecimal) rs.getObject("voltaje_fase_2_3"));
				informeMantenimiento.setVoltajeFase2Tierra((BigDecimal) rs.getObject("voltaje_fase_2_tierra"));
				informeMantenimiento.setVoltajeFaseNeutro((BigDecimal) rs.getObject("voltaje_fase_neutro"));
				informeMantenimiento.setVoltajeFaseTierra((BigDecimal) rs.getObject("voltaje_fase_tierra"));
				informeMantenimiento.setVoltajeNeutroTierra((BigDecimal) rs.getObject("voltaje_neutro_tierra"));
				informeMantenimiento.setObservacionesGenerales((String) rs.getObject("observaciones_generales"));
				informeMantenimiento.setResponsableClienteMomento((String) rs.getObject("responsable_cliente_momento"));
				informeMantenimiento.setFirmaClienteMomento((String) rs.getObject("firma_cliente_momento"));
				informeMantenimiento.setCargoClienteMomento((String) rs.getObject("cargo_cliente_momento"));

				informeMantenimiento.setRecomendaciones((String) rs.getObject("recomendaciones"));
				informeMantenimiento.setRepuestosRequeridos((String) rs.getObject("repuestos_requeridos"));

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return informeMantenimiento;

	}

	/**
	 * Obtiene los cronogramas de acuerdo a la disponibilidad
	 * 
	 * @param aCronograma
	 * @return cronogramas
	 * @throws Exception
	 */
	public static List<Cronograma> getCronogramasMantenimientos(Cronograma aCronograma) throws Exception {
		List<Cronograma> cronogramas = new ArrayList<Cronograma>();
		List<Object> prametros = new ArrayList<Object>();
		Cronograma cronograma = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT cr.*");
			sql.append("  FROM cronograma cr");
			sql.append("  INNER JOIN equipos eq ON eq.id = cr.id_equipo");
			sql.append("  INNER JOIN clientes cl ON cl.id = eq.id_cliente");
			sql.append("  INNER JOIN tecnicos t ON t.id = cr.id_tecnico");
			sql.append("  WHERE CURRENT_DATE >= cr.fecha_desde_holgura AND CURRENT_DATE <= cr.fecha_hasta_holgura");
			sql.append("  AND cl.estado_vigencia = 'A'");
			sql.append("  AND eq.estado = 'A'");

			// como este es obligatorio no lo condicionamos
			sql.append("  AND cr.id_equipo = ? ");
			prametros.add(aCronograma.getEquipo().getId());

			sql.append("  AND eq.id_cliente = ? ");
			prametros.add(aCronograma.getEquipo().getCliente().getId());

			if (aCronograma != null && aCronograma.getTecnico() != null && aCronograma.getTecnico().getId() != null) {
				sql.append("  AND cr.id_tecnico = ? ");
				prametros.add(aCronograma.getTecnico().getId());
			}

			sql.append("  ORDER BY estado, fecha_programacion, tipo_mantenimiento");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				cronograma = new Cronograma();

				cronograma.setId((Integer) rs.getObject("id"));
				cronograma.setEstado((String) rs.getObject("estado"));
				cronograma.setTipoMantenimiento((String) rs.getObject("tipo_mantenimiento"));
				cronograma.setFechaProgramacion((Date) rs.getObject("fecha_programacion"));
				cronograma.setHolgura((Integer) rs.getObject("holgura"));
				cronograma.setVersionReporte((Integer) rs.getObject("version_reporte"));

				cronogramas.add(cronograma);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return cronogramas;

	}

	/**
	 * Obtiene un listado de los equipos de acuerdo a criterios
	 * 
	 * @param aCronograma
	 * @return equipos
	 * @throws Exception
	 */
	public static List<Equipo> getEquiposCronograma(Cronograma aCronograma) throws Exception {
		List<Equipo> equipos = new ArrayList<Equipo>();
		List<Object> prametros = new ArrayList<Object>();
		Equipo equipo = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT DISTINCT eq.id, eq.nombre_equipo, eq.codigo_qr");
			sql.append("  FROM cronograma cr");
			sql.append("  INNER JOIN equipos eq ON eq.id = cr.id_equipo");
			sql.append("  INNER JOIN clientes cl ON cl.id = eq.id_cliente");
			sql.append("  WHERE CURRENT_DATE >= cr.fecha_desde_holgura AND CURRENT_DATE <= cr.fecha_hasta_holgura");
			sql.append("  AND cl.estado_vigencia = 'A'");
			sql.append("  AND eq.estado = 'A'");

			// como este es obligatorio no lo condicionamos
			sql.append("  AND eq.id_cliente = ? ");
			prametros.add(aCronograma.getEquipo().getCliente().getId());

			if (aCronograma != null && aCronograma.getTecnico() != null && aCronograma.getTecnico().getId() != null) {
				sql.append("  AND cr.id_tecnico = ? ");
				prametros.add(aCronograma.getTecnico().getId());
			}

			sql.append("  ORDER BY nombre_equipo");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				equipo = new Equipo();
				equipo.setId((Integer) rs.getObject("id"));
				equipo.setNombreEquipo((String) rs.getObject("nombre_equipo"));
				equipo.setCodigoQr((String) rs.getObject("codigo_qr"));

				equipos.add(equipo);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return equipos;

	}

	/**
	 * Obtiene un listado con los clientes
	 * 
	 * @param aCronograma
	 * @return clientes
	 * @throws Exception
	 */
	public static List<Cliente> getClientesCronograma(Cronograma aCronograma) throws Exception {
		List<Cliente> clientes = new ArrayList<Cliente>();
		List<Object> prametros = new ArrayList<Object>();
		Cliente cliente = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT DISTINCT cl.id, cl.cliente, cl.nit, cl.ubicacion");
			sql.append("  FROM cronograma cr");
			sql.append("  INNER JOIN equipos eq ON eq.id = cr.id_equipo");
			sql.append("  INNER JOIN clientes cl ON cl.id = eq.id_cliente");
			sql.append("  WHERE CURRENT_DATE >= cr.fecha_desde_holgura AND CURRENT_DATE <= cr.fecha_hasta_holgura");
			sql.append("  AND cl.estado_vigencia = 'A'");
			sql.append("  AND eq.estado = 'A'");

			if (aCronograma != null && aCronograma.getTecnico() != null && aCronograma.getTecnico().getId() != null) {
				sql.append("  AND cr.id_tecnico = ? ");
				prametros.add(aCronograma.getTecnico().getId());
			}

			sql.append("  ORDER BY cliente, ubicacion");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				cliente = new Cliente();
				cliente.setId((Integer) rs.getObject("id"));
				cliente.setCliente((String) rs.getObject("cliente"));
				cliente.setNit((String) rs.getObject("nit"));
				cliente.setUbicacion((String) rs.getObject("ubicacion"));
				clientes.add(cliente);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return clientes;

	}

	/**
	 * Obtiene las fotos de un equipo
	 * 
	 * @param aFotoEquipo
	 * @return fotos
	 * @throws Exception
	 */
	public static List<FotoEquipo> getFotosEquipos(FotoEquipo aFotoEquipo) throws Exception {
		List<FotoEquipo> fotos = new ArrayList<FotoEquipo>();
		List<Object> prametros = new ArrayList<Object>();
		FotoEquipo foto = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.leyenda, p.id, p.id_equipo,  encode(archivo::bytea, 'base64') foto_decodificada");
			sql.append("  FROM fotos_equipos p");
			sql.append("  WHERE 1=1 ");

			if (aFotoEquipo != null && aFotoEquipo.getEquipo() != null && aFotoEquipo.getEquipo().getId() != null) {
				sql.append("  AND p.id_equipo =  ? ");
				prametros.add(aFotoEquipo.getEquipo().getId());
			}

			sql.append("  ORDER BY leyenda");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				foto = new FotoEquipo();
				foto.setId((Integer) rs.getObject("id"));
				foto.setLeyenda((String) rs.getObject("leyenda"));
				foto.getEquipo().setId((Integer) rs.getObject("id_equipo"));
				foto.settFotoDecodificada((String) rs.getObject("foto_decodificada"));

				fotos.add(foto);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return fotos;

	}

	/**
	 * Obtiene el documento de calibración requerido
	 * 
	 * @param aCalibracion
	 * @return calibracion
	 * @throws Exception
	 */
	public static Calibracion getCalibracionAdjunta(Calibracion aCalibracion) throws Exception {

		List<Object> prametros = new ArrayList<Object>();
		Calibracion calibracion = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT archivo, p.content_type, p.extension_archivo");
			sql.append("  FROM calibraciones p");
			sql.append("  WHERE 1=1 ");

			if (aCalibracion != null && aCalibracion.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aCalibracion.getId());
			}

			rs = conexion.consultarBD(sql.toString(), prametros);

			if (rs.next()) {

				calibracion = new Calibracion();
				calibracion.setArchivo(rs.getBytes("archivo"));
				calibracion.setContentType((String) rs.getObject("content_type"));
				calibracion.setExtensionArchivo((String) rs.getObject("extension_archivo"));

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return calibracion;

	}

	/**
	 * Obtiene el adjunto de un documento si éste existe
	 * 
	 * @param aDocumento
	 * @return documento
	 * @throws Exception
	 */
	public static DocumentoEquipo getAdjuntoDocumento(DocumentoEquipo aDocumento) throws Exception {

		List<Object> prametros = new ArrayList<Object>();
		DocumentoEquipo documento = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT archivo, p.content_type, p.extension_archivo");
			sql.append("  FROM documentos_equipos p");
			sql.append("  WHERE 1=1 ");

			if (aDocumento != null && aDocumento.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aDocumento.getId());
			}

			rs = conexion.consultarBD(sql.toString(), prametros);

			if (rs.next()) {

				documento = new DocumentoEquipo();
				documento.setArchivo(rs.getBytes("archivo"));
				documento.setContentType((String) rs.getObject("content_type"));
				documento.setExtensionArchivo((String) rs.getObject("extension_archivo"));

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return documento;

	}

	/**
	 * Obtiene las actividades según consulta y transacción activa
	 * 
	 * @param conexion
	 * @param aActividadInformeEquipo
	 * @return actividades
	 * @throws Exception
	 */
	public static List<ActividadInformeEquipo> getActividades(Conexion conexion, ActividadInformeEquipo aActividadInformeEquipo) throws Exception {
		List<ActividadInformeEquipo> actividades = new ArrayList<ActividadInformeEquipo>();
		List<Object> prametros = new ArrayList<Object>();
		ActividadInformeEquipo actividad = null;

		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM actividades_informe_equipo p");
			sql.append("  WHERE 1=1 ");

			if (aActividadInformeEquipo != null && aActividadInformeEquipo.getEquipo() != null && aActividadInformeEquipo.getEquipo().getId() != null) {
				sql.append("  AND p.id_equipo =  ? ");
				prametros.add(aActividadInformeEquipo.getEquipo().getId());
			}

			sql.append("  ORDER BY posicion");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				actividad = new ActividadInformeEquipo();
				actividad.setId((Integer) rs.getObject("id"));
				actividad.setActividad((String) rs.getObject("actividad"));
				actividad.setIndicativoVigencia((String) rs.getObject("indicativo_vigencia"));
				actividad.setPosicion((Integer) rs.getObject("posicion"));
				actividad.getEquipo().setId((Integer) rs.getObject("id_equipo"));

				actividades.add(actividad);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}

		}
		return actividades;

	}

	/**
	 * Obtiene las actividades de un equipo en particular
	 * 
	 * @param aActividadInformeEquipo
	 * @return actividades
	 * @throws Exception
	 */
	public static List<ActividadInformeEquipo> getActividades(ActividadInformeEquipo aActividadInformeEquipo) throws Exception {
		List<ActividadInformeEquipo> actividades = new ArrayList<ActividadInformeEquipo>();
		List<Object> prametros = new ArrayList<Object>();
		ActividadInformeEquipo actividad = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM actividades_informe_equipo p");
			sql.append("  WHERE 1=1 ");

			if (aActividadInformeEquipo != null && aActividadInformeEquipo.getEquipo() != null && aActividadInformeEquipo.getEquipo().getId() != null) {
				sql.append("  AND p.id_equipo =  ? ");
				prametros.add(aActividadInformeEquipo.getEquipo().getId());
			}

			sql.append("  ORDER BY posicion");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				actividad = new ActividadInformeEquipo();
				actividad.setId((Integer) rs.getObject("id"));
				actividad.setActividad((String) rs.getObject("actividad"));
				actividad.setIndicativoVigencia((String) rs.getObject("indicativo_vigencia"));
				actividad.setPosicion((Integer) rs.getObject("posicion"));
				actividad.getEquipo().setId((Integer) rs.getObject("id_equipo"));

				actividades.add(actividad);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return actividades;

	}

	/**
	 * Obtiene las calibraciones a mostrar
	 * 
	 * @param aCalibarcion
	 * @return calibraciones
	 * @throws Exception
	 */
	public static List<Calibracion> getCalibraciones(Calibracion aCalibarcion) throws Exception {
		List<Calibracion> calibraciones = new ArrayList<Calibracion>();
		List<Object> prametros = new ArrayList<Object>();
		Calibracion calibracion = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.id, p.fecha_calibracion, p.id_equipo, p.firma_cliente_momento");
			sql.append("  FROM calibraciones p");
			sql.append("  WHERE 1=1 ");

			if (aCalibarcion != null && aCalibarcion.getEquipo() != null && aCalibarcion.getEquipo().getId() != null) {
				sql.append("  AND p.id_equipo =  ? ");
				prametros.add(aCalibarcion.getEquipo().getId());
			}

			sql.append("  ORDER BY fecha_calibracion");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				calibracion = new Calibracion();
				calibracion.setId((Integer) rs.getObject("id"));
				calibracion.getEquipo().setId((Integer) rs.getObject("id_equipo"));
				calibracion.setFirmaClienteMomento((String) rs.getObject("firma_cliente_momento"));
				calibracion.setFechaCalibracion((Date) rs.getObject("fecha_calibracion"));

				calibraciones.add(calibracion);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return calibraciones;

	}

	/**
	 * Obtiene los documentos pero no precarag el archiv o hasta que se haga click
	 * 
	 * @param aDocumento
	 * @return documentos
	 * @throws Exception
	 */
	public static List<DocumentoEquipo> getDocumentos(DocumentoEquipo aDocumento) throws Exception {
		List<DocumentoEquipo> documentos = new ArrayList<DocumentoEquipo>();
		List<Object> prametros = new ArrayList<Object>();
		DocumentoEquipo documento = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.id, p.nombre, p.id_clase_documento, p.posee_documento, p.id_equipo, c.nombre nommbre_documento");
			sql.append("  FROM documentos_equipos p");
			sql.append("  INNER JOIN clases_documentos c ON p.id_clase_documento = c.id");
			sql.append("  WHERE 1=1 ");

			if (aDocumento != null && aDocumento.getEquipo() != null && aDocumento.getEquipo().getId() != null) {
				sql.append("  AND p.id_equipo =  ? ");
				prametros.add(aDocumento.getEquipo().getId());
			}

			sql.append("  ORDER BY nombre");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				documento = new DocumentoEquipo();
				documento.setId((Integer) rs.getObject("id"));
				documento.setNombre((String) rs.getObject("nombre"));
				documento.setPoseeDocumento((String) rs.getObject("posee_documento"));
				documento.getEquipo().setId((Integer) rs.getObject("id_equipo"));

				documento.getClaseDocumento().setId((Integer) rs.getObject("id_clase_documento"));
				documento.getClaseDocumento().setNombre((String) rs.getObject("nommbre_documento"));

				documentos.add(documento);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return documentos;

	}

	/**
	 * Obtiene los repuestos de un equipo
	 * 
	 * @param aRepuestoEquipo
	 * @return repuestos
	 * @throws Exception
	 */
	public static List<RepuestoEquipo> getRepuestos(RepuestoEquipo aRepuestoEquipo) throws Exception {
		List<RepuestoEquipo> repuestos = new ArrayList<RepuestoEquipo>();
		List<Object> prametros = new ArrayList<Object>();
		RepuestoEquipo repuesto = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM repuestos_equipos p");
			sql.append("  WHERE 1=1 ");

			if (aRepuestoEquipo != null && aRepuestoEquipo.getEquipo() != null && aRepuestoEquipo.getEquipo().getId() != null) {
				sql.append("  AND p.id_equipo =  ? ");
				prametros.add(aRepuestoEquipo.getEquipo().getId());
			}

			sql.append("  ORDER BY nombre");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				repuesto = new RepuestoEquipo();
				repuesto.setId((Integer) rs.getObject("id"));
				repuesto.setDescripcion((String) rs.getObject("descripcion"));
				repuesto.setMarca((String) rs.getObject("marca"));
				repuesto.setModelo((String) rs.getObject("modelo"));
				repuesto.setNombre((String) rs.getObject("nombre"));
				repuesto.setPeriodicidad((String) rs.getObject("periodicidad"));
				repuesto.setUbicacion((String) rs.getObject("ubicacion"));
				repuesto.getEquipo().setId((Integer) rs.getObject("id_equipo"));
				repuesto.setValorPeriodicidad((Integer) rs.getObject("valor_periodicidad"));

				repuestos.add(repuesto);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return repuestos;

	}

	/**
	 * Obtiene los reportes de fallas disponibles
	 * 
	 * @return reportes
	 * @throws Exception
	 */
	public static List<ReporteFalla> getReportesFallasDisponibles(ReporteFalla aReporteFalla) throws Exception {
		List<ReporteFalla> reportes = new ArrayList<ReporteFalla>();
		List<Object> prametros = new ArrayList<Object>();
		ReporteFalla reporte = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.id, p.descripcion_falla, p.fecha_falla");
			sql.append("  FROM reporte_fallas p");
			sql.append("  WHERE p.estado = 'A'");
			sql.append("  AND p.id_equipo = ?");
			sql.append("  AND   p.id NOT IN(");
			sql.append("  									SELECT c.id_reporte_falla");
			sql.append("  									FROM cronograma c");
			sql.append("  									WHERE c.id_reporte_falla IS NOT NULL");
			sql.append("  									AND c.id_equipo = ?");
			sql.append("  								 )");

			sql.append("  ORDER BY p.fecha_falla");

			prametros.add(aReporteFalla.getEquipo().getId());
			prametros.add(aReporteFalla.getEquipo().getId());

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				reporte = new ReporteFalla();

				reporte.setId((Integer) rs.getObject("id"));
				reporte.setDescripcionFalla((String) rs.getObject("descripcion_falla"));
				reporte.setFechaFalla((Date) rs.getObject("fecha_falla"));

				reportes.add(reporte);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return reportes;

	}

	/**
	 * Obtiene un listado de reportes de fallas
	 * 
	 * @param aReporteFalla
	 * @return reportes
	 * @throws Exception
	 */
	public static List<ReporteFalla> getReportesFallas(ReporteFalla aReporteFalla) throws Exception {
		List<ReporteFalla> reportes = new ArrayList<ReporteFalla>();
		List<Object> prametros = new ArrayList<Object>();
		ReporteFalla reporte = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.*, c.nit, c.cliente, c.ubicacion, c.estado_vigencia, e.codigo_qr, e.nombre_equipo, e.estado estado_vigencia_equipo, e.id_cliente, c.representante, c.firma, c.cargo, encode(archivo::bytea, 'base64') foto_decodificada");
			sql.append("  FROM reporte_fallas p");
			sql.append("  INNER JOIN equipos e ON p.id_equipo = e.id");
			sql.append("  INNER JOIN clientes c ON e.id_cliente = c.id");
			sql.append("  WHERE 1=1 ");

			if (aReporteFalla != null && aReporteFalla.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aReporteFalla.getId());
			}

			if (aReporteFalla != null && aReporteFalla.getEquipo().getCliente() != null && aReporteFalla.getEquipo().getCliente().getId() != null) {
				sql.append("  AND e.id_cliente =  ? ");
				prametros.add(aReporteFalla.getEquipo().getCliente().getId());
			}

			if (aReporteFalla != null && aReporteFalla.getEquipo() != null && aReporteFalla.getEquipo().getId() != null) {
				sql.append("  AND p.id_equipo =  ? ");
				prametros.add(aReporteFalla.getEquipo().getId());
			}

			if (aReporteFalla != null && aReporteFalla.getEquipo() != null && aReporteFalla.getEquipo().getNombreEquipo() != null && !aReporteFalla.getEquipo().getNombreEquipo().trim().equals("")) {
				sql.append("  AND UPPER(e.nombre_equipo) LIKE  ? ");
				prametros.add("%" + aReporteFalla.getEquipo().getNombreEquipo().trim().toUpperCase() + "%");
			}

			if (aReporteFalla != null && aReporteFalla.getEquipo() != null && aReporteFalla.getEquipo().getCodigoQr() != null && !aReporteFalla.getEquipo().getCodigoQr().trim().equals("")) {
				sql.append("  AND e.codigo_qr =  ? ");
				prametros.add(aReporteFalla.getEquipo().getCodigoQr().trim());
			}

			if (aReporteFalla != null && aReporteFalla.getEstado() != null && !aReporteFalla.getEstado().equals("")) {
				sql.append("  AND p.estado =  ? ");
				prametros.add(aReporteFalla.getEstado());
			}

			if (aReporteFalla != null && aReporteFalla.gettFechaDesde() != null) {
				sql.append("  AND p.fecha_falla >=  ? ");
				prametros.add(aReporteFalla.gettFechaDesde());
			}

			if (aReporteFalla != null && aReporteFalla.gettFechaHasta() != null) {
				sql.append("  AND p.fecha_falla <=  ? ");
				prametros.add(aReporteFalla.gettFechaHasta());
			}

			if (aReporteFalla != null && aReporteFalla.getFechaFalla() != null) {
				sql.append("  AND p.fecha_falla =  ? ");
				prametros.add(aReporteFalla.getFechaFalla());
			}

			sql.append("  ORDER BY p.fecha_falla, c.cliente, e.nombre_equipo");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				reporte = new ReporteFalla();

				reporte.setId((Integer) rs.getObject("id"));

				reporte.setDescripcionFalla((String) rs.getObject("descripcion_falla"));
				reporte.setConceptoCierreManual((String) rs.getObject("concepto_cierre_manual"));
				reporte.setEstado((String) rs.getObject("estado"));
				reporte.setFechaHoraAtencion((Date) rs.getObject("fecha_atencion"));
				reporte.setFechaFalla((Date) rs.getObject("fecha_falla"));
				// reporte.setArchivo(rs.getBytes("archivo")); //se quita porque la
				// consulta solo muestra la foto decodificada
				reporte.settFotoDecodificada((String) rs.getObject("foto_decodificada"));

				reporte.getEquipo().setId((Integer) rs.getObject("id_equipo"));
				reporte.getEquipo().setNombreEquipo((String) rs.getObject("nombre_equipo"));
				reporte.getEquipo().setEstado((String) rs.getObject("estado_vigencia_equipo"));
				reporte.getEquipo().setCodigoQr((String) rs.getObject("codigo_qr"));

				reporte.getEquipo().getCliente().setNit((String) rs.getObject("nit"));
				reporte.getEquipo().getCliente().setCliente((String) rs.getObject("cliente"));
				reporte.getEquipo().getCliente().setUbicacion((String) rs.getObject("ubicacion"));
				reporte.getEquipo().getCliente().setEstadoVigencia((String) rs.getObject("estado_vigencia"));
				reporte.getEquipo().getCliente().setId((Integer) rs.getObject("id_cliente"));
				reporte.getEquipo().getCliente().setRepresentante((String) rs.getObject("representante"));
				reporte.getEquipo().getCliente().setFirma((String) rs.getObject("firma"));
				reporte.getEquipo().getCliente().setCargo((String) rs.getObject("cargo"));

				reportes.add(reporte);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return reportes;

	}

	/**
	 * Obtiene el cronograma por distintos criterios
	 * 
	 * @param aCronograma
	 * @return cronogramas
	 * @throws Exception
	 */
	public static List<Cronograma> getCronograma(Cronograma aCronograma) throws Exception {
		List<Cronograma> cronogramas = new ArrayList<Cronograma>();
		List<Object> prametros = new ArrayList<Object>();
		Cronograma cronograma = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.*, c.nit, c.cliente, c.ubicacion, c.estado_vigencia, e.codigo_qr, e.nombre_equipo, e.estado estado_vigencia_equipo, t.nombres, t.documento, e.id_cliente, c.representante, c.firma, c.cargo, t.cargo cargo_tecnico, t.firma firma_tecnico, p.id_reporte_falla, r.fecha_falla, r.descripcion_falla");
			sql.append("  FROM cronograma p");
			sql.append("  INNER JOIN equipos e ON p.id_equipo = e.id");
			sql.append("  INNER JOIN clientes c ON e.id_cliente = c.id");
			sql.append("  INNER JOIN tecnicos t ON p.id_tecnico = t.id");
			sql.append("  LEFT  JOIN reporte_fallas r ON p.id_reporte_falla = r.id");
			sql.append("  WHERE 1=1 ");

			if (aCronograma != null && aCronograma.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aCronograma.getId());
			}

			if (aCronograma != null && aCronograma.getEquipo().getCliente() != null && aCronograma.getEquipo().getCliente().getId() != null) {
				sql.append("  AND e.id_cliente =  ? ");
				prametros.add(aCronograma.getEquipo().getCliente().getId());
			}

			if (aCronograma != null && aCronograma.getEquipo() != null && aCronograma.getEquipo().getId() != null) {
				sql.append("  AND p.id_equipo =  ? ");
				prametros.add(aCronograma.getEquipo().getId());
			}

			if (aCronograma != null && aCronograma.getEquipo() != null && aCronograma.getEquipo().getNombreEquipo() != null && !aCronograma.getEquipo().getNombreEquipo().trim().equals("")) {
				sql.append("  AND UPPER(e.nombre_equipo) LIKE  ? ");
				prametros.add("%" + aCronograma.getEquipo().getNombreEquipo().trim().toUpperCase() + "%");
			}

			if (aCronograma != null && aCronograma.getEquipo() != null && aCronograma.getEquipo().getCodigoQr() != null && !aCronograma.getEquipo().getCodigoQr().trim().equals("")) {
				sql.append("  AND e.codigo_qr =  ? ");
				prametros.add(aCronograma.getEquipo().getCodigoQr().trim());
			}

			if (aCronograma != null && aCronograma.getEstado() != null && !aCronograma.getEstado().equals("")) {
				sql.append("  AND p.estado =  ? ");
				prametros.add(aCronograma.getEstado());
			}

			if (aCronograma != null && aCronograma.getTecnico() != null && aCronograma.getTecnico().getId() != null) {
				sql.append("  AND p.id_tecnico =  ? ");
				prametros.add(aCronograma.getTecnico().getId());
			}

			if (aCronograma != null && aCronograma.gettFechaDesde() != null) {
				sql.append("  AND p.fecha_programacion >=  ? ");
				prametros.add(aCronograma.gettFechaDesde());
			}

			if (aCronograma != null && aCronograma.gettFechaHasta() != null) {
				sql.append("  AND p.fecha_programacion <=  ? ");
				prametros.add(aCronograma.gettFechaHasta());
			}

			if (aCronograma != null && aCronograma.getFechaProgramacion() != null) {
				sql.append("  AND p.fecha_programacion =  ? ");
				prametros.add(aCronograma.getFechaProgramacion());
			}

			if (aCronograma != null && aCronograma.getTipoMantenimiento() != null && !aCronograma.getTipoMantenimiento().equals("")) {
				sql.append("  AND p.tipo_mantenimiento =  ? ");
				prametros.add(aCronograma.getTipoMantenimiento());
			}

			sql.append("  ORDER BY p.fecha_programacion, c.cliente, e.nombre_equipo");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {

				cronograma = new Cronograma();

				cronograma.setId((Integer) rs.getObject("id"));

				cronograma.setCosto((BigDecimal) rs.getObject("costo"));
				cronograma.setDuracion((Integer) rs.getObject("duracion"));
				cronograma.setEstado((String) rs.getObject("estado"));
				cronograma.setTipoMantenimiento((String) rs.getObject("tipo_mantenimiento"));
				cronograma.setFechaDesdeHolgura((Date) rs.getObject("fecha_desde_holgura"));
				cronograma.setFechaHastaHolgura((Date) rs.getObject("fecha_hasta_holgura"));
				cronograma.setFechaHoraAprobacionCliente((Date) rs.getObject("fecha_hora_aprobacion_cliente"));
				cronograma.setFechaHoraAtencion((Date) rs.getObject("fecha_hora_atencion"));
				cronograma.setFechaProgramacion((Date) rs.getObject("fecha_programacion"));
				cronograma.setHolgura((Integer) rs.getObject("holgura"));
				cronograma.setVersionReporte((Integer) rs.getObject("version_reporte"));

				cronograma.getEquipo().setNombreEquipo((String) rs.getObject("nombre_equipo"));
				cronograma.getEquipo().setEstado((String) rs.getObject("estado_vigencia_equipo"));
				cronograma.getEquipo().setCodigoQr((String) rs.getObject("codigo_qr"));
				cronograma.getEquipo().setId((Integer) rs.getObject("id_equipo"));

				cronograma.getEquipo().getCliente().setNit((String) rs.getObject("nit"));
				cronograma.getEquipo().getCliente().setCliente((String) rs.getObject("cliente"));
				cronograma.getEquipo().getCliente().setUbicacion((String) rs.getObject("ubicacion"));
				cronograma.getEquipo().getCliente().setEstadoVigencia((String) rs.getObject("estado_vigencia"));
				cronograma.getEquipo().getCliente().setId((Integer) rs.getObject("id_cliente"));
				cronograma.getEquipo().getCliente().setRepresentante((String) rs.getObject("representante"));
				cronograma.getEquipo().getCliente().setFirma((String) rs.getObject("firma"));
				cronograma.getEquipo().getCliente().setCargo((String) rs.getObject("cargo"));

				cronograma.getTecnico().setId((Integer) rs.getObject("id_tecnico"));
				cronograma.getTecnico().setNombres((String) rs.getObject("nombres"));
				cronograma.getTecnico().setDocumento((String) rs.getObject("documento"));
				cronograma.getTecnico().setCargo((String) rs.getObject("cargo_tecnico"));
				cronograma.getTecnico().setFirma((String) rs.getObject("firma_tecnico"));

				cronograma.getReporteFalla().setId((Integer) rs.getObject("id_reporte_falla"));
				cronograma.getReporteFalla().setDescripcionFalla((String) rs.getObject("descripcion_falla"));
				cronograma.getReporteFalla().setFechaFalla((Date) rs.getObject("fecha_falla"));

				cronogramas.add(cronograma);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return cronogramas;

	}

	/**
	 * Obtiene un listado de equipos de acuerdo a criterios de consulta
	 * 
	 * @param aEquipo
	 * @return equipos
	 * @throws Exception
	 */
	public static List<Equipo> getEquipos(Equipo aEquipo, Conexion conexion) throws Exception {
		List<Equipo> equipos = new ArrayList<Equipo>();
		List<Object> prametros = new ArrayList<Object>();
		Equipo equipo = null;

		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.*, c.nit, c.cliente, c.ubicacion, c.estado_vigencia, cr.indicativo_vigencia vigencia_cr, cb.indicativo_vigencia vigencia_cb,  sb.indicativo_vigencia vigencia_sb, cr.nombre nombre_cr, sb.nombre nombre_sb, cb.nombre nombre_cb, c.representante, c.firma, c.cargo, c.correo_electronico correo_cliente");
			sql.append("  FROM equipos p");
			sql.append("  INNER JOIN clientes c ON p.id_cliente = c.id");
			sql.append("  LEFT JOIN clasificaciones_biomedicas cb ON p.id_clasificacion_biomedica = cb.id");
			sql.append("  LEFT JOIN clasificaciones_riesgo cr ON p.id_clasificacion_riesgo = cr.id");
			sql.append("  LEFT JOIN clases_soportes_biomedicos sb ON p.id_clase_soporte_biomedico = sb.id");
			sql.append("  WHERE 1=1 ");

			if (aEquipo != null && aEquipo.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aEquipo.getId());
			}

			if (aEquipo != null && aEquipo.getCliente() != null && aEquipo.getCliente().getId() != null) {
				sql.append("  AND p.id_cliente =  ? ");
				prametros.add(aEquipo.getCliente().getId());
			}

			if (aEquipo != null && aEquipo.getNumeroSerie() != null && !aEquipo.getNumeroSerie().trim().equals("")) {
				sql.append("  AND p.numero_serie =  ? ");
				prametros.add(aEquipo.getNumeroSerie().trim());
			}

			if (aEquipo != null && aEquipo.getNumeroInventario() != null && !aEquipo.getNumeroInventario().trim().equals("")) {
				sql.append("  AND p.numero_inventario =  ? ");
				prametros.add(aEquipo.getNumeroInventario().trim());
			}

			if (aEquipo != null && aEquipo.getMediciones() != null && !aEquipo.getMediciones().trim().equals("")) {
				sql.append("  AND p.mediciones =  ? ");
				prametros.add(aEquipo.getDatosTecnicos().trim());
			}

			if (aEquipo != null && aEquipo.getDatosTecnicos() != null && !aEquipo.getDatosTecnicos().trim().equals("")) {
				sql.append("  AND p.datos_tecnicos =  ? ");
				prametros.add(aEquipo.getDatosTecnicos().trim());
			}

			if (aEquipo != null && aEquipo.getRequiereCalibracion() != null && !aEquipo.getRequiereCalibracion().trim().equals("")) {
				sql.append("  AND p.requiere_calibracion =  ? ");
				prametros.add(aEquipo.getRequiereCalibracion().trim());
			}

			if (aEquipo != null && aEquipo.getEquipoBiomedico() != null && !aEquipo.getEquipoBiomedico().trim().equals("")) {
				sql.append("  AND p.equipo_biomedico =  ? ");
				prametros.add(aEquipo.getEquipoBiomedico().trim());
			}

			if (aEquipo != null && aEquipo.getCodigoQr() != null && !aEquipo.getCodigoQr().trim().equals("")) {
				sql.append("  AND UPPER(p.codigo_qr) =  ? ");
				prametros.add(aEquipo.getCodigoQr().toUpperCase().trim());
			}

			if (aEquipo != null && aEquipo.getNombreEquipo() != null && !aEquipo.getNombreEquipo().trim().equals("")) {
				sql.append("  AND UPPER(p.nombre_equipo) LIKE  ? ");
				prametros.add("%" + aEquipo.getNombreEquipo().trim().toUpperCase() + "%");
			}

			if (aEquipo != null && aEquipo.getCliente() != null && aEquipo.getCliente().getNit() != null && !aEquipo.getCliente().getNit().trim().equals("")) {
				sql.append("  AND UPPER(c.nit) LIKE  ? ");
				prametros.add("%" + aEquipo.getCliente().getNit().trim().toUpperCase() + "%");
			}

			if (aEquipo != null && aEquipo.getCliente() != null && aEquipo.getCliente().getCliente() != null && !aEquipo.getCliente().getCliente().trim().equals("")) {
				sql.append("  AND UPPER(c.cliente) LIKE  ? ");
				prametros.add("%" + aEquipo.getCliente().getCliente().trim().toUpperCase() + "%");
			}

			if (aEquipo != null && aEquipo.getCliente() != null && aEquipo.getCliente().getUbicacion() != null && !aEquipo.getCliente().getUbicacion().trim().equals("")) {
				sql.append("  AND UPPER(c.ubicacion) LIKE  ? ");
				prametros.add("%" + aEquipo.getCliente().getUbicacion().trim().toUpperCase() + "%");
			}

			sql.append("  ORDER BY c.cliente, c.ubicacion, p.nombre_equipo");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				equipo = new Equipo();

				equipo.setId((Integer) rs.getObject("id"));

				equipo.setNombreEquipo((String) rs.getObject("nombre_equipo"));
				equipo.setNumeroInventario((String) rs.getObject("numero_inventario"));
				equipo.setMarca((String) rs.getObject("marca"));
				equipo.setModelo((String) rs.getObject("modelo"));
				equipo.setNumeroSerie((String) rs.getObject("numero_serie"));
				equipo.setEstado((String) rs.getObject("estado"));
				equipo.setCodigoQr((String) rs.getObject("codigo_qr"));
				equipo.setDescripcionEquipo((String) rs.getObject("descripcion_equipo"));
				equipo.setServicio((String) rs.getObject("servicio"));
				equipo.setEquipo((String) rs.getObject("equipo"));
				equipo.setRegistroInvima((String) rs.getObject("registro_invima"));
				equipo.setDatosTecnicos((String) rs.getObject("datos_tecnicos"));
				equipo.setPotencia((String) rs.getObject("potencia"));
				equipo.setVoltaje((String) rs.getObject("voltaje"));
				equipo.setCorriente((String) rs.getObject("corriente"));
				equipo.setFrecuencia((String) rs.getObject("frecuencia"));
				equipo.setNumeroFases((Integer) rs.getObject("numero_fases"));
				equipo.setMinimoPresion((String) rs.getObject("minimo_presion"));
				equipo.setMaximoPresion((String) rs.getObject("maximo_presion"));
				equipo.setMinimoTemperatura((String) rs.getObject("minimo_temperatura"));
				equipo.setMaximoTemperatura((String) rs.getObject("maximo_temperatura"));
				equipo.setMediciones((String) rs.getObject("mediciones"));
				equipo.setRequiereCalibracion((String) rs.getObject("requiere_calibracion"));
				equipo.setContieneBaterias((String) rs.getObject("contiene_baterias"));
				equipo.setNumeroBaterias((Integer) rs.getObject("numero_baterias"));
				equipo.setProveedor((String) rs.getObject("proveedor"));
				equipo.setTelefono((String) rs.getObject("telefono"));
				equipo.setCorreoElectronico((String) rs.getObject("correo_electronico"));
				equipo.setEquipoBiomedico((String) rs.getObject("equipo_biomedico"));

				equipo.getCliente().setId((Integer) rs.getObject("id_cliente"));
				equipo.getCliente().setNit((String) rs.getObject("nit"));
				equipo.getCliente().setCliente((String) rs.getObject("cliente"));
				equipo.getCliente().setUbicacion((String) rs.getObject("ubicacion"));
				equipo.getCliente().setEstadoVigencia((String) rs.getObject("estado_vigencia"));
				equipo.getCliente().setCorreoElectronico((String) rs.getObject("correo_cliente"));

				equipo.getCliente().setRepresentante((String) rs.getObject("representante"));
				equipo.getCliente().setFirma((String) rs.getObject("firma"));
				equipo.getCliente().setCargo((String) rs.getObject("cargo"));

				equipo.getClasificacionBiomedica().setId((Integer) rs.getObject("id_clasificacion_biomedica"));
				equipo.getClasificacionBiomedica().setIndicativoVigencia((String) rs.getObject("vigencia_cb"));
				equipo.getClasificacionBiomedica().setNombre((String) rs.getObject("nombre_cb"));

				equipo.getClasificacionRiesgo().setId((Integer) rs.getObject("id_clasificacion_riesgo"));
				equipo.getClasificacionRiesgo().setIndicativoVigencia((String) rs.getObject("vigencia_cr"));
				equipo.getClasificacionRiesgo().setNombre((String) rs.getObject("nombre_cr"));

				equipo.getClaseSoporteBiomedico().setId((Integer) rs.getObject("id_clase_soporte_biomedico"));
				equipo.getClaseSoporteBiomedico().setIndicativoVigencia((String) rs.getObject("vigencia_sb"));
				equipo.getClaseSoporteBiomedico().setNombre((String) rs.getObject("nombre_sb"));

				equipos.add(equipo);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}

		}
		return equipos;

	}

	/**
	 * Obtiene un listado de equipos de acuerdo a criterios de consulta
	 * 
	 * @param aEquipo
	 * @return equipos
	 * @throws Exception
	 */
	public static List<Equipo> getEquipos(Equipo aEquipo) throws Exception {
		List<Equipo> equipos = new ArrayList<Equipo>();
		List<Object> prametros = new ArrayList<Object>();
		Equipo equipo = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.*, c.nit, c.cliente, c.ubicacion, c.estado_vigencia, cr.indicativo_vigencia vigencia_cr, cb.indicativo_vigencia vigencia_cb,  sb.indicativo_vigencia vigencia_sb, cr.nombre nombre_cr, sb.nombre nombre_sb, cb.nombre nombre_cb, c.representante, c.firma, c.cargo, c.correo_electronico correo_cliente");
			sql.append("  FROM equipos p");
			sql.append("  INNER JOIN clientes c ON p.id_cliente = c.id");
			sql.append("  LEFT JOIN clasificaciones_biomedicas cb ON p.id_clasificacion_biomedica = cb.id");
			sql.append("  LEFT JOIN clasificaciones_riesgo cr ON p.id_clasificacion_riesgo = cr.id");
			sql.append("  LEFT JOIN clases_soportes_biomedicos sb ON p.id_clase_soporte_biomedico = sb.id");
			sql.append("  WHERE 1=1 ");

			if (aEquipo != null && aEquipo.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aEquipo.getId());
			}

			if (aEquipo != null && aEquipo.getCliente() != null && aEquipo.getCliente().getId() != null) {
				sql.append("  AND p.id_cliente =  ? ");
				prametros.add(aEquipo.getCliente().getId());
			}

			if (aEquipo != null && aEquipo.getNumeroSerie() != null && !aEquipo.getNumeroSerie().trim().equals("")) {
				sql.append("  AND p.numero_serie =  ? ");
				prametros.add(aEquipo.getNumeroSerie().trim());
			}

			if (aEquipo != null && aEquipo.getNumeroInventario() != null && !aEquipo.getNumeroInventario().trim().equals("")) {
				sql.append("  AND p.numero_inventario =  ? ");
				prametros.add(aEquipo.getNumeroInventario().trim());
			}

			if (aEquipo != null && aEquipo.getMediciones() != null && !aEquipo.getMediciones().trim().equals("")) {
				sql.append("  AND p.mediciones =  ? ");
				prametros.add(aEquipo.getDatosTecnicos().trim());
			}

			if (aEquipo != null && aEquipo.getDatosTecnicos() != null && !aEquipo.getDatosTecnicos().trim().equals("")) {
				sql.append("  AND p.datos_tecnicos =  ? ");
				prametros.add(aEquipo.getDatosTecnicos().trim());
			}

			if (aEquipo != null && aEquipo.getRequiereCalibracion() != null && !aEquipo.getRequiereCalibracion().trim().equals("")) {
				sql.append("  AND p.requiere_calibracion =  ? ");
				prametros.add(aEquipo.getRequiereCalibracion().trim());
			}

			if (aEquipo != null && aEquipo.getEquipoBiomedico() != null && !aEquipo.getEquipoBiomedico().trim().equals("")) {
				sql.append("  AND p.equipo_biomedico =  ? ");
				prametros.add(aEquipo.getEquipoBiomedico().trim());
			}

			if (aEquipo != null && aEquipo.getCodigoQr() != null && !aEquipo.getCodigoQr().trim().equals("")) {
				sql.append("  AND UPPER(p.codigo_qr) =  ? ");
				prametros.add(aEquipo.getCodigoQr().toUpperCase().trim());
			}

			if (aEquipo != null && aEquipo.getNombreEquipo() != null && !aEquipo.getNombreEquipo().trim().equals("")) {
				sql.append("  AND UPPER(p.nombre_equipo) LIKE  ? ");
				prametros.add("%" + aEquipo.getNombreEquipo().trim().toUpperCase() + "%");
			}

			if (aEquipo != null && aEquipo.getCliente() != null && aEquipo.getCliente().getNit() != null && !aEquipo.getCliente().getNit().trim().equals("")) {
				sql.append("  AND UPPER(c.nit) LIKE  ? ");
				prametros.add("%" + aEquipo.getCliente().getNit().trim().toUpperCase() + "%");
			}

			if (aEquipo != null && aEquipo.getCliente() != null && aEquipo.getCliente().getCliente() != null && !aEquipo.getCliente().getCliente().trim().equals("")) {
				sql.append("  AND UPPER(c.cliente) LIKE  ? ");
				prametros.add("%" + aEquipo.getCliente().getCliente().trim().toUpperCase() + "%");
			}

			if (aEquipo != null && aEquipo.getCliente() != null && aEquipo.getCliente().getUbicacion() != null && !aEquipo.getCliente().getUbicacion().trim().equals("")) {
				sql.append("  AND UPPER(c.ubicacion) LIKE  ? ");
				prametros.add("%" + aEquipo.getCliente().getUbicacion().trim().toUpperCase() + "%");
			}

			sql.append("  ORDER BY c.cliente, c.ubicacion, p.nombre_equipo");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				equipo = new Equipo();

				equipo.setId((Integer) rs.getObject("id"));

				equipo.setNombreEquipo((String) rs.getObject("nombre_equipo"));
				equipo.setNumeroInventario((String) rs.getObject("numero_inventario"));
				equipo.setMarca((String) rs.getObject("marca"));
				equipo.setModelo((String) rs.getObject("modelo"));
				equipo.setNumeroSerie((String) rs.getObject("numero_serie"));
				equipo.setEstado((String) rs.getObject("estado"));
				equipo.setCodigoQr((String) rs.getObject("codigo_qr"));
				equipo.setDescripcionEquipo((String) rs.getObject("descripcion_equipo"));
				equipo.setServicio((String) rs.getObject("servicio"));
				equipo.setEquipo((String) rs.getObject("equipo"));
				equipo.setRegistroInvima((String) rs.getObject("registro_invima"));
				equipo.setDatosTecnicos((String) rs.getObject("datos_tecnicos"));
				equipo.setPotencia((String) rs.getObject("potencia"));
				equipo.setVoltaje((String) rs.getObject("voltaje"));
				equipo.setCorriente((String) rs.getObject("corriente"));
				equipo.setFrecuencia((String) rs.getObject("frecuencia"));
				equipo.setNumeroFases((Integer) rs.getObject("numero_fases"));
				equipo.setMinimoPresion((String) rs.getObject("minimo_presion"));
				equipo.setMaximoPresion((String) rs.getObject("maximo_presion"));
				equipo.setMinimoTemperatura((String) rs.getObject("minimo_temperatura"));
				equipo.setMaximoTemperatura((String) rs.getObject("maximo_temperatura"));
				equipo.setMediciones((String) rs.getObject("mediciones"));
				equipo.setRequiereCalibracion((String) rs.getObject("requiere_calibracion"));
				equipo.setContieneBaterias((String) rs.getObject("contiene_baterias"));
				equipo.setNumeroBaterias((Integer) rs.getObject("numero_baterias"));
				equipo.setProveedor((String) rs.getObject("proveedor"));
				equipo.setTelefono((String) rs.getObject("telefono"));
				equipo.setCorreoElectronico((String) rs.getObject("correo_electronico"));
				equipo.setEquipoBiomedico((String) rs.getObject("equipo_biomedico"));

				equipo.getCliente().setId((Integer) rs.getObject("id_cliente"));
				equipo.getCliente().setNit((String) rs.getObject("nit"));
				equipo.getCliente().setCliente((String) rs.getObject("cliente"));
				equipo.getCliente().setUbicacion((String) rs.getObject("ubicacion"));
				equipo.getCliente().setEstadoVigencia((String) rs.getObject("estado_vigencia"));
				equipo.getCliente().setCorreoElectronico((String) rs.getObject("correo_cliente"));

				equipo.getCliente().setRepresentante((String) rs.getObject("representante"));
				equipo.getCliente().setFirma((String) rs.getObject("firma"));
				equipo.getCliente().setCargo((String) rs.getObject("cargo"));

				equipo.getClasificacionBiomedica().setId((Integer) rs.getObject("id_clasificacion_biomedica"));
				equipo.getClasificacionBiomedica().setIndicativoVigencia((String) rs.getObject("vigencia_cb"));
				equipo.getClasificacionBiomedica().setNombre((String) rs.getObject("nombre_cb"));

				equipo.getClasificacionRiesgo().setId((Integer) rs.getObject("id_clasificacion_riesgo"));
				equipo.getClasificacionRiesgo().setIndicativoVigencia((String) rs.getObject("vigencia_cr"));
				equipo.getClasificacionRiesgo().setNombre((String) rs.getObject("nombre_cr"));

				equipo.getClaseSoporteBiomedico().setId((Integer) rs.getObject("id_clase_soporte_biomedico"));
				equipo.getClaseSoporteBiomedico().setIndicativoVigencia((String) rs.getObject("vigencia_sb"));
				equipo.getClaseSoporteBiomedico().setNombre((String) rs.getObject("nombre_sb"));

				equipos.add(equipo);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return equipos;

	}

	/**
	 * Determina si existe un código qr existente
	 * 
	 * @param aConexion
	 * @param aEquipo
	 * @return exiteQR
	 * @throws Exception
	 */
	public static boolean isExistenteQR(Conexion aConexion, Equipo aEquipo) throws Exception {
		boolean exiteQR = false;
		List<Object> parametros = new ArrayList<Object>();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT p.codigo_qr");
			sql.append("  FROM equipos p");
			sql.append("  WHERE UPPER(p.codigo_qr) = ? ");
			parametros.add(aEquipo.getCodigoQr().toUpperCase().trim());

			rs = aConexion.consultarBD(sql.toString(), parametros);

			if (rs.next()) {
				exiteQR = true;
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}

		}
		return exiteQR;

	}

	/**
	 * Obtiene un registro de los clientes limitado a 100
	 * 
	 * @param aCliente
	 * @return clientes
	 * @throws Exception
	 */
	public static List<Cliente> getClientesLimitados(Cliente aCliente) throws Exception {
		List<Cliente> clientes = new ArrayList<Cliente>();
		List<Object> prametros = new ArrayList<Object>();
		Cliente cliente = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM clientes p");
			sql.append("  WHERE 1=1 ");

			sql.append("  AND UPPER(p.nit || p.cliente || p.ubicacion) LIKE  ? ");
			prametros.add("%" + aCliente.getCliente().trim().toUpperCase() + "%");

			if (aCliente != null && aCliente.getEstadoVigencia() != null && !aCliente.getEstadoVigencia().trim().equals("")) {
				sql.append("  AND p.estado_vigencia = ?   ");
				prametros.add(aCliente.getEstadoVigencia().trim());
			}

			sql.append("  ORDER BY p.cliente, p.ubicacion");

			rs = conexion.consultarBD(sql.toString(), prametros);
			rs.setFetchSize(100);

			while (rs.next()) {

				cliente = new Cliente();
				cliente.setId((Integer) rs.getObject("id"));
				cliente.setCliente((String) rs.getObject("cliente"));
				cliente.setNit((String) rs.getObject("nit"));
				cliente.setRepresentante((String) rs.getObject("representante"));
				cliente.setCorreoElectronico((String) rs.getObject("correo_electronico"));
				cliente.setClave((String) rs.getObject("clave"));
				cliente.setTelefono((String) rs.getObject("telefono"));
				cliente.setDireccionFisica((String) rs.getObject("direccion_fisica"));
				cliente.setUbicacion((String) rs.getObject("ubicacion"));
				cliente.setCargo((String) rs.getObject("cargo"));
				cliente.setEstadoVigencia((String) rs.getObject("estado_vigencia"));

				cliente.setInformeMantenimiento((String) rs.getObject("informe_mantenimiento"));
				cliente.setReporteFallas((String) rs.getObject("reporte_fallas"));
				cliente.setCronograma((String) rs.getObject("cronograma"));
				cliente.setHojaVida((String) rs.getObject("hoja_vida"));
				cliente.setIndicadoresGestion((String) rs.getObject("indicadores_gestion"));

				clientes.add(cliente);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return clientes;

	}

	/**
	 * Obtiene las clases de documento
	 * 
	 * @param aClaseDocumento
	 * @return clasesDocumentos
	 * @throws Exception
	 */
	public static List<ClaseDocumento> getClasesDocumentos(ClaseDocumento aClaseDocumento) throws Exception {

		List<ClaseDocumento> clasesDocumentos = new ArrayList<ClaseDocumento>();
		List<Object> parametros = new ArrayList<Object>();
		ClaseDocumento claseDocumento = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT a.*");
			sql.append("  FROM clases_documentos a ");
			sql.append("  WHERE 1=1");

			if (aClaseDocumento != null && aClaseDocumento.getId() != null) {
				sql.append("  AND a.id = ?");
				parametros.add(aClaseDocumento.getId());
			}

			if (aClaseDocumento != null && aClaseDocumento.getIndicativoVigencia() != null && !aClaseDocumento.getIndicativoVigencia().trim().equals("")) {
				sql.append("  AND a.indicativo_vigencia = ?");
				parametros.add(aClaseDocumento.getIndicativoVigencia().trim());
			}

			sql.append("  ORDER BY a.nombre ");

			rs = conexion.consultarBD(sql.toString(), parametros);

			while (rs.next()) {

				claseDocumento = new ClaseDocumento();

				claseDocumento.setId((Integer) rs.getObject("id"));
				claseDocumento.setNombre((String) rs.getObject("nombre"));
				claseDocumento.setIndicativoVigencia((String) rs.getObject("indicativo_vigencia"));
				claseDocumento.setEuipoBiomedico((String) rs.getObject("equipo_biomedico"));
				clasesDocumentos.add(claseDocumento);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}

		return clasesDocumentos;

	}

	/**
	 * Obtiene un listado de las clases de soporte biomédico
	 * 
	 * @param aClaseSoporteBiomedico
	 * @return clasesSoporte
	 * @throws Exception
	 */
	public static List<ClaseSoporteBiomedico> getClasesSoportesBiomedicos(ClaseSoporteBiomedico aClaseSoporteBiomedico) throws Exception {

		List<ClaseSoporteBiomedico> clasesSoporte = new ArrayList<ClaseSoporteBiomedico>();
		List<Object> parametros = new ArrayList<Object>();
		ClaseSoporteBiomedico claseSoporteBiomedico = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT a.*");
			sql.append("  FROM clases_soportes_biomedicos a ");
			sql.append("  WHERE 1=1");

			if (aClaseSoporteBiomedico != null && aClaseSoporteBiomedico.getId() != null) {
				sql.append("  AND a.id = ?");
				parametros.add(aClaseSoporteBiomedico.getId());
			}

			if (aClaseSoporteBiomedico != null && aClaseSoporteBiomedico.getIndicativoVigencia() != null && !aClaseSoporteBiomedico.getIndicativoVigencia().trim().equals("")) {
				sql.append("  AND a.indicativo_vigencia = ?");
				parametros.add(aClaseSoporteBiomedico.getIndicativoVigencia().trim());
			}

			sql.append("  ORDER BY a.nombre ");

			rs = conexion.consultarBD(sql.toString(), parametros);

			while (rs.next()) {

				claseSoporteBiomedico = new ClaseSoporteBiomedico();

				claseSoporteBiomedico.setId((Integer) rs.getObject("id"));
				claseSoporteBiomedico.setNombre((String) rs.getObject("nombre"));
				claseSoporteBiomedico.setIndicativoVigencia((String) rs.getObject("indicativo_vigencia"));

				clasesSoporte.add(claseSoporteBiomedico);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}

		return clasesSoporte;

	}

	/**
	 * Obtiene un listado de clasificaciones biomédicas
	 * 
	 * @param aClasificacionBiomedica
	 * @return clasificaciones
	 * @throws Exception
	 */
	public static List<ClasificacionBiomedica> getClasificacionesBiomedicas(ClasificacionBiomedica aClasificacionBiomedica) throws Exception {

		List<ClasificacionBiomedica> clasificaciones = new ArrayList<ClasificacionBiomedica>();
		List<Object> parametros = new ArrayList<Object>();
		ClasificacionBiomedica clasificacionBiomedica = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT a.*");
			sql.append("  FROM clasificaciones_biomedicas a ");
			sql.append("  WHERE 1=1");

			if (aClasificacionBiomedica != null && aClasificacionBiomedica.getId() != null) {
				sql.append("  AND a.id = ?");
				parametros.add(aClasificacionBiomedica.getId());
			}

			if (aClasificacionBiomedica != null && aClasificacionBiomedica.getIndicativoVigencia() != null && !aClasificacionBiomedica.getIndicativoVigencia().trim().equals("")) {
				sql.append("  AND a.indicativo_vigencia = ?");
				parametros.add(aClasificacionBiomedica.getIndicativoVigencia().trim());
			}

			sql.append("  ORDER BY a.nombre ");

			rs = conexion.consultarBD(sql.toString(), parametros);

			while (rs.next()) {

				clasificacionBiomedica = new ClasificacionBiomedica();

				clasificacionBiomedica.setId((Integer) rs.getObject("id"));
				clasificacionBiomedica.setNombre((String) rs.getObject("nombre"));
				clasificacionBiomedica.setIndicativoVigencia((String) rs.getObject("indicativo_vigencia"));

				clasificaciones.add(clasificacionBiomedica);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}

		return clasificaciones;

	}

	/**
	 * Obtiene un listado de clasificaciones riesgo
	 * 
	 * @param aClasificacionRiesgo
	 * @return clasificaciones
	 * @throws Exception
	 */
	public static List<ClasificacionRiesgo> getClasificacionesRiesgo(ClasificacionRiesgo aClasificacionRiesgo) throws Exception {

		List<ClasificacionRiesgo> clasificaciones = new ArrayList<ClasificacionRiesgo>();
		List<Object> parametros = new ArrayList<Object>();
		ClasificacionRiesgo clasificacionRiesgo = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT a.*");
			sql.append("  FROM clasificaciones_riesgo a ");
			sql.append("  WHERE 1=1");

			if (aClasificacionRiesgo != null && aClasificacionRiesgo.getId() != null) {
				sql.append("  AND a.id = ?");
				parametros.add(aClasificacionRiesgo.getId());
			}

			if (aClasificacionRiesgo != null && aClasificacionRiesgo.getIndicativoVigencia() != null && !aClasificacionRiesgo.getIndicativoVigencia().trim().equals("")) {
				sql.append("  AND a.indicativo_vigencia = ?");
				parametros.add(aClasificacionRiesgo.getIndicativoVigencia().trim());
			}

			sql.append("  ORDER BY a.nombre ");

			rs = conexion.consultarBD(sql.toString(), parametros);

			while (rs.next()) {

				clasificacionRiesgo = new ClasificacionRiesgo();

				clasificacionRiesgo.setId((Integer) rs.getObject("id"));
				clasificacionRiesgo.setNombre((String) rs.getObject("nombre"));
				clasificacionRiesgo.setIndicativoVigencia((String) rs.getObject("indicativo_vigencia"));

				clasificaciones.add(clasificacionRiesgo);

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}

		return clasificaciones;

	}

	/**
	 * Obtiene un cliente por el nombre
	 * 
	 * @param aNombreCliente
	 * @return cliente
	 * @throws Exception
	 */
	public static Cliente getClientesPorNombre(String aNombreCliente) throws Exception {

		List<Object> prametros = new ArrayList<Object>();
		Cliente cliente = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM clientes p");
			sql.append("  WHERE 1=1 ");
			sql.append("  AND UPPER(p.cliente)=  ? ");

			prametros.add(aNombreCliente.trim().toUpperCase());

			rs = conexion.consultarBD(sql.toString(), prametros);

			if (rs.next()) {
				cliente = new Cliente();
				cliente.setId((Integer) rs.getObject("id"));
				cliente.setCliente((String) rs.getObject("cliente"));
				cliente.setNit((String) rs.getObject("nit"));
				cliente.setRepresentante((String) rs.getObject("representante"));
				cliente.setCorreoElectronico((String) rs.getObject("correo_electronico"));
				cliente.setClave((String) rs.getObject("clave"));
				cliente.setTelefono((String) rs.getObject("telefono"));
				cliente.setDireccionFisica((String) rs.getObject("direccion_fisica"));
				cliente.setUbicacion((String) rs.getObject("ubicacion"));
				cliente.setCargo((String) rs.getObject("cargo"));
				cliente.setEstadoVigencia((String) rs.getObject("estado_vigencia"));

				cliente.setInformeMantenimiento((String) rs.getObject("informe_mantenimiento"));
				cliente.setReporteFallas((String) rs.getObject("reporte_fallas"));
				cliente.setCronograma((String) rs.getObject("cronograma"));
				cliente.setHojaVida((String) rs.getObject("hoja_vida"));
				cliente.setIndicadoresGestion((String) rs.getObject("indicadores_gestion"));

			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return cliente;

	}

	/**
	 * Obtiene el listado de clientes
	 * 
	 * @param aCliente
	 * @return clientes
	 * @throws Exception
	 */
	public static List<Cliente> getClientes(Cliente aCliente) throws Exception {
		List<Cliente> clientes = new ArrayList<Cliente>();
		List<Object> prametros = new ArrayList<Object>();
		Cliente cliente = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM clientes p");
			sql.append("  WHERE 1=1 ");

			if (aCliente != null && aCliente.getId() != null) {
				sql.append("  AND p.id =  ? ");
				prametros.add(aCliente.getId());
			}

			if (aCliente != null && aCliente.getNit() != null && !aCliente.getNit().trim().equals("")) {
				sql.append("  AND UPPER(p.nit) LIKE  ? ");
				prametros.add("%" + aCliente.getNit().trim().toUpperCase() + "%");
			}

			if (aCliente != null && aCliente.getCliente() != null && !aCliente.getCliente().trim().equals("")) {
				sql.append("  AND UPPER(p.cliente) LIKE  ? ");
				prametros.add("%" + aCliente.getCliente().trim().toUpperCase() + "%");
			}

			if (aCliente != null && aCliente.getCorreoElectronico() != null && !aCliente.getCorreoElectronico().trim().equals("")) {
				sql.append("  AND UPPER(p.correo_electronico) = ?");
				prametros.add(aCliente.getCorreoElectronico().trim().toUpperCase());
			}

			if (aCliente != null && aCliente.getEstadoVigencia() != null && !aCliente.getEstadoVigencia().trim().equals("")) {
				sql.append("  AND p.estado_vigencia = ?");
				prametros.add(aCliente.getEstadoVigencia().trim());
			}

			if (aCliente != null && aCliente.getClave() != null && !aCliente.getClave().trim().equals("")) {
				sql.append("  AND p.clave = ?");
				prametros.add(aCliente.getClave().trim());
			}

			sql.append("  ORDER BY cliente");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				cliente = new Cliente();
				cliente.setId((Integer) rs.getObject("id"));
				cliente.setCliente((String) rs.getObject("cliente"));
				cliente.setNit((String) rs.getObject("nit"));
				cliente.setRepresentante((String) rs.getObject("representante"));
				cliente.setCorreoElectronico((String) rs.getObject("correo_electronico"));
				cliente.setClave((String) rs.getObject("clave"));
				cliente.setTelefono((String) rs.getObject("telefono"));
				cliente.setDireccionFisica((String) rs.getObject("direccion_fisica"));
				cliente.setUbicacion((String) rs.getObject("ubicacion"));
				cliente.setCargo((String) rs.getObject("cargo"));
				cliente.setEstadoVigencia((String) rs.getObject("estado_vigencia"));

				cliente.setInformeMantenimiento((String) rs.getObject("informe_mantenimiento"));
				cliente.setReporteFallas((String) rs.getObject("reporte_fallas"));
				cliente.setCronograma((String) rs.getObject("cronograma"));
				cliente.setHojaVida((String) rs.getObject("hoja_vida"));
				cliente.setIndicadoresGestion((String) rs.getObject("indicadores_gestion"));

				cliente.setFirma((String) rs.getObject("firma"));
				clientes.add(cliente);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return clientes;

	}

	/**
	 * Obtiene los clientes existentes con parte del nit
	 * 
	 * @param aParteA
	 * @return clientes
	 * @throws Exception
	 */
	public static List<Cliente> getClienteExistenteCorreo(String aParteA) throws Exception {
		List<Cliente> clientes = new ArrayList<Cliente>();
		Cliente cliente = null;
		List<Object> prametros = new ArrayList<Object>();
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM clientes p");
			sql.append("  WHERE 1=1 ");

			sql.append("  AND ( UPPER(p.nit) = ?");
			sql.append("  		  OR UPPER(p.nit) LIKE ? ");
			sql.append("  		) ");

			prametros.add(aParteA.toUpperCase().trim());
			prametros.add(aParteA.toUpperCase().trim() + "-%");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				cliente = new Cliente();
				cliente.setId((Integer) rs.getObject("id"));
				cliente.setCliente((String) rs.getObject("cliente"));
				cliente.setNit((String) rs.getObject("nit"));
				cliente.setRepresentante((String) rs.getObject("representante"));
				cliente.setCorreoElectronico((String) rs.getObject("correo_electronico"));
				cliente.setClave((String) rs.getObject("clave"));
				cliente.setTelefono((String) rs.getObject("telefono"));
				cliente.setDireccionFisica((String) rs.getObject("direccion_fisica"));
				cliente.setUbicacion((String) rs.getObject("ubicacion"));
				cliente.setCargo((String) rs.getObject("cargo"));
				cliente.setEstadoVigencia((String) rs.getObject("estado_vigencia"));
				cliente.setInformeMantenimiento((String) rs.getObject("informe_mantenimiento"));
				cliente.setReporteFallas((String) rs.getObject("reporte_fallas"));
				cliente.setCronograma((String) rs.getObject("cronograma"));
				cliente.setHojaVida((String) rs.getObject("hoja_vida"));
				cliente.setIndicadoresGestion((String) rs.getObject("indicadores_gestion"));
				clientes.add(cliente);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return clientes;

	}

	/**
	 * Valida si un cliente existe o no de acuerdo a su nit
	 * 
	 * @param aParteA
	 * @param aParteB
	 * @return existente
	 * @throws Exception
	 */
	public static boolean getClienteExistente(String aParteA) throws Exception {
		boolean existente = false;
		List<Object> prametros = new ArrayList<Object>();
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM clientes p");
			sql.append("  WHERE 1=1 ");

			sql.append("  AND ( UPPER(p.nit) = ?");
			sql.append("  		  OR UPPER(p.nit) LIKE ? ");
			sql.append("  		) ");

			prametros.add(aParteA.toUpperCase().trim());
			prametros.add(aParteA.toUpperCase().trim() + "-%");

			rs = conexion.consultarBD(sql.toString(), prametros);

			if (rs.next()) {
				existente = true;
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return existente;

	}

	/**
	 * Obtiene un listado de técnicos
	 * 
	 * @param aTecnico
	 * @return tecnicos
	 * @throws Exception
	 */
	public static List<Tecnico> getTecnicos(Tecnico aTecnico) throws Exception {
		List<Tecnico> tecnicos = new ArrayList<Tecnico>();
		List<Object> prametros = new ArrayList<Object>();
		Tecnico tecnico = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM tecnicos p");
			sql.append("  WHERE 1=1 ");

			if (aTecnico != null && aTecnico.getId() != null) {
				sql.append("  AND p.id = ?");
				prametros.add(aTecnico.getId());
			}

			if (aTecnico != null && aTecnico.getCorreoElectronico() != null && !aTecnico.getCorreoElectronico().trim().equals("")) {
				sql.append("  AND UPPER(p.correo_electronico) = ?");
				prametros.add(aTecnico.getCorreoElectronico().trim().toUpperCase());
			}

			if (aTecnico != null && aTecnico.getClave() != null && !aTecnico.getClave().trim().equals("")) {
				sql.append("  AND p.clave = ?");
				prametros.add(aTecnico.getClave().trim());
			}

			if (aTecnico != null && aTecnico.getEstadoVigencia() != null && !aTecnico.getEstadoVigencia().trim().equals("")) {
				sql.append("  AND p.estado_vigencia = ?");
				prametros.add(aTecnico.getEstadoVigencia().trim());
			}

			sql.append("  ORDER BY nombres");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				tecnico = new Tecnico();
				tecnico.setId((Integer) rs.getObject("id"));
				tecnico.setNombres((String) rs.getObject("nombres"));
				tecnico.setCorreoElectronico((String) rs.getObject("correo_electronico"));
				tecnico.setClave((String) rs.getObject("clave"));
				tecnico.setEstadoVigencia((String) rs.getObject("estado_vigencia"));
				tecnico.setTelefono((String) rs.getObject("telefono"));
				tecnico.setDocumento((String) rs.getObject("documento"));
				tecnico.setCargo((String) rs.getObject("cargo"));

				tecnico.setInformeMantenimiento((String) rs.getObject("informe_mantenimiento"));
				tecnico.setReporteFallas((String) rs.getObject("reporte_fallas"));
				tecnico.setCronograma((String) rs.getObject("cronograma"));
				tecnico.setFirma((String) rs.getObject("firma"));

				tecnicos.add(tecnico);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return tecnicos;

	}

	/**
	 * Obtiene un listado de administradores
	 * 
	 * @param aAdministrador
	 * @return administradores
	 * @throws Exception
	 */
	public static List<Administrador> getAdministradores(Administrador aAdministrador) throws Exception {
		List<Administrador> administradores = new ArrayList<Administrador>();
		List<Object> prametros = new ArrayList<Object>();
		Administrador administrador = null;
		Conexion conexion = new Conexion();
		ResultSet rs = null;
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("  SELECT *");
			sql.append("  FROM administradores p");
			sql.append("  WHERE 1=1 ");

			if (aAdministrador != null && aAdministrador.getCorreoElectronico() != null && !aAdministrador.getCorreoElectronico().trim().equals("")) {
				sql.append("  AND UPPER(p.correo_electronico) = ?");
				prametros.add(aAdministrador.getCorreoElectronico().trim().toUpperCase());
			}

			if (aAdministrador != null && aAdministrador.getClave() != null && !aAdministrador.getClave().trim().equals("")) {
				sql.append("  AND p.clave = ?");
				prametros.add(aAdministrador.getClave().trim());
			}

			if (aAdministrador != null && aAdministrador.getEstadoVigencia() != null && !aAdministrador.getEstadoVigencia().trim().equals("")) {
				sql.append("  AND p.estado_vigencia = ?");
				prametros.add(aAdministrador.getEstadoVigencia().trim());
			}

			sql.append("  ORDER BY nombres");

			rs = conexion.consultarBD(sql.toString(), prametros);

			while (rs.next()) {
				administrador = new Administrador();
				administrador.setId((Integer) rs.getObject("id"));
				administrador.setNombres((String) rs.getObject("nombres"));
				administrador.setCorreoElectronico((String) rs.getObject("correo_electronico"));
				administrador.setClave((String) rs.getObject("clave"));
				administrador.setEstadoVigencia((String) rs.getObject("estado_vigencia"));

				administradores.add(administrador);
			}

		} catch (Exception e) {
			throw new Exception(e);

		} finally {

			if (rs != null) {
				rs.close();
			}
			conexion.cerrarConexion();

		}
		return administradores;

	}

}
