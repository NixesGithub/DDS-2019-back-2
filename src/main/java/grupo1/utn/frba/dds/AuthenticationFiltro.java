package grupo1.utn.frba.dds;

import com.google.common.base.Strings;
import org.eclipse.jetty.http.HttpStatus;
import spark.Filter;

import java.util.Optional;

import static spark.Spark.halt;

public class AuthenticationFiltro {

	//  private static UsuarioService usuarioService = new UsuarioService();

	public static Filter authenticate = (request, response) -> {

		
		final Optional<String> maybeUser = Optional.ofNullable(request.session().attribute("idUsuarioActual"));
		final Optional<String> maybeXUser = Optional.ofNullable(request.headers("X-User"));
		
		if(!request.uri().equals("/")) { //Si es la barra no valido nada, es el login
			if (maybeUser.isPresent()) {

				// ya tenia un usuario en la session y si pasan X-User tiene que ser el mismo ya asignado

				maybeXUser.filter(xUser -> !xUser.equalsIgnoreCase(maybeUser.get()))
				.ifPresent(xUser -> halt(HttpStatus.UNAUTHORIZED_401));

			} else {

				// no tenia usuario en la session y tienen que pasarlo en X-User para asignarlo

				if (!maybeXUser.isPresent() || maybeXUser.filter(Strings::isNullOrEmpty).isPresent()) {
					halt(HttpStatus.UNAUTHORIZED_401);
				}
				/*
                final Optional<String> maybeUsuario = usuarioService.fetchUsuario(maybeXUser.get());

                if (maybeUsuario.isPresent()) {
                    request.session().attribute("user", maybeUsuario.get());
                } else {
                    halt(HttpStatus.UNAUTHORIZED_401);
                }
				 */
			}
		}
	};
}
