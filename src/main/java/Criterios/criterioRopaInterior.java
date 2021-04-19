package Criterios;

import java.io.IOException;

import grupo1.utn.frba.dds.*;
import net.aksingh.owmjapis.api.APIException;

import javax.persistence.Entity;
@Entity
public class criterioRopaInterior extends Criterios {

	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		
		boolean condicion=unAtuendo.getSuperior().stream().anyMatch(prenda->prenda.getTipo().getCapa()==0)&&unAtuendo.getInferior().stream().anyMatch(prenda->prenda.getTipo().getCapa()==0);
		ponderarAtuendo(unAtuendo,condicion);
		return condicion;
	}

	@Override
	public void ponderarAtuendo(Atuendo unAtuendo, boolean condicion) {
		if(condicion) {
			
			unAtuendo.setNivelDePonderacion(unAtuendo.getNivelDePonderacion()+1);
			
		}
		
		
	}

	@Override
	public String getNombre() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public criterioRopaInterior() {}

}
