package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Control_vacunas;
import org.example.util.JPAUtil;
import java.util.List;

public class ControlVacunaRepositoryImpl implements ControlVacunaRepository {

    @Override
    public void guardar(Control_vacunas cv) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(cv);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void actualizar(Control_vacunas cv) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(cv);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Control_vacunas buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        Control_vacunas cv = em.find(Control_vacunas.class, id);
        em.close();
        return cv;
    }

    @Override
    public List<Control_vacunas> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT cv FROM Control_vacunas cv " +
                "JOIN FETCH cv.mascota m " +
                "JOIN FETCH m.cliente " +
                "JOIN FETCH m.especie " +
                "JOIN FETCH cv.vacuna " +
                "ORDER BY m.nombre, cv.fechaAplicacion",
                Control_vacunas.class
            ).getResultList();
        } finally { em.close(); }
    }

    @Override
    public List<Control_vacunas> buscarPorCliente(Integer clienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT cv FROM Control_vacunas cv " +
                "JOIN FETCH cv.mascota m " +
                "JOIN FETCH m.cliente c " +
                "JOIN FETCH m.especie " +
                "JOIN FETCH cv.vacuna " +
                "WHERE c.id = :clienteId " +
                "ORDER BY m.nombre, cv.fechaAplicacion",
                Control_vacunas.class)
                .setParameter("clienteId", clienteId)
                .getResultList();
        } finally { em.close(); }
    }

    @Override
    public void eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Control_vacunas cv = em.find(Control_vacunas.class, id);
        if (cv != null) em.remove(cv);
        em.getTransaction().commit();
        em.close();
    }
}
