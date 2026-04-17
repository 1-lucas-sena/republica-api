package com.lucassena.republica_api.dto.response.perfil;

import com.lucassena.republica_api.domain.OrigemPerfil;
import com.lucassena.republica_api.domain.TipoPerfil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PerfilPrivadoResponseDto(
        UUID id,
        TipoPerfil tipoPerfil,
        OrigemPerfil origemPerfil,
        String nomeCompleto,
        String apelido,
        String curso,
        String periodoCurso,
        String semestreEntrada,
        Integer anoFormatura,
        Integer anoHomenagem,
        Integer numeroQuadrinho,
        Integer numeroQuadrinhoHomenageado,
        String urlFoto,
        String emailContato,
        String telefoneCelular,
        String telefoneResidencial,
        LocalDate dataNascimento,
        String endereco,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String empresaAtual,
        String anotacoes,
        LocalDateTime createdAt,
        LocalDateTime ultimaAtualizacao
) implements PerfilResponseDto {
}