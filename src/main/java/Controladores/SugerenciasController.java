package Controladores;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.eclipse.jetty.http.HttpStatus;


import spark.ModelAndView;
import spark.Request;
import spark.Response;

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
import grupo1.utn.frba.dds.*;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import grupo1.utn.frba.dds.*;

public class SugerenciasController {
	//quizas esto deberia estar en un Evento controller idk
	public static ModelAndView mostrarSugerencias(Request request, Response response) {


		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(map, "SugerenciasEdit.html");
	}

	public static ModelAndView MostrarPorAceptar(Request request, Response response) {

		Map<String, Object> vista = new HashMap<>();
		EntityManager entityManager = EntityManagerSingleton.get();


		/*
		 * CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		 * CriteriaQuery<Evento> queryEvento =
		 * criteriaBuilder.createQuery(Evento.class);//Select proyeccion Root<Usuario>
		 * Root<Evento> root=queryEvento.from(Evento.class); Predicate
		 * predicadoSinAceptados = criteriaBuilder.; //joinear con sugerencias y fijarse
		 * que ninguna sea aceptada Predicate anterioresEnFechaTal =null; //decir que
		 * que el evento sea mayor a la fecha actual //esto me trae dudas, deberia estar
		 * en finalizados? o deberia estar en activos Predicate
		 * and=criteriaBuilder.and(anterioresEnFechaTal,predicadoSinAceptados);
		 * queryEvento.select(root).where(and);
		 */


		Integer id = Server.getRepositorio().idUsuarioActual;


		String stringConsulta ="SELECT e FROM Eventos e JOIN e.planificacion p WHERE p.usuarioAlQuePertenezco='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("' AND unix_timestamp(e.fecha) < unix_timestamp(current_timestamp()) + 86400 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) > 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s where s.estadoActual= 'ACEPTADO') = 0 ");

		TypedQuery<Evento> query = entityManager.createQuery(stringConsulta, Evento.class);



		List<Evento> listaEventos = query.getResultList();
		List<Date> fechas=listaEventos.stream().map(e->e.fecha).collect(Collectors.toList());
		vista.put("fechas", fechas);
		vista.put("eventos",listaEventos  );
		return new ModelAndView(vista,"finalizadosPorAceptar.html");
	}
	public static Object calificarSugerencias(Request request, Response response) {



		response.redirect("/inicio");
		return null;
	}



	public static Object seleccionarSugerenciaDelTinder(Request request, Response response) {
		Integer id = Server.getRepositorio().idUsuarioActual;
		String idEvento = request.params("id");

		EntityManager entityManager = EntityManagerSingleton.get();

		String consulta ="SELECT e FROM Eventos e WHERE e.id='";
		consulta = consulta.concat(idEvento);
		consulta = consulta.concat("'");

		TypedQuery<Evento> queryEvento = entityManager.createQuery(consulta, Evento.class);

		Evento unEvento = queryEvento.getSingleResult();

		//Security stuff
		if(!unEvento.getPlanificacion().getUsuarioAlQuePertenezco().getId().toString().equals(Server.getRepositorio().sesionActual.attribute("idUsuarioActual")))
			halt(HttpStatus.NOT_FOUND_404);

		//AND unix_timestamp(e.fecha) < unix_timestamp(current_timestamp()) + 86400 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s where s.estadoActual= 'ACEPTADO') = 0
		boolean condicion = unEvento.fecha.getTime() < (new Date().getTime() + 86400000) && unEvento.sugerenciasParaSalir.stream().filter(p->p.getEstadoActual().equals(Estado.ACEPTADO)).collect(Collectors.toList()).size() == 0;
		if(!condicion)
			halt(HttpStatus.NOT_FOUND_404);


		Sugerencia unaSugerencia= unEvento.sugerenciasParaSalir.stream().filter(unaS -> unaS.getEstadoActual().equals(Estado.SIN_DECIDIR)).collect(Collectors.toList()).get(0);
		if(request.queryParams("Respuesta").equals("Aceptar") || unEvento.sugerenciasParaSalir.stream().filter(unaS -> unaS.getEstadoActual().equals(Estado.SIN_DECIDIR)).collect(Collectors.toList()).size() == 1) {
			//seteas aceptado
			unaSugerencia.aceptar();
			//unaSugerencia.marcarComoUsada();

		} else {
			//seteas rechazada
			unaSugerencia.rechazar();
			//unaSugerencia.marcarComoNoUsada();
		}


		entityManager.getTransaction().begin();
		entityManager.merge(unaSugerencia);
		entityManager.merge(unEvento);
		entityManager.getTransaction().commit();

		//Server.getRepositorio().sugerencias.add(unaSugerencia);
		Server.iteradorDeAtuendos++;

		if(unaSugerencia.getEstadoActual().equals(Estado.ACEPTADO)) {

			List<Sugerencia> sugerenciasSinDecidir = unEvento.sugerenciasParaSalir.stream().filter(unaS -> unaS.getEstadoActual().equals(Estado.SIN_DECIDIR)).collect(Collectors.toList());

			for(int i = 0; i < sugerenciasSinDecidir.size(); i++) {
				Sugerencia otraSugerencia = sugerenciasSinDecidir.get(i);
				otraSugerencia.rechazar();

				entityManager.getTransaction().begin();
				entityManager.merge(otraSugerencia);
				entityManager.merge(unEvento);
				entityManager.getTransaction().commit();
			}

			response.redirect("/sugerencias/finalizados/PorCalificar"); // used to be /sugerencias
		} else {
			String redireccion= "/sugerencias/finalizados/PorAceptar/";
			redireccion= redireccion.concat(idEvento);
			response.redirect(redireccion);
		}
		entityManager.close();
		return null;



	}
	public static Object pedirSugerenciasEvento(Request request, Response response) {

		String id = request.params("event_id");
		String laSugerencia = request.queryParams("sugerencia");
		EntityManager entityManager = EntityManagerSingleton.get();

		//hacer query
		String consultaEvento="SELECT e FROM Eventos e JOIN e.planificacion p WHERE e.id='";
		consultaEvento = consultaEvento.concat(id);
		consultaEvento = consultaEvento.concat("'");

		TypedQuery<Evento> queryEvento = entityManager.createQuery(consultaEvento, Evento.class);

		if(queryEvento.getResultList().isEmpty()) {
			halt(HttpStatus.NOT_FOUND_404);
		}

		Evento evento = queryEvento.getSingleResult();

		//Security stuff
		if(!evento.getPlanificacion().getUsuarioAlQuePertenezco().getId().toString().equals(Server.getRepositorio().sesionActual.attribute("idUsuarioActual")))
			halt(HttpStatus.NOT_FOUND_404);

		//AND unix_timestamp(e.fecha) <= unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) > 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.calificada = true) = 0		
		boolean condicion = evento.fecha.getTime() <= new Date().getTime() && evento.sugerenciasParaSalir.size() > 0 && evento.sugerenciasParaSalir.stream().filter(unaSug -> unaSug.getCalificada()).collect(Collectors.toList()).size() == 0;
		if(!condicion)
			halt(HttpStatus.NOT_FOUND_404);

		String stringConsulta ="SELECT s FROM Eventos e JOIN e.sugerenciasParaSalir s WHERE e.id='";
		stringConsulta = stringConsulta.concat(id);
		stringConsulta = stringConsulta.concat("'");
		/* Quiero todas las sugerencias de ese evento
		stringConsulta = stringConsulta.concat(" AND s.id='");
		stringConsulta = stringConsulta.concat(laSugerencia);
		stringConsulta = stringConsulta.concat("'");
		*/
		TypedQuery<Sugerencia> query = entityManager.createQuery(stringConsulta, Sugerencia.class);

		List<Sugerencia> sugerencias = query.getResultList();

		entityManager.getTransaction().begin();
		for(int i=0; i < sugerencias.size(); i++) { //Pongo cada sugerencia en "SIN_DECIDIR"
			Sugerencia unaS = sugerencias.get(i);
			unaS.setEstadoActual(Estado.SIN_DECIDIR);
			entityManager.merge(unaS);
		}

		entityManager.getTransaction().commit();
		entityManager.close();
		/*
		//= sugerencias.get(0);
		sugerenciaAModificar.deshacerUltimaAccion();
		//sugerenciaAModificar.marcarComoNoUsada();

		entityManager.getTransaction().begin();
		entityManager.merge(sugerenciaAModificar);
		entityManager.getTransaction().commit();
		entityManager.close();
		 */

		response.redirect(("/sugerencias/finalizados/PorAceptar"));

		return null;
	}

