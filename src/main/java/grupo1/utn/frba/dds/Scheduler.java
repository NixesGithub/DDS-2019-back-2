package grupo1.utn.frba.dds;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Notificador.Notificador;

public class Scheduler {

	private static List<Evento> eventos= new ArrayList(); 
	
	public static List<Evento>getEventos() {
		return eventos;
	}
	/*DEPRECATED: ahora invocar se lo pido desde afuera y es de a un evento por vez.
	public static void invocar() {
	eventos.forEach(evento->{
		try {
			invocarEventos(evento);
		} catch (SugerenciaInvalidaException e) {
			e.printStackTrace();
		} catch (SugerenciasNoGeneradasException e) {
			e.printStackTrace();
		}
	});
	}
	
	public static void invocarEventos(Evento unEvento) throws SugerenciaInvalidaException, SugerenciasNoGeneradasException {
	long delay = unEvento.getFecha().getTime() -  fechaActual.getTime();
	ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
	ses.schedule(new Runnable(){
		@Override
		public void run() {
			//Va la peticion de sugerencias, algo asi:
			//Sugeridor.getSugeridor().queMePongo(guardarropasAsociado,criteriosAsociados);
			//no se que va aca
		}
	}, delay, TimeUnit.MILLISECONDS); // run in "delay" millis
	unEvento.pedirSugerencias();
	}
	*/
	
	/* DEPRECATED: esta logica quedo en el PlanificadorDeEventos
	public static void invocarEvento(Evento unEvento) throws SugerenciaInvalidaException, SugerenciasNoGeneradasException {
		long delay = unEvento.getFecha().getTime() -  new Date().getTime();
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.schedule(new Runnable(){
			@Override
			public void run() {
				try { //Esto me lo pidio Java
					List<Atuendo>listaAtuendosSugeridos=unEvento.pedirSugerencias();
					new Notificador(unEvento, listaAtuendosSugeridos);
				} catch (SugerenciaInvalidaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SugerenciasNoGeneradasException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GuardarropasNoAlzanzaFiltrosBasicosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Aca va la notificacion de que las prendas estan listas
				//Todas las cosas que quieras hacer sobre el evento...
				
				DEPRECATED: la repeticion la maneja el planificador
				if(unEvento.getRepeticion() > 0) {
					Date proximaFecha = new Date(unEvento.getRepeticion());
					try {
						Evento proximoEvento= new Evento(proximaFecha.toGMTString(),unEvento.getLugar(),unEvento.getGuardarropasAsociado(), unEvento.getCriteriosAsociados(), unEvento.getRepeticion()-1 , unEvento.getUsuarioAlQuePertenezco());
					} catch (ParseException | FechaInvaldiaException | SugerenciaInvalidaException
							| SugerenciasNoGeneradasException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					unEvento.setNuevaFecha(proximaFecha);
					try {
						invocar(unEvento);
					} catch (SugerenciaInvalidaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SugerenciasNoGeneradasException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				 catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FechaInvaldiaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, delay, TimeUnit.MILLISECONDS); // run in "delay" millis	
	}
	*/
	public static void invocarNotificacion(Notificador notificacion){
	
		long delay =  Math.max(notificacion.getEventoAsociado().getFecha().getTime() - new Date().getTime(), 0);
		//delay=0; 
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.schedule(new Runnable(){
			@Override
			public void run() { 
				notificacion.sugerenciasListas();//DESCOMENTAR TODO LO DEMAS CUANDO ESTE EN FUNCIONAMIENTO, CON EL DELAY EL TEST NO FUNCIONA
							}
		}, delay, TimeUnit.MILLISECONDS);

		
	}
}
