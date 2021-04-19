package grupo1.utn.frba.dds;

import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import Criterios.*;

@Entity(name="Eventos")
@Table(name="Eventos")
public class Evento {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	@OneToMany(cascade = CascadeType.PERSIST)
	public List<Sugerencia> sugerenciasParaSalir = null; //Como queda esto con la aparicion de las 'Sugerencia's
	
	@Basic
	public Date fecha;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	private PlanificacionEvento planificacion;
	
	public PlanificacionEvento getPlanificacion() {
		return planificacion;
	}
	public String sugerenciasParaSalirString() {
		String retorno="Sugerencia Elegida:";
		retorno=retorno.concat(sugerenciasParaSalir.stream().filter(s->s.getEstadoActual().equals(Estado.ACEPTADO)).collect(Collectors.toList()).get(0).atuendo.getAtuendoString());
		return retorno;
		
	}
	public List<Sugerencia> getSugerenciasParaSalir() throws SugerenciaInvalidaException, SugerenciasNoGeneradasException {

		if(sugerenciasParaSalir==null) {
			throw new SugerenciasNoGeneradasException();
		} else {
			return sugerenciasParaSalir;
			
		}
	}
	
	public void pedirSugerencias() throws SugerenciaInvalidaException, SugerenciasNoGeneradasException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		List<Atuendo> listaASugerir= Sugeridor.getSugeridor().queMePongo(getGuardarropasAsociado(),getUsuarioAlQuePertenezco(),planificacion.getLugar(),planificacion.getFecha());	
		sugerenciasParaSalir = getUsuarioAlQuePertenezco().seleccionarSugerencias(listaASugerir, getGuardarropasAsociado());
	}
	
	public Evento(PlanificacionEvento planificacionEvento) {
		planificacion=planificacionEvento;
		fecha= planificacionEvento.getFecha();
	}
	public boolean estaParaAceptar() {
		
		boolean flag= sugerenciasParaSalir.stream().filter(s->s.getEstadoActual().equals(Estado.ACEPTADO)).collect(Collectors.toList()).isEmpty();
		return flag;
		
		
	}
	/* DEPRECATED
	public Evento() {
		//No hace nada, es para testear sin llamar al scheduler.
	}
	
	public Evento(String fechaDeEvento, String lugarDelEvento, Guardarropas unGuardarropas, List<Criterios> listaDeCriterios, long seRepiteCada, Usuario usuarioQueMeLlama) throws ParseException, FechaInvaldiaException, SugerenciaInvalidaException, SugerenciasNoGeneradasException {


		SimpleDateFormat fechaSinParsear = new SimpleDateFormat ("dd-MM-yyyy");
		fecha=fechaSinParsear.parse(fechaDeEvento);
		
		if(fecha.getTime() < new Date().getTime()) {

			throw new FechaInvaldiaException();

		}
		
		lugar=lugarDelEvento;
		guardarropasAsociado=unGuardarropas;
		criteriosAsociados=listaDeCriterios;
		usuarioAlQuePertenezco=usuarioQueMeLlama;
		//repeticion=seRepiteCada;
		
		//Creo el evento y hago el pedirSugerencia para "prender la mecha", si no me parece que 
		//ese metodo nunca se va a ejecutar entonces mas alla del delay y todo lo que le puse
		//nunca se enciende digamos.
	}
	*/

	public Usuario getUsuarioAlQuePertenezco() {
		return planificacion.getUsuarioAlQuePertenezco();
	}

	public Guardarropas getGuardarropasAsociado() {
		return planificacion.getGuardarropasAsociado();
	}

	public Date getFecha() {
		return planificacion.getFecha();
	}

	public String getLugar() {
		return planificacion.getLugar();
	}
	
	Evento() {}
}
