package com.example.Productos.service;

import com.example.Productos.model.Producto;
import com.example.Productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;

    @Override
    public List<Producto> obtenerTodos() {
        return repository.findAll();
    }

    @Override
    public Producto obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Producto crear(Producto producto) {
        return repository.save(producto);
    }

    @Override
    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto producto = repository.findById(id).orElse(null);
        if (producto == null) return null;

        producto.setNombre(productoActualizado.getNombre());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setCategoria(productoActualizado.getCategoria());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setImagen(productoActualizado.getImagen());

        return repository.save(producto);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
