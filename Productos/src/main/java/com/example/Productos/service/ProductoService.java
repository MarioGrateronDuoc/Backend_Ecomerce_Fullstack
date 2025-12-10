package com.example.Productos.service;

import com.example.Productos.model.Producto;
import java.util.List;

public interface ProductoService {

    List<Producto> obtenerTodos();

    Producto obtenerPorId(Long id);

    Producto crear(Producto producto);

    Producto actualizar(Long id, Producto producto);

    void eliminar(Long id);
}
