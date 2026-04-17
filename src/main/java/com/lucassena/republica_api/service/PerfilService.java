package com.lucassena.republica_api.service;

import com.lucassena.republica_api.domain.OrigemPerfil;
import com.lucassena.republica_api.domain.Perfil;
import com.lucassena.republica_api.domain.RoleSistema;
import com.lucassena.republica_api.domain.TipoPerfil;
import com.lucassena.republica_api.domain.Usuario;
import com.lucassena.republica_api.exception.AcessoNegadoException;
import com.lucassena.republica_api.exception.RegraNegocioException;
import com.lucassena.republica_api.exception.RecursoNaoEncontradoException;
import com.lucassena.republica_api.repository.PerfilRepository;
import com.lucassena.republica_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Perfil criarPerfil(
            UUID usuarioLogadoId,
            UUID usuarioVinculadoId,
            TipoPerfil tipoPerfil,
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
            String emailContato,
            String telefoneCelular,
            String telefoneResidencial,
            LocalDate dataNascimento,
            String endereco,
            String bairro,
            String cidade,
            String estado,
            String cep,
            String empresaAtual,
            String anotacoes
    ) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        validarPermissaoCriacaoPerfil(usuarioLogado, usuarioVinculadoId);

        validarCamposObrigatoriosPorTipo(
                nomeCompleto,
                tipoPerfil,
                anoFormatura,
                anoHomenagem,
                numeroQuadrinho,
                numeroQuadrinhoHomenageado
        );

        validarQuadrinhoExAluno(null, tipoPerfil, numeroQuadrinho);
        validarQuadrinhoHomenageado(null, tipoPerfil, numeroQuadrinhoHomenageado);

        Usuario usuarioVinculado = null;
        if (usuarioVinculadoId != null) {
            usuarioVinculado = buscarUsuarioExistente(usuarioVinculadoId);
            validarUsuarioSemPerfil(usuarioVinculadoId);
        }

        OrigemPerfil origemPerfil = definirOrigemPerfil(usuarioLogado, usuarioVinculadoId);

        Perfil perfil = Perfil.builder()
                .usuario(usuarioVinculado)
                .tipoPerfil(tipoPerfil)
                .origemPerfil(origemPerfil)
                .nomeCompleto(nomeCompleto)
                .apelido(apelido)
                .curso(curso)
                .periodoCurso(periodoCurso)
                .semestreEntrada(semestreEntrada)
                .anoFormatura(anoFormatura)
                .anoHomenagem(anoHomenagem)
                .numeroQuadrinho(numeroQuadrinho)
                .numeroQuadrinhoHomenageado(numeroQuadrinhoHomenageado)
                .urlFoto(urlFoto)
                .emailContato(emailContato)
                .telefoneCelular(telefoneCelular)
                .telefoneResidencial(telefoneResidencial)
                .dataNascimento(dataNascimento)
                .endereco(endereco)
                .bairro(bairro)
                .cidade(cidade)
                .estado(estado)
                .cep(cep)
                .empresaAtual(empresaAtual)
                .anotacoes(anotacoes)
                .build();

        return perfilRepository.save(perfil);
    }

    public Perfil buscarPorId(UUID perfilId) {
        return buscarPerfilExistente(perfilId);
    }

    public Perfil buscarPorUsuarioId(UUID usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado para este usuário."));
    }

    public List<Perfil> listarTodos() {
        return perfilRepository.findAll();
    }

    public List<Perfil> listarPorTipo(TipoPerfil tipoPerfil) {
        return perfilRepository.findByTipoPerfil(tipoPerfil);
    }

    @Transactional
    public Perfil atualizarPerfil(
            UUID perfilAlvoId,
            UUID usuarioLogadoId,
            TipoPerfil novoTipoPerfil,
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
            String emailContato,
            String telefoneCelular,
            String telefoneResidencial,
            LocalDate dataNascimento,
            String endereco,
            String bairro,
            String cidade,
            String estado,
            String cep,
            String empresaAtual,
            String anotacoes
    ) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        Perfil perfilAlvo = buscarPerfilExistente(perfilAlvoId);

        validarPermissaoEdicaoPerfil(usuarioLogado, perfilAlvo);

        TipoPerfil tipoFinal = novoTipoPerfil != null ? novoTipoPerfil : perfilAlvo.getTipoPerfil();
        String nomeCompletoFinal = nomeCompleto != null ? nomeCompleto : perfilAlvo.getNomeCompleto();

        Integer anoFormaturaFinal = tipoFinal == TipoPerfil.EX_ALUNO
                ? (anoFormatura != null ? anoFormatura : perfilAlvo.getAnoFormatura())
                : null;

        Integer numeroQuadrinhoFinal = tipoFinal == TipoPerfil.EX_ALUNO
                ? (numeroQuadrinho != null ? numeroQuadrinho : perfilAlvo.getNumeroQuadrinho())
                : null;

        Integer anoHomenagemFinal = tipoFinal == TipoPerfil.HOMENAGEADO
                ? (anoHomenagem != null ? anoHomenagem : perfilAlvo.getAnoHomenagem())
                : null;

        Integer numeroQuadrinhoHomenageadoFinal = tipoFinal == TipoPerfil.HOMENAGEADO
                ? (numeroQuadrinhoHomenageado != null
                    ? numeroQuadrinhoHomenageado
                    : perfilAlvo.getNumeroQuadrinhoHomenageado())
                : null;

        validarCamposObrigatoriosPorTipo(
                nomeCompletoFinal,
                tipoFinal,
                anoFormaturaFinal,
                anoHomenagemFinal,
                numeroQuadrinhoFinal,
                numeroQuadrinhoHomenageadoFinal
        );

        validarQuadrinhoExAluno(perfilAlvo.getId(), tipoFinal, numeroQuadrinhoFinal);
        validarQuadrinhoHomenageado(perfilAlvo.getId(), tipoFinal, numeroQuadrinhoHomenageadoFinal);

        perfilAlvo.setTipoPerfil(tipoFinal);
        perfilAlvo.setNomeCompleto(nomeCompletoFinal);
        perfilAlvo.setAnoFormatura(anoFormaturaFinal);
        perfilAlvo.setNumeroQuadrinho(numeroQuadrinhoFinal);
        perfilAlvo.setAnoHomenagem(anoHomenagemFinal);
        perfilAlvo.setNumeroQuadrinhoHomenageado(numeroQuadrinhoHomenageadoFinal);

        if (apelido != null) perfilAlvo.setApelido(apelido);
        if (curso != null) perfilAlvo.setCurso(curso);
        if (periodoCurso != null) perfilAlvo.setPeriodoCurso(periodoCurso);
        if (semestreEntrada != null) perfilAlvo.setSemestreEntrada(semestreEntrada);
        if (urlFoto != null) perfilAlvo.setUrlFoto(urlFoto);
        if (emailContato != null) perfilAlvo.setEmailContato(emailContato);
        if (telefoneCelular != null) perfilAlvo.setTelefoneCelular(telefoneCelular);
        if (telefoneResidencial != null) perfilAlvo.setTelefoneResidencial(telefoneResidencial);
        if (dataNascimento != null) perfilAlvo.setDataNascimento(dataNascimento);
        if (endereco != null) perfilAlvo.setEndereco(endereco);
        if (bairro != null) perfilAlvo.setBairro(bairro);
        if (cidade != null) perfilAlvo.setCidade(cidade);
        if (estado != null) perfilAlvo.setEstado(estado);
        if (cep != null) perfilAlvo.setCep(cep);
        if (empresaAtual != null) perfilAlvo.setEmpresaAtual(empresaAtual);
        if (anotacoes != null) perfilAlvo.setAnotacoes(anotacoes);

        return perfilRepository.save(perfilAlvo);
    }

    @Transactional
    public Perfil reivindicarPerfilExAluno(UUID usuarioLogadoId, Integer numeroQuadrinho) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        validarUsuarioSemPerfil(usuarioLogadoId);

        Perfil perfil = perfilRepository.findByTipoPerfilAndNumeroQuadrinho(
                        TipoPerfil.EX_ALUNO,
                        numeroQuadrinho
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Perfil de ex-aluno não encontrado para este quadrinho."
                ));

        validarPerfilDisponivelParaReivindicacao(perfil);

        perfil.setUsuario(usuarioLogado);
        return perfilRepository.save(perfil);
    }

    @Transactional
    public Perfil reivindicarPerfilHomenageado(UUID usuarioLogadoId, Integer numeroQuadrinhoHomenageado) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        validarUsuarioSemPerfil(usuarioLogadoId);

        Perfil perfil = perfilRepository.findByTipoPerfilAndNumeroQuadrinhoHomenageado(
                        TipoPerfil.HOMENAGEADO,
                        numeroQuadrinhoHomenageado
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Perfil de homenageado não encontrado para este quadrinho."
                ));

        validarPerfilDisponivelParaReivindicacao(perfil);

        perfil.setUsuario(usuarioLogado);
        return perfilRepository.save(perfil);
    }

    @Transactional
    public void excluirPerfil(UUID perfilAlvoId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        Perfil perfilAlvo = buscarPerfilExistente(perfilAlvoId);

        validarPermissaoExclusaoPerfil(usuarioLogado, perfilAlvo);

        perfilRepository.delete(perfilAlvo);
    }

    @Transactional
    public void tratarPerfilAoRejeitarUsuario(UUID usuarioId) {
        perfilRepository.findByUsuarioId(usuarioId).ifPresent(perfil -> {
            if (perfil.getOrigemPerfil() == OrigemPerfil.AUTO_CADASTRO) {
                perfilRepository.delete(perfil);
                return;
            }

            perfil.setUsuario(null);
            perfilRepository.save(perfil);
        });
    }

    private Perfil buscarPerfilExistente(UUID perfilId) {
        return perfilRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado."));
    }

    private Usuario buscarUsuarioExistente(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    private void validarPermissaoCriacaoPerfil(Usuario usuarioLogado, UUID usuarioVinculadoId) {
        if (usuarioLogado.getRoleSistema() == RoleSistema.ADMIN) {
            return;
        }

        Perfil perfilSolicitante = perfilRepository.findByUsuarioId(usuarioLogado.getId()).orElse(null);

        if (perfilSolicitante != null && perfilSolicitante.getTipoPerfil() == TipoPerfil.MORADOR) {
            return;
        }

        boolean ehCriacaoDoProprioPerfil =
                usuarioVinculadoId != null && usuarioLogado.getId().equals(usuarioVinculadoId);

        if (!ehCriacaoDoProprioPerfil) {
            throw new AcessoNegadoException("Você não tem permissão para criar este perfil.");
        }
    }

    private void validarPermissaoEdicaoPerfil(Usuario usuarioLogado, Perfil perfilAlvo) {
        if (usuarioLogado.getRoleSistema() == RoleSistema.ADMIN) {
            return;
        }

        Perfil perfilSolicitante = perfilRepository.findByUsuarioId(usuarioLogado.getId()).orElse(null);

        if (perfilSolicitante != null && perfilSolicitante.getTipoPerfil() == TipoPerfil.MORADOR) {
            return;
        }

        boolean ehProprioPerfil = perfilAlvo.getUsuario() != null
                && perfilAlvo.getUsuario().getId().equals(usuarioLogado.getId());

        if (!ehProprioPerfil) {
            throw new AcessoNegadoException("Você não tem permissão para editar este perfil.");
        }
    }

    private void validarPermissaoExclusaoPerfil(Usuario usuarioLogado, Perfil perfilAlvo) {
        if (usuarioLogado.getRoleSistema() == RoleSistema.ADMIN) {
            return;
        }

        Perfil perfilSolicitante = perfilRepository.findByUsuarioId(usuarioLogado.getId()).orElse(null);

        if (perfilSolicitante != null && perfilSolicitante.getTipoPerfil() == TipoPerfil.MORADOR) {
            return;
        }

        boolean ehProprioPerfil = perfilAlvo.getUsuario() != null
                && perfilAlvo.getUsuario().getId().equals(usuarioLogado.getId());

        if (!ehProprioPerfil) {
            throw new AcessoNegadoException("Você não tem permissão para excluir este perfil.");
        }
    }

    private void validarCamposObrigatoriosPorTipo(
            String nomeCompleto,
            TipoPerfil tipoPerfil,
            Integer anoFormatura,
            Integer anoHomenagem,
            Integer numeroQuadrinho,
            Integer numeroQuadrinhoHomenageado
    ) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            throw new RegraNegocioException("Nome completo é obrigatório.");
        }

        if (tipoPerfil == null) {
            throw new RegraNegocioException("Tipo de perfil é obrigatório.");
        }

        if (tipoPerfil == TipoPerfil.EX_ALUNO) {
            if (numeroQuadrinho == null) {
                throw new RegraNegocioException("Número do quadrinho é obrigatório para ex-aluno.");
            }
            if (anoFormatura == null) {
                throw new RegraNegocioException("Ano de formatura é obrigatório para ex-aluno.");
            }
            if (numeroQuadrinhoHomenageado != null || anoHomenagem != null) {
                throw new RegraNegocioException("Ex-aluno não pode possuir campos de homenageado.");
            }
        }

        if (tipoPerfil == TipoPerfil.HOMENAGEADO) {
            if (numeroQuadrinhoHomenageado == null) {
                throw new RegraNegocioException("Número do quadrinho de homenageado é obrigatório.");
            }
            if (anoHomenagem == null) {
                throw new RegraNegocioException("Ano da homenagem é obrigatório.");
            }
            if (numeroQuadrinho != null || anoFormatura != null) {
                throw new RegraNegocioException("Homenageado não pode possuir campos de ex-aluno.");
            }
        }

        if (tipoPerfil == TipoPerfil.CALOURO || tipoPerfil == TipoPerfil.MORADOR) {
            if (numeroQuadrinho != null
                    || numeroQuadrinhoHomenageado != null
                    || anoFormatura != null
                    || anoHomenagem != null) {
                throw new RegraNegocioException(
                        "Calouro e morador não podem possuir campos históricos de ex-aluno ou homenageado."
                );
            }
        }
    }

    private void validarQuadrinhoExAluno(UUID perfilAtualId, TipoPerfil tipoPerfil, Integer numeroQuadrinho) {
        if (tipoPerfil != TipoPerfil.EX_ALUNO || numeroQuadrinho == null) {
            return;
        }

        perfilRepository.findByTipoPerfilAndNumeroQuadrinho(TipoPerfil.EX_ALUNO, numeroQuadrinho)
                .ifPresent(perfilExistente -> {
                    boolean ehOutroPerfil =
                            perfilAtualId == null || !perfilExistente.getId().equals(perfilAtualId);

                    if (ehOutroPerfil) {
                        throw new RegraNegocioException(
                                "Já existe um ex-aluno com este número de quadrinho."
                        );
                    }
                });
    }

    private void validarQuadrinhoHomenageado(
            UUID perfilAtualId,
            TipoPerfil tipoPerfil,
            Integer numeroQuadrinhoHomenageado
    ) {
        if (tipoPerfil != TipoPerfil.HOMENAGEADO || numeroQuadrinhoHomenageado == null) {
            return;
        }

        perfilRepository.findByTipoPerfilAndNumeroQuadrinhoHomenageado(
                        TipoPerfil.HOMENAGEADO,
                        numeroQuadrinhoHomenageado
                )
                .ifPresent(perfilExistente -> {
                    boolean ehOutroPerfil =
                            perfilAtualId == null || !perfilExistente.getId().equals(perfilAtualId);

                    if (ehOutroPerfil) {
                        throw new RegraNegocioException(
                                "Já existe um homenageado com este número de quadrinho."
                        );
                    }
                });
    }

    private void validarPerfilDisponivelParaReivindicacao(Perfil perfil) {
        if (perfil.getUsuario() != null) {
            throw new RegraNegocioException("Este perfil já foi reivindicado por outro usuário.");
        }
    }

    private void validarUsuarioSemPerfil(UUID usuarioId) {
        if (perfilRepository.findByUsuarioId(usuarioId).isPresent()) {
            throw new RegraNegocioException("Este usuário já possui um perfil vinculado.");
        }
    }

    private boolean podeVerDadosSensiveis(UUID usuarioLogadoId, Perfil perfilAlvo) {
        if (usuarioLogadoId == null) {
            return false;
        }

        Usuario usuarioLogado = usuarioRepository.findById(usuarioLogadoId).orElse(null);
        if (usuarioLogado == null) {
            return false;
        }

        if (usuarioLogado.getRoleSistema() == RoleSistema.ADMIN) {
            return true;
        }

        Perfil perfilSolicitante = perfilRepository.findByUsuarioId(usuarioLogadoId).orElse(null);
        if (perfilSolicitante == null) {
            return false;
        }

        if (perfilSolicitante.getTipoPerfil() == TipoPerfil.EX_ALUNO
                || perfilSolicitante.getTipoPerfil() == TipoPerfil.HOMENAGEADO
                || perfilSolicitante.getTipoPerfil() == TipoPerfil.MORADOR) {
            return true;
        }

        return perfilSolicitante.getTipoPerfil() == TipoPerfil.CALOURO
                && perfilAlvo.getTipoPerfil() == TipoPerfil.CALOURO;
    }

    private OrigemPerfil definirOrigemPerfil(Usuario usuarioLogado, UUID usuarioVinculadoId) {
        if (usuarioLogado.getRoleSistema() == RoleSistema.ADMIN) {
            return OrigemPerfil.PRE_CADASTRADO;
        }

        Perfil perfilSolicitante = perfilRepository.findByUsuarioId(usuarioLogado.getId()).orElse(null);

        if (perfilSolicitante != null && perfilSolicitante.getTipoPerfil() == TipoPerfil.MORADOR) {
            return OrigemPerfil.PRE_CADASTRADO;
        }

        boolean ehCriacaoDoProprioPerfil =
                usuarioVinculadoId != null && usuarioLogado.getId().equals(usuarioVinculadoId);

        if (ehCriacaoDoProprioPerfil) {
            return OrigemPerfil.AUTO_CADASTRO;
        }

        throw new AcessoNegadoException("Não foi possível definir a origem do perfil.");
    }
}