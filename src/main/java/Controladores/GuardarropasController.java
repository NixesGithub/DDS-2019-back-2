package Controladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import Prendas.Prenda;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import grupo1.utn.frba.dds.*;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.awt.Color;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import APIs.AdaptadorAW;
import APIs.AdaptadorOW;
import APIs.Clima;
import APIs.ClimaNormal;
import APIs.ClimaXCiudad;
import CoordinadorDeServicion.ServicioClima;
import Criterios.CriterioColorinche;
import Criterios.CriterioMonoCromatico;
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
import Tipos.Campera;
import Tipos.Guantes;
import Tipos.Pantalon;
import Tipos.Remera;
import Tipos.Tipo;
import Tipos.Zapatillas;
import junit.framework.Assert;
import Controladores.*;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;



public class GuardarropasController {

	public static ModelAndView mostrarGuardarropas(Request request, Response response) {
		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT g FROM Usuarios u JOIN u.guardarropas g WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Guardarropas> query = entityManager.createQuery(stringConsulta, Guardarropas.class);

		List<Guardarropas> guardarropasResultados = query.getResultList();

		guardarropasResultados.stream().map(guardarropas->guardarropas.getNombre());

		List <List<String>> prendasDeGuardarropasString= new ArrayList();

		for(int i=0; i< guardarropasResultados.size();i++) {

			List <Prenda> prendas= guardarropasResultados.get(i).getPrendas();
			List <String>prendasString=new ArrayList();
			prendas.forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			prendasDeGuardarropasString.add(prendasString);
		}

		List <List<Prenda>> prendasDeGuardarropas=guardarropasResultados.stream().map(guardarropas->guardarropas.getPrendas()).collect(Collectors.toList());
		prendasDeGuardarropasString=prendasDeGuardarropas.stream().map(prendas->prendas.stream().map(prenda->prenda.tipo.descripcion).collect(Collectors.toList())).collect(Collectors.toList());
		Map<String, Object> map = new HashMap<>();
		map.put("guardarropas", guardarropasResultados);
		map.put("Prendas", prendasDeGuardarropasString);
		return new ModelAndView(map, "guardarropas.html");

		/* VERSION SIN SQL
		//obtengo el usuario
		Usuario usuario= Server.getRepositorio().usuarios.get(0);

		//trabajo con los guardarropas

		usuario.getGuardarropas().stream().map(guardarropas->guardarropas.getNombre());

		List <List<String>> prendasDeGuardarropasString= new ArrayList();

		for(int i=0; i< usuario.getGuardarropas().size();i++) {

			List <Prenda> prendas= usuario.getGuardarropas().get(i).getPrendas();
			List <String>prendasString=new ArrayList();
			prendas.forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			prendasDeGuardarropasString.add(prendasString);
		}

		List <List<Prenda>> prendasDeGuardarropas=usuario.getGuardarropas().stream().map(guardarropas->guardarropas.getPrendas()).collect(Collectors.toList());
		prendasDeGuardarropasString=prendasDeGuardarropas.stream().map(prendas->prendas.stream().map(prenda->prenda.tipo.descripcion).collect(Collectors.toList())).collect(Collectors.toList());
		Map<String, Object> map = new HashMap<>();
		map.put("guardarropas", usuario.getGuardarropas());
		map.put("Prendas", prendasDeGuardarropasString);
		return new ModelAndView(map, "guardarropas.html");
		 */
	}

	public static ModelAndView mostrarCrearPrenda(Request request, Response response) {
		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(map, "crearPrenda.html");	
	}

	public static ModelAndView recargarPrendaFallida(Request request, Response response) {

		Map<String, Object> map = new HashMap<>();

		map.put("guardarropas",Server.guardarropasFallido);
		map.put("PrendaFallida", Server.prendaFallida);
		map.put("tipo", Server.parsearTipo(Server.prendaFallida.tipo));
		map.put("descripcionPrendaFallida", Server.prendaFallida.getTipo().descripcion);
		map.put("ColorPrincipal", Server.antiParsearColor(Server.prendaFallida.getColorPrimario()));
		map.put("ColorSecundario",Server.antiParsearColor(Server.prendaFallida.getColorSecundario()));
		return new ModelAndView(map, "RecargarPrenda.html");	

	}

	public static ModelAndView exitoPrenda(Request request,Response response) {
		Map<String,Object> map=new HashMap<>();
		return new ModelAndView(map,"exitoPrenda.html");
	}

	public static Object crearPrenda(Request request, Response response)  {

		//Prenda unaPrenda0=new Prenda(azul, rojo, "Algodon", tipoRemera, categoriaTorso);

		//Parseo el input
		Color colorPrimario ;
		Color colorSecundario;
		String descripcion ;
		String tipoString ;
		int nivelDeAbrigo ;
		Tipo tipo  ;
		String material ;
		Categoria categoria ;
		int capa ;



		try {
			colorPrimario = Server.colorParser(request.queryParams("Color primario"));
			colorSecundario = Server.colorParser(request.queryParams("Color secundario"));
			descripcion = request.queryParams("Descripcion");
			tipoString = request.queryParams("Tipo");
			nivelDeAbrigo = Integer.parseInt(request.queryParams("Nivel de abrigo"));
			tipo = Server.tipoParser(tipoString, descripcion, nivelDeAbrigo);
			material = request.queryParams("Material");
			categoria = Server.categoriaParser(request.queryParams("Categoria"));
			capa = Integer.parseInt(request.queryParams("Capa"));

		} catch (NumberFormatException e){
			response.redirect("/crearPrenda");
			return null;
		}

		//Mas burocracia
		categoria.getPermitidos().add(tipo);
		tipo.permitidos.add(material);
		tipo.setCapa(capa);

		Prenda prendaACrear = new Prenda(colorPrimario, colorSecundario, material, tipo, categoria);

		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT g FROM Usuarios u JOIN u.guardarropas g WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Guardarropas> query = entityManager.createQuery(stringConsulta, Guardarropas.class);

		List<Guardarropas> listaGuardarropas = query.getResultList();

		if(listaGuardarropas.size() == 0) {
			response.redirect("/AlgoFallo");
		} else {

			try {	
				prendaACrear.validate();
			} catch (PrendaInvalidaException e) {
				Server.prendaFallida=prendaACrear;
				Server.guardarropasFallido=listaGuardarropas.get(0);
				response.redirect("/recargarPrenda");
				return null;
			}

			try {
				listaGuardarropas.get(0).addPrenda(prendaACrear);
			} catch (PrendaYaExistenteException e) {
				response.redirect("/AlgoFallo");
				return null;
			}
		}

		entityManager.getTransaction().begin();
		entityManager.persist(tipo);
		entityManager.merge(categoria);
		entityManager.persist(prendaACrear);
		entityManager.getTransaction().commit();
		entityManager.close();

		response.redirect("/exitoPrenda");

		return null;
	}

}
