package Criterios;

import java.io.IOException;

import net.aksingh.owmjapis.api.APIException;
import grupo1.utn.frba.dds.*;
import javax.persistence.Entity;
@Entity
public class CriterioPrendaNoCompartida extends Criterios {

	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		
		boolean condicion=unAtuendo.prendas.stream().allMatch(p->!p.isEstaSiendoUsada());
		ponderarAtuendo(unAtuendo,condicion);
		return condicion;
	}

	@Override
	public void ponderarAtuendo(Atuendo unAtuendo, boolean condicion) {
		if(condicion)
		unAtuendo.setNivelDePonderacion(unAtuendo.getNivelDePonderacion()+1);

	}

	@Override
	public String getNombre() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CriterioPrendaNoCompartida() {}

}
