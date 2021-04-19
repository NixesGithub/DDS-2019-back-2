package Criterios;

import java.io.IOException;
import java.util.List;

import Prendas.Prenda;
import net.aksingh.owmjapis.api.APIException;
import grupo1.utn.frba.dds.*;
/*
 * Este criterio chequea que tenga al menos una Prenda en la capa 1 por cada categoria. Es decir, no puede
 * tener directamente una campera sin remera.
 */

import javax.persistence.Entity;
@Entity
public class CriterioVestimentaMinima extends Criterios {

	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		
		List<Prenda> prendasSuperior =unAtuendo.getSuperior();
		List<Prenda> prendasInferior =unAtuendo.getInferior();
		List<Prenda> prendasCalzado =unAtuendo.getCalzado();
		boolean condicion;
		condicion= chequearListas(prendasSuperior, prendasInferior, prendasCalzado);
		ponderarAtuendo(unAtuendo, condicion);
		return condicion;
	}
	
	private boolean chequearListas(List<Prenda> superior, List<Prenda> inferior, List<Prenda> calzado) {
		
		return (porLoMenosUnaCapaUno(calzado) && porLoMenosUnaCapaUno(superior) && porLoMenosUnaCapaUno(inferior));
	}
	
	private boolean porLoMenosUnaCapaUno(List<Prenda> prendas) {
		return prendas.stream().mapToInt(unaPrenda -> unaPrenda.getTipo().getCapa()).filter(unNumero -> unNumero == 1).count() > 0;
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
	
	public CriterioVestimentaMinima() {} 

}
