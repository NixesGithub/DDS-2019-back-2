package grupo1.utn.frba.dds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.*;

import javax.persistence.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import Criterios.Criterios;
import Decisiones.Aceptar;
import Decisiones.Decision;
import Decisiones.DeshacerDecisionSobre;
import Decisiones.Rechazar;
import ExceptionsPrendas.PrendaInvalidaException;
import ExceptionsPrendas.PrendaInvalidaPorMaterialException;
import ExceptionsPrendas.PrendaInvalidaPorTipoException;
import ExceptionsPrendas.PrendaYaExistenteException;
import Notificador.Notificador;
import Prendas.Prenda;

import java.awt.Color;
import java.awt.color.*;
import java.text.ParseException;
import java.time.LocalDateTime;

/*
 * ----------------------------------------------------
 * Ezequiel(12/05/19): 
 * 		Hice el modelo basico del Usuario
 * ----------------------------------------------------
 * 
 * 
 */
@Entity(name="Usuarios")
@Table(name="Usuarios")
public class Usuario {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	@OneToMany(cascade = CascadeType.PERSIST)
	public List<Sugerencia> sugerencias;
	@OneToMany(cascade = CascadeType.PERSIST)
	List<Guardarropas> guardarropas;
	@OneToMany
	List <Criterios> criterios=new ArrayList();
	@Transient
	List<Evento> eventos= new ArrayList();//esto es necesario?
	@Transient
	boolean esPremium;
	@Transient
	List<Decision>decisiones= new ArrayList();
	@Basic
	String lugar;
	//private List<Sugerencia> sugerencias=new ArrayList();
	//A mayor sensibilidad "mas frio" siente el usuario
	@OneToOne(cascade = CascadeType.PERSIST)
	private Feedback feedback=new Feedback();
	@Transient
	private List<Atuendo>sugerenciasAValidar=new ArrayList();
	@OneToMany(cascade = CascadeType.ALL)
	public List<Notificador> notificaciones= new ArrayList();
	
