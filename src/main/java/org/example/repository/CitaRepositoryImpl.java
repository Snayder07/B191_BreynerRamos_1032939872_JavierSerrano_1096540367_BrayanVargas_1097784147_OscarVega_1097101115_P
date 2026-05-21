package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Citas;
import org.example.util.JPAUtil;
import java.util.List;

public class CitaRepositoryImpl implements CitaRepository {

    @Override
    public void guardar(Citas cita) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(cita);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Citas buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        Citas cita = em.find(Citas.class, id);
        em.close();
        return cita;
    }

    @Override
    public List<Citas> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Citas> citas = em.createQuery("SELECT c FROM Citas c", Citas.class).getResultList();
        em.close();
        return citas;
    }

    @Override
    public List<Citas> buscarPorCliente(Integer clienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Citas> citas = em.createQuery(
                        "SELECT c FROM Citas c WHERE c.mascota.cliente.id = :clienteId ORDER BY c.fechaCita DESC, c.horaCita DESC", Citas.class)
                .setParameter("clienteId", clienteId)
                .getResultList();
        em.close();
        return citas;
    }

    @Override
    public List<Citas> buscarPasadasPorCliente(Integer clienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Citas> citas = em.createQuery(
                        "SELECT c FROM Citas c WHERE c.mascota.cliente.id = :clienteId AND c.fechaCita < CURRENT_DATE ORDER BY c.fechaCita DESC", Citas.class)
                .setParameter("clienteId", clienteId)
                .getResultList();
        em.close();
        return citas;
    }

    @Override
    public List<Citas> buscarDeHoy() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Citas> citas = em.createQuery(
                        "SELECT c FROM Citas c WHERE c.fechaCita = CURRENT_DATE ORDER BY c.horaCita ASC", Citas.class)
                .getResultList();
        em.close();
        return citas;
    }

    @Override
    public void eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Citas cita = em.find(Citas.class, id);
        if (cita != null) em.remove(cita);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Actualiza una cita existente (usado al mover bloques en el calendario).
     */
    public void actualizar(Citas cita) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(cita);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Citas> buscarCitasVacunas() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Citas> citas = em.createQuery(
                "SELECT c FROM Citas c " +
                "JOIN FETCH c.mascota m " +
                "JOIN FETCH m.cliente " +
                "JOIN FETCH c.empleado " +
                "WHERE c.estadoCita IN (:p, :conf) " +
                "AND LOWER(c.motivo) LIKE :motivo " +
                "ORDER BY c.fechaCita ASC, c.horaCita ASC", Citas.class)
                .setParameter("p",    org.example.model.EstadoCita.PENDIENTE)
                .setParameter("conf", org.example.model.EstadoCita.CONFIRMADA)
                .setParameter("motivo", "%vacun%")
                .getResultList();
        em.close();
        return citas;
    }

    /**
     * Cambia solo el estado de una cita sin tocar los demas campos.
     */
    public void actualizarEstado(Integer id, org.example.model.EstadoCita nuevoEstado) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Citas cita = em.find(Citas.class, id);
        if (cita != null) {
            cita.setEstadoCita(nuevoEstado);
            em.merge(cita);
        }
        em.getTransaction().commit();
        em.close();
    }
}
