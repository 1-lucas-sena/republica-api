-- Cria o novo Enum no PostgreSQL
CREATE TYPE origem_perfil_enum AS ENUM ('PRE_CADASTRADO', 'AUTO_CADASTRO');

-- Adiciona a coluna na tabela de perfis
ALTER TABLE perfis
ADD COLUMN origem_perfil origem_perfil_enum NOT NULL DEFAULT 'AUTO_CADASTRO';