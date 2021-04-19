package grupo1.utn.frba.dds;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Criterios.Criterios;
import Notificador.Notificador;
import Prendas.Prenda;

public class PlanificadorDeEventos { //Es un singleton

	
	private static PlanificadorDeEventos planificadorDeEventos=null;
	
	public static PlanificadorDeEventos getPlanificadorDeEventos() {
		if(planificadorDeEventos==null)
			planificadorDeEventos= new PlanificadorDeEventos();
			
		return planificadorDeEventos;
	}
	
	private PlanificadorDeEventos() {}
	
	public void planificarEvento(PlanificacionEvento unaPlanificacionEvento) throws SugerenciaInvalidaException, SugerenciasNoGeneradasException, ParseException, FechaInvaldiaException, InterruptedException {
		
		Thread t1 = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	
		    	boolean eventoEjecutado = false;
		    	
		    	while(!eventoEjecutado) {
					if(unaPlanificacionEvento.aplica()) {
						try {
							unaPlanificacionEvento.ejecuta();
						} catch (SugerenciaInvalidaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SugerenciasNoGeneradasException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FechaInvaldiaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						eventoEjecutado = true;
					}
					try {
						TimeUnit.MILLISECONDS.sleep(3600000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //Espera una hora y vuelve a hacer el loop
				}
		    }
		});  
		t1.start();

		/* DEPRECATED
		if(unEvento.getRepeticion() > 0) {
			Scheduler.invocarEvento(unEvento);
			long nuevaFechaMS = unEvento.getFecha().getTime() + unEvento.getRepeticion();
			Date nuevaFecha = new Date(nuevaFechaMS);
			Evento nuevoEvento = new Evento(nuevaFecha.toString(), unEvento.getLugar(), unEvento.getGuardarropasAsociado(), unEvento.getCriteriosAsociados(), unEvento.getRepeticion(), unEvento.getUsuarioAlQuePertenezco());
			TimeUnit.MILLISECONDS.sleep(unEvento.getRepeticion());
			planificarEvento(nuevoEvento);
		} else {
			Scheduler.invocarEvento(unEvento);
		}
		*/
	}
	
	
	public void planificarNotificacion(Notificador notificacion) throws SugerenciaInvalidaException, SugerenciasNoGeneradasException, ParseException, FechaInvaldiaException, InterruptedException {
		
			Scheduler.invocarNotificacion(notificacion);
		
	}
	
}
/*
long delay = unEvento.getFecha().getTime() -  fechaActual.getTime();
ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
ses.schedule(new Runnable(){
	@Override
	public void run() {
		
	}
}, delay, TimeUnit.MILLISECONDS); // run in "delay" millis
*/
