package Tipos;

public class TipoDefault extends Tipo {

	public TipoDefault(String desc, int suNivelAbrigo) {
		super(desc, suNivelAbrigo);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int manejarCalor() {
		return getNivelAbrigo();
	}

}
