package APIs;

import Criterios.CriterioClimaSoleado;
import Criterios.Criterios;

public class ClimaSoleado implements Clima {

	

	@Override
	public Criterios aplicar() {
		return new CriterioClimaSoleado();

	}

}
