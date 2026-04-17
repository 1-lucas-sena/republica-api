package com.lucassena.republica_api.dto.request.perfil;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PerfilReivindicarHomenageadoRequestDto(

        @NotNull(message = "Número do quadrinho de homenageado é obrigatório.")
        @Positive(message = "Número do quadrinho de homenageado deve ser positivo.")
        Integer numeroQuadrinhoHomenageado
) {
}