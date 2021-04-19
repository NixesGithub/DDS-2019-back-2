package Criterios;

import java.io.IOException;

import javax.persistence.Entity;

import grupo1.utn.frba.dds.Atuendo;
import grupo1.utn.frba.dds.Usuario;
import net.aksingh.owmjapis.api.APIException;

@Entity
public class CriterioNulo extends Criterios {


	public String nombre="No hay mas criterios";
	@Override
	public boolean aplicar(Atuendo unAtuendo, Usuario usuario) throws IOException, APIException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void ponderarAtuendo(Atuendo unAtuendo, boolean condicion) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNombre() {
		// TODO Auto-generated method stub
		return nombre;
	}
	
	public CriterioNulo() {}

}
