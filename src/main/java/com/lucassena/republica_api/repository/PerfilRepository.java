package com.lucassena.republica_api.repository;

import com.lucassena.republica_api.domain.Perfil;
import com.lucassena.republica_api.domain.TipoPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    List<Perfil> findByTipoPerfil(TipoPerfil tipoPerfil);

    List<Perfil> findByNomeCompletoContainingIgnoreCase(String nome);

    List<Perfil> findByApelidoContainingIgnoreCase(String apelido);

    boolean existsByTipoPerfilAndNumeroQuadrinho(TipoPerfil tipoPerfil, Integer numeroQuadrinho);

    boolean existsByTipoPerfilAndNumeroQuadrinhoHomenageado(
            TipoPerfil tipoPerfil,
            Integer numeroQuadrinhoHomenageado
    );
}