package com.lucassena.republica_api.repository;

import com.lucassena.republica_api.domain.Perfil;
import com.lucassena.republica_api.domain.TipoPerfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID> {

    Optional<Perfil> findByUsuarioId(UUID usuarioId);

    Optional<Perfil> findByTipoPerfilAndNumeroQuadrinho(TipoPerfil tipoPerfil, Integer numeroQuadrinho);

    Optional<Perfil> findByTipoPerfilAndNumeroQuadrinhoHomenageado(
            TipoPerfil tipoPerfil,
            Integer numeroQuadrinhoHomenageado
    );

    boolean existsByTipoPerfilAndNumeroQuadrinho(TipoPerfil tipoPerfil, Integer numeroQuadrinho);

    boolean existsByTipoPerfilAndNumeroQuadrinhoHomenageado(
            TipoPerfil tipoPerfil,
            Integer numeroQuadrinhoHomenageado
    );

    Page<Perfil> findByTipoPerfil(TipoPerfil tipoPerfil, Pageable pageable);

    Page<Perfil> findByNomeCompletoContainingIgnoreCase(String nome, Pageable pageable);

    Page<Perfil> findByApelidoContainingIgnoreCase(String apelido, Pageable pageable);
}