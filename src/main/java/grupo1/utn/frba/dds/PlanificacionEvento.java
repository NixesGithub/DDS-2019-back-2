package grupo1.utn.frba.dds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.*;

import Criterios.CriterioEventoFormal;
import Criterios.Criterios;

@Entity(name="Planificaciones")
@Table(name="Planificaciones")
public class PlanificacionEvento {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	@Basic
	private Date fecha;
	@Basic
	private String lugar;
	@OneToOne(cascade = CascadeType.PERSIST)
	private Guardarropas guardarropasAsociado;
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<Criterios> criteriosAsociados = new ArrayList<Criterios>();
	@OneToOne
	private Usuario usuarioAlQuePertenezco;
	private boolean esFormal;
	private long repeticion;
	private String descripcion;
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String unaDesc) {
		descripcion = unaDesc;
	}
	
	public boolean aplica() {
		return (fecha.getTime() - new Date().getTime()) < 43200000; //"Aplica" si faltan menos de 12 horas para la fecha
	}

	public void ejecuta() throws SugerenciaInvalidaException, SugerenciasNoGeneradasException, ParseException, FechaInvaldiaException, InterruptedException {
		
		Evento eventoConcreto = new Evento(this);
		//eventoConcreto.pedirCosas;
		if(repeticion > 0) {
			replanificar();
		}else {
			
			this.usuarioAlQuePertenezco.planificaciones.remove(this);
			}
	}
	
	private void replanificar() throws SugerenciaInvalidaException, SugerenciasNoGeneradasException, ParseException, FechaInvaldiaException, InterruptedException {
		long nuevaFechaMS = fecha.getTime() + repeticion;
		Date nuevaFecha = new Date(nuevaFechaMS);
		fecha=nuevaFecha;
		PlanificadorDeEventos.getPlanificadorDeEventos().planificarEvento(this);
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public Guardarropas getGuardarropasAsociado() {
		return guardarropasAsociado;
	}

	public void setGuardarropasAsociado(Guardarropas guardarropasAsociado) {
		this.guardarropasAsociado = guardarropasAsociado;
	}

	public List<Criterios> getCriteriosAsociados() {
		return criteriosAsociados;
	}

	public void setCriteriosAsociados(List<Criterios> criteriosAsociados) {
		this.criteriosAsociados = criteriosAsociados;
	}

	public Usuario getUsuarioAlQuePertenezco() {
		return usuarioAlQuePertenezco;
	}

	public void setUsuarioAlQuePertenezco(Usuario usuarioAlQuePertenezco) {
		this.usuarioAlQuePertenezco = usuarioAlQuePertenezco;
	}

	public boolean isEsFormal() {
		return esFormal;
	}

	public void setEsFormal(boolean esFormal) {
		this.esFormal = esFormal;
	}

	public long getRepeticion() {
		return repeticion;
	}

	public void setRepeticion(long repeticion) {
		this.repeticion = repeticion;
	}

	public PlanificacionEvento(String fechaDeEvento, String lugarDelEvento, Guardarropas unGuardarropas, List<Criterios> listaDeCriterios,
			long seRepiteCada, boolean formalParam, Usuario usuario) throws ParseException, FechaInvaldiaException {
		
		SimpleDateFormat fechaSinParsear = new SimpleDateFormat ("dd-MM-yyyy");
		fecha=fechaSinParsear.parse(fechaDeEvento);
		if(fecha.getTime() < new Date().getTime())
			throw new FechaInvaldiaException();
		
		lugar=lugarDelEvento;
		guardarropasAsociado=unGuardarropas;
		criteriosAsociados=listaDeCriterios;
		usuarioAlQuePertenezco=usuario;
		repeticion=seRepiteCada;
		if(formalParam)
			criteriosAsociados.add(new CriterioEventoFormal());
		esFormal=formalParam;
	}
	
	public PlanificacionEvento() {}
}
