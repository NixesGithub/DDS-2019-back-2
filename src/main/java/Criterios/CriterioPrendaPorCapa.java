package Criterios;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import Prendas.Prenda;
import grupo1.utn.frba.dds.*;
import net.aksingh.owmjapis.api.APIException;

import javax.persistence.Entity;
@Entity
public class CriterioPrendaPorCapa extends Criterios {

	boolean flag;
	
	@Override
public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		
		flag = true;
		List<Prenda> prendasSuperior =unAtuendo.getSuperior();
		List<Prenda> prendasInferior =unAtuendo.getInferior();
		List<Prenda> prendasCalzado =unAtuendo.getCalzado();
		boolean condicion;
		condicion= chequearListas(prendasSuperior, prendasInferior, prendasCalzado);
		ponderarAtuendo(unAtuendo,condicion);
		return condicion;
	}
	
	private boolean chequearListas(List<Prenda> superior, List<Prenda> inferior, List<Prenda> calzado) {
		
		return (prendaUnicaPorNivel(calzado) && prendaUnicaPorNivel(superior) && prendaUnicaPorNivel(inferior));
	}
	
	private boolean prendaUnicaPorNivel(List<Prenda> prendas) {
		 List<Integer> capas = prendas.stream().mapToInt(unaPrenda -> unaPrenda.getTipo().getCapa()).boxed().collect(Collectors.toList());
		 capas.forEach(unaCapa -> estaUnaSolaVez(unaCapa, capas));
		 return flag;
	}

	private void estaUnaSolaVez(Integer laCapa, List<Integer> capas) {
		flag = flag && (capas.stream().filter(unaCapa -> unaCapa.equals(laCapa)).count() == 1);
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
	
	public CriterioPrendaPorCapa() {}
}