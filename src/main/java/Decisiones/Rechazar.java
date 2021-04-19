package Decisiones;

import grupo1.utn.frba.dds.Atuendo;

public class Rechazar implements Decision {

	
	Atuendo unAtuendo;
	
	
	public Rechazar(Atuendo atuendo) {
		// TODO Auto-generated constructor stub
	
		unAtuendo=atuendo;
	}

	@Override
	public void sugerencia() {
		// TODO Auto-generated method stub
		//unAtuendo.setAceptado(false);
	
	}

	
}
