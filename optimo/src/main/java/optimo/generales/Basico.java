package optimo.generales;

import java.io.Serializable;

import optimo.beans.MaterialFotografico;

public class Basico implements Serializable {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= -5376774620516280234L;

	private String							descripcion;

	private MaterialFotografico	material1;
	private MaterialFotografico	material2;
	private MaterialFotografico	material3;

	public Basico() {
		this.material1 = new MaterialFotografico();
		this.material2 = new MaterialFotografico();
		this.material3 = new MaterialFotografico();

	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public MaterialFotografico getMaterial1() {
		return material1;
	}

	public void setMaterial1(MaterialFotografico material1) {
		this.material1 = material1;
	}

	public MaterialFotografico getMaterial2() {
		return material2;
	}

	public void setMaterial2(MaterialFotografico material2) {
		this.material2 = material2;
	}

	public MaterialFotografico getMaterial3() {
		return material3;
	}

	public void setMaterial3(MaterialFotografico material3) {
		this.material3 = material3;
	}

}
