package optimo.beans;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.primefaces.model.UploadedFile;

import optimo.generales.IConstantes;

public class MaterialFotografico implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6376860693256519222L;
	private Integer								id;
	private String								titulo;

	private InformeMantenimiento	informeMantenimiento;

	private String								tFotoDecodificada;
	private String								tNombreFoto;

	private byte[]								archivo;
	private UploadedFile					tFile;

	private EstructuraTabla				estructuraTabla;

	public MaterialFotografico() {
		this.estructuraTabla = new EstructuraTabla();
		this.informeMantenimiento = new InformeMantenimiento();
	}

	public void getCamposBD() {

		this.estructuraTabla.setTabla("material_fotografico");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("titulo", this.titulo);

		this.estructuraTabla.getPersistencia().put("archivo", this.archivo);

		if (this.informeMantenimiento != null && this.informeMantenimiento.getCronograma() != null && this.informeMantenimiento.getCronograma().getId() != null) {
			this.estructuraTabla.getPersistencia().put("id_cronograma", this.informeMantenimiento.getCronograma().getId());
		} else {
			this.estructuraTabla.getPersistencia().put("id_cronograma", this.informeMantenimiento.getCronograma().getId());
		}

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getArchivo() {
		return archivo;
	}

	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
	}

	public InformeMantenimiento getInformeMantenimiento() {
		return informeMantenimiento;
	}

	public void setInformeMantenimiento(InformeMantenimiento informeMantenimiento) {
		this.informeMantenimiento = informeMantenimiento;
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

	public UploadedFile gettFile() {
		return tFile;
	}

	public void settFile(UploadedFile tFile) {
		this.tFile = tFile;
	}

	public String gettNombreFoto() {
		return tNombreFoto;
	}

	public void settNombreFoto(String tNombreFoto) {
		this.tNombreFoto = tNombreFoto;
	}

	@Size(max = 250, message = IConstantes.VALIDACION_MAXIMA_LONGITUD)
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
