package org.example.model;

import java.math.BigDecimal;

public class Detalle_pedido {

    private Integer    id;
    private Pedidos    pedido;
    private Productos  producto;
    private Integer    cantidad;
    private BigDecimal precioUnitario;

    public Detalle_pedido() {}

    public Integer    getId()              { return id; }
    public void       setId(Integer id)    { this.id = id; }

    public Pedidos    getPedido()              { return pedido; }
    public void       setPedido(Pedidos p)     { this.pedido = p; }

    public Productos  getProducto()            { return producto; }
    public void       setProducto(Productos p) { this.producto = p; }

    public Integer    getCantidad()            { return cantidad; }
    public void       setCantidad(Integer c)   { this.cantidad = c; }

    public BigDecimal getPrecioUnitario()              { return precioUnitario; }
    public void       setPrecioUnitario(BigDecimal p)  { this.precioUnitario = p; }
}
