package com.farmacia.fatima.service;

import com.farmacia.fatima.model.Sucursal;
import com.farmacia.fatima.model.dto.SucursalRequest;
import com.farmacia.fatima.repository.ReservaRepository;
import com.farmacia.fatima.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SucursalService {

    private final SucursalRepository repository;
    private final ReservaRepository reservaRepository; // ✅ NUEVO

    public SucursalService(SucursalRepository repository, ReservaRepository reservaRepository) {
        this.repository = repository;
        this.reservaRepository = reservaRepository;
    }

    public List<Sucursal> listar() {
        return repository.findAll();
    }

    public Sucursal buscarPorId(Long id) throws Exception {
        return repository.findById(id)
                .orElseThrow(() -> new Exception("Sucursal no encontrada"));
    }

    public Sucursal crear(SucursalRequest req) {
        Sucursal s = Sucursal.builder()
                .nombre(req.getNombre())
                .distrito(req.getDistrito())
                .direccion(req.getDireccion())
                .telefono(req.getTelefono())
                .build();
        return repository.save(s);
    }

    public Sucursal actualizar(Long id, SucursalRequest req) throws Exception {
        Sucursal s = buscarPorId(id);
        s.setNombre(req.getNombre());
        s.setDistrito(req.getDistrito());
        s.setDireccion(req.getDireccion());
        s.setTelefono(req.getTelefono());
        return repository.save(s);
    }

    // ✅ Eliminación con validación de dependencias
    public void eliminar(Long id) throws Exception {
        Sucursal sucursal = buscarPorId(id);

        // 🔸 Verificar si existen reservas asociadas
        boolean tieneReservas = reservaRepository.existsBySucursalId(id);
        if (tieneReservas) {
            throw new Exception("No se puede eliminar la sucursal: tiene reservas asociadas.");
        }

        repository.delete(sucursal);
    }
}
