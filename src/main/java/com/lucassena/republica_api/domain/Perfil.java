package com.lucassena.republica_api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "perfis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relacionamento 1 para 1 com o Usuario
    // Fica nulo até o membro "reivindicar" esse perfil no aplicativo
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_perfil", nullable = false)
    private TipoPerfil tipoPerfil;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    private String apelido;
    private String curso;
    
    @Column(name = "periodo_curso")
    private String periodoCurso;
    
    @Column(name = "semestre_entrada")
    private String semestreEntrada;

    @Column(name = "ano_formatura")
    private Integer anoFormatura;
    
    @Column(name = "ano_homenagem")
    private Integer anoHomenagem;

    @Column(name = "numero_quadrinho")
    private Integer numeroQuadrinho;
    
    @Column(name = "numero_quadrinho_homenageado")
    private Integer numeroQuadrinhoHomenageado;

    @Column(name = "url_foto", columnDefinition = "TEXT")
    private String urlFoto;
    
    @Column(name = "email_contato")
    private String emailContato;

    @Column(name = "telefone_celular", length = 20)
    private String telefoneCelular;
    
    @Column(name = "telefone_residencial", length = 20)
    private String telefoneResidencial;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(columnDefinition = "TEXT")
    private String endereco;
    private String bairro;
    private String cidade;
    @Column(length = 2)
    private String estado;
    @Column(length = 20)
    private String cep;

    @Column(name = "empresa_atual")
    private String empresaAtual;
    
    @Column(columnDefinition = "TEXT")
    private String anotacoes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ultima_atualizacao", nullable = false)
    private LocalDateTime ultimaAtualizacao;
}