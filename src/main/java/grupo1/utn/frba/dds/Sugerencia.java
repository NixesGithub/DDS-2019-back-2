package grupo1.utn.frba.dds;

import javax.persistence.*;

@Entity(name="Sugerencias")
@Table(name="Sugerencias")
public class Sugerencia {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	@OneToOne(cascade = CascadeType.PERSIST)
	public Atuendo atuendo=null;
	@Enumerated(EnumType.STRING) 
	private Estado estadoActual;
	private Boolean calificada=false;
	
	public Boolean getCalificada() {
		return calificada;
	}
	public void setCalificada(Boolean rta) {
		this.calificada = rta;
	}
	
	public Atuendo getAtuendo() {
		return atuendo;
	}
	public void setAtuendo(Atuendo atuendo) {
		this.atuendo = atuendo;
	}
	public Estado getEstadoActual() {
		return estadoActual;
	}
	public void setEstadoActual(Estado estadoActual) {
		this.estadoActual = estadoActual;
	}
	
	public void aceptar() {
		setEstadoActual(Estado.ACEPTADO);
		}
	
	public void marcarComoUsada() {
		atuendo.prendas.forEach(p->p.setEstaSiendoUsada(true));
		
	}
	public void rechazar() {
		setEstadoActual(Estado.RECHAZADO);
		
	
	}
	
	public void marcarComoNoUsada() {
		atuendo.prendas.forEach(p->p.setEstaSiendoUsada(false));
	}
	public void deshacerUltimaAccion() {
		
		if(estadoActual.equals(Estado.ACEPTADO)) {
			this.rechazar();
		}else {
			this.aceptar();
		}
	}

}
