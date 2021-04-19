package Tipos;

import javax.persistence.Entity;

@Entity
public class Remera extends Tipo {
	
	public Remera(String desc, int suNivelAbrigo) {
		super(desc, suNivelAbrigo);
		
	}

	@Override
	public int manejarCalor() {
		return getNivelAbrigo();
	}
	
	public Remera() {}

}
