package com.novatech.inventario.service;

import com.novatech.inventario.exception.RecursoNoEncontradoException;
import com.novatech.inventario.model.Categoria;
import com.novatech.inventario.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada con id " + id));
    }

    public Categoria crear(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        Categoria categoria = obtenerPorId(id);
        categoriaRepository.delete(categoria);
    }
}
