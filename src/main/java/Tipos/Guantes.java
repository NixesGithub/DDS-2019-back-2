package Tipos;

import javax.persistence.Entity;

@Entity
public class Guantes extends Tipo{
	
	public Guantes(String desc, int suNivelAbrigo) {
		super(desc, suNivelAbrigo);
		this.setCapa(1);
	}

	@Override
	public int manejarCalor() {
		return getNivelAbrigo();
	}
	
	public Guantes() {}
}
