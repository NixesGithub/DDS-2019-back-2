package Criterios;

import java.io.IOException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import grupo1.utn.frba.dds.Atuendo;
import net.aksingh.owmjapis.api.APIException;
import grupo1.utn.frba.dds.*;

@Entity
public class CriterioEventoFormal extends Criterios {
	
	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		boolean condicion=unAtuendo.prendas.stream().allMatch(p->p.isEsFormal());
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
	
	public CriterioEventoFormal() {}

}
