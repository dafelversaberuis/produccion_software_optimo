package optimo.beans;

import java.io.Serializable;

public class PermisoCliente implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5522631851659864951L;
	private Integer						id;
	private PersonaAcceso			personaAcceso;
	private Cliente						cliente;

	private EstructuraTabla		estructuraTabla;

	public PermisoCliente() {
		this.estructuraTabla = new EstructuraTabla();

		this.personaAcceso = new PersonaAcceso();
		this.cliente = new Cliente();

	}

	public void getCamposBD() {
		this.estructuraTabla.setTabla("permisos_cliente");
		this.estructuraTabla.getLlavePrimaria().put("id", this.id);
		this.estructuraTabla.getPersistencia().put("id_persona", this.personaAcceso.getId());
		this.estructuraTabla.getPersistencia().put("id_cliente", this.cliente.getId());

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EstructuraTabla getEstructuraTabla() {
		return estructuraTabla;
	}

	public void setEstructuraTabla(EstructuraTabla estructuraTabla) {
		this.estructuraTabla = estructuraTabla;
	}

	public PersonaAcceso getPersonaAcceso() {
		return personaAcceso;
	}

	public void setPersonaAcceso(PersonaAcceso personaAcceso) {
		this.personaAcceso = personaAcceso;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	

}
