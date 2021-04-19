package APIs;

public class ClimaXCiudad {
	
	private float maxima;
	private float minima;
	public float temperaturaActual;
	public String estadoAtmosferico;
	private Clima climaDia;
	private Clima climaNoche;
	
	
	public Clima getClimaDia() {
		return climaDia;
	}

	public void setClimaDia(Clima climaDia) {
		this.climaDia = climaDia;
	}

	public Clima getClimaNoche() {
		return climaNoche;
	}

	public void setClimaNoche(Clima climaNoche) {
		this.climaNoche = climaNoche;
	}

	public String getEstadoAtmosferico() {
		return estadoAtmosferico;
	}

	public void setEstadoAtmosferico(String estadoAtmosferico) {
		estadoAtmosferico = estadoAtmosferico;
	}

	public float getTemperaturaActual() {
		return temperaturaActual;
	}
	
	public void setTemperaturaActual(float temperaturaActual) {
		this.temperaturaActual = temperaturaActual;
	}

	public void setMaxima(float maxima) {
		this.maxima = maxima;
	}

	public void setMinima(float minima) {
		this.minima = minima;
	}

	public float getMaxima() {
		return maxima;
	}

	public float getMinima() {
		return minima;
	}
	

	
	

}
