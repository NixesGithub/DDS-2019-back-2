package Principal;

import java.awt.Color;
import java.io.IOException;

import APIs.AdaptadorAW;
import APIs.AdaptadorOW;
import ExceptionsPrendas.PrendaInvalidaException;
import ExceptionsPrendas.PrendaInvalidaPorMaterialException;
import ExceptionsPrendas.PrendaInvalidaPorTipoException;
import Prendas.Categoria;
import Prendas.ParteDelCuerpo;
import Prendas.Prenda;
import Tipos.Campera;
import Tipos.Tipo;
import Tipos.TipoDefault;
import net.aksingh.owmjapis.api.APIException;

public class Main {

	public static void main(String[] args) throws IOException, APIException, PrendaInvalidaPorTipoException, PrendaInvalidaPorMaterialException, PrendaInvalidaException {

		System.out.println(Campera.class.toString());
	}

}
