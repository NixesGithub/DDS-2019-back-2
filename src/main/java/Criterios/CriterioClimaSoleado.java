package Criterios;

import java.io.IOException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import APIs.Clima;
import Prendas.Descripcion;
import grupo1.utn.frba.dds.*;
import net.aksingh.owmjapis.api.APIException;

@Entity
public class CriterioClimaSoleado extends Criterios {
	
	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		boolean condicion;
		condicion=unAtuendo.getAccesorios().stream().anyMatch(prenda->prenda.getCategoria().getDescripcion().equals(Descripcion.ANTEOJOS)||prenda.getCategoria().getDescripcion().equals(Descripcion.GORRA) );
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
	
	public CriterioClimaSoleado() {}

	}
