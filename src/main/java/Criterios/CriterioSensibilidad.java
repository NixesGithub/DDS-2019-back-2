package Criterios;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import grupo1.utn.frba.dds.Atuendo;
import grupo1.utn.frba.dds.Usuario;
import Prendas.*;
import net.aksingh.owmjapis.api.APIException;

import javax.persistence.Entity;
@Entity
public class CriterioSensibilidad extends Criterios {

	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		boolean condicion;
		condicion = todoBienAbrigado(unAtuendo, usuario);
		ponderarAtuendo(unAtuendo, condicion);
		return condicion;
	}

	@Override
	public void ponderarAtuendo(Atuendo unAtuendo, boolean condicion) {
			if(condicion)
				unAtuendo.setNivelDePonderacion(unAtuendo.getNivelDePonderacion()+1);	
	}
	
	private boolean todoBienAbrigado(Atuendo unAtuendo, Usuario usuario) {
		boolean cabezaOK = false, superiorOK = false, inferiorOK = false, calzadoOK = false, manosOK = false;
		List<Prenda> prendasCabeza;
		List<Prenda> prendasManos;
		
		prendasCabeza = unAtuendo.getAccesorios().stream().filter(unaPrenda -> unaPrenda.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Cabeza)).collect(Collectors.toList());
		prendasManos = unAtuendo.getAccesorios().stream().filter(unaPrenda -> unaPrenda.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Manos)).collect(Collectors.toList());
		
		if(usuario.getFeedback().getSensibilidadCabeza()>0) {
			for(Prenda unaPrenda : prendasCabeza) {
				cabezaOK = unaPrenda.getTipo().getNivelAbrigo() >= usuario.getFeedback().getSensibilidadCabeza();
				if(!cabezaOK)
					break;
			}
		} else {
			cabezaOK = true;
		}
		
		if(usuario.getFeedback().getSensibilidadManos()>0) {
			for(Prenda unaPrenda : prendasManos) {
				manosOK = unaPrenda.getTipo().getNivelAbrigo() >= usuario.getFeedback().getSensibilidadManos();
				if(!manosOK)
					break;
			}
		} else {
			manosOK = true;
		}
		
		if(usuario.getFeedback().getSensibilidadTorso()>0) {
			for(Prenda unaPrenda : unAtuendo.getSuperior()) {
				superiorOK = unaPrenda.getTipo().getNivelAbrigo() >= usuario.getFeedback().getSensibilidadTorso();
				if(!superiorOK)
					break;
			}
		} else {
			superiorOK = true;
		}
		
		if(usuario.getFeedback().getSensibilidadPiernas()>0) {
			for(Prenda unaPrenda : unAtuendo.getInferior()) {
				inferiorOK = unaPrenda.getTipo().getNivelAbrigo() >= usuario.getFeedback().getSensibilidadPiernas();
				if(!inferiorOK)
					break;
			}
		} else {
			inferiorOK = true;
		}
		
		if(usuario.getFeedback().getSensibilidadPies()>0) {
			for(Prenda unaPrenda : unAtuendo.getCalzado()) {
				calzadoOK = unaPrenda.getTipo().getNivelAbrigo() >= usuario.getFeedback().getSensibilidadPies();
				if(!calzadoOK)
					break;
			}
		} else {
			calzadoOK = true;
		}
		
		return cabezaOK && manosOK && superiorOK && inferiorOK && calzadoOK;
	}

	@Override
	public String getNombre() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CriterioSensibilidad() {}

}
