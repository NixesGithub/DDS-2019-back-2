package grupo1.utn.frba.dds;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.awt.Color;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.eclipse.jetty.server.session.Session;

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
import Notificador.AtuendoAMostrar;
import Notificador.Notificador;
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
import static grupo1.utn.frba.dds.AuthenticationFiltro.authenticate;

//TODO LIST
/*
 *	autenticacion para agarrar eventos con /id eventos y id usuario + fecha :( --> validar lo de la query
 * 	solution al fail de seleccionar para aceptar---> paginar
 * 	mejorar el paginado del historico
 * 	activos----> generar sugerencias
 * 	No mostrar boton de aceptar en paraCalificar solo para rechazar la aceptada/tampoco mostrar las sugerencias rechazadas
 * 	criterio para determinar "quien esta para aceptar":->el que no tiene nada aceptado?(todo rechazado)(entonces hay que setear todo en "SinDecidir")
 * 	Nuevo Valor de Estado---> "SIN_DECIDIR"
 * 	
 * 	retocar el recargarPrenda y el crearPrenda
*/

public class Server {

	static Repositorio repositorio=new Repositorio();
	public static int iteradorDeAtuendos;
	static Map<String, Object> mapPrendaError = new HashMap<>();
	static List<PlanificacionEvento> eventosListos=new ArrayList<PlanificacionEvento>();
	static public Prenda prendaFallida;
	static public Guardarropas guardarropasFallido;

	public static void main(String[] args) throws PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException, GuardarropasNoEncontradoException, SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException, ParseException, FechaInvaldiaException, SugerenciasNoGeneradasException, InterruptedException {
		enableDebugScreen();
		port(4575);
		
		
		
		boolean localhost = true;
		if (localhost) {
			String projectDir = System.getProperty("user.dir");
			String staticDir = "/src/main/resources/static/";
			staticFiles.externalLocation(projectDir + staticDir);
		} else {
			staticFiles.location("/public");
		}

		repositorio.inicializarRepo();

		// acceso: http://localhost:4570/
		HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();

		get("/", Server::mostrarLogin, engine);
		post("/", Server::validarLogin);
		get("/cerrarSesion", Server::cerrarSesion);

		get("/inicio", InicioController::mostrarInicio, engine);
		get("/guardarropas", GuardarropasController::mostrarGuardarropas, engine);
		get("/sugerencias",SugerenciasController::mostrarSugerencias,engine);
		get("/sugerencias/futuro",SugerenciasController::mostrarEventosFuturos,engine);
		get("/sugerencias/activos",SugerenciasController::mostrarEventosActivos,engine);
		get("/sugerencias/finalizados",SugerenciasController::mostrarEventosFinalizados,engine);
		get("/sugerencias/finalizados/Historico",SugerenciasController::mostrarEventosFinalizadosHistorico,engine);
		get("/sugerencias/finalizados/PorCalificar",SugerenciasController::mostrarEventosFinalizadosPorCalificar,engine);
		get("/sugerencias/finalizados/PorCalificar/:id",SugerenciasController::CalificarEvento,engine);
		get("/sugerencias/finalizados/PorAceptar",SugerenciasController::MostrarPorAceptar,engine);
		get("/sugerencias/finalizados/PorAceptar/:id",SugerenciasController::AceptarSugerenciasEnTinder,engine);

		get("/AlgoFallo", ErrorController::PaginaDeError ,engine);

		//Paginas para generar cosas
		get("/pedirSugerencias", SugerenciasController::mostrarPedirSugerencias, engine);
		post("/pedirSugerencias",SugerenciasController::pedirSugerencias);
		get("/crearPrenda", GuardarropasController::mostrarCrearPrenda, engine);
		get("/crearEvento", CalendarioController::mostrarCrearEvento, engine);
		get("/feedback/:event_id", SugerenciasController::mostrarFeedbackDeEvento, engine);
		get("/recargarPrenda",GuardarropasController::recargarPrendaFallida,engine);
		get("/exitoEvento", CalendarioController::exitoEvento,engine);
		get("/exitoFeedback", SugerenciasController::exitoFeedback,engine);
		get("/exitoPrenda", GuardarropasController::exitoPrenda,engine);
		
		post("/sugerencias/finalizados/PorAceptar/:id",SugerenciasController::seleccionarSugerenciaDelTinder);
		post("/crearEvento", CalendarioController::crearEvento);
		post("/crearPrenda", GuardarropasController::crearPrenda);
		post("/sugerencias/:event_id",SugerenciasController::pedirSugerenciasEvento);
		post("/seleccionar", SugerenciasController::seleccionar);
		post("/feedback/:event_id/:sug_id", SugerenciasController::feedback);
		post("/AlgoFallo", InicioController::redireccionarAInicio );
		post("/sugerencias/activos/generarSugerencias",SugerenciasController::generarSugerenciasEvento);
		get("/calendario", CalendarioController::mostrarCalendario,engine);
		//Organizar llamados bajo los titulos Calendario-Error-Guardarropas-Inicio-Sugerencias-Miscellaneous

		before("/*", authenticate);
		
		
		//Deprecados
		get("/feedback", SugerenciasController::mostrarFeedback, engine);
		post("/notificaciones", Server::rehacerSugerencias);
		post("/sugerencias", SugerenciasController::deshacer );
		get("/notificaciones", Server::MostrarNotificacion,engine);
		get("/seleccionar", SugerenciasController::mostrarSeleccionar, engine);
		get("/triggerEvento", Server::triggerEvento, engine);
		

	}

