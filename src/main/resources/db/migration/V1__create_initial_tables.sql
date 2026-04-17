-- 1. Enums
CREATE TYPE role_sistema_enum AS ENUM ('ADMIN', 'USUARIO');
CREATE TYPE status_aprovacao_enum AS ENUM ('PENDENTE', 'APROVADO', 'REJEITADO');
CREATE TYPE tipo_perfil_enum AS ENUM ('CALOURO', 'MORADOR', 'EX_ALUNO', 'HOMENAGEADO');

-- 2. Usuários
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role_sistema role_sistema_enum NOT NULL DEFAULT 'USUARIO',
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

    -- EX_ALUNO:
    -- deve ter numero_quadrinho
    -- deve ter ano_formatura
    -- não pode ter quadrinho de homenageado
    -- não pode ter ano_homenagem
    CONSTRAINT ck_perfis_ex_aluno_campos
        CHECK (
            tipo_perfil <> 'EX_ALUNO'
            OR (
                numero_quadrinho IS NOT NULL
                AND ano_formatura IS NOT NULL
                AND numero_quadrinho_homenageado IS NULL
                AND ano_homenagem IS NULL
            )
        ),

    -- HOMENAGEADO:
    -- deve ter numero_quadrinho_homenageado
    -- deve ter ano_homenagem
    -- não pode ter quadrinho de ex-aluno
    -- não pode ter ano_formatura
    CONSTRAINT ck_perfis_homenageado_campos
        CHECK (
            tipo_perfil <> 'HOMENAGEADO'
            OR (
                numero_quadrinho_homenageado IS NOT NULL
                AND ano_homenagem IS NOT NULL
                AND numero_quadrinho IS NULL
                AND ano_formatura IS NULL
            )
        ),

    -- CALOURO e MORADOR:
    -- não podem ter campos de quadrinho/anos históricos
    CONSTRAINT ck_perfis_outros_sem_campos_historicos
        CHECK (
            tipo_perfil IN ('EX_ALUNO', 'HOMENAGEADO')
            OR (
                numero_quadrinho IS NULL
                AND numero_quadrinho_homenageado IS NULL
                AND ano_formatura IS NULL
                AND ano_homenagem IS NULL
            )
        )
);

-- 4. Índices únicos parciais
CREATE UNIQUE INDEX uk_perfis_numero_quadrinho_ex_aluno
    ON perfis (numero_quadrinho)
    WHERE tipo_perfil = 'EX_ALUNO'
      AND numero_quadrinho IS NOT NULL;

CREATE UNIQUE INDEX uk_perfis_numero_quadrinho_homenageado
    ON perfis (numero_quadrinho_homenageado)
    WHERE tipo_perfil = 'HOMENAGEADO'
      AND numero_quadrinho_homenageado IS NOT NULL;

-- 5. Índices úteis
CREATE INDEX idx_perfis_tipo_perfil ON perfis(tipo_perfil);
CREATE INDEX idx_perfis_nome_completo ON perfis(nome_completo);
CREATE INDEX idx_perfis_apelido ON perfis(apelido);
CREATE INDEX idx_perfis_usuario_id ON perfis(usuario_id);