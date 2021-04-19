package Decisiones;

import grupo1.utn.frba.dds.Atuendo;
import grupo1.utn.frba.dds.DecisionInvalidaException;
import grupo1.utn.frba.dds.Usuario;

public class DeshacerDecisionSobre implements Decision {
	
	Atuendo unAtuendo;
	Usuario usuarioAlQuePertenezco;
	
	public DeshacerDecisionSobre(Atuendo atuendo, Usuario usuario) {
		
		usuarioAlQuePertenezco=usuario;
		unAtuendo=atuendo;
	}

	
	@Override
	public void sugerencia() throws DecisionInvalidaException {
		
		//usuarioAlQuePertenezco.validar(unAtuendo);
		usuarioAlQuePertenezco.getDecisiones().remove(this);
	}


	

}
