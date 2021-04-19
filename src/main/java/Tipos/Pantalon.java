package Tipos;

import javax.persistence.Entity;

@Entity
public class Pantalon extends Tipo {

	public Pantalon(String desc, int suNivelAbrigo) {
		super(desc, suNivelAbrigo);
		this.setCapa(1);
	}

	@Override
	public int manejarCalor() {
		return getNivelAbrigo();
	}
	
	public Pantalon() {}

}
