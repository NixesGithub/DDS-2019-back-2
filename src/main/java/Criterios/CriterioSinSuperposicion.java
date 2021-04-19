package Criterios;

import grupo1.utn.frba.dds.*;

import javax.persistence.Entity;
@Entity
public class CriterioSinSuperposicion extends Criterios{
	

	public String nombre="Sin Superposicion";
	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) {

		long cantAccesorios=unAtuendo.getAccesorios().size();
		long cantCalzados=unAtuendo.getCalzado().size();
		long cantSuperior=unAtuendo.getSuperior().size();
		long cantInferior=unAtuendo.getInferior().size();
		boolean condicion;
		
		condicion= (cantCalzados==1&&cantSuperior==1&&cantInferior==1 && cantAccesorios <= 1);
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
		return nombre;
	}
	
	public CriterioSinSuperposicion() {}
	
}
