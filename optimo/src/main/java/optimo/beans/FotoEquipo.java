package optimo.beans;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.primefaces.model.UploadedFile;

import optimo.generales.IConstantes;

public class FotoEquipo implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8933205066407761398L;
	private Integer						id;
	private String						leyenda;
	private Equipo						equipo;
	private byte[]						archivo;
	private String						tFotoDecodificada;
	private String						tNombreFoto;

	private UploadedFile			tFile;

	private EstructuraTabla		estructuraTabla;

	public FotoEquipo() {
		this.estructuraTabla = new EstructuraTabla();

		this.equipo = new Equipo();

	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("fotos_equipos");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("leyenda", this.leyenda);
		this.estructuraTabla.getPersistencia().put("archivo", this.archivo);

		if (this.equipo != null && this.equipo.getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_equipo", this.equipo.getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_equipo", this.equipo.getId());
		}

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Size(max = 100, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getLeyenda() {
		return leyenda;
	}

	public void setLeyenda(String leyenda) {
		this.leyenda = leyenda;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public byte[] getArchivo() {
		return archivo;
	}

	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
	}

	public UploadedFile gettFile() {
		return tFile;
	}

	public void settFile(UploadedFile tFile) {
		this.tFile = tFile;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public String gettFotoDecodificada() {
		return tFotoDecodificada;
	}

	public void settFotoDecodificada(String tFotoDecodificada) {
		this.tFotoDecodificada = tFotoDecodificada;
	}

	public String gettNombreFoto() {
		return tNombreFoto;
	}

	public void settNombreFoto(String tNombreFoto) {
		this.tNombreFoto = tNombreFoto;
	}

}