	static public Repositorio getRepositorio() {
		return repositorio;
	}

	public static Object rehacerSugerencias(Request request, Response response) {


		response.redirect("/pedirSugerencias");

		return null;




	}
	public static ModelAndView MostrarNotificacion(Request request,Response response) throws ParseException, FechaInvaldiaException, SugerenciaInvalidaException, SugerenciasNoGeneradasException, InterruptedException, GuardarropasNoEncontradoException {	
		Map<String,Object> map=new HashMap<>();

		Integer id = Server.getRepositorio().idUsuarioActual;

		EntityManager entityManager = EntityManagerSingleton.get();

		String consulta ="SELECT u FROM Usuarios u JOIN u.notificaciones WHERE u.id='";
		consulta = consulta.concat(id.toString());
		consulta = consulta.concat("'");

		TypedQuery<Usuario> queryUsr = entityManager.createQuery(consulta, Usuario.class);

		Usuario usuario = queryUsr.getResultList().get(0);

		List<Notificador> notificaciones = usuario.notificaciones;

		List<PlanificacionEvento>planificaciones=notificaciones.stream().map(notificacion->notificacion.getEventoAsociado().getPlanificacion()).collect(Collectors.toList());
		List<List<Atuendo>> atuendosAsociados=notificaciones.stream().map(notificacion->notificacion.atuendosAsociados).collect(Collectors.toList()); 

		List<List<String>> prendasXAtuendo = convertirAListaDePrendas(atuendosAsociados);

		map.put("notificaciones", notificaciones);
		map.put("planificacion", planificaciones);
		map.put("AtuendosParaMostrar", prendasXAtuendo);

		return new ModelAndView(map,"notificaciones.html");

		/*
		Map<String,Object> map=new HashMap<>();
		Usuario usuario= obtenerUsuario(request, response);
		List<PlanificacionEvento>planificaciones=usuario.notificaciones.stream().map(notificacion->notificacion.getEventoAsociado().getPlanificacion()).collect(Collectors.toList());
		List<List<Atuendo>> atuendosAsociados=usuario.notificaciones.stream().map(notificacion->notificacion.atuendosAsociados).collect(Collectors.toList()); 

		List<List<String>> prendasXAtuendo = convertirAListaDePrendas(atuendosAsociados);

		map.put("notificaciones", usuario.notificaciones);
		map.put("planificacion", planificaciones);
		map.put("AtuendosParaMostrar", prendasXAtuendo);

		return new ModelAndView(map,"notificaciones.html");
		 */

	}

