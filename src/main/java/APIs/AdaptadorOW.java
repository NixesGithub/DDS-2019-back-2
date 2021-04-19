package APIs;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

//import com.google.gson.JsonElement;
//import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
//import java.io.FileReader;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.*;
import java.net.CookieManager;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.aksingh.owmjapis.model.param.Cloud;
import net.aksingh.owmjapis.model.param.Rain;
import net.aksingh.owmjapis.model.param.Wind;


public class AdaptadorOW implements Adapter {
	
	private static AdaptadorOW adaptador=null;
	
		@Override
	public ClimaXCiudad pedirClima(String ciudad) throws IOException, APIException {
	
		OWM owm = new OWM("1716397db23e537af801947591f96fc9");
		
       CurrentWeather cwd = owm.currentWeatherByCityName(ciudad);
        
        ClimaXCiudad climaActual=new ClimaXCiudad();
        float maxima= cwd.getMainData().getTempMax().floatValue();
        float minima= cwd.getMainData().getTempMin().floatValue();
        Rain datoLLuvia = cwd.getRainData();
        Wind datoViento = cwd.getWindData();
        Cloud datoNubes = cwd.getCloudData();
        determinarEstado(datoLLuvia,datoNubes,datoViento,climaActual);
        
        maxima=(float) (maxima-273.15);
        minima=(float) (minima-273.15);
        float promedio= cwd.getMainData().getTemp().floatValue();
        promedio=(float) (promedio-273.15);
        climaActual.setMaxima(maxima);
        climaActual.setMinima(minima);
        climaActual.setTemperaturaActual(promedio);
        
		return climaActual;
		
		
	}
		





	private void determinarEstado(Rain datoLLuvia, Cloud datoNubes, Wind datoViento, ClimaXCiudad climaActual) {
			
		Clima clima;
		if(datoLLuvia.hasPrecipVol3h()) {
				
			clima= new ClimaLLuvioso();
			}
			if(!datoNubes.hasCloud()) {
				clima= new ClimaSoleado();
			}
			if(datoViento.hasSpeed()) {
				clima= new ClimaVentoso();
			}else {
				
				clima=new ClimaNormal();
				
			}
			
			climaActual.setClimaDia(clima);
			climaActual.setClimaNoche(clima);
		}






	public static AdaptadorOW getAdaptador() {
		
		if(adaptador==null) {
			
		 adaptador= new AdaptadorOW();
		
		
		}	
		 return adaptador;
		 }
			
		
		
	}

