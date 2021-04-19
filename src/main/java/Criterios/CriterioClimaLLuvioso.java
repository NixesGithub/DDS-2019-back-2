package Criterios;

import java.io.IOException;

import javax.persistence.*;

import APIs.Clima;
import Prendas.Descripcion;
import grupo1.utn.frba.dds.*;
import net.aksingh.owmjapis.api.APIException;

@Entity
public class CriterioClimaLLuvioso extends Criterios {
	
	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		boolean condicion;
		condicion=unAtuendo.getAccesorios().stream().anyMatch(p->p.getCategoria().getDescripcion().equals(Descripcion.PARAGUAS))||unAtuendo.getSuperior().stream().anyMatch(p->p.getCategoria().getDescripcion().equals(Descripcion.IMPERMEABLE));
		ponderarAtuendo(unAtuendo, condicion);
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
	
	public CriterioClimaLLuvioso() {}

}
