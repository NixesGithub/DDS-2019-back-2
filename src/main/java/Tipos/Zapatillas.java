package Tipos;

import javax.persistence.Entity;

@Entity
public class Zapatillas extends Tipo {

	public Zapatillas(String desc, int suNivelAbrigo) {
		super(desc, suNivelAbrigo);
		this.setCapa(1);
	}

	@Override
	public int manejarCalor() {
		return getNivelAbrigo();
	}
	
	Zapatillas() {}

}
