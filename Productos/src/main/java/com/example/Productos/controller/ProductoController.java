package com.example.Productos.controller;

import com.example.Productos.model.Producto;
import com.example.Productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Operaciones CRUD para la gestión de productos")
public class ProductoController {

    private final ProductoService productoService;


    @GetMapping  // Enlista todos los productos
    @Operation(summary = "Obtener todos los productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }


    @GetMapping("/{id}") // Obtener producto por ID
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerPorId(id);

        if (producto == null) {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }

        return ResponseEntity.ok(producto);
    }


    @GetMapping("/categoria/{categoria}") // Obtener productos por categoría
    @Operation(summary = "Obtener productos por categoría")
    public ResponseEntity<List<Producto>> productosPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(
                productoService.obtenerTodos()
                        .stream()
                        .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                        .toList()
        );
    }


    @PostMapping // Crear nuevo producto
    @Operation(summary = "Crear un nuevo producto")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.crear(producto));
    }


    @PutMapping("/{id}") // Actualizar producto existente
    @Operation(summary = "Actualizar un producto existente")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto producto
    ) {
        Producto actualizado = productoService.actualizar(id, producto);

        if (actualizado == null) {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }

        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}") // Eliminar producto por ID
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {

        Producto existe = productoService.obtenerPorId(id);
        if (existe == null) {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }

        productoService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }
}
