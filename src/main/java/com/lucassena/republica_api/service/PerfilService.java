package com.lucassena.republica_api.service;

import com.lucassena.republica_api.domain.OrigemPerfil;
import com.lucassena.republica_api.domain.Perfil;
import com.lucassena.republica_api.domain.RoleSistema;
import com.lucassena.republica_api.domain.TipoPerfil;
import com.lucassena.republica_api.domain.Usuario;
import com.lucassena.republica_api.dto.request.perfil.PerfilCreateRequestDto;
import com.lucassena.republica_api.dto.request.perfil.PerfilReivindicarExAlunoRequestDto;
import com.lucassena.republica_api.dto.request.perfil.PerfilReivindicarHomenageadoRequestDto;
import com.lucassena.republica_api.dto.request.perfil.PerfilUpdateRequestDto;
import com.lucassena.republica_api.dto.response.perfil.PerfilPrivadoResponseDto;
import com.lucassena.republica_api.dto.response.perfil.PerfilResponseDto;
import com.lucassena.republica_api.exception.AcessoNegadoException;
import com.lucassena.republica_api.exception.RegraNegocioException;
import com.lucassena.republica_api.exception.RecursoNaoEncontradoException;
import com.lucassena.republica_api.mapper.PerfilMapper;
import com.lucassena.republica_api.repository.PerfilRepository;
import com.lucassena.republica_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PerfilPrivadoResponseDto criarPerfil(UUID usuarioLogadoId, PerfilCreateRequestDto request) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);

        UUID usuarioVinculadoIdEfetivo = resolverUsuarioVinculadoId(usuarioLogado, request.usuarioVinculadoId());

        validarPermissaoCriacaoPerfil(usuarioLogado, usuarioVinculadoIdEfetivo);

        validarCamposObrigatoriosPorTipo(
                request.nomeCompleto(),
                request.tipoPerfil(),
                request.anoFormatura(),
                request.anoHomenagem(),
                request.numeroQuadrinho(),
                request.numeroQuadrinhoHomenageado()
        );

        validarQuadrinhoExAluno(null, request.tipoPerfil(), request.numeroQuadrinho());
        validarQuadrinhoHomenageado(null, request.tipoPerfil(), request.numeroQuadrinhoHomenageado());

        Usuario usuarioVinculado = null;
        if (usuarioVinculadoIdEfetivo != null) {
            usuarioVinculado = buscarUsuarioExistente(usuarioVinculadoIdEfetivo);
            validarUsuarioSemPerfil(usuarioVinculadoIdEfetivo);
        }

        OrigemPerfil origemPerfil = definirOrigemPerfil(usuarioLogado);

        Perfil perfil = Perfil.builder()
                .usuario(usuarioVinculado)
                .tipoPerfil(request.tipoPerfil())
                .origemPerfil(origemPerfil)
                .nomeCompleto(request.nomeCompleto())
                .apelido(request.apelido())
                .curso(request.curso())
                .periodoCurso(request.periodoCurso())
                .semestreEntrada(request.semestreEntrada())
                .anoFormatura(request.anoFormatura())
                .anoHomenagem(request.anoHomenagem())
                .numeroQuadrinho(request.numeroQuadrinho())
                .numeroQuadrinhoHomenageado(request.numeroQuadrinhoHomenageado())
                .urlFoto(request.urlFoto())
                .emailContato(request.emailContato())
                .telefoneCelular(request.telefoneCelular())
                .telefoneResidencial(request.telefoneResidencial())
                .dataNascimento(request.dataNascimento())
                .endereco(request.endereco())
                .bairro(request.bairro())
                .cidade(request.cidade())
                .estado(request.estado())
                .cep(request.cep())
                .empresaAtual(request.empresaAtual())
                .anotacoes(request.anotacoes())
                .build();

        Perfil salvo = perfilRepository.save(perfil);
        return PerfilMapper.toPrivadoResponseDto(salvo);
    }

    public PerfilResponseDto buscarPorId(UUID perfilId, UUID usuarioLogadoId) {
        Perfil perfil = buscarPerfilExistente(perfilId);
        return mapearParaVisualizacao(usuarioLogadoId, perfil);
    }

    public PerfilResponseDto buscarPorUsuarioId(UUID usuarioAlvoId, UUID usuarioLogadoId) {
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioAlvoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado para este usuário."));
        return mapearParaVisualizacao(usuarioLogadoId, perfil);
    }

    public Page<PerfilResponseDto> listarTodos(UUID usuarioLogadoId, Pageable pageable) {
        return perfilRepository.findAll(pageable)
                .map(perfil -> mapearParaVisualizacao(usuarioLogadoId, perfil));
    }

    public Page<PerfilResponseDto> listarPorTipo(TipoPerfil tipoPerfil, UUID usuarioLogadoId, Pageable pageable) {
        return perfilRepository.findByTipoPerfil(tipoPerfil, pageable)
                .map(perfil -> mapearParaVisualizacao(usuarioLogadoId, perfil));
    }

    @Transactional
    public PerfilPrivadoResponseDto atualizarPerfil(
            UUID perfilId,
            UUID usuarioLogadoId,
            PerfilUpdateRequestDto request
    ) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        Perfil perfilAlvo = buscarPerfilExistente(perfilId);

        validarPermissaoEdicaoPerfil(usuarioLogado, perfilAlvo);

        TipoPerfil tipoFinal = request.tipoPerfil() != null
                ? request.tipoPerfil()
                : perfilAlvo.getTipoPerfil();

        String nomeCompletoFinal = request.nomeCompleto() != null
                ? request.nomeCompleto()
                : perfilAlvo.getNomeCompleto();

        Integer anoFormaturaFinal = tipoFinal == TipoPerfil.EX_ALUNO
                ? (request.anoFormatura() != null ? request.anoFormatura() : perfilAlvo.getAnoFormatura())
                : null;

        Integer numeroQuadrinhoFinal = tipoFinal == TipoPerfil.EX_ALUNO
                ? (request.numeroQuadrinho() != null ? request.numeroQuadrinho() : perfilAlvo.getNumeroQuadrinho())
                : null;

        Integer anoHomenagemFinal = tipoFinal == TipoPerfil.HOMENAGEADO
                ? (request.anoHomenagem() != null ? request.anoHomenagem() : perfilAlvo.getAnoHomenagem())
                : null;

        Integer numeroQuadrinhoHomenageadoFinal = tipoFinal == TipoPerfil.HOMENAGEADO
                ? (request.numeroQuadrinhoHomenageado() != null
                    ? request.numeroQuadrinhoHomenageado()
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

        if (request.apelido() != null) perfilAlvo.setApelido(request.apelido());
        if (request.curso() != null) perfilAlvo.setCurso(request.curso());
        if (request.periodoCurso() != null) perfilAlvo.setPeriodoCurso(request.periodoCurso());
        if (request.semestreEntrada() != null) perfilAlvo.setSemestreEntrada(request.semestreEntrada());
        if (request.urlFoto() != null) perfilAlvo.setUrlFoto(request.urlFoto());
        if (request.emailContato() != null) perfilAlvo.setEmailContato(request.emailContato());
        if (request.telefoneCelular() != null) perfilAlvo.setTelefoneCelular(request.telefoneCelular());
        if (request.telefoneResidencial() != null) perfilAlvo.setTelefoneResidencial(request.telefoneResidencial());
        if (request.dataNascimento() != null) perfilAlvo.setDataNascimento(request.dataNascimento());
        if (request.endereco() != null) perfilAlvo.setEndereco(request.endereco());
        if (request.bairro() != null) perfilAlvo.setBairro(request.bairro());
        if (request.cidade() != null) perfilAlvo.setCidade(request.cidade());
        if (request.estado() != null) perfilAlvo.setEstado(request.estado());
        if (request.cep() != null) perfilAlvo.setCep(request.cep());
        if (request.empresaAtual() != null) perfilAlvo.setEmpresaAtual(request.empresaAtual());
        if (request.anotacoes() != null) perfilAlvo.setAnotacoes(request.anotacoes());

        Perfil atualizado = perfilRepository.save(perfilAlvo);
        return PerfilMapper.toPrivadoResponseDto(atualizado);
    }

    @Transactional
    public PerfilPrivadoResponseDto reivindicarPerfilExAluno(
            UUID usuarioLogadoId,
            PerfilReivindicarExAlunoRequestDto request
    ) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        validarUsuarioSemPerfil(usuarioLogadoId);

        Perfil perfil = perfilRepository.findByTipoPerfilAndNumeroQuadrinho(
                        TipoPerfil.EX_ALUNO,
                        request.numeroQuadrinho()
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Perfil de ex-aluno não encontrado para este quadrinho."
                ));

        validarPerfilDisponivelParaReivindicacao(perfil);
        validarPerfilPreCadastrado(perfil);

        perfil.setUsuario(usuarioLogado);

        Perfil atualizado = perfilRepository.save(perfil);
        return PerfilMapper.toPrivadoResponseDto(atualizado);
    }

    @Transactional
    public PerfilPrivadoResponseDto reivindicarPerfilHomenageado(
            UUID usuarioLogadoId,
            PerfilReivindicarHomenageadoRequestDto request
    ) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        validarUsuarioSemPerfil(usuarioLogadoId);

        Perfil perfil = perfilRepository.findByTipoPerfilAndNumeroQuadrinhoHomenageado(
                        TipoPerfil.HOMENAGEADO,
                        request.numeroQuadrinhoHomenageado()
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Perfil de homenageado não encontrado para este quadrinho."
                ));

        validarPerfilDisponivelParaReivindicacao(perfil);
        validarPerfilPreCadastrado(perfil);

        perfil.setUsuario(usuarioLogado);

        Perfil atualizado = perfilRepository.save(perfil);
        return PerfilMapper.toPrivadoResponseDto(atualizado);
    }

    @Transactional
    public void excluirPerfil(UUID perfilId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = buscarUsuarioExistente(usuarioLogadoId);
        Perfil perfilAlvo = buscarPerfilExistente(perfilId);

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

    private PerfilResponseDto mapearParaVisualizacao(UUID usuarioLogadoId, Perfil perfil) {
        if (podeVerDadosSensiveis(usuarioLogadoId, perfil)) {
            return PerfilMapper.toPrivadoResponseDto(perfil);
        }
        return PerfilMapper.toPublicoResponseDto(perfil);
    }

    private void validarPermissaoCriacaoPerfil(Usuario usuarioLogado, UUID usuarioVinculadoIdEfetivo) {
        if (usuarioLogado.getRoleSistema() == RoleSistema.ADMIN) {
            return;
        }

        Perfil perfilSolicitante = perfilRepository.findByUsuarioId(usuarioLogado.getId()).orElse(null);

        if (perfilSolicitante != null && perfilSolicitante.getTipoPerfil() == TipoPerfil.MORADOR) {
            return;
        }

        boolean ehCriacaoDoProprioPerfil = usuarioLogado.getId().equals(usuarioVinculadoIdEfetivo);

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
                    boolean ehOutroPerfil = perfilAtualId == null || !perfilExistente.getId().equals(perfilAtualId);

                    if (ehOutroPerfil) {
                        throw new RegraNegocioException("Já existe um ex-aluno com este número de quadrinho.");
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
                    boolean ehOutroPerfil = perfilAtualId == null || !perfilExistente.getId().equals(perfilAtualId);

                    if (ehOutroPerfil) {
                        throw new RegraNegocioException("Já existe um homenageado com este número de quadrinho.");
                    }
                });
    }

    private void validarPerfilDisponivelParaReivindicacao(Perfil perfil) {
        if (perfil.getUsuario() != null) {
            throw new RegraNegocioException("Este perfil já foi reivindicado por outro usuário.");
        }
    }

    private void validarPerfilPreCadastrado(Perfil perfil) {
        if (perfil.getOrigemPerfil() != OrigemPerfil.PRE_CADASTRADO) {
            throw new RegraNegocioException("Apenas perfis pré-cadastrados podem ser reivindicados.");
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

    private OrigemPerfil definirOrigemPerfil(Usuario usuarioLogado) {
        if (usuarioLogado.getRoleSistema() == RoleSistema.ADMIN) {
            return OrigemPerfil.PRE_CADASTRADO;
        }

        Perfil perfilSolicitante = perfilRepository.findByUsuarioId(usuarioLogado.getId()).orElse(null);

        if (perfilSolicitante != null && perfilSolicitante.getTipoPerfil() == TipoPerfil.MORADOR) {
            return OrigemPerfil.PRE_CADASTRADO;
        }

        return OrigemPerfil.AUTO_CADASTRO;
    }

    private UUID resolverUsuarioVinculadoId(Usuario usuarioLogado, UUID usuarioVinculadoIdInformado) {
        boolean ehAdmin = usuarioLogado.getRoleSistema() == RoleSistema.ADMIN;

        Perfil perfilSolicitante = perfilRepository.findByUsuarioId(usuarioLogado.getId()).orElse(null);
        boolean ehMorador = perfilSolicitante != null && perfilSolicitante.getTipoPerfil() == TipoPerfil.MORADOR;

        if (ehAdmin || ehMorador) {
            return usuarioVinculadoIdInformado;
        }

        return usuarioLogado.getId();
    }
}