	public static List<List<String>> convertirAListaDePrendas(List<List<Atuendo>>atuendosAsociados){

		List<List<String>> resultado= new ArrayList();
		for(int i=0; i< atuendosAsociados.size();i++) {

			List<Atuendo>lista= atuendosAsociados.get(i);
			List<String>listaDeString= new ArrayList();
			for(int j=0; j< lista.size();j++) {

				Atuendo unAtuendo= lista.get(j);
				String atuendoParciado=unAtuendo.getAllPrendas().toString();
				listaDeString.add(atuendoParciado);

			}
			resultado.add(listaDeString);
		}

		return resultado;

	}
	public static ModelAndView triggerEvento(Request request,Response response) throws ParseException, FechaInvaldiaException, SugerenciaInvalidaException, SugerenciasNoGeneradasException, InterruptedException, GuardarropasNoEncontradoException {
		Map<String,Object> map=new HashMap<>();

		List<Criterios>criterios= new ArrayList<Criterios>();
		repositorio.getUsuarios().get(0).planificarEvento("10-10-2020","Buenos Aires",repositorio.getUsuarios().get(0).getUnGuardarropas("MiPlacard"),criterios,0,false);

		repositorio.getUsuarios().get(0).planificaciones.get(0).setDescripcion("Cumpleagnito");

		eventosListos.add(repositorio.getUsuarios().get(0).planificaciones.get(0));

		List<String> eventosString = new ArrayList<String>();
		eventosListos.forEach(planif->eventosString.add(planif.getDescripcion()));
		map.put("eventos", eventosString);

		List<String> guardarropasString = new ArrayList<String>();
		eventosListos.forEach(planif->guardarropasString.add(planif.getGuardarropasAsociado().getNombre()));
		map.put("guardarropas", guardarropasString);

		return new ModelAndView(map,"inicio.html");
	}

	public static Criterios criteriosParse(String queryParams,String lugar) {
		if(queryParams != null) {
			EntityManager entityManager = EntityManagerSingleton.get();

			String stringConsulta ="SELECT c FROM Criterios c WHERE c.nombre='";

			switch (queryParams) {
			case "Sin Superposicion":
				stringConsulta = stringConsulta.concat(queryParams);
				stringConsulta = stringConsulta.concat("'");
				TypedQuery<Criterios> querySup = entityManager.createQuery(stringConsulta, Criterios.class);
				Criterios criterioSup = querySup.getResultList().get(0);

				return criterioSup;

			case "Temperatura":
				Criterios criterioTemp = new CriterioTemperatura(lugar);
				entityManager.persist(criterioTemp);

				return criterioTemp;

			case"MonoCromatico":
				stringConsulta = stringConsulta.concat(queryParams);
				stringConsulta = stringConsulta.concat("'");
				TypedQuery<Criterios> queryMono = entityManager.createQuery(stringConsulta, Criterios.class);
				Criterios critMono = queryMono.getResultList().get(0);

				return critMono;

			case"Colorinche":
				stringConsulta = stringConsulta.concat(queryParams);
				stringConsulta = stringConsulta.concat("'");
				TypedQuery<Criterios> queryColor = entityManager.createQuery(stringConsulta, Criterios.class);
				Criterios critColor = queryColor.getResultList().get(0);

				return critColor;
			default:
				return null;
			}
		} else {
			return null;
		}

	}

	public static boolean booleanParse(String queryParams) {

		return queryParams.equals("True");
	}

	private static Usuario obtenerUsuario(Request request, Response response) {
		return repositorio.usuarios.get(0);
	}

