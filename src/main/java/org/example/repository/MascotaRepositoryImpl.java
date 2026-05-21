package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Mascotas;
import org.example.util.JPAUtil;
import java.util.List;

public class MascotaRepositoryImpl implements MascotaRepository {

    @Override
    public void guardar(Mascotas mascota) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(mascota);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Mascotas buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        Mascotas mascota = em.find(Mascotas.class, id);
        em.close();
        return mascota;
    }

    @Override
    public List<Mascotas> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Mascotas> mascotas = em.createQuery("SELECT m FROM Mascotas m", Mascotas.class).getResultList();
        em.close();
        return mascotas;
    }

    public List<Mascotas> buscarPorCliente(Integer clienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Mascotas> mascotas = em.createQuery(
            "SELECT m FROM Mascotas m JOIN FETCH m.especie WHERE m.cliente.id = :clienteId ORDER BY m.nombre",
            Mascotas.class)
            .setParameter("clienteId", clienteId)
            .getResultList();
        em.close();
        return mascotas;
    }

    public void actualizar(Mascotas mascota) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(mascota);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Mascotas mascota = em.find(Mascotas.class, id);
        if (mascota != null) em.remove(mascota);
        em.getTransaction().commit();
        em.close();
    }
}