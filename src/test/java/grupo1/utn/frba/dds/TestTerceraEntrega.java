package grupo1.utn.frba.dds;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.stubbing.Answer;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.io.IOException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import APIs.AdaptadorAW;
import APIs.AdaptadorOW;
import APIs.Clima;
import APIs.ClimaNormal;
import APIs.ClimaXCiudad;
import Controladores.ClimaController;
import CoordinadorDeServicion.ServicioClima;
import Criterios.CriterioEventoFormal;
import Criterios.CriterioSensibilidad;
import Criterios.CriterioSinSuperposicion;
import Criterios.CriterioTemperatura;
import Criterios.Criterios;
import ExceptionsPrendas.PrendaInvalidaException;
import ExceptionsPrendas.PrendaInvalidaPorMaterialException;
import ExceptionsPrendas.PrendaInvalidaPorTipoException;
import Notificador.Notificador;
import Prendas.Categoria;
import Prendas.ParteDelCuerpo;
import Prendas.Prenda;
import Tipos.*;
import net.aksingh.owmjapis.api.APIException;

public class TestTerceraEntrega {
	
	@Test(expected= GuardarropasNoAlzanzaFiltrosBasicosException.class)
	public void TestGuardarropasCompartidosExcepcion() throws PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, GuardarropasNoEncontradoException, IOException, APIException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		AdaptadorAW mockAW = mock(AdaptadorAW.class);
		AdaptadorOW mockOW = mock(AdaptadorOW.class);
		
		Clima climaNoche= new ClimaNormal();
		Clima climaDia=new ClimaNormal();
		ClimaXCiudad climaMock= new ClimaXCiudad();
		climaMock.setClimaDia(climaDia);
		climaMock.setClimaNoche(climaNoche);
		
