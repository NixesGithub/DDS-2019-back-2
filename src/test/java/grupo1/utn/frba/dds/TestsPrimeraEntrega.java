package grupo1.utn.frba.dds;

import org.junit.Test;

import APIs.AdaptadorAW;
import APIs.AdaptadorOW;
import APIs.Clima;
import APIs.ClimaNormal;
import APIs.ClimaXCiudad;

import CoordinadorDeServicion.ServicioClima;
import Criterios.CriterioSinSuperposicion;
import ExceptionsPrendas.PrendaInvalidaException;
import ExceptionsPrendas.PrendaInvalidaPorMaterialException;
import ExceptionsPrendas.PrendaInvalidaPorTipoException;
import ExceptionsPrendas.PrendaYaExistenteException;
import Prendas.Categoria;
import Prendas.Prenda;
import Tipos.Campera;
import Tipos.Pantalon;
import Tipos.Remera;
import Tipos.Tipo;
import Tipos.TipoDefault;
import Tipos.Zapatillas;
import Prendas.ParteDelCuerpo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.color.*;
import java.io.IOException;

import junit.framework.Assert;
import net.aksingh.owmjapis.api.APIException;

public class TestsPrimeraEntrega {

	/*	No hay test de "consistencia del tipo de prenda y la parte del cuerpo donde se utiliza o su funcion"
	 * 	porque sera problema del usuario si definio mal la prenda.
	 * 	Hay requerimientos que no tienen un test explicito porque se traducen simplemente en atributos
	 * 	o metodos de alguna clase. (Son demasiado triviales para ser testeados).
	 */

	@Test (expected = GuardarropasNoEncontradoException.class)
	public void siNoSeEncuentraElGuardarropasArrojaExcepcion() throws GuardarropasNoEncontradoException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires");
		
