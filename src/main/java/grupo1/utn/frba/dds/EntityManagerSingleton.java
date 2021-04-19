package grupo1.utn.frba.dds;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerSingleton {

    private static EntityManagerFactory sessionFactory;

    public synchronized static EntityManager get(){

        if(sessionFactory == null){
            sessionFactory = Persistence.createEntityManagerFactory("grupo1.utn.frba.dds.quemepongo");
        }

        return sessionFactory.createEntityManager();
    }
}
