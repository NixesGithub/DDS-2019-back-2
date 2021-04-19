package Criterios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import Prendas.Descripcion;
import Prendas.Prenda;
import grupo1.utn.frba.dds.Atuendo;
import grupo1.utn.frba.dds.Usuario;
import net.aksingh.owmjapis.api.APIException;

@Entity
public class CriterioColorinche extends Criterios {
	
	public String nombre="Colorinche";

	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		boolean condicion;
		condicion=todosConDistintoColor(unAtuendo);
		ponderarAtuendo(unAtuendo, condicion);
		return condicion;
	}

	private boolean todosConDistintoColor(Atuendo unAtuendo) {
		return unAtuendo.prendas.stream().map(prenda->prenda.getColorPrimario()).distinct().collect(Collectors.toList()).size()==unAtuendo.prendas.size();
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
	
	public CriterioColorinche() {}
}
