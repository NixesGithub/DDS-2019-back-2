package Controladores;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import APIs.AdaptadorAW;
import APIs.ClimaXCiudad;
import CoordinadorDeServicion.ServicioClima;
import Notificador.Notificador;

public class ClimaController implements Runnable{
	
	 public ClimaXCiudad climaAnterior;
	  Notificador notificacionAsociada;
	  
	
	ScheduledExecutorService ses;
	
	public ClimaController(Notificador notificador) {
		notificacionAsociada=notificador;
		
	}

	//en un main deberia dispararse la funcion programar() que creo que es la iniciadora
    public void programar()
    {
        ses = Executors.newScheduledThreadPool(1);
        // Ejecutar dentro de 0 milisegundos, repetir cada 3600 segundos
        ses.scheduleAtFixedRate(this, 0 * 1000, 60 * 1000, TimeUnit.MILLISECONDS);
    }

    public void run()
    {
    	System.out.println("Solicito el clima");
        this.solicitarAlertas();
        //TODO usar el scheduler para que cada x tiempo llame este metodo
    }
    
	
    
	public void solicitarAlertas() {
		if(ServicioClima.getAlerta(notificacionAsociada.getEventoAsociado().getLugar(), climaAnterior));
		this.notificacionAsociada.alertaMeteorologica();
		
	}
	
	
//	 void detener proceso de repetir cada hora()
//	    {
//	        ses.shutdown();
//	    }

	

}
