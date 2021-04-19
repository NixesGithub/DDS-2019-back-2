package Criterios;

import java.io.IOException;

import APIs.AdaptadorAW;
import APIs.AdaptadorOW;
import APIs.Adapter;
import grupo1.utn.frba.dds.Atuendo;
import net.aksingh.owmjapis.api.APIException;
import CoordinadorDeServicion.*;
import grupo1.utn.frba.dds.*;

import javax.persistence.*;
@Entity
public class CriterioTemperatura extends Criterios {
	
	@Transient
	public AdaptadorAW adaptadorAW = AdaptadorAW.getAdaptador(); //Habria que deprecar estos atributos?
	@Transient
	public AdaptadorOW adaptadorOW = AdaptadorOW.getAdaptador();
	public String ciudad;
	public String nombre="Criterio para Temperaturas Estables";
	
	public CriterioTemperatura(String lugar) {
		// TODO Auto-generated constructor stub
		ciudad=lugar;
	}

	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		//Segun la temperatura en Celsius, el nivel de abrigo minimo es temperatura
		//y el nivel de abrigo maximo es temperatura
		int nivelDeAbrigoDelAtuendo = unAtuendo.getNivelDeAbrigo();
		float temperatura=ServicioClima.getTemperatura(ciudad) - usuario.getFeedback().getSensibilidadGlobal();
		boolean condicion;
		if(haceFrio(temperatura)) {
			condicion=nivelDeAbrigoDelAtuendo>=temperatura*2  && nivelDeAbrigoDelAtuendo<temperatura+15;
			
			
		}else {
			
		condicion= 	nivelDeAbrigoDelAtuendo<temperatura/2 && nivelDeAbrigoDelAtuendo>=5;
			
		}
		
		ponderarAtuendo(unAtuendo, condicion);
		
		return condicion;
	}

	private boolean haceFrio(Float temperatura) {
	
	
		return temperatura< 15.0 ;
		
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
	
	public CriterioTemperatura() {}
}
