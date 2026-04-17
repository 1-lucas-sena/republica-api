package com.lucassena.republica_api.dto.response.perfil;

import com.lucassena.republica_api.domain.TipoPerfil;

import java.util.UUID;

public record PerfilPublicoResponseDto(
        UUID id,
        TipoPerfil tipoPerfil,
        String nomeCompleto,
        String apelido,
        String curso,
        String periodoCurso,
        String semestreEntrada,
        Integer anoFormatura,
        Integer anoHomenagem,
        Integer numeroQuadrinho,
        Integer numeroQuadrinhoHomenageado,
        String urlFoto
) implements PerfilResponseDto {
}