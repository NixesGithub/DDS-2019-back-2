package Controladores;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import Criterios.Criterios;
import ExceptionsPrendas.PrendaInvalidaException;
import ExceptionsPrendas.PrendaYaExistenteException;
import grupo1.utn.frba.dds.EntityManagerSingleton;
import grupo1.utn.frba.dds.FechaInvaldiaException;
import grupo1.utn.frba.dds.Guardarropas;
import grupo1.utn.frba.dds.GuardarropasNoEncontradoException;
import grupo1.utn.frba.dds.PlanificacionEvento;
import grupo1.utn.frba.dds.Server;
import grupo1.utn.frba.dds.SugerenciaInvalidaException;
import grupo1.utn.frba.dds.SugerenciasNoGeneradasException;
import grupo1.utn.frba.dds.Usuario;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class CalendarioController {

	public static ModelAndView mostrarCalendario(Request request, Response response) {
		
		Integer id = Server.getRepositorio().idUsuarioActual;
		
		EntityManager entityManager = EntityManagerSingleton.get();
		
		String stringConsulta ="SELECT g FROM Usuarios u JOIN u.planificaciones g WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");
		
		TypedQuery<PlanificacionEvento> query = entityManager.createQuery(stringConsulta, PlanificacionEvento.class);
		
		List<PlanificacionEvento> eventos = query.getResultList();
		List<List<Criterios>>criterios=eventos.stream().map(planificacion->planificacion.getCriteriosAsociados()).collect(Collectors.toList());
		criterios.forEach(crs->crs.stream().map(criterio->criterio.getNombre()).collect(Collectors.toList()));
		Map<String, Object> map = new HashMap<>();
		map.put("Evento", eventos);
		map.put("criterios", criterios);
		return new ModelAndView(map, "calendario.html");
		
		/* VERSION SIN SQL
		//Consigo al usuario\\
		Usuario unUsuario=Server.getRepositorio().usuarios.get(0);
		//Trabajo con el usuario usuario\\
		List<PlanificacionEvento>eventos=unUsuario.getEventos().stream().map(evento->evento.getPlanificacion()).collect(Collectors.toList());
		List<List<Criterios>>criterios=eventos.stream().map(planificacion->planificacion.getCriteriosAsociados()).collect(Collectors.toList());
		criterios.forEach(crs->crs.stream().map(criterio->criterio.getNombre()).collect(Collectors.toList()));
		Map<String, Object> map = new HashMap<>();
		map.put("Evento", unUsuario.planificaciones);
		map.put("criterios", criterios);
		return new ModelAndView(map, "calendario.html");
		 */
	}

	public static ModelAndView mostrarCrearEvento(Request request, Response response) {
		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(map, "crearEvento.html");	
	}

	public static ModelAndView exitoEvento(Request request,Response response) {
		Map<String,Object> map=new HashMap<>();
		return new ModelAndView(map,"exitoEvento.html");
	}

	public static Object crearEvento(Request request, Response response) {
		Integer id = Server.getRepositorio().idUsuarioActual;
		
		EntityManager entityManager = EntityManagerSingleton.get();
		
		String consulta ="SELECT u FROM Usuarios u WHERE u.id='";
		consulta = consulta.concat(id.toString());
		consulta = consulta.concat("'");
		
		TypedQuery<Usuario> queryUsr = entityManager.createQuery(consulta, Usuario.class);
		
		Usuario unUsuario = queryUsr.getResultList().get(0);
		
		String lugarDelEvento=request.queryParams("lugar");
		String fechaDeEvento=(request.queryParams("Fecha"));
		long seRepiteCada=0;
		Criterios sinSup=Server.criteriosParse(request.queryParams("SinSup"),lugarDelEvento);
		Criterios Temp=Server.criteriosParse(request.queryParams("Temp"), lugarDelEvento);
		Criterios UnCol=Server.criteriosParse(request.queryParams("UnCol"),lugarDelEvento);
		Criterios VariosCol=Server.criteriosParse(request.queryParams("VariosCol"), lugarDelEvento);
		List<Criterios> listaDeCriterios=new ArrayList();

		//Agrego siempre y cuando no sea null
		if(sinSup != null)
			listaDeCriterios.add(sinSup);

		if(Temp != null)
			listaDeCriterios.add(Temp);

		if(UnCol != null)
			listaDeCriterios.add(UnCol);

		if(VariosCol != null)
			listaDeCriterios.add(VariosCol);
		
		String stringConsulta ="SELECT g FROM Usuarios u JOIN u.guardarropas g WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");
		
		TypedQuery<Guardarropas> query = entityManager.createQuery(stringConsulta, Guardarropas.class);
		
		List<Guardarropas> listaGuardarropas = query.getResultList();
		
		if(listaGuardarropas.size() == 0) {
			response.redirect("/AlgoFallo");
			return null;
		} else {
			boolean formalParam=Server.booleanParse(request.queryParams("esFormal"));
			
			try {
				unUsuario.planificarEvento(fechaDeEvento, lugarDelEvento, listaGuardarropas.get(0), listaDeCriterios, seRepiteCada, formalParam);
			} catch (ParseException | FechaInvaldiaException | SugerenciaInvalidaException | SugerenciasNoGeneradasException
					| InterruptedException e) {
				response.redirect("/AlgoFallo");
			}
			
			entityManager.getTransaction().begin();
			entityManager.merge(unUsuario);
			entityManager.getTransaction().commit();
			entityManager.close();
			
			response.redirect("/exitoEvento");

			return null;
		}
		
		/*
		String lugarDelEvento=request.queryParams("lugar");
		String fechaDeEvento=(request.queryParams("Fecha"));
		long seRepiteCada=0;
		Criterios sinSup=Server.criteriosParse(request.queryParams("SinSup"),lugarDelEvento);
		Criterios Temp=Server.criteriosParse(request.queryParams("Temp"), lugarDelEvento);
		Criterios UnCol=Server.criteriosParse(request.queryParams("UnCol"),lugarDelEvento);
		Criterios VariosCol=Server.criteriosParse(request.queryParams("VariosCol"), lugarDelEvento);
		List<Criterios> listaDeCriterios=new ArrayList();

		//Agrego siempre y cuando no sea null
		if(sinSup != null)
			listaDeCriterios.add(sinSup);

		if(Temp != null)
			listaDeCriterios.add(Temp);

		if(UnCol != null)
			listaDeCriterios.add(UnCol);

		if(VariosCol != null)
			listaDeCriterios.add(VariosCol);

		Usuario usuario=Server.getRepositorio().getUsuarios().get(0);
		Guardarropas unGuardarropas=null;
		try {
			unGuardarropas = usuario.getUnGuardarropas(request.queryParams("guardarropasAsociado"));
		} catch (GuardarropasNoEncontradoException e) {
			response.redirect("/AlgoFallo");
		}

		boolean formalParam=Server.booleanParse(request.queryParams("esFormal"));

		try {
			usuario.planificarEvento(fechaDeEvento, lugarDelEvento, unGuardarropas, listaDeCriterios, seRepiteCada, formalParam);
		} catch (ParseException | FechaInvaldiaException | SugerenciaInvalidaException | SugerenciasNoGeneradasException
				| InterruptedException e) {
			response.redirect("/AlgoFallo");
		}

		//
		PlanificacionEvento unaPlanificacion= new PlanificacionEvento(fechaDeEvento, lugarDelEvento, unGuardarropas, listaDeCriterios, seRepiteCada, esFormal, this);
		PlanificadorDeEventos.getPlanificadorDeEventos().planificarEvento(unaPlanificacion);

		Agregar la planificacion a la lista en funcion de la cual se cargan los eventos que mostras en la pagina
		//

		response.redirect("/exitoEvento");

		return null;
		*/

	}

}