		when(mockAW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		when(mockOW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		
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
		Tipo tipoCampera = new Campera("Campera", 10);
		
		categoriaInferior.getPermitidos().add(tipoLompa);
		categoriaCalzado.getPermitidos().add(tipoZapas);
		categoriaTorso.getPermitidos().add(tipoRemera);
		categoriaTorso.getPermitidos().add(tipoRemeraRayada);
		categoriaTorso.getPermitidos().add(tipoCampera);
		
		tipoRemera.permitidos.add("Algodon");
		tipoRemera.permitidos.add("tela");
		tipoLompa.permitidos.add("Algodon");
		tipoLompa.permitidos.add("tela");
		tipoRemeraRayada.permitidos.add("Algodon");
		tipoRemeraRayada.permitidos.add("tela");
		tipoZapas.permitidos.add("Algodon");
		tipoZapas.permitidos.add("tela");
		tipoCampera.permitidos.add("Impermeable");
		
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda2.getTipo().setCapa(1);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda3.getTipo().setCapa(1);
		Prenda unaPrenda4=new Prenda(negro, azul, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda4.getTipo().setCapa(1);
		Prenda unaPrenda5=new Prenda(rojo, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda5.getTipo().setCapa(1);
		
		
		
		ServicioClima.setAW(mockAW);
		ServicioClima.setOW(mockOW);
		
		Usuario usuario1 = new Usuario("MiPlacard","Buenos Aires");
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(new CriterioSinSuperposicion());
		usuario1.criterios=criterios;
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda5);
		
		Usuario usuario2 = new Usuario("pepe","Buenos Aires");
		usuario2.guardarropas.add(usuario1.getUnGuardarropas("MiPlacard"));
		List <Criterios> criteriosUsuario2=new ArrayList<Criterios>();
		
		usuario2.criterios=criteriosUsuario2;
		usuario1.pedirSugerencias("MiPlacard");
		usuario2.pedirSugerencias("MiPlacard");
		
		//nota tenemos un bug que si aceptas primero y despues rechazas te quedan atuendos que los compartis
		//esta fixeado pero cuando haya interaccion con el usuario quizas cambie creo depende de como la hagamos
		

		
	}
	
	@Test
	public void TestGuardarropasCompartidosFuncionalidad() throws PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, GuardarropasNoEncontradoException, IOException, APIException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		AdaptadorAW mockAW = mock(AdaptadorAW.class);
		AdaptadorOW mockOW = mock(AdaptadorOW.class);

		Clima climaNoche= new ClimaNormal();
		Clima climaDia=new ClimaNormal();
		ClimaXCiudad climaMock= new ClimaXCiudad();
		climaMock.setClimaDia(climaDia);
		climaMock.setClimaNoche(climaNoche);
		
		when(mockAW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		when(mockOW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		
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
		Tipo tipoCampera = new Campera("Campera", 10);
		
		categoriaInferior.getPermitidos().add(tipoLompa);
		categoriaCalzado.getPermitidos().add(tipoZapas);
		categoriaTorso.getPermitidos().add(tipoRemera);
		categoriaTorso.getPermitidos().add(tipoRemeraRayada);
		categoriaTorso.getPermitidos().add(tipoCampera);
		
		tipoRemera.permitidos.add("Algodon");
		tipoRemera.permitidos.add("tela");
		tipoLompa.permitidos.add("Algodon");
		tipoLompa.permitidos.add("tela");
		tipoRemeraRayada.permitidos.add("Algodon");
		tipoRemeraRayada.permitidos.add("tela");
		tipoZapas.permitidos.add("Algodon");
		tipoZapas.permitidos.add("tela");
		tipoCampera.permitidos.add("Impermeable");
		
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda2.getTipo().setCapa(1);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda3.getTipo().setCapa(1);
		Prenda unaPrenda4=new Prenda(negro, azul, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda4.getTipo().setCapa(1);
		
		Prenda unaPrenda5=new Prenda(rojo, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda5.getTipo().setCapa(1);
		Prenda unaPrenda6=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda6.getTipo().setCapa(1);
		Prenda unaPrenda7=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda7.getTipo().setCapa(1);
		
		
		
		ServicioClima.setAW(mockAW);
		ServicioClima.setOW(mockOW);
		
		Usuario usuario1 = new Usuario("MiPlacard","Buenos Aires");
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(new CriterioSinSuperposicion());
		usuario1.criterios=criterios;
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda5);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda6);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda7);
		
		Usuario usuario2 = new Usuario("pepe","Buenos Aires");
		usuario2.guardarropas.add(usuario1.getUnGuardarropas("MiPlacard"));
		List <Criterios> criteriosUsuario2=new ArrayList<Criterios>();
		
		usuario2.criterios=criteriosUsuario2;
		
		List<Sugerencia> sugerenciaQueQuieroLibre=usuario1.pedirSugerencias("MiPlacard").stream().filter(sugerencia->sugerencia.getAtuendo().getSuperior().get(0).equals(unaPrenda5)&&sugerencia.getAtuendo().getInferior().get(0).equals(unaPrenda6)&&sugerencia.getAtuendo().getCalzado().get(0).equals(unaPrenda7)).collect(Collectors.toList());
		sugerenciaQueQuieroLibre.get(0).rechazar(); 
		sugerenciaQueQuieroLibre.get(0).marcarComoNoUsada();
		
		Assert.assertEquals(1,usuario2.pedirSugerencias("MiPlacard").size());

		
	}
	
	@Test
	public void TestFiltroFormal() throws PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, GuardarropasNoEncontradoException, IOException, APIException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException, ParseException, FechaInvaldiaException, SugerenciasNoGeneradasException {
		
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
		Tipo tipoCampera = new Campera("Campera", 10);
		
		categoriaInferior.getPermitidos().add(tipoLompa);
		categoriaCalzado.getPermitidos().add(tipoZapas);
		categoriaTorso.getPermitidos().add(tipoRemera);
		categoriaTorso.getPermitidos().add(tipoRemeraRayada);
		categoriaTorso.getPermitidos().add(tipoCampera);
		
		tipoRemera.permitidos.add("Algodon");
		tipoRemera.permitidos.add("tela");
		tipoLompa.permitidos.add("Algodon");
		tipoLompa.permitidos.add("tela");
		tipoRemeraRayada.permitidos.add("Algodon");
		tipoRemeraRayada.permitidos.add("tela");
		tipoZapas.permitidos.add("Algodon");
		tipoZapas.permitidos.add("tela");
		tipoCampera.permitidos.add("Impermeable");
		
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda2.getTipo().setCapa(1);
		unaPrenda2.setEsFormal(true);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda3.getTipo().setCapa(1);
		unaPrenda3.setEsFormal(true);
		Prenda unaPrenda4=new Prenda(negro, azul, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda4.getTipo().setCapa(1);
		unaPrenda4.setEsFormal(true);
		
		Prenda unaPrenda5=new Prenda(rojo, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda5.getTipo().setCapa(1);
		Prenda unaPrenda6=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda6.getTipo().setCapa(1);
		Prenda unaPrenda7=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda7.getTipo().setCapa(1);
		
		ServicioClima.setAW(mockAW);
		ServicioClima.setOW(mockOW);
		
		Usuario usuario1 = new Usuario("MiPlacard","Buenos Aires");
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(new CriterioSinSuperposicion());
		criterios.add(new CriterioEventoFormal());
		usuario1.criterios=criterios;
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda5);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda6);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda7);
		
		List<Atuendo>lista=Sugeridor.getSugeridor().queMePongo(usuario1.getUnGuardarropas("MiPlacard"),usuario1,"Buenos Aires",new Date());
		
		
		Assert.assertEquals(1,lista.size());

		
	}
	@Test
	public void TestSensibilidadGlobal() throws PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, GuardarropasNoEncontradoException, IOException, APIException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		AdaptadorAW mockAW = mock(AdaptadorAW.class);
		AdaptadorOW mockOW = mock(AdaptadorOW.class);

		ClimaXCiudad climaMock= new ClimaXCiudad();
		climaMock.setClimaDia(new ClimaNormal());
		climaMock.setClimaNoche(new ClimaNormal());
		climaMock.setTemperaturaActual((float) 16.0);
		
		when(mockAW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		when(mockOW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		
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
		Tipo tipoRemeraRayada = new Remera("Remera rayada",20);
		Tipo tipoCampera = new Campera("Campera", 10);
		
		categoriaInferior.getPermitidos().add(tipoLompa);
		categoriaCalzado.getPermitidos().add(tipoZapas);
		categoriaTorso.getPermitidos().add(tipoRemera);
		categoriaTorso.getPermitidos().add(tipoRemeraRayada);
		categoriaTorso.getPermitidos().add(tipoCampera);
		
		tipoRemera.permitidos.add("Algodon");
		tipoRemera.permitidos.add("tela");
		tipoLompa.permitidos.add("Algodon");
		tipoLompa.permitidos.add("tela");
		tipoRemeraRayada.permitidos.add("Algodon");
		tipoRemeraRayada.permitidos.add("tela");
		tipoZapas.permitidos.add("Algodon");
		tipoZapas.permitidos.add("tela");
		tipoCampera.permitidos.add("Impermeable");
		
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda2.getTipo().setCapa(1);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda3.getTipo().setCapa(1);
		Prenda unaPrenda4=new Prenda(negro, azul, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda4.getTipo().setCapa(1);
		
		Prenda unaPrenda5=new Prenda(rojo, negro, "Algodon", tipoRemera, categoriaTorso);
		unaPrenda5.getTipo().setCapa(1);
		Prenda unaPrenda6=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda6.getTipo().setCapa(1);
		Prenda unaPrenda7=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda7.getTipo().setCapa(1);
		
		ServicioClima.setAW(mockAW);
		ServicioClima.setOW(mockOW);
		
		Usuario usuario1 = new Usuario("MiPlacard","Buenos Aires");
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(new CriterioSinSuperposicion());
		criterios.add(new CriterioTemperatura("Buenos Aires"));
		usuario1.criterios=criterios;
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda5);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda6);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda7);
		Feedback feedback = new Feedback(10,0,0,0,0,0);
		usuario1.setFeedback(feedback);
		
		Assert.assertEquals(4,usuario1.pedirSugerencias("MiPlacard").size());

		
	}
	