	public static ModelAndView mostrarFeedbackDeEvento(Request request, Response response) {

		EntityManager entityManager = EntityManagerSingleton.get();

		String id = request.params("event_id");

		String consultaEvento="SELECT e FROM Eventos e JOIN e.planificacion p WHERE e.id='";
		consultaEvento = consultaEvento.concat(id);
		consultaEvento = consultaEvento.concat("'");

		TypedQuery<Evento> queryEvento = entityManager.createQuery(consultaEvento, Evento.class);

		if(queryEvento.getResultList().isEmpty()) {
			halt(HttpStatus.NOT_FOUND_404);
		}

		Evento evento = queryEvento.getSingleResult();

		if(!evento.getPlanificacion().getUsuarioAlQuePertenezco().getId().toString().equals(Server.getRepositorio().sesionActual.attribute("idUsuarioActual"))) {
			//Si el evento no es del usuario que lo pide
			halt(HttpStatus.NOT_FOUND_404);
		}

		//AND unix_timestamp(p.fecha) <= unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) > 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.calificada = true) = 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.estadoActual='ACEPTADO') > 0
		boolean condicion = evento.fecha.getTime() <= new Date().getTime() && evento.sugerenciasParaSalir.size() > 0 && evento.sugerenciasParaSalir.stream().filter(unaSug -> unaSug.getCalificada()).collect(Collectors.toList()).size() == 0 && evento.sugerenciasParaSalir.stream().filter(unaSug -> unaSug.getEstadoActual().equals(Estado.ACEPTADO)).collect(Collectors.toList()).size() > 0;
		if(!condicion)
			halt(HttpStatus.NOT_FOUND_404);

		Map<String, Object> map = new HashMap<>();

		map.put("idSug", request.queryParams("sugerencia"));
		map.put("event_id", id);

		return new ModelAndView(map,"feedback.html");

	}
	public static ModelAndView CalificarEvento(Request request, Response response) {
		String id = request.params("id");

		EntityManager entityManager = EntityManagerSingleton.get();

		//hacer query
		String stringConsulta="SELECT e FROM Eventos e JOIN e.planificacion p WHERE e.id='";
		stringConsulta = stringConsulta.concat(id);
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Evento> query = entityManager.createQuery(stringConsulta, Evento.class);

		if(query.getResultList().isEmpty()) {
			halt(HttpStatus.NOT_FOUND_404);
		}

		Evento evento = query.getSingleResult();

		//Security stuff
		if(!evento.getPlanificacion().getUsuarioAlQuePertenezco().getId().toString().equals(Server.getRepositorio().sesionActual.attribute("idUsuarioActual")))
			halt(HttpStatus.NOT_FOUND_404);

		//AND unix_timestamp(e.fecha) <= unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) > 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.calificada = true) = 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.estadoActual='ACEPTADO') > 0		
		boolean condicion = evento.fecha.getTime() <= new Date().getTime() && evento.sugerenciasParaSalir.size() > 0 && evento.sugerenciasParaSalir.stream().filter(unaSug -> unaSug.getCalificada()).collect(Collectors.toList()).size() == 0 && evento.sugerenciasParaSalir.stream().filter(unaSug -> unaSug.getEstadoActual().equals(Estado.ACEPTADO)).collect(Collectors.toList()).size() > 0;
		if(!condicion)
			halt(HttpStatus.NOT_FOUND_404);

		List<Sugerencia> calificadas=evento.sugerenciasParaSalir.stream().filter(s->s.getEstadoActual().equals(Estado.ACEPTADO)&& s.getCalificada()).collect(Collectors.toList());
		List<Sugerencia> noCalificadas=evento.sugerenciasParaSalir.stream().filter(s->s.getEstadoActual().equals(Estado.ACEPTADO)&& !s.getCalificada()).collect(Collectors.toList());
		List<Sugerencia> rechazadas=evento.sugerenciasParaSalir.stream().filter(s->s.getEstadoActual().equals(Estado.RECHAZADO)).collect(Collectors.toList());
		List<String>idsRechazadas=rechazadas.stream().map(r->r.getId().toString()).collect(Collectors.toList());
		List<String>idsSinCalificar=noCalificadas.stream().map(r->r.getId().toString()).collect(Collectors.toList());



		Map<String, Object> map = new HashMap<>();
		map.put("idsRechazadas", idsRechazadas);
		map.put("idsSinCalificar", idsSinCalificar);
		map.put("sugerenciasSinCalificar", noCalificadas.stream().map(s->s.atuendo.getAtuendoString()).collect(Collectors.toList()));
		map.put("sugerenciasRechazadas", rechazadas.stream().map(s->s.atuendo.getAtuendoString()).collect(Collectors.toList()));
		map.put("sugerenciasCalificadas", calificadas.stream().map(s->s.atuendo.getAtuendoString()).collect(Collectors.toList()));
		map.put("event_id",id);

		return new ModelAndView(map, "calificarSugerenciasEventos.html");
	}

