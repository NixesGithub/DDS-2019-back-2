package APIs;

import Criterios.*;

public class ClimaNormal implements Clima {

	
	@Override
	public Criterios aplicar() {
		return new CriterioNulo();
	}

}
