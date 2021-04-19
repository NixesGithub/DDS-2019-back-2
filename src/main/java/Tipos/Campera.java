package Tipos;

import javax.persistence.*;

@Entity
public class Campera extends Tipo {

	public Campera(String desc, int suNivelAbrigo) {
		super(desc, suNivelAbrigo);
		this.setCapa(3);
	}

	@Override
	public int manejarCalor() {
		return getNivelAbrigo();
	}
	
	public Campera() {}

}
