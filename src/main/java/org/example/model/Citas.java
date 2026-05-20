package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CITAS")
public class Citas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascotas mascota;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleados empleado;

    @Column(name = "fecha_cita", nullable = false)
    private LocalDate fechaCita;

    @Column(name = "hora_cita", nullable = false)
    private LocalTime horaCita;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cita", length = 50)
    private EstadoCita estadoCita;

    @Column(name = "direccion_domicilio", length = 200)
    private String direccionDomicilio;

    @Column(name = "motivo", length = 200)
    private String motivo;

    @OneToMany(mappedBy = "cita", fetch = FetchType.EAGER)
    private List<Cita_servicio> servicios = new ArrayList<>();

    public Citas() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Mascotas getMascota() { return mascota; }
    public void setMascota(Mascotas mascota) { this.mascota = mascota; }

    public Empleados getEmpleado() { return empleado; }
    public void setEmpleado(Empleados empleado) { this.empleado = empleado; }

    public LocalDate getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDate fechaCita) { this.fechaCita = fechaCita; }

    public LocalTime getHoraCita() { return horaCita; }
    public void setHoraCita(LocalTime horaCita) { this.horaCita = horaCita; }

    public EstadoCita getEstadoCita() { return estadoCita; }
    public void setEstadoCita(EstadoCita estadoCita) { this.estadoCita = estadoCita; }

    public String getDireccionDomicilio() { return direccionDomicilio; }
    public void setDireccionDomicilio(String d) { this.direccionDomicilio = d; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public List<Cita_servicio> getServicios() { return servicios; }
}