package com.farmacia.fatima.service;

import com.farmacia.fatima.model.dto.CarritoAddRequest;
import com.farmacia.fatima.model.dto.CarritoResponse;
import com.farmacia.fatima.model.dto.CarritoUpdateQtyRequest;
import com.farmacia.fatima.model.dto.*;
public interface CarritoService {
    CarritoResponse obtenerCarritoActual(Long usuarioId);
    CarritoResponse agregarProducto(Long usuarioId, CarritoAddRequest req);
    CarritoResponse actualizarCantidad(Long usuarioId, CarritoUpdateQtyRequest req);
    CarritoResponse quitarItem(Long usuarioId, Long itemId);
    void vaciar(Long usuarioId);
    Long checkout(Long usuarioId); 
}