	public static ModelAndView mostrarEventosFinalizadosHistorico(Request request, Response response) {

		String page = request.queryParams("page");
		Integer id = Server.getRepositorio().idUsuarioActual;
		int offset = Integer.parseInt(page) * 2;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT e FROM Eventos e JOIN e.planificacion p WHERE p.usuarioAlQuePertenezco='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("' AND unix_timestamp(p.fecha) < unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) > 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.calificada = true) > 0 ");

		TypedQuery<Evento> query = entityManager.createQuery(stringConsulta, Evento.class).setFirstResult(offset).setMaxResults(2);

		List<Evento> eventos = query.getResultList();
		List<String> sugerencias=eventos.stream().map(e->e.sugerenciasParaSalirString()).collect(Collectors.toList());

		Map<String, Object> map = new HashMap<>();
		List<Date> fechas=eventos.stream().map(e->e.fecha).collect(Collectors.toList());
		map.put("fechas", fechas);
		map.put("eventos", eventos);
		map.put("sugerencias", sugerencias);

		int pagAnterior = Math.max(Integer.parseInt(page) - 1, 0);
		int pagSiguiente = Integer.parseInt(page) + 1;
		map.put("pagAnterior", Integer.toString(pagAnterior));
		map.put("pagSig", Integer.toString(pagSiguiente));
		return new ModelAndView(map, "eventosFinalizadosHistorico.html");
	}

	public static ModelAndView mostrarEventosFinalizadosPorCalificar(Request request, Response response) {

		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT e FROM Eventos e JOIN e.planificacion p WHERE p.usuarioAlQuePertenezco='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("' AND unix_timestamp(e.fecha) <= unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) > 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.calificada = true) = 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.estadoActual='ACEPTADO') > 0 ");

		TypedQuery<Evento> query = entityManager.createQuery(stringConsulta, Evento.class);

		List<Evento> eventos = query.getResultList();

		List<Date> fechas=eventos.stream().map(e->e.fecha).collect(Collectors.toList());
		Map<String, Object> viewData = new HashMap<>();
		viewData.put("eventos", eventos);
		viewData.put("fechas", fechas);

		return new ModelAndView(viewData, "eventosFinalizadosPorCalificar.html");

	}
	public static ModelAndView mostrarEventosFuturos(Request request, Response response){

		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT p FROM Usuarios u JOIN u.planificaciones p WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("' AND unix_timestamp(p.fecha) > unix_timestamp(current_timestamp())");

		TypedQuery<PlanificacionEvento> query = entityManager.createQuery(stringConsulta, PlanificacionEvento.class);

		List<PlanificacionEvento> eventos = query.getResultList();


		Map<String, Object> map = new HashMap<>();
		map.put("eventos", eventos);
		return new ModelAndView(map, "eventosFuturos.html");

	}
	public static ModelAndView mostrarEventosFinalizados(Request request, Response response){
		Map<String, Object> map = new HashMap<>();


		return new ModelAndView(map, "eventosFinalizados.html");

	}

	public static ModelAndView mostrarEventosActivos(Request request, Response response){
		Map<String, Object> map = new HashMap<>();

		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT e FROM Eventos e JOIN e.planificacion p WHERE p.usuarioAlQuePertenezco='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("' AND unix_timestamp(e.fecha) < unix_timestamp(current_timestamp()) + 86400 AND unix_timestamp(e.fecha) > unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) = 0");

		TypedQuery<Evento> query = entityManager.createQuery(stringConsulta, Evento.class);

		List<Evento> eventos = query.getResultList();
		List<Date> fechas=eventos.stream().map(e->e.fecha).collect(Collectors.toList());
		map.put("fechas", fechas);
		map.put("eventos", eventos);
		map.put("ids", eventos.stream().map(unEvento -> unEvento.getId()).collect(Collectors.toList()));
		return new ModelAndView(map, "eventosActivos.html");

	}




	//ESTO NO VA MAS PAPITO GOMEZ
	public static ModelAndView mostrarSugerencias2(Request request, Response response) {
		Map<String, Object> map = new HashMap<>();

		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT s FROM Usuarios u JOIN u.sugerencias s WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Sugerencia> query = entityManager.createQuery(stringConsulta, Sugerencia.class);

		List<Sugerencia> sugerencias = query.getResultList();

		/*
		String consultaAceptadas ="SELECT s FROM Usuarios u JOIN u.sugerencias s WHERE u.id='";
		consultaAceptadas = consultaAceptadas.concat(id.toString());
		consultaAceptadas = consultaAceptadas.concat("'");
		consultaAceptadas = consultaAceptadas.concat(" AND s.estadoActual='ACEPTADO'");
		TypedQuery<Sugerencia> queryAceptadas = entityManager.createQuery(consultaAceptadas, Sugerencia.class);
		List<Sugerencia> sugerenciasAceptadas = queryAceptadas.getResultList();
		 */

		String consultaRechazadas ="SELECT s FROM Usuarios u JOIN u.sugerencias s WHERE u.id='";
		consultaRechazadas = consultaRechazadas.concat(id.toString());
		consultaRechazadas = consultaRechazadas.concat("'");
		consultaRechazadas = consultaRechazadas.concat(" AND s.estadoActual='RECHAZADO'");
		TypedQuery<Sugerencia> queryRechazadas = entityManager.createQuery(consultaRechazadas, Sugerencia.class);
		List<Sugerencia> sugerenciasRechazadas = queryRechazadas.getResultList();

		String consultaCalificadas ="SELECT s FROM Usuarios u JOIN u.sugerencias s WHERE u.id='";
		consultaCalificadas = consultaCalificadas.concat(id.toString());
		consultaCalificadas = consultaCalificadas.concat("'");
		consultaCalificadas = consultaCalificadas.concat(" AND s.estadoActual='ACEPTADO' AND s.calificada='1'");
		TypedQuery<Sugerencia> queryCalificadas = entityManager.createQuery(consultaCalificadas, Sugerencia.class);
		List<Sugerencia> sugerenciasCalificadas = queryCalificadas.getResultList();

		String consultaSinCalificar ="SELECT s FROM Usuarios u JOIN u.sugerencias s WHERE u.id='";
		consultaSinCalificar = consultaSinCalificar.concat(id.toString());
		consultaSinCalificar = consultaSinCalificar.concat("'");
		consultaSinCalificar = consultaSinCalificar.concat(" AND s.estadoActual='ACEPTADO' AND s.calificada='0'");
		TypedQuery<Sugerencia> querySinCalificar = entityManager.createQuery(consultaSinCalificar, Sugerencia.class);
		List<Sugerencia> sugerenciasSinCalificar = querySinCalificar.getResultList();

		//De cada sugerencia --> Atuendo --> Prendas		
		List<String> idsSinCalificar = new ArrayList<String>();
		List<List<String>> atuendosSinCalificarString = new ArrayList<List<String>>();
		for(int i=0; sugerenciasSinCalificar.size() > i; i++) {
			List<String> prendasString = new ArrayList<String>();
			Atuendo unAtuendo = sugerenciasSinCalificar.get(i).getAtuendo();
			unAtuendo.getSuperior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getInferior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getCalzado().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getAccesorios().forEach(prenda->prendasString.add(prenda.tipo.descripcion));

			atuendosSinCalificarString.add(prendasString);
			idsSinCalificar.add(sugerenciasSinCalificar.get(i).getId().toString());
		}
		map.put("sugerenciasSinCalificar", atuendosSinCalificarString);
		map.put("idsSinCalificar", idsSinCalificar);

		List<String> idsRechazadas = new ArrayList<String>();
		List<List<String>> atuendosRechazadosString = new ArrayList<List<String>>();
		for(int i=0; sugerenciasRechazadas.size() > i; i++) {
			List<String> prendasString = new ArrayList<String>();
			Atuendo unAtuendo = sugerenciasRechazadas.get(i).getAtuendo();
			unAtuendo.getSuperior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getInferior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getCalzado().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getAccesorios().forEach(prenda->prendasString.add(prenda.tipo.descripcion));

			atuendosRechazadosString.add(prendasString);
			idsRechazadas.add(sugerenciasRechazadas.get(i).getId().toString());
		}
		map.put("sugerenciasRechazadas", atuendosRechazadosString);
		map.put("idsRechazadas", idsRechazadas);

		List<List<String>> atuendosCalificadosString = new ArrayList<List<String>>();
		for(int i=0; sugerenciasCalificadas.size() > i; i++) {
			List<String> prendasString = new ArrayList<String>();
			Atuendo unAtuendo = sugerenciasCalificadas.get(i).getAtuendo();
			unAtuendo.getSuperior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getInferior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getCalzado().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getAccesorios().forEach(prenda->prendasString.add(prenda.tipo.descripcion));

			atuendosCalificadosString.add(prendasString);
		}
		map.put("sugerenciasCalificadas", atuendosCalificadosString);

		return new ModelAndView(map, "sugerencias.html");


		/* VERSION SIN SQL
		Map<String, Object> map = new HashMap<>();

		List<Sugerencia> sugerencias = Server.getRepositorio().getSugerencias();

		List<Sugerencia> sugerenciasAceptadas = sugerencias.stream().filter(unaSug -> unaSug.getEstadoActual().equals(Estado.ACEPTADO)).collect(Collectors.toList());

		List<Sugerencia> sugerenciasRechazadas = sugerencias.stream().filter(unaSug -> unaSug.getEstadoActual().equals(Estado.RECHAZADO)).collect(Collectors.toList());
		List<Sugerencia> sugerenciasCalificadas = sugerenciasAceptadas.stream().filter(unaSug -> unaSug.getCalificada()).collect(Collectors.toList());
		List<Sugerencia> sugerenciasSinCalificar = sugerenciasAceptadas.stream().filter(unaSug -> !unaSug.getCalificada()).collect(Collectors.toList());

		//De cada sugerencia --> Atuendo --> Prendas		

		List<List<String>> atuendosSinCalificarString = new ArrayList<List<String>>();
		for(int i=0; sugerenciasSinCalificar.size() > i; i++) {
			List<String> prendasString = new ArrayList<String>();
			Atuendo unAtuendo = sugerenciasSinCalificar.get(i).getAtuendo();
			unAtuendo.getSuperior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getInferior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getCalzado().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getAccesorios().forEach(prenda->prendasString.add(prenda.tipo.descripcion));

			atuendosSinCalificarString.add(prendasString);
		}
		map.put("sugerenciasSinCalificar", atuendosSinCalificarString);

		List<List<String>> atuendosRechazadosString = new ArrayList<List<String>>();
		for(int i=0; sugerenciasRechazadas.size() > i; i++) {
			List<String> prendasString = new ArrayList<String>();
			Atuendo unAtuendo = sugerenciasRechazadas.get(i).getAtuendo();
			unAtuendo.getSuperior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getInferior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getCalzado().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getAccesorios().forEach(prenda->prendasString.add(prenda.tipo.descripcion));

			atuendosRechazadosString.add(prendasString);
		}
		map.put("sugerenciasRechazadas", atuendosRechazadosString);

		List<List<String>> atuendosCalificadosString = new ArrayList<List<String>>();
		for(int i=0; sugerenciasCalificadas.size() > i; i++) {
			List<String> prendasString = new ArrayList<String>();
			Atuendo unAtuendo = sugerenciasCalificadas.get(i).getAtuendo();
			unAtuendo.getSuperior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getInferior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getCalzado().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
			unAtuendo.getAccesorios().forEach(prenda->prendasString.add(prenda.tipo.descripcion));

			atuendosCalificadosString.add(prendasString);
		}
		map.put("sugerenciasCalificadas", atuendosCalificadosString);

		return new ModelAndView(map, "sugerencias.html");
		 */

	}

	public static ModelAndView mostrarPedirSugerencias(Request request, Response response) {		
		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(map, "pedirSugerencias.html");	
	}

	public static ModelAndView mostrarFeedback(Request request, Response response) {
		Integer id = Server.getRepositorio().idUsuarioActual;
		String laSugerencia = request.queryParams("sugerencia");

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT s FROM Usuarios u JOIN u.sugerencias s WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");
		stringConsulta = stringConsulta.concat(" AND s.id='");
		stringConsulta = stringConsulta.concat(laSugerencia);
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Sugerencia> query = entityManager.createQuery(stringConsulta, Sugerencia.class);

		List<Sugerencia> sugerencias = query.getResultList();

		Sugerencia sugerenciaAModificar = sugerencias.get(0);
		sugerenciaAModificar.setCalificada(true);
		sugerenciaAModificar.marcarComoNoUsada();

		entityManager.getTransaction().begin();
		entityManager.merge(sugerenciaAModificar);
		entityManager.getTransaction().commit();
		entityManager.close();

		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(map, "feedback.html");

		/*
		//Seteo la sugerencia como calificada y libero las prendas
		String laSugerencia = request.queryParams("sugerencia");
		Sugerencia sugerenciaAModificar = Server.getRepositorio().sugerencias.stream().filter(sugLista -> Server.sugerenciaAsStringList(sugLista).equals(laSugerencia)).collect(Collectors.toList()).get(0);
		sugerenciaAModificar.setCalificada(true);
		sugerenciaAModificar.marcarComoNoUsada();

		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(map, "feedback.html");
		 */
	}

	public static ModelAndView exitoFeedback(Request request,Response response) {
		Map<String,Object> map=new HashMap<>();
		return new ModelAndView(map,"exitoFeedback.html");
	}

	public static Object pedirSugerencias(Request request, Response response) throws ParseException, FechaInvaldiaException {
		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT g FROM Usuarios u JOIN u.guardarropas g WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");
		stringConsulta = stringConsulta.concat(" AND g.nombre='");
		stringConsulta = stringConsulta.concat(request.queryParams("Guardarropas"));
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Guardarropas> query = entityManager.createQuery(stringConsulta, Guardarropas.class);

		List<Guardarropas> guardarropasResultados = query.getResultList();

		String consulta ="SELECT u FROM Usuarios u WHERE u.id='";
		consulta = consulta.concat(id.toString());
		consulta = consulta.concat("'");

		TypedQuery<Usuario> queryUsr = entityManager.createQuery(consulta, Usuario.class);

		Usuario unUsuario = queryUsr.getResultList().get(0);

		if(guardarropasResultados.size() == 0)
			response.redirect("/AlgoFallo");

		try {

			List<Criterios>criterios= new ArrayList();
			criterios.add(new CriterioMonoCromatico());
			criterios.add(new CriterioColorinche());

			PlanificacionEvento nuevaPlanificacion=new PlanificacionEvento("10-10-2020","Buenos Aires",guardarropasResultados.get(0), criterios,0,false,unUsuario);
			nuevaPlanificacion.setFecha(new Date());
			unUsuario.planificaciones.add(nuevaPlanificacion);
			Evento nuevoEvento = new Evento(nuevaPlanificacion);
			nuevoEvento.fecha = new Date();

			Server.getRepositorio().idEventoEnSeleccion = nuevoEvento.getId();
			Server.getRepositorio().atuendos = Sugeridor.getSugeridor().queMePongo(guardarropasResultados.get(0), unUsuario, unUsuario.getLugar(), new Date());


			/*
			 * if(!nuevoEvento.getPlanificacion().getUsuarioAlQuePertenezco().getId().
			 * toString().equals(Server.getRepositorio().sesionActual.attribute(
			 * "idUsuarioActual"))) halt(HttpStatus.NOT_FOUND_404);
			 */
			
			/* NO HACE FALTA HACER UNA VALIDACION PORQUE EL EVENTO SE GENERA ACA MISMO, SIN QUE EL CLIENTE TOQUE NADA
			//AND unix_timestamp(e.fecha) < unix_timestamp(current_timestamp()) + 86400 AND unix_timestamp(e.fecha) > unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) = 0
			boolean condicion = nuevoEvento.fecha.getTime() < (new Date().getTime() + 86400000) && nuevoEvento.fecha.getTime() > new Date().getTime() && nuevoEvento.sugerenciasParaSalir.size() == 0;
			if(!condicion)
				halt(HttpStatus.NOT_FOUND_404);
			 */
			try {
				List<Atuendo>atuendos = Sugeridor.getSugeridor().queMePongo(nuevoEvento.getGuardarropasAsociado(), nuevoEvento.getUsuarioAlQuePertenezco(), nuevoEvento.getUsuarioAlQuePertenezco().getLugar(), new Date());
				nuevoEvento.sugerenciasParaSalir = new ArrayList<Sugerencia>();

				for(int i=0;i<atuendos.size();i++) {
					Sugerencia unaSugerencia=new Sugerencia();
					unaSugerencia.atuendo=atuendos.get(i);
					unaSugerencia.setEstadoActual(Estado.SIN_DECIDIR);
					nuevoEvento.sugerenciasParaSalir.add(unaSugerencia);

				}
				
				entityManager.getTransaction().begin();
				entityManager.persist(nuevoEvento);
				entityManager.merge(unUsuario);
				entityManager.getTransaction().commit();
				entityManager.close();

			} catch (SugerenciaInvalidaException | GuardarropasNoAlzanzaFiltrosBasicosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.redirect("/AlgoFallo");
			}


			entityManager.close();
		} catch (SugerenciaInvalidaException | GuardarropasNoAlzanzaFiltrosBasicosException e) {
			response.redirect("/AlgoFallo");
		}


		Server.iteradorDeAtuendos = 0;
		response.redirect("/sugerencias/finalizados/PorAceptar");

		return null;
		/*
		//Parseo el input
		Guardarropas unGuardarropas=null;
		try {
			unGuardarropas = Server.getRepositorio().getUsuarios().get(0).getUnGuardarropas(request.queryParams("Guardarropas"));
		} catch (GuardarropasNoEncontradoException e) {
			response.redirect("/AlgoFallo");
		}
		try {
			Server.getRepositorio().atuendos = Sugeridor.getSugeridor().queMePongo(unGuardarropas, Server.getRepositorio().usuarios.get(0), Server.getRepositorio().usuarios.get(0).getLugar(), new Date());
		} catch (SugerenciaInvalidaException | GuardarropasNoAlzanzaFiltrosBasicosException e) {
			response.redirect("/AlgoFallo");
		} //El usuario habria que sacarlo de las cookies
		Server.iteradorDeAtuendos = 0;

		//Quizas deberia guardar los atuendos generados en el sv para 
		//ir viendo si la lista tiene elementos o no.

		//Muestro los atuendos para que el tipo acepte o no. 
		//Muestro hasta que acepte una (o muestro todas?).

		//Las no aceptadas se consideran rechazadas (cuak)

		//Las sugerencias aceptadas son las que despues se muestran en /sugerencias

		response.redirect("/seleccionar");

		return null;
		 */
	}

	public static ModelAndView mostrarSeleccionar(Request request, Response response) {

		if(Server.getRepositorio().atuendos.size() > Server.iteradorDeAtuendos) {
			Map<String, Object> map = new HashMap<>();
			Atuendo atuendo = Server.getRepositorio().atuendos.get(Server.iteradorDeAtuendos);

			//Mapeo cada categoria a String
			List<String> superiorString = new ArrayList<String>();
			atuendo.getSuperior().forEach(prenda->superiorString.add(prenda.tipo.descripcion));
			map.put("superior", superiorString);

			List<String> inferiorString = new ArrayList<String>();
			atuendo.getInferior().forEach(prenda->inferiorString.add(prenda.tipo.descripcion));
			map.put("inferior", inferiorString);

			List<String> calzadoString = new ArrayList<String>();
			atuendo.getCalzado().forEach(prenda->calzadoString.add(prenda.tipo.descripcion));
			map.put("calzado", calzadoString);

			List<String> accesoriosString = new ArrayList<String>();
			atuendo.getAccesorios().forEach(prenda->accesoriosString.add(prenda.tipo.descripcion));
			map.put("accesorios", accesoriosString);

			return new ModelAndView(map, "seleccionar.html");		
		} else {
			response.redirect("/sugerencias/finalizados/PorCalificar"); // used to be /sugerencias
			return null;
		}
	}

	public static ModelAndView AceptarSugerenciasEnTinder(Request request, Response response) {

		Integer id = Server.getRepositorio().idUsuarioActual;
		String idEvento = request.params("id");

		EntityManager entityManager = EntityManagerSingleton.get();

		String consulta ="SELECT e FROM Eventos e WHERE e.id='";
		consulta = consulta.concat(idEvento);
		consulta = consulta.concat("'");

		TypedQuery<Evento> queryEvento = entityManager.createQuery(consulta, Evento.class);

		Evento unEvento = queryEvento.getSingleResult();

		//Security stuff
		if(!unEvento.getPlanificacion().getUsuarioAlQuePertenezco().getId().toString().equals(Server.getRepositorio().sesionActual.attribute("idUsuarioActual")))
			halt(HttpStatus.NOT_FOUND_404);

		//AND unix_timestamp(e.fecha) < unix_timestamp(current_timestamp()) + 86400 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) > 0 AND  (SELECT COUNT(*) FROM e.sugerenciasParaSalir s where s.estadoActual= 'ACEPTADO') = 0
		boolean condicion = unEvento.fecha.getTime() < (new Date().getTime() + 86400000) && unEvento.sugerenciasParaSalir.size() > 0 && unEvento.sugerenciasParaSalir.stream().filter(p->p.getEstadoActual().equals(Estado.ACEPTADO)).collect(Collectors.toList()).size() == 0;
		if(!condicion)
			halt(HttpStatus.NOT_FOUND_404);

		List<Atuendo> listaAtuendos=unEvento.sugerenciasParaSalir.stream().filter(unaS -> unaS.getEstadoActual().equals(Estado.SIN_DECIDIR)).map(s->s.atuendo).collect(Collectors.toList());
		Map<String, Object> map = new HashMap<>();

		Atuendo atuendo = listaAtuendos.get(0);
		//Mapeo cada categoria a String
		List<String> superiorString = new ArrayList<String>();
		atuendo.getSuperior().forEach(prenda->superiorString.add(prenda.tipo.descripcion));
		map.put("superior", superiorString);

		List<String> inferiorString = new ArrayList<String>();
		atuendo.getInferior().forEach(prenda->inferiorString.add(prenda.tipo.descripcion));
		map.put("inferior", inferiorString);

		List<String> calzadoString = new ArrayList<String>();
		atuendo.getCalzado().forEach(prenda->calzadoString.add(prenda.tipo.descripcion));
		map.put("calzado", calzadoString);

		List<String> accesoriosString = new ArrayList<String>();
		atuendo.getAccesorios().forEach(prenda->accesoriosString.add(prenda.tipo.descripcion));
		map.put("accesorios", accesoriosString);

		map.put("id", idEvento);
		return new ModelAndView(map, "seleccionar.html");		


	}
	public static Object seleccionar(Request request, Response response) {
		Integer id = Server.getRepositorio().idUsuarioActual;
		Integer idEvento = Server.getRepositorio().idEventoEnSeleccion;

		EntityManager entityManager = EntityManagerSingleton.get();

		String consulta ="SELECT e FROM Eventos e WHERE e.id='";
		consulta = consulta.concat(idEvento.toString());
		consulta = consulta.concat("'");

		TypedQuery<Evento> queryEvento = entityManager.createQuery(consulta, Evento.class);

		Evento unEvento = queryEvento.getResultList().get(0);

		Sugerencia unaSugerencia= new Sugerencia();
		unaSugerencia.setAtuendo(Server.getRepositorio().atuendos.get(Server.iteradorDeAtuendos));
		if(request.queryParams("Respuesta").equals("Aceptar") || (Server.iteradorDeAtuendos + 1) >= Server.getRepositorio().atuendos.size()) {
			//seteas aceptado
			unaSugerencia.aceptar();
			//unaSugerencia.marcarComoUsada();

		} else {
			//seteas rechazada
			unaSugerencia.rechazar();
			//unaSugerencia.marcarComoNoUsada();
		}

		unEvento.sugerenciasParaSalir.add(unaSugerencia);

		entityManager.getTransaction().begin();
		entityManager.persist(unaSugerencia);
		entityManager.merge(unEvento);
		entityManager.getTransaction().commit();

		Server.getRepositorio().sugerencias.add(unaSugerencia);
		Server.iteradorDeAtuendos++;

		if(unaSugerencia.getEstadoActual().equals(Estado.ACEPTADO)) {
			for(int i = Server.iteradorDeAtuendos; i < Server.getRepositorio().atuendos.size(); i++) {
				Sugerencia otraSugerencia= new Sugerencia();
				otraSugerencia.setAtuendo(Server.getRepositorio().atuendos.get(i));
				otraSugerencia.rechazar();

				unEvento.sugerenciasParaSalir.add(otraSugerencia);

				entityManager.getTransaction().begin();
				entityManager.persist(otraSugerencia);
				entityManager.merge(unEvento);
				entityManager.getTransaction().commit();
			}
			response.redirect("/sugerencias/finalizados/PorCalificar"); // used to be /sugerencias
		} else {
			response.redirect("/seleccionar");
		}
		entityManager.close();
		return null;
	}

	public static Object feedback(Request request, Response response) {
		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String consulta ="SELECT u FROM Usuarios u WHERE u.id='";
		consulta = consulta.concat(id.toString());
		consulta = consulta.concat("'");

		TypedQuery<Usuario> queryUsr = entityManager.createQuery(consulta, Usuario.class);

		Usuario unUsuario = queryUsr.getResultList().get(0);
		
		String global = request.queryParams("Global");
		if(global.isEmpty())
			global = "0";
		
		String cabeza = request.queryParams("Cabeza");
		if(cabeza.isEmpty())
			cabeza = "0";
		
		String torso = request.queryParams("Torso");
		if(torso.isEmpty())
			torso = "0";
		
		String piernas = request.queryParams("Piernas");
		if(piernas.isEmpty())
			piernas = "0";
		
		String pies = request.queryParams("Pies");
		if(pies.isEmpty())
			pies = "0";
		
		String manos = request.queryParams("Manos");
		if(manos.isEmpty())
			manos = "0";

		int sensibilidadGlobal=Integer.parseInt(global);
		int sensibilidadCabeza=Integer.parseInt(cabeza);
		int sensibilidadTorso=Integer.parseInt(torso);
		int sensibilidadPiernas=Integer.parseInt(piernas);
		int sensibilidadPies=Integer.parseInt(pies);
		int sensibilidadManos=Integer.parseInt(manos);

		Feedback unFeedback = new Feedback(sensibilidadGlobal, sensibilidadCabeza, sensibilidadTorso, sensibilidadPiernas, sensibilidadPies, sensibilidadManos);

		unUsuario.setFeedback(unFeedback);

		entityManager.getTransaction().begin();
		entityManager.merge(unUsuario);
		entityManager.getTransaction().commit();

		String laSugerencia = request.params("sug_id");
		String idEvento = request.params("event_id");

		//La sugerencia
		String stringConsulta ="SELECT s FROM Eventos e JOIN e.sugerenciasParaSalir s WHERE e.id='";
		stringConsulta = stringConsulta.concat(idEvento);
		stringConsulta = stringConsulta.concat("'");
		stringConsulta = stringConsulta.concat(" AND s.id='");
		stringConsulta = stringConsulta.concat(laSugerencia);
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Sugerencia> query = entityManager.createQuery(stringConsulta, Sugerencia.class);

		Sugerencia sugerenciaAModificar = query.getSingleResult();

		///

		//El evento
		String consultaEvento ="SELECT e FROM Eventos e WHERE e.id='";
		consultaEvento = consultaEvento.concat(idEvento);
		consultaEvento = consultaEvento.concat("'");

		TypedQuery<Evento> queryEvento = entityManager.createQuery(consultaEvento, Evento.class);

		Evento evento = queryEvento.getSingleResult();


		//Security stuff
		if(!evento.getPlanificacion().getUsuarioAlQuePertenezco().getId().toString().equals(Server.getRepositorio().sesionActual.attribute("idUsuarioActual")))
			halt(HttpStatus.NOT_FOUND_404);

		//AND unix_timestamp(e.fecha) <= unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) > 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.calificada = true) = 0 AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s WHERE s.estadoActual='ACEPTADO') > 0		
		boolean condicion = evento.fecha.getTime() <= new Date().getTime() && evento.sugerenciasParaSalir.size() > 0 && evento.sugerenciasParaSalir.stream().filter(unaSug -> unaSug.getCalificada()).collect(Collectors.toList()).size() == 0 && evento.sugerenciasParaSalir.stream().filter(unaSug -> unaSug.getEstadoActual().equals(Estado.ACEPTADO)).collect(Collectors.toList()).size() > 0;
		if(!condicion)
			halt(HttpStatus.NOT_FOUND_404);


		sugerenciaAModificar.setCalificada(true);
		sugerenciaAModificar.marcarComoNoUsada();

		entityManager.getTransaction().begin();
		entityManager.merge(sugerenciaAModificar);
		entityManager.getTransaction().commit();
		entityManager.close();

		response.redirect("/exitoFeedback");

		return null;
		/*
		int sensibilidadGlobal=Integer.parseInt(request.queryParams("Global"));;
		int sensibilidadCabeza=Integer.parseInt(request.queryParams("Cabeza"));
		int sensibilidadTorso=Integer.parseInt(request.queryParams("Torso"));
		int sensibilidadPiernas=Integer.parseInt(request.queryParams("Piernas"));
		int sensibilidadPies=Integer.parseInt(request.queryParams("Pies"));
		int sensibilidadManos=Integer.parseInt(request.queryParams("Manos"));

		Feedback unFeedback = new Feedback(sensibilidadGlobal, sensibilidadCabeza, sensibilidadTorso, sensibilidadPiernas, sensibilidadPies, sensibilidadManos);

		//obtengo el usuario
		Server.getRepositorio().usuarios.get(0).setFeedback(unFeedback);

		response.redirect("/exitoFeedback");

		return null;
		 */
	}

	public static Object deshacer(Request request, Response response) {	
		Integer id = Server.getRepositorio().idUsuarioActual;
		String laSugerencia = request.queryParams("sugerencia");

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT s FROM Usuarios u JOIN u.sugerencias s WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");
		stringConsulta = stringConsulta.concat(" AND s.id='");
		stringConsulta = stringConsulta.concat(laSugerencia);
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Sugerencia> query = entityManager.createQuery(stringConsulta, Sugerencia.class);

		List<Sugerencia> sugerencias = query.getResultList();

		Sugerencia sugerenciaAModificar = sugerencias.get(0);
		sugerenciaAModificar.deshacerUltimaAccion();
		//sugerenciaAModificar.marcarComoNoUsada();

		entityManager.getTransaction().begin();
		entityManager.merge(sugerenciaAModificar);
		entityManager.getTransaction().commit();
		entityManager.close();

		response.redirect("/sugerencias");
		return null;

		/*
		String laSugerencia = request.queryParams("sugerencia");
		Sugerencia sugerenciaAModificar = Server.getRepositorio().sugerencias.stream().filter(sugLista -> Server.sugerenciaAsStringList(sugLista).equals(laSugerencia)).collect(Collectors.toList()).get(0);
		sugerenciaAModificar.deshacerUltimaAccion();

		response.redirect("/sugerencias");	
		return null;	
		 */
	}

	public static Object generarSugerenciasEvento(Request request, Response response) throws SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {

		String id = request.queryParams("idEvento");
		Server.getRepositorio().idEventoEnSeleccion = Integer.parseInt(id);

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT e FROM Eventos e WHERE e.id='";
		stringConsulta = stringConsulta.concat(id);
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Evento> query = entityManager.createQuery(stringConsulta, Evento.class);

		List<Evento> eventosResultados = query.getResultList();

		Evento elEvento = eventosResultados.get(0);

		//Security stuff
		if(!elEvento.getPlanificacion().getUsuarioAlQuePertenezco().getId().toString().equals(Server.getRepositorio().sesionActual.attribute("idUsuarioActual")))
			halt(HttpStatus.NOT_FOUND_404);

		//AND unix_timestamp(e.fecha) < unix_timestamp(current_timestamp()) + 86400 AND unix_timestamp(e.fecha) > unix_timestamp(current_timestamp()) AND (SELECT COUNT(*) FROM e.sugerenciasParaSalir s) = 0
		boolean condicion = elEvento.fecha.getTime() < (new Date().getTime() + 86400000) && elEvento.fecha.getTime() > new Date().getTime() && elEvento.sugerenciasParaSalir.size() == 0;
		if(!condicion)
			halt(HttpStatus.NOT_FOUND_404);

		try {
			List<Atuendo>atuendos = Sugeridor.getSugeridor().queMePongo(elEvento.getGuardarropasAsociado(), elEvento.getUsuarioAlQuePertenezco(), elEvento.getUsuarioAlQuePertenezco().getLugar(), new Date());

			entityManager.getTransaction().begin();

			for(int i=0;i<atuendos.size();i++) {

				Sugerencia unaSugerencia=new Sugerencia();
				unaSugerencia.atuendo=atuendos.get(i);
				unaSugerencia.setEstadoActual(Estado.SIN_DECIDIR);
				elEvento.sugerenciasParaSalir.add(unaSugerencia);

				entityManager.persist(unaSugerencia);
				entityManager.merge(elEvento);

			}
			entityManager.getTransaction().commit();
			entityManager.close();



		} catch (SugerenciaInvalidaException | GuardarropasNoAlzanzaFiltrosBasicosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.redirect("/AlgoFallo");
		}

		Server.iteradorDeAtuendos = 0;
		response.redirect("/sugerencias/finalizados/PorAceptar");

		return null;

		/*
		//Parseo el input
		Guardarropas unGuardarropas=null;
		try {
			unGuardarropas = Server.getRepositorio().getUsuarios().get(0).getUnGuardarropas(request.queryParams("Guardarropas"));
		} catch (GuardarropasNoEncontradoException e) {
			response.redirect("/AlgoFallo");
		}
		try {
			Server.getRepositorio().atuendos = Sugeridor.getSugeridor().queMePongo(unGuardarropas, Server.getRepositorio().usuarios.get(0), Server.getRepositorio().usuarios.get(0).getLugar(), new Date());
		} catch (SugerenciaInvalidaException | GuardarropasNoAlzanzaFiltrosBasicosException e) {
			response.redirect("/AlgoFallo");
		} //El usuario habria que sacarlo de las cookies
		Server.iteradorDeAtuendos = 0;

		//Quizas deberia guardar los atuendos generados en el sv para 
		//ir viendo si la lista tiene elementos o no.

		//Muestro los atuendos para que el tipo acepte o no. 
		//Muestro hasta que acepte una (o muestro todas?).

		//Las no aceptadas se consideran rechazadas (cuak)

		//Las sugerencias aceptadas son las que despues se muestran en /sugerencias

		response.redirect("/seleccionar");

		return null;
		 */
	}

}

