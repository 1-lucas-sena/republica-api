package com.lucassena.republica_api.dto.request.usuario;

import com.lucassena.republica_api.domain.StatusAprovacao;
import jakarta.validation.constraints.NotNull;

public record UsuarioStatusUpdateRequestDto(

        @NotNull(message = "Status é obrigatório.")
        StatusAprovacao status
) {
}