package grupo1.utn.frba.dds;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Criterios.CriterioColorinche;
import Criterios.CriterioMonoCromatico;
import Criterios.CriterioTemperatura;
import Criterios.Criterios;
import ExceptionsPrendas.PrendaInvalidaException;
import ExceptionsPrendas.PrendaInvalidaPorMaterialException;
import ExceptionsPrendas.PrendaInvalidaPorTipoException;
import Notificador.Notificador;
import Prendas.Categoria;
import Prendas.ParteDelCuerpo;
import Prendas.Prenda;
import Tipos.Campera;
import Tipos.Pantalon;
import Tipos.Remera;
import Tipos.Tipo;
import Tipos.Zapatillas;
import spark.Session;

public class Repositorio {
	public Session sesionActual;
	public Integer idUsuarioActual;
	public Integer idEventoEnSeleccion;
	public List<Usuario> usuarios = new ArrayList<Usuario>();
	public List<Sugerencia> sugerencias = new ArrayList<Sugerencia>();//si vamos a mezclar usuarios contra sugerencias no es un hashmap?
	public List<Categoria> categorias = new ArrayList<Categoria>();
	public List<Atuendo> atuendos = new ArrayList<Atuendo>();
	
	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Sugerencia> getSugerencias() {
		return sugerencias;
	}

	public void setSugerencias(List<Sugerencia> sugerencias) {
		this.sugerencias = sugerencias;
	}
	
	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void inicializarRepo() throws PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, GuardarropasNoEncontradoException, ParseException, FechaInvaldiaException, SugerenciaInvalidaException, SugerenciasNoGeneradasException, InterruptedException, GuardarropasNoAlzanzaFiltrosBasicosException {
		////////////////////// Inicializacion de los colores //////////////////////
		
		Color rojo = Color.RED;
		Color azul = Color.BLUE;
		Color noColor = Color.DARK_GRAY;
		Color negro = Color.BLACK;
		
		////////////////////// Inicializacion de las prendas //////////////////////
		
		Categoria categoriaInferior = new Categoria(ParteDelCuerpo.Piernas);
		Categoria categoriaTorso = new Categoria(ParteDelCuerpo.Torso);
		Categoria categoriaCalzado = new Categoria(ParteDelCuerpo.Pies);
		Categoria categoriaCabeza = new Categoria(ParteDelCuerpo.Cabeza);
		Categoria categoriaManos = new Categoria(ParteDelCuerpo.Manos);
		
		//Agrego las categorias al repositorio para que las sepa parsear
		categorias.add(categoriaTorso); //0
		categorias.add(categoriaInferior); //1
		categorias.add(categoriaCalzado); //2
		categorias.add(categoriaCabeza); //3
		categorias.add(categoriaManos); //4
		
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
		
		Prenda unaPrenda5=new Prenda(negro, rojo, "Algodon", tipoRemera, categoriaTorso);
		Prenda unaPrenda6=new Prenda(azul, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
		Prenda unaPrenda7=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
		Prenda unaPrenda8=new Prenda(negro, rojo, "Algodon", tipoLompa, categoriaInferior);
		Prenda unaPrenda9=new Prenda(negro, azul, "Impermeable", tipoCampera, categoriaTorso);
		
		////////////////////// Inicializacion del Usuario //////////////////////
		/*
		CriterioTemperatura criterioTemperatura = new CriterioTemperatura("Buenos Aires");
		
		List <Criterios> criterios=new ArrayList<Criterios>();
		criterios.add(criterioTemperatura);
		Si no termino superando el limite de accuweather
		*/
		
		Usuario usuario = new Usuario("MiPlacard","Buenos Aires");
		
		//usuario.criterios=criterios;
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda0);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda1);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);
		
		Date date= new Date();
		
		usuario.setMail("pepeargento@gmail.com");
		usuario.setPassword("akd");
		
		usuarios.add(usuario);

		//////////////////////Inicializacion de Eventos //////////////////////
		
		List<Criterios>criterios= new ArrayList();
		
		usuario.planificarEvento("10-10-2020","Buenos Aires",usuario.getUnGuardarropas("MiPlacard"),criterios,0,false);
		usuario.planificarEvento("10-10-2020","Buenos Aires",usuario.getUnGuardarropas("MiPlacard"),criterios,0,false);
		PlanificacionEvento unaPlanificacion=usuario.planificaciones.get(0);
		usuario.pedirSugerencias("MiPlacard");
		
		
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda5);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda6);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda7);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda8);
		usuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda9);
		
		Atuendo atuendo1=new Atuendo();
		atuendo1.prendas.add(unaPrenda7);
		atuendo1.prendas.add(unaPrenda8);
		atuendo1.prendas.add(unaPrenda6);
		Atuendo atuendo2=new Atuendo();
		atuendo1.prendas.add(unaPrenda7);
		atuendo1.prendas.add(unaPrenda8);
		atuendo1.prendas.add(unaPrenda5);
		List<Atuendo> atuendos=Arrays.asList(atuendo1,atuendo2);
		criterios.add(new CriterioMonoCromatico());
		criterios.add(new CriterioColorinche());
		usuario.OcurrioUnaAlerta(new Notificador(new Evento(unaPlanificacion),atuendos));
			
		
	}
}
