package grupo1.utn.frba.dds;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="Feedback")
@Table(name="Feedback")
public class Feedback {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private int sensibilidadGlobal=0;
	private int sensibilidadCabeza=0;
	private int sensibilidadTorso=0;
	private int sensibilidadPiernas=0;
	private int sensibilidadPies=0;
	private int sensibilidadManos=0;
	
	public int getSensibilidadGlobal() {
		return sensibilidadGlobal;
	}
	public void setSensibilidadGlobal(int sensibilidadGlobal) {
		this.sensibilidadGlobal = sensibilidadGlobal;
	}
	public int getSensibilidadCabeza() {
		return sensibilidadCabeza;
	}
	public void setSensibilidadCabeza(int sensibilidadCabeza) {
		this.sensibilidadCabeza = sensibilidadCabeza;
	}
	public int getSensibilidadTorso() {
		return sensibilidadTorso;
	}
	public void setSensibilidadTorso(int sensibilidadTorso) {
		this.sensibilidadTorso = sensibilidadTorso;
	}
	public int getSensibilidadPiernas() {
		return sensibilidadPiernas;
	}
	public void setSensibilidadPiernas(int sensibilidadPiernas) {
		this.sensibilidadPiernas = sensibilidadPiernas;
	}
	public int getSensibilidadPies() {
		return sensibilidadPies;
	}
	public void setSensibilidadPies(int sensibilidadPies) {
		this.sensibilidadPies = sensibilidadPies;
	}
	public int getSensibilidadManos() {
		return sensibilidadManos;
	}
	public void setSensibilidadManos(int sensibilidadManos) {
		this.sensibilidadManos = sensibilidadManos;
	}
	
	public Feedback(int global, int cabeza, int torso, int piernas, int pies, int manos) {
		sensibilidadGlobal=global;
		sensibilidadCabeza=cabeza;
		sensibilidadTorso=torso;
		sensibilidadPiernas=piernas;
		sensibilidadPies=pies;
		sensibilidadManos=manos;
	}
	
	public Feedback() {
	}
	
}
