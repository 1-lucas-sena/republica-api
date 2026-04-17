package com.lucassena.republica_api.dto.response.usuario;

import com.lucassena.republica_api.domain.RoleSistema;
import com.lucassena.republica_api.domain.StatusAprovacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponseDto(
        UUID id,
        String email,
        RoleSistema roleSistema,
        StatusAprovacao status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}