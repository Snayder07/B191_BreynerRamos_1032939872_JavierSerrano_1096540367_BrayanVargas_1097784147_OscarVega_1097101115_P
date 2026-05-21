package org.example.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;
import java.time.LocalDate;

@Entity
@Table(name = "CONTROL_VACUNAS")
public class Control_vacunas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascotas mascota;

    @ManyToOne
    @JoinColumn(name = "id_vacuna", nullable = false)
    private Vacunas vacuna;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;

    @Formula("fn_estado_vacuna(proxima_dosis)")
    private String estado;

    public Control_vacunas() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Mascotas getMascota() { return mascota; }
    public void setMascota(Mascotas mascota) { this.mascota = mascota; }

    public Vacunas getVacuna() { return vacuna; }
    public void setVacuna(Vacunas vacuna) { this.vacuna = vacuna; }

    public LocalDate getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDate fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }

    public LocalDate getProximaDosis() { return proximaDosis; }
    public void setProximaDosis(LocalDate proximaDosis) { this.proximaDosis = proximaDosis; }

    public String getEstado() {
        if (fechaAplicacion != null && fechaAplicacion.isAfter(java.time.LocalDate.now())) {
            return "Pendiente";
        }
        return estado != null ? estado : "Al día";
    }
}