	public static String antiParsearColor(Color color) {

		if(color.equals(Color.RED))
			return "Rojo";

		if(color.equals(Color.BLUE))
			return "Azul";

		if(color.equals(Color.GREEN))
			return "Verde";

		if(color.equals(Color.YELLOW))
			return "Amarillo";

		if(color.equals(Color.BLACK))
			return "Negro";

		if(color.equals(Color.WHITE))
			return "Blanco";

		return null;
	}

	public static String parsearTipo(Tipo tipo) {


		switch(tipo.getClass().toString()){
		case "class Tipos.Campera":
			return "Campera";
		case "class Tipos.Pantalon":
			return "Pantalon";
		case "class Tipos.Remera":
			return "Remera";
		case "class Tipos.Zapatillas":
			return "Zapatillas";

		default:
			return null;
		}


	}

	public static Object obtenerGuardarropasPrendaFallida(Usuario unUsuario,Prenda prendaFallida) {
		return unUsuario.guardarropas.stream().filter(guardarropas->guardarropas.prendas.contains(prendaFallida)).collect(Collectors.toList()).get(0);
	}

	public static ModelAndView listaPrendas(Request request, Response response) {

		List<Prenda> autos = null;
		if (request.queryParams("guardarropas") == null || request.queryParams("guardarropas").isEmpty()) {
			try {
				autos = repositorio.getUsuarios().get(0).getUnGuardarropas("MiPlacard").getPrendas();
			} catch (GuardarropasNoEncontradoException e) {
				response.redirect("/AlgoFallo");
			}

		} else {
			autos = repositorio.getUsuarios().get(0).guardarropas.stream().filter(guardarropas->guardarropas.nombre.equals(request.queryParams("guardarropas"))).collect(Collectors.toList()).get(0).getPrendas();
		}
		Map<String, Object> map = new HashMap<>();
		map.put("prendas", autos);
		return new ModelAndView(map, "guardarropas.html");
	}

	public static ModelAndView mostrarLogin(Request request, Response response) {

		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(map, "login.html");
	}

	public static Object validarLogin(Request request, Response response)  {

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT u FROM Usuarios u WHERE u.mail='";
		stringConsulta = stringConsulta.concat(request.queryParams("inputEmail"));
		stringConsulta = stringConsulta.concat("' and u.password='");
		stringConsulta = stringConsulta.concat(request.queryParams("inputPassword"));
		stringConsulta = stringConsulta.concat("'");

		/////////
		
		/*
		 * CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		 * 
		 * CriteriaQuery<Usuario> queryUsuario =
		 * criteriaBuilder.createQuery(Usuario.class);//Select proyeccion Root<Usuario>
		 * root = queryUsuario.from(Usuario.class);//from «tabla» Predicate equalMail =
		 * criteriaBuilder.equal(root.get("mail"), request.queryParams("inputEmail"));
		 * Predicate equalPassword = criteriaBuilder.equal(root.get("password"),
		 * request.queryParams("inputPassword")); Predicate and =
		 * criteriaBuilder.and(equalMail,equalPassword);
		 * queryUsuario.select(root).where(and);
		 * 
		 * 
		 * Usuario resultado =
		 * entityManager.createQuery(queryUsuario).getSingleResult();
		 */

		
		//////
		
		TypedQuery<Usuario> query = entityManager.createQuery(stringConsulta, Usuario.class);

		List<Usuario> results = query.getResultList();
		if (results.size() == 0) {
			response.redirect("/");
		} else {
			repositorio.sesionActual = request.session(true);
			repositorio.sesionActual.attribute("idUsuarioActual", results.get(0).getId().toString());
			
			response.redirect("/inicio");
			repositorio.idUsuarioActual = results.get(0).getId();
		}
		/*
		int matches;

		List<Usuario> usuariosConMail = repositorio.getUsuarios().stream().filter(unUsuario -> unUsuario.getMail().equals(request.queryParams("inputEmail"))).collect(Collectors.toList());
		matches=(int) usuariosConMail.stream().filter(unUsuario -> unUsuario.getPassword().equals(request.queryParams("inputPassword"))).count();

		if(matches == 1) {
			response.redirect("/inicio");
		} else {
			response.redirect("/");
			//mostrar cartelito de que estan mal mail y contraseña
		}
		 */
		return null;
	}

