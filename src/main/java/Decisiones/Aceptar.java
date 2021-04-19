package Decisiones;

import grupo1.utn.frba.dds.Atuendo;

public class Aceptar implements Decision {

		Atuendo sobreQueHagoLaDecision;
	
	public Aceptar(Atuendo atuendo) {
		// TODO Auto-generated constructor stub
	
		sobreQueHagoLaDecision=atuendo;
	}

	@Override
	public void sugerencia() {
		//sobreQueHagoLaDecision.setAceptado(true);
		
	}

}

