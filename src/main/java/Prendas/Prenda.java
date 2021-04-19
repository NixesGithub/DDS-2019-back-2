package Prendas;
import java.awt.Color;
import java.awt.Image;
import java.awt.color.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.persistence.*;

import ExceptionsPrendas.PrendaInvalidaException;
import ExceptionsPrendas.PrendaInvalidaPorMaterialException;
import ExceptionsPrendas.PrendaInvalidaPorTipoException;
import Tipos.Tipo;
import grupo1.utn.frba.dds.ColorConverter;

@Entity(name="Prendas")
@Table(name="Prendas")
public class Prenda {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	public Integer getId() {
		return id;
	}
	
	@Column
	@Convert(converter = ColorConverter.class)
	private Color colorPrimario;
	@Column
	@Convert(converter = ColorConverter.class)
	private Color colorSecundario;
	@Basic
	private String tipoMaterial;
	@OneToOne
	public Tipo tipo;
	private boolean estaSiendoUsada=false;
	@OneToOne
	private Categoria categoria;
	@Transient
	private Image image=null;
	private boolean esFormal=false;
	
	
	
	public boolean isEsFormal() {
		return esFormal;
	}



	public void setEsFormal(boolean esFormal) {
		this.esFormal = esFormal;
	}



	public boolean isEstaSiendoUsada() {
		return estaSiendoUsada;
	}



	public void setEstaSiendoUsada(boolean estaSiendoUsada) {
		this.estaSiendoUsada = estaSiendoUsada;
	}



	public Color getColorSecundario() {
		return colorSecundario;
	}



	public Image getImage() {
		if(image==null) {
			
			/*podriamos tirar una excepcion en vez de esto pero bueno, detalles, 
			 * en realidad lo bonito seria tirar la excepcion y en la excepcion hacer esto*/
			
			leerImagen("src/main/java/images/noDisponible.jpg");//aca habria que mandarle un path a una imagen que sea "no disponible"
			
		}
		
		return image;
	}

	public void leerImagen(String path) {
		
		try {
		    File pathToFile = new File(path);
		     image = ImageIO.read(pathToFile);// hay que preguntar si esto es legal al profe porque no hay forma de probarlo
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
		
	}
	
	public Color getColorPrimario() {
		return colorPrimario;
	}

	public void setColorPrimario(Color colorPrimario) {
		this.colorPrimario = colorPrimario;
	}

	public String getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(String tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}


	
	public Prenda(Color colorPrimarioCostructor,Color colorSecundarioCostructor , String tipoMaterialCostructor ,Tipo tipoCostructor, Categoria categoriaCostructor){
	
		
		colorPrimario=colorPrimarioCostructor;
		colorSecundario= colorSecundarioCostructor;
		categoria=categoriaCostructor;
		tipo=tipoCostructor;
		tipoMaterial= tipoMaterialCostructor;
	
	}
	
	public void validate() throws PrendaInvalidaException{
		categoria.esPosible(tipo);
		tipo.materialErroneo( tipoMaterial);
		
		if(colorPrimario.equals(colorSecundario) )
			throw new PrendaInvalidaException(); 
		
		
		
	}
	
	public boolean admiteSuperposicion() {
		return categoria.admiteSuperposicion();
	}

	public void mostrar() {
		
		System.out.print(tipo.descripcion+" de "+tipoMaterial+"de color/es"+ colorPrimario + colorSecundario);
	}
	
	public Prenda(){};
}