	@Test
	public void TestSensibilidadManos() throws PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, GuardarropasNoEncontradoException, IOException, APIException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		AdaptadorAW mockAW = mock(AdaptadorAW.class);
		AdaptadorOW mockOW = mock(AdaptadorOW.class);
		


		Clima climaNoche= new ClimaNormal();
		Clima climaDia=new ClimaNormal();
		ClimaXCiudad climaMock= new ClimaXCiudad();
		climaMock.setClimaDia(climaDia);
		climaMock.setClimaNoche(climaNoche);
		climaMock.setTemperaturaActual((float) 20.0);
		
		when(mockAW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		when(mockOW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		
		Color rojo = Color.RED;
		Color azul = Color.BLUE;
		Color noColor = Color.DARK_GRAY;
		Color negro = Color.BLACK;
		
		Categoria categoriaInferior = new Categoria(ParteDelCuerpo.Piernas);
		Categoria categoriaTorso = new Categoria(ParteDelCuerpo.Torso);
		Categoria categoriaCalzado = new Categoria(ParteDelCuerpo.Pies);
		Categoria categoriaAccManos = new Categoria(ParteDelCuerpo.Manos);
		
		Tipo tipoRemera = new Remera("Remera roja",2);
		Tipo tipoLompa = new Pantalon("Pantalon azul",2);
		Tipo tipoZapas = new Zapatillas("Zapas negras",1);
		Tipo tipoRemeraRayada = new Remera("Remera rayada",2);
		Tipo tipoGuantes = new Guantes("Guantes",4);
		
		categoriaInferior.getPermitidos().add(tipoLompa);
		categoriaCalzado.getPermitidos().add(tipoZapas);
		categoriaTorso.getPermitidos().add(tipoRemera);
		categoriaTorso.getPermitidos().add(tipoRemeraRayada);
		categoriaAccManos.getPermitidos().add(tipoGuantes);
		
		tipoGuantes.permitidos.add("Algodon");
		tipoRemera.permitidos.add("Algodon");
		tipoRemera.permitidos.add("tela");
		tipoLompa.permitidos.add("Algodon");
		tipoLompa.permitidos.add("tela");
		tipoRemeraRayada.permitidos.add("Algodon");
		tipoRemeraRayada.permitidos.add("tela");
		tipoZapas.permitidos.add("Algodon");
		tipoZapas.permitidos.add("tela");
		
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda2.getTipo().setCapa(1);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda3.getTipo().setCapa(1);
		Prenda unaPrenda4=new Prenda(negro, azul, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda4.getTipo().setCapa(1);
		
		Prenda unaPrenda5=new Prenda(rojo, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		unaPrenda5.getTipo().setCapa(1);
		Prenda unaPrenda6=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		unaPrenda6.getTipo().setCapa(1);
		Prenda unaPrenda7=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		unaPrenda7.getTipo().setCapa(1);
		
		Prenda guantes=new Prenda(negro, noColor, "Algodon", tipoGuantes, categoriaAccManos);
		
		ServicioClima.setAW(mockAW);
		ServicioClima.setOW(mockOW);
		
		Usuario usuario1 = new Usuario("MiPlacard","Buenos Aires");
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(new CriterioSinSuperposicion());
		criterios.add(new CriterioTemperatura("Buenos Aires"));
		criterios.add(new CriterioSensibilidad());
		usuario1.criterios=criterios;
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda5);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda6);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda7);
		usuario1.getUnGuardarropas("MiPlacard").addPrenda(guantes);
		Feedback feedback = new Feedback(0,0,0,0,0,4);
		usuario1.setFeedback(feedback);
		
		Assert.assertEquals(8,usuario1.pedirSugerencias("MiPlacard").size());

		
	}
	
	
	/*este test pierde el sentido si delego comportamiento en el servicioClima
	 * @Test
	public void ObtenerClimaCorrectamenteDesdeControlador() throws IOException, APIException, ParseException, FechaInvaldiaException, SugerenciaInvalidaException, SugerenciasNoGeneradasException, GuardarropasNoEncontradoException {
		
		//adaptadores no deberian ser estaticos?
		AdaptadorAW mockAW = mock(AdaptadorAW.class);
		AdaptadorOW mockOW = mock(AdaptadorOW.class);
		ClimaXCiudad climaMock= new ClimaXCiudad();
		String ciudad= "asdasd";
		
		when(ServicioClima.getAlerta(ciudad, climaMock)).thenReturn(false);
		boolean climaActual = ServicioClima.getAlerta(ciudad, climaMock);
		
		
		Assert.assertFalse(climaActual);

	}*/
	
	/*@Test
	public void notificacionErronea() throws IOException, APIException {
		
		CON LOS CAMBIOS NO PUEDE FALLAR LAS NOTIFICACIONES :D
		
		ClimaXCiudad climaDePrueba= new ClimaXCiudad();
		climaDePrueba.setEstadoAtmosferico("Rain");
		climaDePrueba.setTemperaturaActual(70);
		ClimaController climaController= mock(ClimaController.class);
		when(climaController.getClimaActual()).thenReturn(climaDePrueba);
		
		climaController.climaAnterior=climaDePrueba;
		climaController.solicitarClima();
		 
		Assert.assertNotEquals("", Notificador.getUltimoSuceso());

	}*/
}
