package APIs;

import Criterios.CriterioClimaLLuvioso;
import Criterios.Criterios;

public class ClimaLLuvioso implements Clima {

	

	@Override
	public Criterios aplicar() {
		return new CriterioClimaLLuvioso();
		
	}

}


