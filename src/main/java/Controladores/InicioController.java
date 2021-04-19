package Controladores;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class InicioController {

	public static ModelAndView mostrarInicio(Request request, Response response) {

		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(map, "inicio.html");
	}

	public static Object redireccionarAInicio(Request request, Response response) {

		response.redirect("/inicio");

		return null;	
	}

}