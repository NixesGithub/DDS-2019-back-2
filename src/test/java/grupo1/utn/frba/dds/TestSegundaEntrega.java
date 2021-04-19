package grupo1.utn.frba.dds;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import APIs.AdaptadorAW;
import APIs.AdaptadorOW;
import APIs.Clima;
import APIs.ClimaNormal;
import APIs.ClimaXCiudad;
import CoordinadorDeServicion.ServicioClima;
import Criterios.CriterioSinSuperposicion;
import Criterios.CriterioTemperatura;
import Criterios.Criterios;
import ExceptionsPrendas.PrendaInvalidaException;
import ExceptionsPrendas.PrendaInvalidaPorMaterialException;
import ExceptionsPrendas.PrendaInvalidaPorTipoException;
import ExceptionsPrendas.PrendaYaExistenteException;
import Prendas.Categoria;
import Prendas.ParteDelCuerpo;
import Prendas.Prenda;
import Tipos.*;
import junit.framework.Assert;
import net.aksingh.owmjapis.api.APIException;

public class TestSegundaEntrega {
	
	@Test(expected = FechaInvaldiaException.class)
	public void testFechaInvalidaException() throws ParseException, FechaInvaldiaException, SugerenciaInvalidaException, GuardarropasNoEncontradoException, SugerenciasNoGeneradasException {
		Usuario unUsuario= new Usuario("guardarropasPepito","Buenos Aires");
		List<Criterios> criterios= new ArrayList();
		CriterioSinSuperposicion criterio= new CriterioSinSuperposicion();
		criterios.add(criterio);
		PlanificacionEvento unEvento= new PlanificacionEvento("21-06-2019","Capital Federal",unUsuario.getUnGuardarropas("guardarropasPepito"),criterios, 0, false, unUsuario );
		
	}
	
