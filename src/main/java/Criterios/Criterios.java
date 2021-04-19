package Criterios;

import java.io.IOException;

import javax.persistence.*;

import grupo1.utn.frba.dds.Atuendo;
import net.aksingh.owmjapis.api.APIException;
import grupo1.utn.frba.dds.*;

@Entity(name="Criterios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name="Criterios")
public abstract class Criterios {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	public String getNombre() { return "DEFAULT"; }
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException { return false; }
	public void ponderarAtuendo(Atuendo unAtuendo, boolean condicion) {}

	public Criterios() {}
	
}