	@Basic
	String mail;
	@Basic
	String password;
	@OneToMany(cascade = CascadeType.ALL)
	public List<PlanificacionEvento> planificaciones=new ArrayList();
	@Transient
	public Prenda prendaFallida;
	
	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}
	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public List<Atuendo> getSugerenciasAValidar() {
		return sugerenciasAValidar;
	}

	public void setSugerenciasAValidar(List<Atuendo> sugerenciasAValidar) {
		this.sugerenciasAValidar = sugerenciasAValidar;
	}	
	public Feedback getFeedback() {
		return feedback;
	}
	
	public void setFeedback(Feedback unFeedback) {
		feedback.setSensibilidadCabeza((unFeedback.getSensibilidadCabeza() + feedback.getSensibilidadCabeza())/2);
		feedback.setSensibilidadGlobal((unFeedback.getSensibilidadGlobal() + feedback.getSensibilidadGlobal())/2);
		feedback.setSensibilidadManos((unFeedback.getSensibilidadManos() + feedback.getSensibilidadManos())/2);
		feedback.setSensibilidadPiernas((unFeedback.getSensibilidadPiernas() + feedback.getSensibilidadPiernas())/2);
		feedback.setSensibilidadPies((unFeedback.getSensibilidadPies() + feedback.getSensibilidadPies())/2);
		feedback.setSensibilidadTorso((unFeedback.getSensibilidadTorso() + feedback.getSensibilidadTorso())/2);
	}

	public List<Guardarropas> getListaDeGuardarropas() {
		return guardarropas;
	}
	/*
	public List<Sugerencia> getSugerencias() {
		return sugerencias;
	}
	*/
	public List<Guardarropas> getGuardarropas() {
		return guardarropas;
	}

	public List<Criterios> getCriterios() {
		return criterios;
	}



	public List<Evento> getEventos() {
		return eventos;
	}



	public boolean isEsPremium() {
		return esPremium;
	}



	public List<Decision> getDecisiones() {
		return decisiones;
	}



	public void AgregarEvento(Evento evento) {
		
		eventos.add(evento);//puede ser medio al pedo por ahora pero me gusta que tenga un nombre que no sea add.
							// por ahi depsues empieza a diferir los eventos que podes agregar como usuario primiun o no
		
		
	}
	
	public Guardarropas getUnGuardarropas(String nombreDelGuardarropas) throws GuardarropasNoEncontradoException {
		
		Guardarropas guardarropasHallado = guardarropas.stream()
											.filter(unGuardarropas -> nombreDelGuardarropas
											.equals(unGuardarropas.getNombre()))
											.findAny().orElse(null);
		if(guardarropasHallado == null)
			throw new GuardarropasNoEncontradoException();
		
		return guardarropasHallado;
	}
	
	/*public void crearPrenda(Color colorPrimario, Color colorSecundario, String tipoMaterial ,Tipo tipo, Categoria categoria, String nombreDelGuardarropas)throws PrendaInvalidaException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, GuardarropasNoEncontradoException, PrendaYaExistenteException {
		Prenda unaPrenda = new Prenda(colorPrimario, colorSecundario, tipoMaterial , tipo, categoria);
		this.addPrendaAUnGuardarropas(unaPrenda, nombreDelGuardarropas);
		me da miedo borrar pero es cosa misma de la prenda, no tiene sentido tenerlo aca como decia german
	}*/
	
	public void addPrendaAUnGuardarropas(Prenda unaPrenda, String nombreDelGuardarropas) throws GuardarropasNoEncontradoException, PrendaYaExistenteException, NoEsPremiumException {
		
		Guardarropas guardarropa = this.getUnGuardarropas(nombreDelGuardarropas);
		if(!esPremium&&guardarropa.prendas.size()>=50)//Este size podra cambiar? En los requerimientos no dice cuanto es el maximo.
				throw new NoEsPremiumException();// quizas flasheo pero aca re estaria para mandarle el msjito de premiun por pantalla 
			
		guardarropa.addPrenda(unaPrenda);
	}
	
	/*
	public void elegirDesiciones(List<Atuendo>atuendosSugeridos) {
		
		
		atuendosSugeridos.forEach(sugerencia->{
			try {
				this.validar(sugerencia);
			} catch (DecisionInvalidaException e) {

				e.printStackTrace();
			}
		});
		
		
		this.realizarDecisiones();
		
	}
	
	public void realizarDecision(Decision unaDecision) throws DecisionInvalidaException {
		
		
		unaDecision.sugerencia();
		
	}
	
	public void realizarDecisiones() {
		
		decisiones.forEach(decision->{
			try {
				this.realizarDecision(decision);
			} catch (DecisionInvalidaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		sugerenciasAceptadas=this.filtrarSugerencias(atuendosSugeridos);

	}

*/
	public List<Sugerencia> pedirSugerencias(String nombreDelGuardarropas) throws GuardarropasNoEncontradoException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		Guardarropas guardarropas = this.getUnGuardarropas(nombreDelGuardarropas);
		Date date=new Date();
		List<Sugerencia> sugerencias = this.seleccionarSugerencias(Sugeridor.getSugeridor().queMePongo(guardarropas,this,this.lugar, date),guardarropas);
		return sugerencias;
	}
	
	
	public List<Sugerencia> seleccionarSugerencias(List<Atuendo> list,Guardarropas guardarropas) {
		
		List<Sugerencia> sugerencias = new ArrayList<Sugerencia>();
		list.forEach(atuendo->crearSugerenciaConEstado(atuendo,sugerencias));
		/*
		sugerencias.stream().filter(atuendo->atuendo.getEstadoActual().equals(Estado.RECHAZADO)).forEach(sugerencia->sugerencia.marcarComoNoUsada());
		sugerencias.stream().filter(atuendo->atuendo.getEstadoActual().equals(Estado.ACEPTADO)).forEach(sugerencia->sugerencia.marcarComoUsada());
		*/
		guardarropas.EstaOcupado=false;
		
		return sugerencias;
	}



	private Sugerencia crearSugerenciaConEstado(Atuendo atuendo, List<Sugerencia> sugerencias) {
		
		Sugerencia unaSugerencia= new Sugerencia();
		unaSugerencia.setAtuendo(atuendo);
		unaSugerencia.aceptar();//es una interaccion con el usuario
		sugerencias.add(unaSugerencia);//Queremos que se agregue 
		return unaSugerencia;
	}

	public void calificarSugerencia() {
		//Cambia la temperatura relativa del Usuario
	}
	//Quizas el nombre de este metodo aparente que hace cosas que en realidad no hace,
	//sin embargo me parece razonable teniendo en cuenta que responde al requerimiento
	//y que calificar se traduce simplemente en si el Usuario estuvo bien o tuvo calor/frio

	public Usuario(String nombreGuardarropasInicial,String ciudad) {
		Guardarropas guardarropasInicial = new Guardarropas(nombreGuardarropasInicial);
		List<Guardarropas> listaDeGuardarropas = new ArrayList<Guardarropas>();
		listaDeGuardarropas.add(guardarropasInicial);
		guardarropas = listaDeGuardarropas;
		esPremium=false;
		lugar=ciudad;
	}

	public void OcurrioUnaAlerta(Notificador notificador) {
		notificaciones.add(notificador);
		
	}
	
	public void planificarEvento(String fechaDeEvento, String lugarDelEvento, Guardarropas unGuardarropas, List<Criterios> listaDeCriterios, long seRepiteCada, boolean esFormal) throws ParseException, FechaInvaldiaException, SugerenciaInvalidaException, SugerenciasNoGeneradasException, InterruptedException {
		
		PlanificacionEvento unaPlanificacion= new PlanificacionEvento(fechaDeEvento, lugarDelEvento, unGuardarropas, listaDeCriterios, seRepiteCada, esFormal, this);
		PlanificadorDeEventos.getPlanificadorDeEventos().planificarEvento(unaPlanificacion);
		planificaciones.add(unaPlanificacion);
		//aca esta el tema
	}
	
	//"Default constructor" porque Hibernate lo necesita
	public Usuario() {}
	
