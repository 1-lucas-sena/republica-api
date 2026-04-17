package com.lucassena.republica_api.dto.request.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequestDto(

        @Email(message = "E-mail inválido.")
        String email,

        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
        String senha
) {
}