/* seleccionar original
public static Object seleccionar(Request request, Response response) {
		Integer id = Server.getRepositorio().idUsuarioActual;
		Integer idEvento = Server.getRepositorio().idEventoEnSeleccion;

		EntityManager entityManager = EntityManagerSingleton.get();

		String consulta ="SELECT u FROM Usuarios u WHERE u.id='";
		consulta = consulta.concat(id.toString());
		consulta = consulta.concat("'");

		TypedQuery<Usuario> queryUsr = entityManager.createQuery(consulta, Usuario.class);

		Usuario unUsuario = queryUsr.getResultList().get(0);

		Sugerencia unaSugerencia= new Sugerencia();
		unaSugerencia.setAtuendo(Server.getRepositorio().atuendos.get(Server.iteradorDeAtuendos));
		if(request.queryParams("Respuesta").equals("Aceptar")) {
			//seteas aceptado
			unaSugerencia.aceptar();
			//unaSugerencia.marcarComoUsada();

		} else {
			//seteas rechazada
			unaSugerencia.rechazar();
			//unaSugerencia.marcarComoNoUsada();
		}

		unUsuario.sugerencias.add(unaSugerencia);

		entityManager.getTransaction().begin();
		entityManager.persist(unaSugerencia);
		entityManager.merge(unUsuario);
		entityManager.getTransaction().commit();
		entityManager.close();

		Server.getRepositorio().sugerencias.add(unaSugerencia);
		Server.iteradorDeAtuendos++;
		response.redirect("/seleccionar");
		return null;
	}

 PEDIR SUGERENCIAS ORIGINAL

 public static Object pedirSugerencias(Request request, Response response) {
		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT g FROM Usuarios u JOIN u.guardarropas g WHERE u.id='";
		stringConsulta = stringConsulta.concat(id.toString());
		stringConsulta = stringConsulta.concat("'");
		stringConsulta = stringConsulta.concat(" AND g.nombre='");
		stringConsulta = stringConsulta.concat(request.queryParams("Guardarropas"));
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Guardarropas> query = entityManager.createQuery(stringConsulta, Guardarropas.class);

		List<Guardarropas> guardarropasResultados = query.getResultList();

		String consulta ="SELECT u FROM Usuarios u WHERE u.id='";
		consulta = consulta.concat(id.toString());
		consulta = consulta.concat("'");

		TypedQuery<Usuario> queryUsr = entityManager.createQuery(consulta, Usuario.class);

		Usuario unUsuario = queryUsr.getResultList().get(0);

		if(guardarropasResultados.size() == 0)
			response.redirect("/AlgoFallo");

		try {
			Server.getRepositorio().atuendos = Sugeridor.getSugeridor().queMePongo(guardarropasResultados.get(0), unUsuario, unUsuario.getLugar(), new Date());
		} catch (SugerenciaInvalidaException | GuardarropasNoAlzanzaFiltrosBasicosException e) {
			response.redirect("/AlgoFallo");
		}
		Server.iteradorDeAtuendos = 0;
		response.redirect("/seleccionar");
		//idEventoEnSeleccion

		return null;

		--
		//Parseo el input
		Guardarropas unGuardarropas=null;
		try {
			unGuardarropas = Server.getRepositorio().getUsuarios().get(0).getUnGuardarropas(request.queryParams("Guardarropas"));
		} catch (GuardarropasNoEncontradoException e) {
			response.redirect("/AlgoFallo");
		}
		try {
			Server.getRepositorio().atuendos = Sugeridor.getSugeridor().queMePongo(unGuardarropas, Server.getRepositorio().usuarios.get(0), Server.getRepositorio().usuarios.get(0).getLugar(), new Date());
		} catch (SugerenciaInvalidaException | GuardarropasNoAlzanzaFiltrosBasicosException e) {
			response.redirect("/AlgoFallo");
		} //El usuario habria que sacarlo de las cookies
		Server.iteradorDeAtuendos = 0;

		//Quizas deberia guardar los atuendos generados en el sv para 
		//ir viendo si la lista tiene elementos o no.

		//Muestro los atuendos para que el tipo acepte o no. 
		//Muestro hasta que acepte una (o muestro todas?).

		//Las no aceptadas se consideran rechazadas (cuak)

		//Las sugerencias aceptadas son las que despues se muestran en /sugerencias

		response.redirect("/seleccionar");

		return null;
		--
	}
 */
