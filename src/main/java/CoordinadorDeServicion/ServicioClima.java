package CoordinadorDeServicion;
import java.io.IOException;

import APIs.*;
import net.aksingh.owmjapis.api.APIException;

public class ServicioClima {
	
	static AdaptadorAW adaptadorAW = null;
	static AdaptadorOW adaptadorOW = null;
	
	public static ClimaXCiudad getClima(String ciudad){


		ClimaXCiudad climaActual=new ClimaXCiudad();
		try {
			if(adaptadorAW == null)
				adaptadorAW = AdaptadorAW.getAdaptador();
			climaActual=adaptadorAW.pedirClima(ciudad);
		} catch (IOException e) {
			
			try {
				if(adaptadorOW == null)
					AdaptadorOW.getAdaptador();
				climaActual=AdaptadorOW.getAdaptador().pedirClima(ciudad);
			} catch (IOException | APIException e1) {
				// no sabemos que hacer si fallan las 2
				e1.printStackTrace();
			}
			//nota mental revisar bien donde dejamos el dto para no pedir info de mas
		}
		
		return climaActual;
	}
	
	public static float getTemperatura(String ciudad) {
		
		ClimaXCiudad climaActual=getClima(ciudad);
		return climaActual.getTemperaturaActual();
		
	}

	public static void setAW(AdaptadorAW mockAW) {
		adaptadorAW=mockAW;

		
	}

	public static void setOW(AdaptadorOW mockOW) {
		adaptadorOW=mockOW;
	}
	
	public static boolean getAlerta(String ciudad, ClimaXCiudad climaAnterior) {
		ClimaXCiudad climaActual = ServicioClima.getClima(ciudad);
		if(climaAnterior==null) {
			climaAnterior=climaActual;

		}
		return compararClima(climaActual, climaAnterior);
		//TODO: discutir esto, porque tengo que guardarme el estado anterior en esta clase static...
		
	}

	private static boolean compararClima(ClimaXCiudad climaActual, ClimaXCiudad climaAnterior) {
		if(cambioEnElEstadoAtmosferico(climaActual, climaAnterior)||cambioLaTemperaturaConsiderablemente(climaActual, climaAnterior)) {
			return true;
			
			}else {
				//si no hubo cambios de clima se guarda el dato y se corta el hilo
				climaAnterior=climaActual;
				return false;
			}
		
	}

	private static boolean cambioLaTemperaturaConsiderablemente(ClimaXCiudad climaActual, ClimaXCiudad climaAnterior) {
		float diferencia = climaActual.temperaturaActual - climaAnterior.temperaturaActual;
		 if(diferencia<0) {
			 diferencia *= -1;
		 }
			 return diferencia>=10;
	}

	private static boolean cambioEnElEstadoAtmosferico(ClimaXCiudad climaActual, ClimaXCiudad climaAnterior) {
		return climaAnterior.estadoAtmosferico != climaAnterior.estadoAtmosferico;	}

}
