package APIs;

import Criterios.CriterioClimaVentoso;
import Criterios.Criterios;

public class ClimaVentoso implements Clima {
	
	

	
	

	@Override
	public Criterios aplicar() {
		return new CriterioClimaVentoso();
	}

}
