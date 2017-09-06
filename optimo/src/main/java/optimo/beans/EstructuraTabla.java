package optimo.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EstructuraTabla implements Serializable {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= -9163144612525364946L;
	private String							tabla;
	private Map<String, Object>	persistencia;
	private Map<String, Object>	llavePrimaria;

	public EstructuraTabla() {
		this.llavePrimaria = new HashMap<String, Object>();
		this.persistencia = new HashMap<String, Object>();
	}

	public String getTabla() {
		return tabla;
	}

	public void setTabla(String tabla) {
		this.tabla = tabla;
	}

	public Map<String, Object> getPersistencia() {
		return persistencia;
	}

	public void setPersistencia(Map<String, Object> persistencia) {
		this.persistencia = persistencia;
	}

	public Map<String, Object> getLlavePrimaria() {
		return llavePrimaria;
	}

	public void setLlavePrimaria(Map<String, Object> llavePrimaria) {
		this.llavePrimaria = llavePrimaria;
	}

}
