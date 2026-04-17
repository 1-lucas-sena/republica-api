package com.lucassena.republica_api.dto.request.perfil;

import com.lucassena.republica_api.domain.TipoPerfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record PerfilCreateRequestDto(

        UUID usuarioVinculadoId,

        @NotNull(message = "Tipo de perfil é obrigatório.")
        TipoPerfil tipoPerfil,

        @NotBlank(message = "Nome completo é obrigatório.")
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

        @Size(max = 255, message = "E-mail de contato deve ter no máximo 255 caracteres.")
        String emailContato,

        @Size(max = 20, message = "Telefone celular deve ter no máximo 20 caracteres.")
        String telefoneCelular,

        @Size(max = 20, message = "Telefone residencial deve ter no máximo 20 caracteres.")
        String telefoneResidencial,

        LocalDate dataNascimento,

        String endereco,

        String bairro,

        String cidade,

        @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ter 2 letras maiúsculas.")
        String estado,

        @Size(max = 20, message = "CEP deve ter no máximo 20 caracteres.")
        String cep,

        String empresaAtual,

        String anotacoes
) {
}