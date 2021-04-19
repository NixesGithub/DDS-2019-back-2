package Notificador;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.*;

import Controladores.ClimaController;
import grupo1.utn.frba.dds.Atuendo;
import grupo1.utn.frba.dds.Evento;
import grupo1.utn.frba.dds.FechaInvaldiaException;
import grupo1.utn.frba.dds.GuardarropasNoAlzanzaFiltrosBasicosException;
import grupo1.utn.frba.dds.PlanificadorDeEventos;
import grupo1.utn.frba.dds.Sugerencia;
import grupo1.utn.frba.dds.SugerenciaInvalidaException;
import grupo1.utn.frba.dds.SugerenciasNoGeneradasException;
import grupo1.utn.frba.dds.Usuario;

@Entity(name="Notificaciones")
@Table(name="Notificaciones")
public class Notificador {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	private Evento eventoAsociado;
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Atuendo> atuendosAsociados;
	
	//hacer notificador para todos
	
	
	
	
	public Evento getEventoAsociado() {
		return eventoAsociado;
	}
	
	public void alertaMeteorologica() /*throws ParseException, FechaInvaldiaException, SugerenciaInvalidaException, SugerenciasNoGeneradasException */{
		/* respuesta=*/eventoAsociado.getUsuarioAlQuePertenezco().OcurrioUnaAlerta(this);
		/*
		 if(respuesta){
		 
		 
		Evento eventoNuevo= new Evento(eventoAsociado.getFecha().toGMTString(),eventoAsociado.getLugar(),eventoAsociado.getGuardarropasAsociado(),eventoAsociado.getCriteriosAsociados(),eventoAsociado.getRepeticion(),eventoAsociado.getUsuarioAlQuePertenezco());
		 //crea otro evento y le dice que haga el queMePongo 
		 }
		 
		 
		 */
		
	}
	public void sugerenciasListas(){
		eventoAsociado.getUsuarioAlQuePertenezco().seleccionarSugerencias(atuendosAsociados,eventoAsociado.getGuardarropasAsociado()); //gui�o gui�o

	}
	public Notificador(Evento eventoAsociado, List<Atuendo> listaAtuendosSugeridos) throws SugerenciaInvalidaException, SugerenciasNoGeneradasException, ParseException, FechaInvaldiaException, InterruptedException{
	this.eventoAsociado=eventoAsociado;
	ClimaController controlador= new ClimaController(this);
	controlador.programar();
	this.atuendosAsociados=(listaAtuendosSugeridos);
	PlanificadorDeEventos.getPlanificadorDeEventos().planificarNotificacion(this);
	
	}
	
		


	public void cambioDeTemperatura() {
		eventoAsociado.getUsuarioAlQuePertenezco().OcurrioUnaAlerta(this);//no deberia cambiar el tipo, me parece que rompe con el polimorfismo, si alguien me apoya saquele el parametro pls
		
	}
	
	public Notificador() {}
}
