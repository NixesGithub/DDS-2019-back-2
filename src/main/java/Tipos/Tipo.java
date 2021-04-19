package Tipos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import ExceptionsPrendas.PrendaInvalidaPorMaterialException;

@Entity(name="Tipos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name="Tipos")
public abstract class Tipo {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	@Basic
	public String descripcion;
	@ElementCollection
	@CollectionTable(name="Permitidos", joinColumns=@JoinColumn(name="tipo_id"))
	@Column(name="Permitidos")
	public List<String> permitidos= new ArrayList<String>();
	@Basic
	public int nivelAbrigo;
	@Basic
	private int capa;
	
	public int getNivelAbrigo() {
		return nivelAbrigo;
	}
	
	public int getCapa() {
		return capa;
	}
	
	public int setCapa(int unNumero) {
		return capa = unNumero;
	}

	public void setNivelAbrigo(int nivelAbrigo) {
		this.nivelAbrigo = nivelAbrigo;
	}

	public void materialErroneo(String tipoMaterial) throws PrendaInvalidaPorMaterialException {
		
		if(!permitidos.contains(tipoMaterial)) {
			throw new PrendaInvalidaPorMaterialException();
			
		}
	}
	
	public Tipo(String desc, int suNivelAbrigo) {
		descripcion = desc;
		nivelAbrigo=suNivelAbrigo;
	}
	
	public abstract int manejarCalor();
	
	public Tipo() {};
}
