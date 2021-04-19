package grupo1.utn.frba.dds;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import ExceptionsPrendas.PrendaYaExistenteException;
import Prendas.ParteDelCuerpo;
import Prendas.Prenda;

/*
 * ----------------------------------------------------
 * Ezequiel(12/05/19): 
 * 		Hice el modelo basico del Guardarropas, en principio
 * 		tiene nada mas que una lista de prendas.
 * 		Tendra mas cosas?
 * ----------------------------------------------------
 * 
 * 
 */
@Entity(name="Guardarropas")
@Table(name="Guardarropas")
public class Guardarropas {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	@Basic
	String nombre;
	
	@OneToMany
	List<Prenda> prendas;
	
	public boolean EstaOcupado;
	
	public List<Prenda> getPrendas() {
		return prendas;
	}

	public void setPrendas(List<Prenda> prendas) {
		this.prendas = prendas;
	}
	
	public void addPrenda(Prenda unaPrenda) throws PrendaYaExistenteException {
		if(this.tienePrenda(unaPrenda))
			throw new PrendaYaExistenteException(); 
		
		prendas.add(unaPrenda);
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public boolean tienePrenda(Prenda unaPrenda) {
	
		return prendas.contains(unaPrenda);//dat me lo hiciste pullear para borrar las barritas del comentario? >:v
	}
	
	public Guardarropas(String unNombre) {
		nombre = unNombre;
		prendas= new ArrayList();
	}

	public void hayPrendasSuficientes() throws SugerenciaInvalidaException {
		// TODO Auto-generated method stub
		
	if(	!(prendas.stream().anyMatch(prenda->prenda.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Torso))&& prendas.stream().anyMatch(prenda->prenda.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Piernas))&& prendas.stream().anyMatch(prenda->prenda.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Pies))
	)){
		throw new SugerenciaInvalidaException(
				"Error: el guardarropas carece de alguna categoria de prendas para crear una sugerencia valida.");
	}
}

	public List<Prenda> getPrendasTorso() {
		
		return prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Torso)).collect(Collectors.toList());
	}

	public List<Prenda> getPrendasInferior() {
		// TODO Auto-generated method stub
		return prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Piernas)).collect(Collectors.toList());
	}

	public List<Prenda> getPrendasCalzado() {
		// TODO Auto-generated method stub
		return prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Pies)).collect(Collectors.toList());
	}

	public List<Prenda> getPrendasAccesorio() {
		// TODO Auto-generated method stub
		List<Prenda> listaFinal = new ArrayList();
		listaFinal.addAll(prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Cabeza)).collect(Collectors.toList()));
		listaFinal.addAll(prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Manos)).collect(Collectors.toList()));
		return listaFinal;
	}
	
	public Guardarropas() {}
		
	}
	
