package com.farmacia.fatima.repository;

import com.farmacia.fatima.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);


    @Query("""
    select date(p.creadoEn) as fecha, sum(p.total) as total
    from Pedido p
    where p.creadoEn between :ini and :fin
    group by date(p.creadoEn)
    order by date(p.creadoEn)
    """)
    List<Object[]> ventasPorDia(@Param("ini") LocalDateTime ini,
                                @Param("fin") LocalDateTime fin);

    @Query("""
    select pr.nombre as producto, sum(i.cantidad) as cant
    from PedidoItem i
    join i.producto pr
    join i.pedido p
    where p.creadoEn between :ini and :fin
    group by pr.nombre
    order by sum(i.cantidad) desc
    """)
    List<Object[]> topProductos(@Param("ini") LocalDateTime ini,
                                @Param("fin") LocalDateTime fin);

    @Query("""
    select s.nombre as sucursal, sum(i.subtotal) as total
    from PedidoItem i
    join i.producto pr
    left join pr.sucursal s
    join i.pedido p
    where p.creadoEn between :ini and :fin
    group by s.nombre
    order by sum(i.subtotal) desc
    """)
    List<Object[]> ventasPorSucursal(@Param("ini") LocalDateTime ini,
                                     @Param("fin") LocalDateTime fin);

    @Query("""
    select coalesce(sum(p.total),0), count(p)
    from Pedido p
    where p.creadoEn between :ini and :fin
    """)
    List<Object[]> resumen(@Param("ini") LocalDateTime ini,
                           @Param("fin") LocalDateTime fin);

}