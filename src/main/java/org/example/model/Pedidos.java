package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pedidos {

    private Integer    id;
    private Cliente    cliente;
    private LocalDate  fechaPedido;
    private BigDecimal totalPedido;

    public Pedidos() {}

    public Integer    getId()              { return id; }
    public void       setId(Integer id)    { this.id = id; }

    public Cliente    getCliente()              { return cliente; }
    public void       setCliente(Cliente c)     { this.cliente = c; }

    public LocalDate  getFechaPedido()              { return fechaPedido; }
    public void       setFechaPedido(LocalDate f)   { this.fechaPedido = f; }

    public BigDecimal getTotalPedido()              { return totalPedido; }
    public void       setTotalPedido(BigDecimal t)  { this.totalPedido = t; }
}