		usuario.pedirSugerencias("GUARDARROPAJES");
	}
	
	@Test (expected = SugerenciaInvalidaException.class)
	public void siNoHayPrendasArrojaExcepcion() throws  GuardarropasNoEncontradoException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires");
		
		usuario.pedirSugerencias("MiPlacard");
	}
	
	@Test (expected = SugerenciaInvalidaException.class) //No tiene remras (prendas de torso)
	public void siNoHayPrendasDeAlgunaCategoriaObligatoriaArrojaExcepcion() throws GuardarropasNoEncontradoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, PrendaInvalidaPorTipoException, PrendaYaExistenteException, SugerenciaInvalidaException, NoEsPremiumException, GuardarropasNoAlzanzaFiltrosBasicosException {
		Color rojo = Color.RED;
		Color noColor = Color.DARK_GRAY;
		Color negro= Color.BLACK;
		
		Tipo tipoLompa = new TipoDefault("pantalon", 3);
		tipoLompa.permitidos.add("Algodon");
		Categoria categoriaInferior = new Categoria(ParteDelCuerpo.Piernas);
		categoriaInferior.getPermitidos().add(tipoLompa);
		
		Tipo tipoZapas = new TipoDefault("zapatillas", 1);
		tipoZapas.permitidos.add("tela");
		Categoria categoriaCalzado = new Categoria(ParteDelCuerpo.Pies);
		categoriaCalzado.getPermitidos().add(tipoZapas);
		
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires");
		
		Prenda unaPrenda= new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		Prenda otraPrenda= new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		usuario.addPrendaAUnGuardarropas(unaPrenda, "MiPlacard");
		usuario.addPrendaAUnGuardarropas(otraPrenda, "MiPlacard");
		usuario.pedirSugerencias("MiPlacard");
	}
	
	@Test(expected= PrendaInvalidaPorMaterialException.class)
	public void siElMaterialDeLaPrendaEsInconsistenteArrojaExcepcion()throws PrendaInvalidaPorMaterialException, PrendaInvalidaException, PrendaInvalidaPorTipoException {
	
		Categoria categoriaTorso= new Categoria(ParteDelCuerpo.Cabeza);
		Tipo tipo= new TipoDefault("pantalon",3);
		categoriaTorso.getPermitidos().add(tipo);
		Color colorVerde= Color.GREEN;
		Color colorAzul= Color.BLUE;
		Prenda nuevaPrenda= new Prenda(colorVerde,colorAzul,("cuero"),tipo,categoriaTorso);
		nuevaPrenda.validate();
		
	}
	
	@Test (expected= PrendaInvalidaException.class)
	public void colorPrimarioYSecundarioIgualesArrojaExcepcion() throws PrendaInvalidaException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException {
	
	
		Categoria categoriaTorso= new Categoria(ParteDelCuerpo.Torso);
		Tipo tipo= new TipoDefault("pantalon",3);
		categoriaTorso.getPermitidos().add(tipo);
		tipo.permitidos.add("materialx");
		Color colorVerde1= Color.GREEN;
		Color colorVerde2= Color.GREEN;
		Prenda nuevaPrenda=new Prenda(colorVerde1,colorVerde2,("materialx"),tipo,categoriaTorso);
		nuevaPrenda.validate();
		
	}
	
	@Test(expected= PrendaInvalidaPorTipoException.class)
	public void tipoDePrendaNoPermitidoException() throws PrendaInvalidaPorTipoException, PrendaInvalidaException, PrendaInvalidaPorMaterialException {
	
	
		Categoria categoriaTorso= new Categoria(ParteDelCuerpo.Torso);
		Tipo tipoInvalido= new TipoDefault("pantalon",3);
		tipoInvalido.permitidos.add("materialx");
		Color rojo= Color.RED;
		Color azul= Color.BLUE;
		Prenda nuevaPrenda = new Prenda(rojo,azul,("materialx"),tipoInvalido,categoriaTorso);
		nuevaPrenda.validate();
		
	}
	

	
	@Test
	public void seGeneranTodasLasCombiancionesPosiblesSinCriterio() throws GuardarropasNoEncontradoException, PrendaInvalidaException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaYaExistenteException, SugerenciaInvalidaException, NoEsPremiumException, GuardarropasNoAlzanzaFiltrosBasicosException {
		Color rojo = Color.RED;
		Color azul = Color.BLUE;
		Color noColor = Color.DARK_GRAY;
		Color negro = Color.BLACK;
		
		Categoria categoriaInferior = new Categoria(ParteDelCuerpo.Piernas);
		Categoria categoriaTorso = new Categoria(ParteDelCuerpo.Torso);
		Categoria categoriaCalzado = new Categoria(ParteDelCuerpo.Pies);
		
		Tipo tipoRemera = new Remera("Remera roja",2);
		Tipo tipoLompa = new Pantalon("Pantalon azul",2);
		Tipo tipoZapas = new Zapatillas("Zapas negras",1);
		Tipo tipoRemeraRayada = new Remera("Remera rayada",2);
		
		categoriaInferior.getPermitidos().add(tipoLompa);
		categoriaCalzado.getPermitidos().add(tipoZapas);
		categoriaTorso.getPermitidos().add(tipoRemera);
		categoriaTorso.getPermitidos().add(tipoRemeraRayada);
		
		tipoRemera.permitidos.add("Algodon");
		tipoRemera.permitidos.add("tela");
		tipoLompa.permitidos.add("Algodon");
		tipoLompa.permitidos.add("tela");
		tipoRemeraRayada.permitidos.add("Algodon");
		tipoRemeraRayada.permitidos.add("tela");
		tipoZapas.permitidos.add("algodon");
		tipoZapas.permitidos.add("tela");
		

		tipoZapas.setCapa(1);
		tipoLompa.setCapa(1);
		tipoRemera.setCapa(1);
		tipoRemeraRayada.setCapa(1);
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires");
		Prenda unaPrenda0=new Prenda(azul, rojo, "Algodon", tipoRemera, categoriaTorso);
		Prenda unaPrenda1=new Prenda(azul, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		
		usuario.addPrendaAUnGuardarropas(unaPrenda0, "MiPlacard");

		usuario.addPrendaAUnGuardarropas(unaPrenda1, "MiPlacard");

		usuario.addPrendaAUnGuardarropas(unaPrenda2, "MiPlacard");

		usuario.addPrendaAUnGuardarropas(unaPrenda3, "MiPlacard");
		
		int n=usuario.pedirSugerencias("MiPlacard").size();
		Assert.assertEquals(2, n);
	}

	@Test
	public void seGeneranTodasLasCombiancionesSegunCriterio() throws GuardarropasNoEncontradoException, PrendaInvalidaException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaYaExistenteException, SugerenciaInvalidaException, NoEsPremiumException, GuardarropasNoAlzanzaFiltrosBasicosException, IOException, APIException {
		
		AdaptadorAW mockAW = mock(AdaptadorAW.class);
		AdaptadorOW mockOW = mock(AdaptadorOW.class);
		
		Clima climaNoche= new ClimaNormal();
		Clima climaDia=new ClimaNormal();
		ClimaXCiudad climaMock= new ClimaXCiudad();
		climaMock.setClimaDia(climaDia);
		climaMock.setClimaNoche(climaNoche);
		climaMock.setTemperaturaActual((float) 16.0);
		
		when(mockAW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		when(mockOW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		
		
		ServicioClima.setAW(mockAW);
		ServicioClima.setOW(mockOW);
		
		Color rojo = Color.RED;
		Color azul = Color.BLUE;
		Color noColor = Color.DARK_GRAY;
		Color negro = Color.BLACK;
		
		Categoria categoriaInferior = new Categoria(ParteDelCuerpo.Piernas);
		Categoria categoriaTorso = new Categoria(ParteDelCuerpo.Torso);
		Categoria categoriaCalzado = new Categoria(ParteDelCuerpo.Pies);
		

		Tipo tipoRemera = new Remera("Remera roja",2);
		Tipo tipoLompa = new Pantalon("Pantalon azul",2);
		Tipo tipoZapas = new Zapatillas("Zapas negras",1);
		Tipo tipoRemeraRayada = new Remera("Remera rayada",2);
		

		categoriaInferior.getPermitidos().add(tipoLompa);
		categoriaCalzado.getPermitidos().add(tipoZapas);
		categoriaTorso.getPermitidos().add(tipoRemera);
		categoriaTorso.getPermitidos().add(tipoRemeraRayada);
		
		tipoRemera.permitidos.add("Algodon");
		tipoRemera.permitidos.add("tela");
		tipoLompa.permitidos.add("Algodon");
		tipoLompa.permitidos.add("tela");
		tipoRemeraRayada.permitidos.add("Algodon");
		tipoRemeraRayada.permitidos.add("tela");
		tipoZapas.permitidos.add("algodon");
		tipoZapas.permitidos.add("tela");
		
		tipoZapas.setCapa(1);
		tipoLompa.setCapa(1);
		tipoRemera.setCapa(1);
		tipoRemeraRayada.setCapa(1);
		
		CriterioSinSuperposicion criterio= new CriterioSinSuperposicion();
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires");
		usuario.criterios.add(criterio);
		
		Prenda unaPrenda0= new Prenda(azul, rojo, "Algodon", tipoRemera, categoriaTorso);
		Prenda unaPrenda1= new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		Prenda unaPrenda2= new Prenda(azul, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		Prenda unaPrenda3= new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		usuario.addPrendaAUnGuardarropas(unaPrenda0, "MiPlacard");
		usuario.addPrendaAUnGuardarropas(unaPrenda1, "MiPlacard");
		usuario.addPrendaAUnGuardarropas(unaPrenda2, "MiPlacard");
		usuario.addPrendaAUnGuardarropas(unaPrenda3, "MiPlacard");
		
		int n=usuario.pedirSugerencias("MiPlacard").size();
		Assert.assertEquals(2, n);
	}
	
	@Test(expected= PrendaYaExistenteException.class)
	public void noPuedoAgregar2PrendasIguales()throws PrendaInvalidaPorMaterialException, PrendaInvalidaException, PrendaInvalidaPorTipoException, PrendaYaExistenteException {
	
		Categoria categoriaTorso= new Categoria(ParteDelCuerpo.Torso);
		Tipo tipo= new TipoDefault("pantalon",3);
		categoriaTorso.getPermitidos().add(tipo);
		tipo.permitidos.add("cuero");
		Color colorVerde= Color.GREEN;
		Color colorAzul= Color.BLUE;
		Prenda nuevaPrenda= new Prenda(colorVerde,colorAzul,("cuero"),tipo,categoriaTorso);
		Guardarropas ropero= new Guardarropas("roperoDeGeorge");
		ropero.addPrenda(nuevaPrenda);
		ropero.addPrenda(nuevaPrenda);
	}
	
}
