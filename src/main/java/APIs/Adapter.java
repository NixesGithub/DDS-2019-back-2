package APIs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import net.aksingh.owmjapis.api.APIException;

public interface Adapter {
	
	
	public 	ClimaXCiudad pedirClima(String ciudad) throws IOException, APIException;

	
	
}
