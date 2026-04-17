package com.lucassena.republica_api.service;

import com.lucassena.republica_api.domain.RoleSistema;
import com.lucassena.republica_api.domain.StatusAprovacao;
import com.lucassena.republica_api.domain.Usuario;
import com.lucassena.republica_api.dto.request.usuario.UsuarioCadastroRequestDto;
import com.lucassena.republica_api.dto.request.usuario.UsuarioStatusUpdateRequestDto;
import com.lucassena.republica_api.dto.request.usuario.UsuarioUpdateRequestDto;
import com.lucassena.republica_api.dto.response.usuario.UsuarioResponseDto;
import com.lucassena.republica_api.exception.AcessoNegadoException;
import com.lucassena.republica_api.exception.RegraNegocioException;
import com.lucassena.republica_api.exception.RecursoNaoEncontradoException;
import com.lucassena.republica_api.mapper.UsuarioMapper;
import com.lucassena.republica_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilService perfilService;

    @Transactional
    public UsuarioResponseDto criarUsuario(UsuarioCadastroRequestDto request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = Usuario.builder()
                .email(request.email())
                // TODO: trocar por PasswordEncoder quando a autenticação for implementada
                .senhaHash(request.senha())
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDto(usuarioSalvo);
    }

    public UsuarioResponseDto buscarPorId(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        return UsuarioMapper.toResponseDto(usuario);
    }

    public UsuarioResponseDto buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com este e-mail."));

        return UsuarioMapper.toResponseDto(usuario);
    }

    public Page<UsuarioResponseDto> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(UsuarioMapper::toResponseDto);
    }

    @Transactional
    public UsuarioResponseDto atualizarUsuario(
            UUID usuarioAlvoId,
            UUID usuarioLogadoId,
            UsuarioUpdateRequestDto request
    ) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        Usuario usuarioAlvo = buscarUsuarioExistente(usuarioAlvoId);

        validarPermissaoEdicao(usuarioLogado, usuarioAlvo);

        if (request.email() != null
                && !request.email().isBlank()
                && !request.email().equalsIgnoreCase(usuarioAlvo.getEmail())) {

            if (usuarioRepository.existsByEmail(request.email())) {
                throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
            }

            usuarioAlvo.setEmail(request.email());
        }

        if (request.senha() != null && !request.senha().isBlank()) {
            // TODO: trocar por PasswordEncoder quando a autenticação for implementada
            usuarioAlvo.setSenhaHash(request.senha());
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioAlvo);
        return UsuarioMapper.toResponseDto(usuarioAtualizado);
    }

    @Transactional
    public UsuarioResponseDto atualizarStatusUsuario(
            UUID usuarioAlvoId,
            UUID usuarioLogadoId,
            UsuarioStatusUpdateRequestDto request
    ) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        validarAdmin(usuarioLogado);

        Usuario usuarioAlvo = buscarUsuarioExistente(usuarioAlvoId);

        if (request.status() == null) {
            throw new RegraNegocioException("Status é obrigatório.");
        }

        usuarioAlvo.setStatus(request.status());

        if (request.status() == StatusAprovacao.REJEITADO) {
            perfilService.tratarPerfilAoRejeitarUsuario(usuarioAlvoId);
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioAlvo);
        return UsuarioMapper.toResponseDto(usuarioAtualizado);
    }

    @Transactional
    public void excluirUsuario(UUID usuarioAlvoId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        Usuario usuarioAlvo = buscarUsuarioExistente(usuarioAlvoId);

        validarPermissaoEdicao(usuarioLogado, usuarioAlvo);

        usuarioRepository.delete(usuarioAlvo);
    }

    private Usuario buscarUsuarioExistente(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    private void validarPermissaoEdicao(Usuario usuarioLogado, Usuario usuarioAlvo) {
        boolean ehAdmin = usuarioLogado.getRoleSistema() == RoleSistema.ADMIN;
        boolean ehProprioUsuario = usuarioLogado.getId().equals(usuarioAlvo.getId());

        if (!ehAdmin && !ehProprioUsuario) {
            throw new AcessoNegadoException("Você não tem permissão para editar este usuário.");
        }
    }

    private void validarAdmin(Usuario usuarioLogado) {
        if (usuarioLogado.getRoleSistema() != RoleSistema.ADMIN) {
            throw new AcessoNegadoException("Apenas administradores podem executar esta ação.");
        }
    }
}