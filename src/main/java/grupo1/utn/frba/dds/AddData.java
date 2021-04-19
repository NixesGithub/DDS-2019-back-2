package grupo1.utn.frba.dds;

import java.awt.Color;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.management.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import Criterios.CriterioColorinche;
import Criterios.CriterioMonoCromatico;
import Criterios.CriterioSinSuperposicion;
import Criterios.Criterios;
import ExceptionsPrendas.PrendaYaExistenteException;
import Notificador.Notificador;
import Prendas.Categoria;
import Prendas.ParteDelCuerpo;
import Prendas.Prenda;
import Tipos.Campera;
import Tipos.Pantalon;
import Tipos.Remera;
import Tipos.Tipo;
import Tipos.Zapatillas;

public class AddData {

	public static void main(String[] args) throws PrendaYaExistenteException, GuardarropasNoEncontradoException, ParseException, FechaInvaldiaException, SugerenciaInvalidaException, SugerenciasNoGeneradasException, InterruptedException, GuardarropasNoAlzanzaFiltrosBasicosException {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("grupo1.utn.frba.dds.quemepongo");

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		TypedQuery<Usuario> query = entityManager.createQuery("SELECT u FROM Usuarios u", Usuario.class);

		List<Usuario> results = query.getResultList();
		if (results.size() == 0) {
			Usuario unUsuario = new Usuario("MiPlacard","Buenos Aires");
			unUsuario.setMail("pepeargento@gmail.com");
			unUsuario.setPassword("akd");

			//////////////////////Inicializacion de los colores //////////////////////
			Color rojo = Color.RED;
			Color azul = Color.BLUE;
			Color noColor = Color.DARK_GRAY;
			Color negro = Color.BLACK;

			//////////////////////Inicializacion de las prendas //////////////////////

			Categoria categoriaInferior = new Categoria(ParteDelCuerpo.Piernas);
			Categoria categoriaTorso = new Categoria(ParteDelCuerpo.Torso);
			Categoria categoriaCalzado = new Categoria(ParteDelCuerpo.Pies);
			Categoria categoriaCabeza = new Categoria(ParteDelCuerpo.Cabeza);
			Categoria categoriaManos = new Categoria(ParteDelCuerpo.Manos);

			//////////////////////Inicializacion de los tipos //////////////////////
			Tipo tipoRemera = new Remera("Remera roja",4);
			Tipo tipoLompa = new Pantalon("Pantalon azul",4);
			Tipo tipoZapas = new Zapatillas("Zapas negras",2);
			Tipo tipoRemeraRayada = new Remera("Remera rayada",4);
			Tipo tipoCampera = new Campera("Campera", 10);

			categoriaInferior.getPermitidos().add(tipoLompa);
			categoriaCalzado.getPermitidos().add(tipoZapas);
			categoriaTorso.getPermitidos().add(tipoRemera);
			categoriaTorso.getPermitidos().add(tipoRemeraRayada);
			categoriaTorso.getPermitidos().add(tipoCampera);
			tipoRemera.permitidos.add("Algodon");
			tipoRemera.permitidos.add("tela");
			tipoLompa.permitidos.add("Algodon");
			tipoLompa.permitidos.add("tela");
			tipoRemeraRayada.permitidos.add("Algodon");
			tipoRemeraRayada.permitidos.add("tela");
			tipoZapas.permitidos.add("Algodon");
			tipoZapas.permitidos.add("tela");
			tipoCampera.permitidos.add("Impermeable");
			tipoCampera.setCapa(2);
			tipoZapas.setCapa(1);
			tipoLompa.setCapa(1);
			tipoRemera.setCapa(1);
			tipoRemeraRayada.setCapa(1);

			Prenda unaPrenda0=new Prenda(azul, rojo, "Algodon", tipoRemera, categoriaTorso);
			Prenda unaPrenda1=new Prenda(azul, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
			Prenda unaPrenda2=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
			Prenda unaPrenda3=new Prenda(rojo, negro, "Algodon", tipoLompa, categoriaInferior);
			Prenda unaPrenda4=new Prenda(negro, azul, "Impermeable", tipoCampera, categoriaTorso);
			unUsuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda0);
			unUsuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda1);
			unUsuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda2);
			unUsuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda3);
			unUsuario.getUnGuardarropas("MiPlacard").addPrenda(unaPrenda4);

			List<Criterios>criterios= new ArrayList();
			criterios.add(new CriterioMonoCromatico());
			criterios.add(new CriterioColorinche());

			Criterios otroCriterio = new CriterioSinSuperposicion();

			unUsuario.planificarEvento("10-10-2020","Buenos Aires",unUsuario.getUnGuardarropas("MiPlacard"),criterios,0,false);
			unUsuario.planificarEvento("10-10-2020","Buenos Aires",unUsuario.getUnGuardarropas("MiPlacard"),criterios,0,false);
			PlanificacionEvento unaPlanificacionVieja=new PlanificacionEvento("10-10-2020","Buenos Aires",unUsuario.getUnGuardarropas("MiPlacard"),criterios,0,false,unUsuario);
			PlanificacionEvento otraPlanificacionVieja=new PlanificacionEvento("10-10-2020","Buenos Aires",unUsuario.getUnGuardarropas("MiPlacard"),criterios,0,false,unUsuario);
			unaPlanificacionVieja.setFecha(new Date(110, 10, 10)); //Por algun motivo Date se construye con año=primerParametro+1900
			otraPlanificacionVieja.setFecha(new Date(114, 8, 10));

			unUsuario.planificaciones.add(unaPlanificacionVieja);
			unUsuario.planificaciones.add(otraPlanificacionVieja);

			//Armando el evento finalizado Historico
			Evento eventoHistorico = new Evento(otraPlanificacionVieja);
			eventoHistorico.sugerenciasParaSalir = unUsuario.seleccionarSugerencias(Sugeridor.getSugeridor().queMePongo(unUsuario.getUnGuardarropas("MiPlacard"), unUsuario, unUsuario.getLugar(), new Date()), unUsuario.getUnGuardarropas("MiPlacard"));
			for(int i = 0; i < eventoHistorico.sugerenciasParaSalir.size() ; i++) {
				if(i == 0) {
					eventoHistorico.sugerenciasParaSalir.get(i).aceptar(); //Asi tengo alguna aceptada
					eventoHistorico.sugerenciasParaSalir.get(i).setCalificada(true); //Califico la aceptada
				} else {
					eventoHistorico.sugerenciasParaSalir.get(i).rechazar(); //Rechazo todo lo demas
				}
			}

			//Armando el evento activo
			Evento eventoActivo = new Evento(otraPlanificacionVieja);
			eventoActivo.fecha = new Date();
			eventoActivo.fecha.setHours(12);

			//Armando el evento finalizado para calificar
			Evento eventoParaCalificar = new Evento(otraPlanificacionVieja);
			eventoParaCalificar.fecha = new Date();
			eventoParaCalificar.sugerenciasParaSalir = unUsuario.seleccionarSugerencias(Sugeridor.getSugeridor().queMePongo(unUsuario.getUnGuardarropas("MiPlacard"), unUsuario, unUsuario.getLugar(), new Date()), unUsuario.getUnGuardarropas("MiPlacard"));
			for(int i = 0; i < eventoParaCalificar.sugerenciasParaSalir.size() ; i++) {
				if(i == 0) {
					eventoParaCalificar.sugerenciasParaSalir.get(i).aceptar(); //Asi tengo alguna aceptada
				} else {
					eventoParaCalificar.sugerenciasParaSalir.get(i).rechazar(); //Rechazo todo lo demas
				}
			}


			entityManager.getTransaction().begin();

			//Persisto los tipos creados para usar en Pepe
			entityManager.persist(tipoRemera);
			entityManager.persist(tipoLompa);
			entityManager.persist(tipoZapas);
			entityManager.persist(tipoRemeraRayada);
			entityManager.persist(tipoCampera);

			//Persisto las categorias
			entityManager.persist(categoriaInferior);
			entityManager.persist(categoriaTorso);
			entityManager.persist(categoriaCalzado);
			entityManager.persist(categoriaCabeza);
			entityManager.persist(categoriaManos);

			//Persisto las prendas
			entityManager.persist(unaPrenda0);
			entityManager.persist(unaPrenda1);
			entityManager.persist(unaPrenda2);
			entityManager.persist(unaPrenda3);
			entityManager.persist(unaPrenda4);

			//Persisto todas las cositas del Usuario
			//unUsuario.getGuardarropas().forEach(unGuarda -> entityManager.persist(unGuarda));
			//entityManager.persist(unUsuario.getFeedback());
			//List<PlanificacionEvento> planis = unUsuario.planificaciones;
			//planis.forEach(unaPlani -> unaPlani.getCriteriosAsociados().forEach(crit -> entityManager.persist(crit)));
			//planis.forEach(unaPlani -> entityManager.persist(unaPlani));

			//Persisto criterios(aparte de los que se persisten con los eventos)
			entityManager.persist(otroCriterio);

			//Persisto la notificacion
			/*
			Prenda unaPrenda5=new Prenda(negro, rojo, "Algodon", tipoRemera, categoriaTorso);
			Prenda unaPrenda6=new Prenda(azul, negro, "Algodon", tipoRemeraRayada, categoriaTorso);
			Prenda unaPrenda7=new Prenda(negro, noColor, "tela", tipoZapas, categoriaCalzado);
			Prenda unaPrenda8=new Prenda(negro, rojo, "Algodon", tipoLompa, categoriaInferior);
			Prenda unaPrenda9=new Prenda(negro, azul, "Impermeable", tipoCampera, categoriaTorso);

			entityManager.persist(unaPrenda5);
			entityManager.persist(unaPrenda6);
			entityManager.persist(unaPrenda7);
			entityManager.persist(unaPrenda8);
			entityManager.persist(unaPrenda9);
			 */
			Atuendo atuendo1=new Atuendo();
			atuendo1.prendas.add(unaPrenda2);
			atuendo1.prendas.add(unaPrenda3);
			atuendo1.prendas.add(unaPrenda1);
			Atuendo atuendo2=new Atuendo();
			atuendo1.prendas.add(unaPrenda2);
			atuendo1.prendas.add(unaPrenda3);
			atuendo1.prendas.add(unaPrenda0);
			List<Atuendo> atuendos=Arrays.asList(atuendo1,atuendo2);
			criterios.add(new CriterioMonoCromatico());
			criterios.add(new CriterioColorinche());
			PlanificacionEvento unaPlanificacion=unUsuario.planificaciones.get(0);
			unUsuario.OcurrioUnaAlerta(new Notificador(new Evento(unaPlanificacion),atuendos));

			//Persisto el Usuario
			entityManager.persist(unUsuario);

			//Persisto los eventos creados
			entityManager.persist(eventoHistorico);
			entityManager.persist(eventoActivo);
			entityManager.persist(eventoParaCalificar);
			
			entityManager.getTransaction().commit();

		} else {
			System.out.println("Ya se tienen datos");
		}


	}
}
