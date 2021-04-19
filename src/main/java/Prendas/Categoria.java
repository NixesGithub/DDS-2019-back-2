package Prendas;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import ExceptionsPrendas.PrendaInvalidaPorTipoException;
import Tipos.Tipo;

@Entity(name="Categorias")
@Table(name="Categorias")
public class Categoria {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	private ParteDelCuerpo parteDelCuerpo;
	@Transient
	private Descripcion Descripcion;
	@OneToMany
	private List<Tipo> permitidos= new ArrayList<Tipo>();
	private int cantidadMinima;
	private int cantidadMaxima;
	
	
	public Descripcion getDescripcion() {
		return Descripcion;
	}


	public void setDescripcion(Descripcion descripcion) {
		Descripcion = descripcion;
	}


	public ParteDelCuerpo getParteDelCuerpo() {
		return parteDelCuerpo;
	}


	public List<Tipo> getPermitidos() {
		return permitidos;
	}


	public int getCantidadMinima() {
		return cantidadMinima;
	}


	public int getCantidadMaxima() {
		return cantidadMaxima;
	}


	public void esPosible(Tipo tipo) throws PrendaInvalidaPorTipoException{
	
	if(!permitidos.contains(tipo)) {
	 		throw new PrendaInvalidaPorTipoException();
	 	}	
	}


	public boolean admiteSuperposicion() {
		return (cantidadMaxima-cantidadMinima>1);
	}
	
	public Categoria (ParteDelCuerpo unaParteDelCuerpo) {
		parteDelCuerpo = unaParteDelCuerpo;
	}
	
	public Categoria() {}

}


