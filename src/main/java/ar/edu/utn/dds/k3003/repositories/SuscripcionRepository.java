package ar.edu.utn.dds.k3003.repositories;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ar.edu.utn.dds.k3003.model.Suscripcion;


public class SuscripcionRepository {
    private EntityManagerFactory _emf;

    public SuscripcionRepository() {
        _emf = Persistence.createEntityManagerFactory("dds");
    }

    public Suscripcion save(Suscripcion suscripcion) {
        EntityManager em = _emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(suscripcion);
        em.getTransaction().commit();
        em.close();
        return suscripcion;
    }

    public Suscripcion findById(Integer id) {
        EntityManager em = _emf.createEntityManager();
        Optional<Suscripcion> suscripcion = Optional.ofNullable(em.find(Suscripcion.class, id));
        em.close();
        return suscripcion.orElseThrow(() -> new NoSuchElementException(
            String.format("No hay una suscripcion de id: %s", id)
        ));
    }

    public List<Suscripcion> findByHeladeraId(Integer heladeraId) {
        EntityManager em = _emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Suscripcion> cq = cb.createQuery(Suscripcion.class);
        Root<Suscripcion> suscripcion = cq.from(Suscripcion.class);
        cq.select(suscripcion)
          .where(cb.equal(suscripcion.get("heladeraId"), heladeraId));
        List<Suscripcion> suscripciones = em.createQuery(cq).getResultList();
        em.close();
        return suscripciones;
    }

    public void deleteAll() {
        EntityManager em = _emf.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Suscripcion> delete = cb.createCriteriaDelete(Suscripcion.class);
        delete.from(Suscripcion.class);
        em.createQuery(delete).executeUpdate();
        // Reiniciar los IDs
        em.createNativeQuery("ALTER SEQUENCE suscripciones_id_seq RESTART WITH 1").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }
}
