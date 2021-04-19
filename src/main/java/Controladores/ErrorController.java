package Controladores;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class ErrorController {

	public static ModelAndView PaginaDeError(Request request,Response response) {
		Map<String,Object> map=new HashMap<>();
		return new ModelAndView(map,"AlgoFallo.html");
	}

}
