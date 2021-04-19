package Criterios;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import Prendas.Prenda;
import grupo1.utn.frba.dds.Atuendo;
import grupo1.utn.frba.dds.Usuario;
import net.aksingh.owmjapis.api.APIException;

@Entity
public class CriterioMonoCromatico extends Criterios {


	public String nombre="MonoCromatico";
	
	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		boolean condicion;
		condicion=todosConMismoColor(unAtuendo);
		ponderarAtuendo(unAtuendo, condicion);
		return condicion;
	}
	
	private boolean todosConMismoColor(Atuendo unAtuendo) {
		
		Color colorParaTodos=unAtuendo.prendas.get(0).getColorPrimario();
		return unAtuendo.prendas.stream().allMatch(prenda->prenda.getColorPrimario().equals(colorParaTodos));
		
	}
	

	@Override
	public void ponderarAtuendo(Atuendo unAtuendo, boolean condicion) {
		if(condicion)
			unAtuendo.setNivelDePonderacion(unAtuendo.getNivelDePonderacion()+1);


	}

	@Override
	public String getNombre() {
		// TODO Auto-generated method stub
		return nombre;
	}
	
	public CriterioMonoCromatico() {}

}