/*
	public List<Atuendo> filtrarSugerencias(List<Atuendo> sugerenciasParaSalir) {
		
		return sugerenciasParaSalir.stream().filter(atuendo-> atuendo.isAceptado()).collect(Collectors.toList());
		
	}

	

	public void validar(Atuendo sugerencia) throws DecisionInvalidaException {
	
		
		sugerencia.mostrar();
		System.out.println("Escriba que desea efectuar: Deshacer Anterior; Aceptar ; Rechazar");
		 
		String linea= this.escanearLinea();
		if(linea.equalsIgnoreCase("ACEPTAR")) {
	    	 
	    	 Aceptar aceptarSugerencia= new Aceptar(sugerencia);
	    	 decisiones.add(aceptarSugerencia);
	     }else  if(linea.equalsIgnoreCase("RECHAZAR")){
	    	 
	    	 Rechazar rechazarSugerencia= new Rechazar(sugerencia);
	    	 decisiones.add(rechazarSugerencia);
	    		 
	    		 
	    	 }else {
	    		 
	    		 int index=atuendosSugeridos.indexOf(sugerencia);
	    		 if(index>0) {
	    		 DeshacerDecisionSobre deshacerSugerencia= new DeshacerDecisionSobre(atuendosSugeridos.get(index-1), this);
		    	 decisiones.add(deshacerSugerencia);
	    		 }else {
	    			 
	    			 throw new DecisionInvalidaException();
	    			 
	    		 } this.validar(sugerencia);
	    		 
	    	 }
	    	 
	    	if(atuendosSugeridos.indexOf(sugerencia)==atuendosSugeridos.size()) {
	    		
	    		System.out.println("�Desea deshacer su ultima decision?");
	    		String ultimaLinea=escanearLinea();
	   		 	if(ultimaLinea.equalsIgnoreCase("SI"));
	   		 	DeshacerDecisionSobre deshacerSugerencia= new DeshacerDecisionSobre(sugerencia, this);
	   		 	decisiones.add(deshacerSugerencia);
    		 
	    		
	    		
	    	} 
	   	}



	public String escanearLinea() {
		// TODO Auto-generated method stub
		Scanner entradaEscaner = new Scanner (System.in);
	     String linea=entradaEscaner.nextLine().toString().toUpperCase();
		return linea; 
	}

	
*/

	
	
	
}
