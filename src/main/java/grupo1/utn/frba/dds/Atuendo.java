package grupo1.utn.frba.dds;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.*;

import Prendas.ParteDelCuerpo;
import Prendas.Prenda;

@Entity(name="Atuendos")
@Table(name="Atuendos")
public class Atuendo {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}

	private int nivelDePonderacion=0;
	//private boolean aceptado;
	@ManyToMany(cascade = CascadeType.MERGE)
	public List<Prenda> prendas = new ArrayList<Prenda>();
	
	public int getNivelDePonderacion() {
		return nivelDePonderacion;
	}
	
	public List<String>getAllPrendas(){
		return prendas.stream().map(prenda->prenda.tipo.descripcion).collect(Collectors.toList());
	}
	
	public String getAtuendoString() {
		
		String atuendo="[";
		List<String> atuendoNoParseado= getAllPrendas();
		
		atuendo=atuendo.concat(atuendoNoParseado.get(0));
		for(int i=1;i< atuendoNoParseado.size();i++) {
			atuendo=atuendo.concat(",");
			atuendo=atuendo.concat(atuendoNoParseado.get(i));
			
		}
		atuendo=atuendo.concat("]");
		
		return atuendo;
		
	}

	public void setNivelDePonderacion(int nivelDePonderacion) {
		this.nivelDePonderacion = nivelDePonderacion;
	}
/*
	public boolean isAceptado() {
		return aceptado;
	}

	public void setAceptado(boolean aceptado1) {
		aceptado = aceptado1;
	}
*/
	public List<Prenda> getSuperior() {
		return prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Torso)).collect(Collectors.toList());
	}

	public List<Prenda> getInferior() {
		return prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Piernas)).collect(Collectors.toList());
	}

	public List<Prenda> getCalzado() {
		return prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Pies)).collect(Collectors.toList());
	}

	public List<Prenda> getAccesorios() {
		List<Prenda> listaFinal = new ArrayList();
		listaFinal.addAll(prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Cabeza)).collect(Collectors.toList()));
		listaFinal.addAll(prendas.stream().filter(p->p.getCategoria().getParteDelCuerpo().equals(ParteDelCuerpo.Manos)).collect(Collectors.toList()));
		return listaFinal;
	}
	
	public int getNivelDeAbrigo() {
		return nivelDeAbrigoSuperior() + nivelDeAbrigoInferior() + nivelDeAbrigoCalzado() + nivelDeAbrigoAccesorios() ;
	}
	
	public int nivelDeAbrigoSuperior() {
		return getSuperior().stream().mapToInt(p->p.getTipo().getNivelAbrigo()).sum();
	}
	
	public int nivelDeAbrigoInferior() {
		return getInferior().stream().mapToInt(p->p.getTipo().getNivelAbrigo()).sum();
	}
	
	public int nivelDeAbrigoCalzado() {
		return getCalzado().stream().mapToInt(p->p.getTipo().getNivelAbrigo()).sum();
	}
	
	public int nivelDeAbrigoAccesorios() {
		return getAccesorios().stream().mapToInt(p->p.getTipo().getNivelAbrigo()).sum();
	}
/*
	public void mostrar() {
		// TODO Auto-generated method stub
		System.out.print("Atuendo:");
		System.out.print("[");
		superior.forEach(prenda->prenda.mostrar());
		System.out.print(",");
		inferior.forEach(prenda->prenda.mostrar());
		System.out.print(",");
		calzado.forEach(prenda->prenda.mostrar());
		System.out.print(",");
		accesorios.forEach(prenda->prenda.mostrar());
		System.out.print("]");
		
		
	}
*/
}