	public static Object cerrarSesion(Request request, Response response)  {
		repositorio.sesionActual.removeAttribute("idUsuarioActual");
		repositorio.sesionActual.invalidate();
		response.redirect("/");
		return null;
	}

	//Metodos para parsear cosas

	public static Color colorParser(String colorString) {
		if(colorString.equals("Rojo"))
			return Color.RED;

		if(colorString.equals("Azul"))
			return Color.BLUE;

		if(colorString.equals("Verde"))
			return Color.GREEN;

		if(colorString.equals("Amarillo"))
			return Color.YELLOW;

		if(colorString.equals("Negro"))
			return Color.BLACK;

		if(colorString.equals("Blanco"))
			return Color.WHITE;

		return null;
	}

	public static Tipo tipoParser(String tipo, String descripcion, int nivelDeAbrigo) {
		if(tipo.equals("Campera"))
			return new Campera(descripcion,nivelDeAbrigo);

		if(tipo.equals("Guantes"))
			return new Guantes(descripcion, nivelDeAbrigo);

		if(tipo.equals("Pantalon"))
			return new Pantalon(descripcion, nivelDeAbrigo);

		if(tipo.equals("Remera"))
			return new Remera(descripcion, nivelDeAbrigo);

		if(tipo.equals("Zapatillas"))
			return new Zapatillas(descripcion, nivelDeAbrigo);

		return null;
	}

	public static Categoria categoriaParser(String categoriaString) {

		String numParteDelCuerpo="";

		if(categoriaString.contentEquals("Torso"))
			numParteDelCuerpo = "1";

		if(categoriaString.contentEquals("Piernas"))
			numParteDelCuerpo = "2";

		if(categoriaString.contentEquals("Calzado"))
			numParteDelCuerpo = "3";

		if(categoriaString.contentEquals("Cabeza"))
			numParteDelCuerpo = "0";

		if(categoriaString.contentEquals("Manos"))
			numParteDelCuerpo = "4";

		EntityManager entityManager = EntityManagerSingleton.get();

		String stringConsulta ="SELECT c FROM Categorias c WHERE c.parteDelCuerpo='";
		stringConsulta = stringConsulta.concat(numParteDelCuerpo);
		stringConsulta = stringConsulta.concat("'");

		TypedQuery<Categoria> query = entityManager.createQuery(stringConsulta, Categoria.class);

		Categoria categoria = query.getResultList().get(0);

		return categoria;

		/*
		if(categoriaString.contentEquals("Torso"))
			return repositorio.getCategorias().get(0);

		if(categoriaString.contentEquals("Piernas"))
			return repositorio.getCategorias().get(1);

		if(categoriaString.contentEquals("Calzado"))
			return repositorio.getCategorias().get(2);

		if(categoriaString.contentEquals("Cabeza"))
			return repositorio.getCategorias().get(3);

		if(categoriaString.contentEquals("Manos"))
			return repositorio.getCategorias().get(4);

		return null;
		 */
	}

	public static String sugerenciaAsStringList(Sugerencia unaSugerencia) {
		List<String> prendasString = new ArrayList<String>();
		Atuendo unAtuendo = unaSugerencia.getAtuendo();
		unAtuendo.getSuperior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
		unAtuendo.getInferior().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
		unAtuendo.getCalzado().forEach(prenda->prendasString.add(prenda.tipo.descripcion));
		unAtuendo.getAccesorios().forEach(prenda->prendasString.add(prenda.tipo.descripcion));

		return prendasString.toString();
	}

}
