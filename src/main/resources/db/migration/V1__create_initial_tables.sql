-- 1. Enums
CREATE TYPE role_sistema_enum AS ENUM ('ADMIN', 'MEMBRO_PLENO', 'MEMBRO_CALOURO', 'RESTRITO');
CREATE TYPE status_aprovacao_enum AS ENUM ('PENDENTE', 'APROVADO', 'REJEITADO');
CREATE TYPE tipo_perfil_enum AS ENUM ('VISITANTE', 'CALOURO', 'MORADOR', 'EX_ALUNO', 'HOMENAGEADO');

-- 2. Usuários
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role_sistema role_sistema_enum NOT NULL DEFAULT 'RESTRITO',
    status status_aprovacao_enum NOT NULL DEFAULT 'PENDENTE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Perfis
CREATE TABLE perfis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID UNIQUE,

    tipo_perfil tipo_perfil_enum NOT NULL,

    nome_completo VARCHAR(255) NOT NULL,
    apelido VARCHAR(100),
    curso VARCHAR(150),
    periodo_curso VARCHAR(50),
    semestre_entrada VARCHAR(10),

    ano_formatura INT,
    ano_homenagem INT,

    numero_quadrinho INT,
    numero_quadrinho_homenageado INT,

    url_foto TEXT,
    email_contato VARCHAR(255),
    telefone_celular VARCHAR(20),
    telefone_residencial VARCHAR(20),
    data_nascimento DATE,

    endereco TEXT,
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    cep VARCHAR(20),

    empresa_atual VARCHAR(150),
    anotacoes TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_perfis_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE SET NULL,

    CONSTRAINT ck_ex_aluno_quadrinho
        CHECK (
            tipo_perfil <> 'EX_ALUNO'
            OR numero_quadrinho IS NOT NULL
        ),

    CONSTRAINT ck_homenageado_quadrinho
        CHECK (
            tipo_perfil <> 'HOMENAGEADO'
            OR numero_quadrinho_homenageado IS NOT NULL
        ),

    CONSTRAINT ck_homenageado_ano
        CHECK (
            tipo_perfil <> 'HOMENAGEADO'
            OR ano_homenagem IS NOT NULL
        ),

    CONSTRAINT ck_exclusividade_quadrinho
        CHECK (
            NOT (
                numero_quadrinho IS NOT NULL
                AND numero_quadrinho_homenageado IS NOT NULL
            )
        )
);

-- 4. Índices únicos parciais
CREATE UNIQUE INDEX uk_perfis_numero_quadrinho_ex_aluno
    ON perfis (numero_quadrinho)
    WHERE tipo_perfil = 'EX_ALUNO' AND numero_quadrinho IS NOT NULL;

CREATE UNIQUE INDEX uk_perfis_numero_quadrinho_homenageado
    ON perfis (numero_quadrinho_homenageado)
    WHERE tipo_perfil = 'HOMENAGEADO' AND numero_quadrinho_homenageado IS NOT NULL;

-- 5. Índices úteis
CREATE INDEX idx_perfis_tipo_perfil ON perfis(tipo_perfil);
CREATE INDEX idx_perfis_nome_completo ON perfis(nome_completo);
CREATE INDEX idx_perfis_usuario_id ON perfis(usuario_id);