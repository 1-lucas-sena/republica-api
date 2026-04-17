package com.lucassena.republica_api.mapper;

import java.util.List;

import com.lucassena.republica_api.domain.Perfil;
import com.lucassena.republica_api.dto.response.perfil.PerfilPrivadoResponseDto;
import com.lucassena.republica_api.dto.response.perfil.PerfilPublicoResponseDto;

public final class PerfilMapper {

    private PerfilMapper() {
    }

    public static PerfilPublicoResponseDto toPublicoResponseDto(Perfil perfil) {
        if (perfil == null) {
            return null;
        }

        return new PerfilPublicoResponseDto(
                perfil.getId(),
                perfil.getTipoPerfil(),
                perfil.getNomeCompleto(),
                perfil.getApelido(),
                perfil.getCurso(),
                perfil.getPeriodoCurso(),
                perfil.getSemestreEntrada(),
                perfil.getAnoFormatura(),
                perfil.getAnoHomenagem(),
                perfil.getNumeroQuadrinho(),
                perfil.getNumeroQuadrinhoHomenageado(),
                perfil.getUrlFoto()
        );
    }

    public static PerfilPrivadoResponseDto toPrivadoResponseDto(Perfil perfil) {
        if (perfil == null) {
            return null;
        }

        return new PerfilPrivadoResponseDto(
                perfil.getId(),
                perfil.getTipoPerfil(),
                perfil.getOrigemPerfil(),
                perfil.getNomeCompleto(),
                perfil.getApelido(),
                perfil.getCurso(),
                perfil.getPeriodoCurso(),
                perfil.getSemestreEntrada(),
                perfil.getAnoFormatura(),
                perfil.getAnoHomenagem(),
                perfil.getNumeroQuadrinho(),
                perfil.getNumeroQuadrinhoHomenageado(),
                perfil.getUrlFoto(),
                perfil.getEmailContato(),
                perfil.getTelefoneCelular(),
                perfil.getTelefoneResidencial(),
                perfil.getDataNascimento(),
                perfil.getEndereco(),
                perfil.getBairro(),
                perfil.getCidade(),
                perfil.getEstado(),
                perfil.getCep(),
                perfil.getEmpresaAtual(),
                perfil.getAnotacoes(),
                perfil.getCreatedAt(),
                perfil.getUltimaAtualizacao()
        );
    }

    public static List<PerfilPublicoResponseDto> toPublicoResponseDtoList(List<Perfil> perfis) {

    return perfis.stream()

            .map(PerfilMapper::toPublicoResponseDto)

            .toList();

    }


    public static List<PerfilPrivadoResponseDto> toPrivadoResponseDtoList(List<Perfil> perfis) {

        return perfis.stream()

                .map(PerfilMapper::toPrivadoResponseDto)

                .toList();

    }
}