package com.lucassena.republica_api.mapper;

import java.util.List;

import com.lucassena.republica_api.domain.Usuario;
import com.lucassena.republica_api.dto.response.usuario.UsuarioResponseDto;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponseDto toResponseDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRoleSistema(),
                usuario.getStatus(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt()
        );
    }

    public static List<UsuarioResponseDto> toResponseDtoList(List<Usuario> usuarios) {

    return usuarios.stream()

            .map(UsuarioMapper::toResponseDto)

            .toList();

    }
}