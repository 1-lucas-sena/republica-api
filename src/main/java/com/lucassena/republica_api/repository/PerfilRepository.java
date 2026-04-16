package com.lucassena.republica_api.repository;

import com.lucassena.republica_api.domain.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID> {

    Optional<Perfil> findByUsuarioId(UUID usuarioId);

    Optional<Perfil> findByNumeroQuadrinho(Integer numeroQuadrinho);
    Optional<Perfil> findByNumeroQuadrinhoHomenageado(Integer numeroQuadrinhoHomenageado);

    List<Perfil> findByNomeCompletoContainingIgnoreCase(String nome);
    List<Perfil> findByApelidoContainingIgnoreCase(String apelido);

    boolean existsByNumeroQuadrinho(Integer numeroQuadrinho);
    boolean existsByNumeroQuadrinhoHomenageado(Integer numeroQuadrinhoHomenageado);
}