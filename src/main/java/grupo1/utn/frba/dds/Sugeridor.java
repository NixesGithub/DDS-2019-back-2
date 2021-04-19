package grupo1.utn.frba.dds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.*;

import com.google.common.collect.Sets;

import APIs.AdaptadorAW;
import APIs.AdaptadorOW;
import APIs.Adapter;

import CoordinadorDeServicion.ServicioClima;
import Criterios.*;
import Notificador.Notificador;
import Prendas.Prenda;
import net.aksingh.owmjapis.api.APIException;

public class Sugeridor {
	List<Criterios> criteriosBasicos = Arrays.asList(new CriterioVestimentaMinima(),new CriterioPrendaPorCapa(), new CriterioPrendaNoCompartida()); 
	
	
	
	private static Sugeridor sugeridor=null;
	
	public static Sugeridor getSugeridor() {
		
		if(sugeridor==null) {
			
			sugeridor= new Sugeridor();
			
		}
	return sugeridor;
		
	
		
	}
	
	private Sugeridor() {
		
		
	}
	
	public List<Atuendo> queMePongo(Guardarropas guardarropa, Usuario usuario,String ciudad, Date tiempo ) throws  SugerenciaInvalidaException, GuardarropasNoAlzanzaFiltrosBasicosException {
		
		
		guardarropa.hayPrendasSuficientes();
			
			
		//while(guardarropa.EstaOcupado) {} COMO VAS A HACER ESTO JORGE ME QUERES MATAR?!
		
		guardarropa.EstaOcupado=true;
		
		List<List<Prenda>> todosLosTorsos=permutarConSuperposicion(guardarropa.getPrendasTorso());
		List<List<Prenda>> todosLosInferiores=permutarConSuperposicion(guardarropa.getPrendasInferior());
		List<List<Prenda>> todosLosCalzados=permutarConSuperposicion(guardarropa.getPrendasCalzado());
		List<List<Prenda>> todosLosAccesorios=permutarConSuperposicion(guardarropa.getPrendasAccesorio());
		List<Atuendo> todasLasCombinaciones= permutarEntreTodos(todosLosTorsos,todosLosInferiores, todosLosCalzados,todosLosAccesorios);
		
		List<Atuendo>filtroBasico=filtrarPorCriterios(todasLasCombinaciones, criteriosBasicos, usuario);
		List<Atuendo>filtro= new ArrayList();
		List<Atuendo>retorno= new ArrayList();
		List <Criterios> filtrosNoBasicos=new ArrayList();
		filtrosNoBasicos.addAll(usuario.criterios);
		
		if(!usuario.getCriterios().isEmpty()) {
			if(tiempo.getHours()>7 &&tiempo.getHours()<18) {
			
				Criterios unCriterioDia=ServicioClima.getClima(ciudad).getClimaDia().aplicar();
				filtrosNoBasicos.add(unCriterioDia);
			
			}else {
			
				Criterios unCriterioNoche=ServicioClima.getClima(ciudad).getClimaNoche().aplicar();
				filtrosNoBasicos.add(unCriterioNoche);
			
			}
			List<Atuendo>atuendos=filtrarPorCriterios(filtroBasico, filtrosNoBasicos, usuario);
			filtro= atuendos;
		}
		
		if(filtro.isEmpty()) {
			
			if(filtroBasico.isEmpty()) {
				
				throw new GuardarropasNoAlzanzaFiltrosBasicosException("no supero ningun filtro basico las combinaciones posibles");
				
			}else {
				
				retorno=reEvaluarLista(filtroBasico,usuario.getCriterios(), usuario);
				
			}
			
			
			
		}else {
			
			retorno=filtro;
			
		}
		
		
		return retorno;
		
		
	
	}

	
	private List<Atuendo> reEvaluarLista(List<Atuendo> todasLasCombinaciones, List<Criterios> criterios2, Usuario usuario) {
		
		List<Atuendo> retorno= new ArrayList();
		int i=criterios2.size();
		do {
			i--;
			CriterioPonderacionNivelN criterio= new CriterioPonderacionNivelN(i);
			List<Criterios> criterios= new ArrayList();
			criterios.add(criterio);
			retorno=filtrarPorCriterios(todasLasCombinaciones,criterios, usuario); 
			
			
		}while(retorno.isEmpty());

		return retorno;
	}

	private List<Atuendo> filtrarPorCriterios(List<Atuendo> combinaciones, List<Criterios> criterios, Usuario usuario) {
		List<Atuendo> listaFiltrada = combinaciones;
		
		for (Criterios unCriterio : criterios) {
			listaFiltrada = listaFiltrada.stream().filter(unAtuendo -> {
				try {
					return aplicarCriterio(unAtuendo, unCriterio, usuario);
				} catch (IOException | APIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;//creo que habria que arreglar esto
			}).collect(Collectors.toList());
		}
		
		return listaFiltrada;
		 
	}

	private boolean aplicarCriterio(Atuendo unAtuendo, Criterios unCriterio, Usuario usuario) throws IOException, APIException {
		return unCriterio.aplicar(unAtuendo,usuario);
	}

	private List<Atuendo> permutarEntreTodos(List<List<Prenda>> torso, List<List<Prenda>> inferiores,
													List<List<Prenda>> calzado, List<List<Prenda>> accesorios) {
		List<Atuendo> resultado= new ArrayList();
		
	
		for(int i=0;i<torso.size();i++) {
			List<Prenda> superior=torso.get(i);
			
			for(int j=0;j<inferiores.size();j++) {
				List <Prenda>inferior=inferiores.get(j);
			
				for(int k=0;k<calzado.size();k++) {
					List <Prenda>calzados =calzado.get(k);
					if(accesorios.size()==0) {
					Atuendo unAtuendo=new Atuendo();
					superior.forEach(unaPrenda -> unAtuendo.prendas.add(unaPrenda));
					inferior.forEach(unaPrenda -> unAtuendo.prendas.add(unaPrenda));
					calzados.forEach(unaPrenda -> unAtuendo.prendas.add(unaPrenda));
					resultado.add(unAtuendo);
					}else {
					for(int l=0;l<accesorios.size();l++) {
						List <Prenda> accesorio=accesorios.get(l);
						Atuendo unAtuendo= new Atuendo();
						superior.forEach(unaPrenda -> unAtuendo.prendas.add(unaPrenda));
						inferior.forEach(unaPrenda -> unAtuendo.prendas.add(unaPrenda));
						calzados.forEach(unaPrenda -> unAtuendo.prendas.add(unaPrenda));
						accesorio.forEach(unaPrenda -> unAtuendo.prendas.add(unaPrenda));
						resultado.add(unAtuendo);
					}
				}
				}			
			}
			
		}
		return resultado;
	}

	private List<List<Prenda>> permutarConSuperposicion(List<Prenda> listaDePrendas) {
		
		List<Prenda>listaAux=new ArrayList();
		listaAux.addAll(listaDePrendas);
		Set<Prenda> setDePrendas = new HashSet<Prenda>(listaAux);
		Set<Set<Prenda>> resultado = new HashSet<Set<Prenda>>();
		
		for(int i=1; i <= listaDePrendas.size(); i++) {
			Set<Set<Prenda>> combinaciones = Sets.combinations(setDePrendas, i);
			resultado.addAll(combinaciones);
		}
		
		List<List<Prenda>> listaResultado = new ArrayList<List<Prenda>>();
		
		for (Set<Prenda> unSet : resultado) {
			List<Prenda> unaLista = new ArrayList<Prenda>(unSet);
			listaResultado.add(unaLista);
		}
		
		return listaResultado;
	}
	
}