	@Test
	public void testPrendaCalor() throws GuardarropasNoEncontradoException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, IOException, APIException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException{
	
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
		
		tipoCampera.setCapa(2);
		tipoZapas.setCapa(1);
		tipoLompa.setCapa(1);
		tipoRemera.setCapa(1);
		tipoRemeraRayada.setCapa(1);
		Prenda unaPrenda0=new Prenda(azul, rojo, "Algodon", tipoRemera, categoriaTorso);
		Prenda unaPrenda1=new Prenda(azul, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		Prenda unaPrenda4=new Prenda(negro, azul, "Impermeable", tipoCampera, categoriaTorso);
		
		
		CriterioTemperatura criterioTemperatura = new CriterioTemperatura("Buenos Aires");
		
		criterioTemperatura.adaptadorAW = mockAW;
		criterioTemperatura.adaptadorOW = mockOW;
		
		ServicioClima.setAW(mockAW);
		ServicioClima.setOW(mockOW);
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires");
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(criterioTemperatura);
		
		usuario.criterios=criterios;
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda0);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda1);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);
		//chequeenlo
		Date date=new Date();
		Sugeridor.getSugeridor().queMePongo(usuario.guardarropas.get(0), usuario,usuario.lugar,date);
		
		List<List<Prenda>> prendas= usuario.getSugerenciasAValidar().stream().map(atuendo->atuendo.getSuperior()).collect(Collectors.toList());
		List<Prenda> merged = Lists.newArrayList(Iterables.concat(prendas));
		boolean resultado=merged.stream().map(prenda->prenda.getTipo()).collect(Collectors.toList()).contains(tipoCampera);
		
		Assert.assertEquals(false, resultado);
	}
	
	@Test(expected = NoEsPremiumException.class)
	public void testUsuarioNoPremiumException() throws GuardarropasNoEncontradoException, NoEsPremiumException, SugerenciaInvalidaException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, DecisionInvalidaException{
	
		Usuario usuarioTest= new Usuario("MiGuardarropas","Buenos Aires");

		Tipo tipoRemera = new Remera("Remera roja",2);

		Color rojo = Color.RED;
		Color azul = Color.BLUE;

		Categoria categoriaTorso = new Categoria(ParteDelCuerpo.Torso);
		categoriaTorso.getPermitidos().add(tipoRemera);
		
		tipoRemera.permitidos.add("Algodon");
		tipoRemera.permitidos.add("tela");
		
		
		usuarioTest.esPremium=false;
		for(int i=0; i<51;i++) {
			
			Prenda unaPrenda0=new Prenda(azul, rojo, "Algodon", tipoRemera, categoriaTorso);
			usuarioTest.addPrendaAUnGuardarropas(unaPrenda0, "MiGuardarropas");
		}
	}
	
	/* DEPRECATED: Las decisiones ya no seran invalidas dado que solo podes elegir ACEPTAR o RECHAZAR
	@Test(expected = DecisionInvalidaException.class)
	public void testDecisionInvalidaException() throws GuardarropasNoEncontradoException, NoEsPremiumException, SugerenciaInvalidaException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, DecisionInvalidaException{
	
		
		Color rojo = Color.RED;
		Color azul = Color.BLUE;
		Color noColor = Color.DARK_GRAY;
		Color negro = Color.BLACK;
		
		Categoria categoriaInferior = new Categoria(ParteDelCuerpo.PIERNAS);
		Categoria categoriaTorso = new Categoria(ParteDelCuerpo.TORSO);
		Categoria categoriaCalzado = new Categoria(ParteDelCuerpo.PIES);
		
		Tipo tipoRemera = new Remera("Remera roja",4);
		Tipo tipoLompa = new Pantalon("Pantalon azul",4);
		Tipo tipoZapas = new Zapatillas("Zapas negras",2);
		Tipo tipoRemeraRayada = new Remera("Remera rayada",4);
		
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
		

		
		Prenda unaPrenda0=new Prenda(azul, rojo, "Algodon", tipoRemera, categoriaTorso);
		Prenda unaPrenda1=new Prenda(azul, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
				
		
		CriterioSinSuperposicion criterio= new CriterioSinSuperposicion();
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires"); //mock(Usuario.class);
		when(usuario.escanearLinea()).thenReturn("deshacer"); ESTO ESABA COMENTADO
		//
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(criterio);
		//usuario.guardarropas=new ArrayList<Guardarropas>();
		usuario.atuendosSugeridos= new ArrayList();
		usuario.esPremium=false;
		usuario.sugerenciasAceptadas= new ArrayList();	ESTO ESTABA COMENTADO
		usuario.sugerenciasAceptadas=new ArrayList();
		usuario.eventos= new ArrayList();
		usuario.decisiones= new ArrayList();
		Guardarropas guardarropa= new Guardarropas("MiPlacard");
		usuario.getListaDeGuardarropas().add(guardarropa);
		//
		
		usuario.criterios=criterios;
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda0);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda1);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		List<Atuendo> atuendos=(Sugeridor.getSugeridor().queMePongo(usuario.guardarropas.get(0), criterios,usuario.lugar));
		//usuario.pedirSugerencias("MiPlacard");
		usuario.validar(atuendos.get(0));	
	
		
	}
*/
	
	@Test
	public void testPrendasValidasFrio() throws IOException, APIException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, GuardarropasNoEncontradoException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		AdaptadorAW mockAW = mock(AdaptadorAW.class);
		AdaptadorOW mockOW = mock(AdaptadorOW.class);
		Clima climaNoche= new ClimaNormal();
		Clima climaDia=new ClimaNormal();
		ClimaXCiudad climaMock= new ClimaXCiudad();
		climaMock.setClimaDia(climaDia);
		climaMock.setClimaNoche(climaNoche);
		climaMock.setTemperaturaActual((float) 10.0);
		when(mockAW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		when(mockOW.pedirClima("Buenos Aires")).thenReturn(climaMock);
		
		Color rojo = Color.RED;
		Color azul = Color.BLUE;
		Color noColor = Color.DARK_GRAY;
		Color negro = Color.BLACK;
		
		Categoria categoriaInferior = new Categoria(ParteDelCuerpo.Piernas);
		Categoria categoriaTorso = new Categoria(ParteDelCuerpo.Torso);
		Categoria categoriaCalzado = new Categoria(ParteDelCuerpo.Pies);
		
		Tipo tipoRemera = new Remera("Remera roja",4);
		Tipo tipoLompa = new Pantalon("Pantalon azul",4);
		Tipo tipoZapas = new Zapatillas("Zapas negras",2);
		Tipo tipoRemeraRayada = new Remera("Remera rayada",4);
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
		
		tipoCampera.setCapa(2);
		tipoZapas.setCapa(1);
		tipoLompa.setCapa(1);
		tipoRemera.setCapa(1);
		tipoRemeraRayada.setCapa(1);
		
		
		Prenda unaPrenda0=new Prenda(azul, rojo, "Algodon", tipoRemera, categoriaTorso);
		Prenda unaPrenda1=new Prenda(azul, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
		Prenda unaPrenda4=new Prenda(negro, azul, "Impermeable", tipoCampera, categoriaTorso);
		
		CriterioTemperatura criterioTemperatura = new CriterioTemperatura("Buenos Aires");
		
		criterioTemperatura.adaptadorAW = mockAW;
		criterioTemperatura.adaptadorOW = mockOW;
		
		ServicioClima.setAW(mockAW);
		ServicioClima.setOW(mockOW);
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires");
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(criterioTemperatura);
		
		usuario.criterios=criterios;
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda0);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda1);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);
		Date date= new Date();
		;
		
		int n=Sugeridor.getSugeridor().queMePongo(usuario.guardarropas.get(0), usuario,usuario.lugar,date).size();
		Assert.assertEquals(2, n);
	}

}
