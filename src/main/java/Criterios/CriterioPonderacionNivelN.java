package Criterios;

import java.io.IOException;

import grupo1.utn.frba.dds.*;
import net.aksingh.owmjapis.api.APIException;

import javax.persistence.Entity;
@Entity
public class CriterioPonderacionNivelN extends Criterios {
	
	private int nivelDePoneracionFiltro;

	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		
		boolean condicion= unAtuendo.getNivelDePonderacion()>=nivelDePoneracionFiltro;
		
		return condicion;
	}

	@Override
	public void ponderarAtuendo(Atuendo unAtuendo, boolean condicion) {
		

	}
	
	public CriterioPonderacionNivelN(int nivel) {
		
		this.nivelDePoneracionFiltro=nivel;
	}

	@Override
	public String getNombre() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CriterioPonderacionNivelN() {}

}
