package APIs;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.aksingh.owmjapis.api.APIException;

import org.apache.commons.lang3.StringUtils;

public class AdaptadorAW implements Adapter {
	
	private static AdaptadorAW adaptador=null;

	

	@Override
	public ClimaXCiudad pedirClima(String ciudad) throws IOException {//esto es un copypaste del otro pero con la otra api total el singleton ya lo tiene
				
		String idCity=getidCiudad(ciudad);
		String url = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/"+idCity+"?apikey=THbEFylYKTVyDzcaEY1Bzg4F0RwwtCMG"; 
		//http://dataservice.accuweather.com/forecasts/v1/daily/5day/{locationKey} puede ser 5,10,15 o 20 dias
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse resp = null;
        
        	
            resp = client.execute(get);
            HttpEntity entity = resp.getEntity();
            	
            ClimaXCiudad climaActual=new ClimaXCiudad();
            	
            JsonObject objeto= null;
            	JsonObject responseObj = null;
            	JsonElement cosa=null;
            	String responseStr = IOUtils.toString(entity.getContent(), "UTF-8");
                if (responseStr != null && !responseStr.isEmpty()) {
                    JsonParser parser = new JsonParser();
                    responseObj = parser.parse(responseStr).getAsJsonObject();
                    cosa= responseObj.get("DailyForecasts");
                    objeto= cosa.getAsJsonArray().get(0).getAsJsonObject();
                    cosa=(objeto.get("Temperature"));
  
                    JsonObject objeto1=cosa.getAsJsonObject();
                    JsonElement cosa2=objeto1.get("Maximum");
                    
                    JsonObject objeto2=cosa2.getAsJsonObject();
                    float maxima=objeto2.get("Value").getAsFloat();
                    maxima= (maxima-32)* 5/9;// pasamos a celcius
                    
                    climaActual.setMaxima(maxima);
                    
                    objeto1=cosa.getAsJsonObject();
                    cosa2=objeto1.get("Minimum");
                    
                    objeto2=cosa2.getAsJsonObject();
                    float minima=(objeto2.get("Value").getAsFloat());
                    minima= (minima - 32) * 5/9 ;// pasamos a celcius se pude delegar
                    
                    climaActual.setMinima(minima);
                    float promedio= (float) ((maxima+minima)*0.5);
                    climaActual.setTemperaturaActual(promedio); 
                    
                     
                    JsonElement cosa3=objeto.get("Day");
                    JsonObject objeto3=cosa3.getAsJsonObject();
                    climaActual.setEstadoAtmosferico(objeto3.get("IconPhrase").getAsString());
                    //esto es solo del dia quizas habria que ver como manipularlo para poder sacarlo como 
                    String climaDia=objeto3.get("IconPhrase").getAsString();
                    
                    // ver el tema de la noche/dia que hay inconsistencia, con el otro adaptador (OW) 
                    
                    JsonElement cosa4=objeto.get("Night");
                    JsonObject objeto4=cosa4.getAsJsonObject();
                    String climaNoche=objeto4.get("IconPhrase").getAsString();  
                     
                     seleccionarClima(climaActual,climaDia,climaNoche);
                     
                     
                     
                   //TODO:falta lo de climaActual.setClimaActual(clima)
                   
                }
                //TODO:nota mental, arreglar esto y rellenar bien el dto
	return climaActual;
	}            
           

			
	
	

	private void seleccionarClima(ClimaXCiudad climaActual, String climaNoche, String climaDia) {
		Clima Dia;
		Clima Noche;
		
		//TODO:duplicacion de codigo DETECTED: corregir 
		switch(climaNoche) {
		
		case "Cloudy":
					
					Noche= new ClimaVentoso();
					break;
					
		case "Rain":
					
					Noche= new ClimaLLuvioso();
					break;
					
		case "clear":
					Noche= new ClimaSoleado();
					break;
					
		default:
					Noche= new ClimaNormal();
					break;
				
				}
		
		switch(climaDia) {
	
		
		
		case "Cloudy":
			
			Dia= new ClimaVentoso();
			break;
			//TODO:esto esta dudoso, preguntar al profe
			
		case "Rain":
			
			Dia= new ClimaLLuvioso();
			break;
			
		case "Sunny":
				Dia= new ClimaSoleado();
			break;
			
		default:
				Dia= new ClimaNormal();
			break;
		
		}
		
		
		
		climaActual.setClimaDia(Dia);
		climaActual.setClimaNoche(Noche);
		
	}






	public String getidCiudad(String ciudad) throws ClientProtocolException, IOException {
		String url = "http://dataservice.accuweather.com/locations/v1/topcities/150?apikey=THbEFylYKTVyDzcaEY1Bzg4F0RwwtCMG"; 
		
		CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse resp = null;
        
        	
            resp = client.execute(get);
            HttpEntity entity = resp.getEntity();
            
            List<String> respuesta= new ArrayList();
        	JsonArray array= null;
        	JsonObject responseObj = null;
        	JsonElement cosa=null;
        	String responseStr = IOUtils.toString(entity.getContent(), "UTF-8");
            if (responseStr != null && !responseStr.isEmpty()) {
                JsonParser parser = new JsonParser();
                array = parser.parse(responseStr).getAsJsonArray();
                String ciudadId=filtrarid(ciudad,array);
            
      return ciudadId;
	}
			return "7894";
            
	}






	private String filtrarid(String ciudad, JsonArray array) {
		for(int i=0;i<array.size();i++) {
			
			JsonObject unaCiudad=array.get(i).getAsJsonObject();
			JsonElement nombre=unaCiudad.get("EnglishName");
			if(nombre.getAsString().equalsIgnoreCase(ciudad)) {
				
				return unaCiudad.get("Key").getAsString();
			}
			
		}
		return null;
	}






	public static AdaptadorAW getAdaptador() {
		
		if(adaptador==null) {
			
		 adaptador= new AdaptadorAW();
		
		
		}	
		 return adaptador;
		 }





		
		
		
	}
