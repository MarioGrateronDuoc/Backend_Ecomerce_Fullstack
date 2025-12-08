package com.example.Productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Productos.model.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(String categoria);
}
