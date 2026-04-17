package com.lucassena.republica_api.service;

import com.lucassena.republica_api.domain.RoleSistema;
import com.lucassena.republica_api.domain.StatusAprovacao;
import com.lucassena.republica_api.domain.Usuario;
import com.lucassena.republica_api.exception.AcessoNegadoException;
import com.lucassena.republica_api.exception.RegraNegocioException;
import com.lucassena.republica_api.exception.RecursoNaoEncontradoException;
import com.lucassena.republica_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilService perfilService;

    @Transactional
    public Usuario criarUsuario(String email, String senhaHash) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = Usuario.builder()
                .email(email)
                .senhaHash(senhaHash)
                .build();

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com este e-mail."));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario atualizarUsuario(
            UUID usuarioAlvoId,
            UUID usuarioLogadoId,
            String novoEmail,
            String novaSenhaHash
    ) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        Usuario usuarioAlvo = buscarUsuarioExistente(usuarioAlvoId);

        validarPermissaoEdicao(usuarioLogado, usuarioAlvo);

        if (novoEmail != null && !novoEmail.isBlank() && !novoEmail.equalsIgnoreCase(usuarioAlvo.getEmail())) {
            if (usuarioRepository.existsByEmail(novoEmail)) {
                throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
            }
            usuarioAlvo.setEmail(novoEmail);
        }

        if (novaSenhaHash != null && !novaSenhaHash.isBlank()) {
            usuarioAlvo.setSenhaHash(novaSenhaHash);
        }

        return usuarioRepository.save(usuarioAlvo);
    }

    @Transactional
    public Usuario aprovarUsuario(UUID usuarioAlvoId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        validarAdmin(usuarioLogado);

        Usuario usuarioAlvo = buscarUsuarioExistente(usuarioAlvoId);
        usuarioAlvo.setStatus(StatusAprovacao.APROVADO);

        return usuarioRepository.save(usuarioAlvo);
    }

   @Transactional
    public Usuario rejeitarUsuario(UUID usuarioAlvoId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        validarAdmin(usuarioLogado);

        Usuario usuarioAlvo = buscarUsuarioExistente(usuarioAlvoId);
        usuarioAlvo.setStatus(StatusAprovacao.REJEITADO);

        perfilService.tratarPerfilAoRejeitarUsuario(usuarioAlvoId);

        return usuarioRepository.save(usuarioAlvo);
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