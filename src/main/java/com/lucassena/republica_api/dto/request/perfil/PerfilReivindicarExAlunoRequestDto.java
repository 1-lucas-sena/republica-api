package com.lucassena.republica_api.dto.request.perfil;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PerfilReivindicarExAlunoRequestDto(

        @NotNull(message = "Número do quadrinho é obrigatório.")
        @Positive(message = "Número do quadrinho deve ser positivo.")
        Integer numeroQuadrinho
) {
}