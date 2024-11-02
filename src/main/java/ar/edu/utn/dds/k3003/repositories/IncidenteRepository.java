package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Incidente;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class IncidenteRepository {
     private EntityManagerFactory _emf;

    public IncidenteRepository() {
        _emf = Persistence.createEntityManagerFactory("dds");
    }
    
    public Incidente save(Incidente incidente) {
        EntityManager em = _emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(incidente);
        em.getTransaction().commit();
        em.close();
        return incidente;
    }

    public List<Incidente> findByHeladeraId(Integer heladeraId) {
        EntityManager em = _emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Incidente> cq = cb.createQuery(Incidente.class);
        Root<Incidente> incidente = cq.from(Incidente.class);
        cq.select(incidente)
          .where(cb.equal(incidente.get("heladeraId"), heladeraId));
        List<Incidente> incidentes = em.createQuery(cq).getResultList();
        em.close();
        return incidentes;
    }
} 