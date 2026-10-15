SET TIME ZONE 'America/Sao_Paulo';

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE aeroporto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    vagas_estacionamento INTEGER NOT NULL CHECK (vagas_estacionamento > 0),
    CONSTRAINT aeroporto_codigo_nao_vazio_ck CHECK (btrim(codigo) <> ''),
    CONSTRAINT aeroporto_nome_nao_vazio_ck CHECK (btrim(nome) <> '')
);

CREATE TABLE piloto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(64) NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    CONSTRAINT piloto_codigo_nao_vazio_ck CHECK (btrim(codigo) <> ''),
    CONSTRAINT piloto_nome_nao_vazio_ck CHECK (btrim(nome) <> '')
);

CREATE TABLE aeronave (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prefixo VARCHAR(32) NOT NULL UNIQUE,
    velocidade_cruzeiro_kmh NUMERIC(12, 3) NOT NULL CHECK (velocidade_cruzeiro_kmh > 0),
    autonomia_nominal_km NUMERIC(12, 3) NOT NULL CHECK (autonomia_nominal_km > 0),
    categoria VARCHAR(32) NOT NULL CHECK (categoria IN ('PARTICULAR', 'COMERCIAL_PASSAGEIROS', 'COMERCIAL_CARGA')),
    empresa_manutencao VARCHAR(200),
    companhia_aerea VARCHAR(200),
    capacidade_maxima_passageiros INTEGER,
    capacidade_maxima_carga_toneladas NUMERIC(12, 3),
    CONSTRAINT aeronave_prefixo_nao_vazio_ck CHECK (btrim(prefixo) <> ''),
    CONSTRAINT aeronave_categoria_dados_ck CHECK (
        (categoria = 'PARTICULAR' AND empresa_manutencao IS NOT NULL AND btrim(empresa_manutencao) <> '' AND companhia_aerea IS NULL
            AND capacidade_maxima_passageiros IS NULL AND capacidade_maxima_carga_toneladas IS NULL)
        OR (categoria = 'COMERCIAL_PASSAGEIROS' AND companhia_aerea IS NOT NULL AND btrim(companhia_aerea) <> ''
            AND capacidade_maxima_passageiros > 0 AND empresa_manutencao IS NULL AND capacidade_maxima_carga_toneladas IS NULL)
        OR (categoria = 'COMERCIAL_CARGA' AND companhia_aerea IS NOT NULL AND btrim(companhia_aerea) <> ''
            AND capacidade_maxima_carga_toneladas > 0 AND empresa_manutencao IS NULL AND capacidade_maxima_passageiros IS NULL)
    )
);

CREATE TABLE aerovia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    identificador VARCHAR(32) NOT NULL UNIQUE,
    aeroporto_origem_id UUID NOT NULL REFERENCES aeroporto(id),
    aeroporto_destino_id UUID NOT NULL REFERENCES aeroporto(id),
    distancia_quilometros NUMERIC(12, 3) NOT NULL CHECK (distancia_quilometros > 0),
    CONSTRAINT aerovia_identificador_nao_vazio_ck CHECK (btrim(identificador) <> ''),
    CONSTRAINT aerovia_origem_destino_distintos_ck CHECK (aeroporto_origem_id <> aeroporto_destino_id)
);

CREATE TABLE plano_voo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    identificador VARCHAR(64) NOT NULL UNIQUE,
    piloto_id UUID NOT NULL REFERENCES piloto(id),
    aeronave_id UUID NOT NULL REFERENCES aeronave(id),
    aeroporto_origem_id UUID NOT NULL REFERENCES aeroporto(id),
    aeroporto_destino_id UUID NOT NULL REFERENCES aeroporto(id),
    partida_prevista TIMESTAMP NOT NULL,
    situacao VARCHAR(16) NOT NULL CHECK (situacao IN ('EM_EDICAO', 'APROVADO', 'CANCELADO', 'ABANDONADO')),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT plano_voo_identificador_nao_vazio_ck CHECK (btrim(identificador) <> '')
);

CREATE TABLE trecho (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plano_voo_id UUID NOT NULL REFERENCES plano_voo(id),
    ordem INTEGER NOT NULL CHECK (ordem > 0),
    aerovia_id UUID NOT NULL REFERENCES aerovia(id),
    altitude_pes INTEGER NOT NULL CHECK (altitude_pes BETWEEN 25000 AND 35000 AND MOD(altitude_pes, 1000) = 0),
    entrada TIMESTAMP NOT NULL,
    chegada TIMESTAMP,
    operacao_chegada VARCHAR(10) NOT NULL CHECK (operacao_chegada IN ('SOBREVOO', 'POUSO')),
    permanencia_minutos INTEGER NOT NULL DEFAULT 0 CHECK (permanencia_minutos >= 0),
    vaga INTEGER,
    CONSTRAINT trecho_plano_ordem_uk UNIQUE (plano_voo_id, ordem),
    CONSTRAINT trecho_pouso_vaga_ck CHECK ((operacao_chegada = 'POUSO' AND vaga IS NOT NULL AND permanencia_minutos >= 0) OR (operacao_chegada = 'SOBREVOO' AND vaga IS NULL AND permanencia_minutos = 0))
);

CREATE TABLE reserva_aerovia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plano_voo_id UUID NOT NULL REFERENCES plano_voo(id),
    aerovia_id UUID NOT NULL REFERENCES aerovia(id),
    altitude_pes INTEGER NOT NULL CHECK (altitude_pes BETWEEN 25000 AND 35000 AND MOD(altitude_pes, 1000) = 0),
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    ocupacao TSRANGE GENERATED ALWAYS AS (
        tsrange(date_trunc('hour', inicio), date_trunc('hour', fim) +
            CASE WHEN fim = date_trunc('hour', fim) THEN INTERVAL '0 hour' ELSE INTERVAL '1 hour' END, '[)')
    ) STORED,
    CONSTRAINT reserva_aerovia_intervalo_ck CHECK (inicio < fim)
);

ALTER TABLE reserva_aerovia
    ADD CONSTRAINT reserva_aerovia_sem_conflito_ex
    EXCLUDE USING gist (aerovia_id WITH =, altitude_pes WITH =, ocupacao WITH &&);

CREATE TABLE reserva_vaga (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plano_voo_id UUID NOT NULL REFERENCES plano_voo(id),
    aeroporto_id UUID NOT NULL REFERENCES aeroporto(id),
    vaga INTEGER NOT NULL CHECK (vaga > 0),
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    ocupacao TSRANGE GENERATED ALWAYS AS (
        tsrange(date_trunc('hour', inicio), date_trunc('hour', fim) +
            CASE WHEN fim = date_trunc('hour', fim) THEN INTERVAL '0 hour' ELSE INTERVAL '1 hour' END, '[)')
    ) STORED,
    CONSTRAINT reserva_vaga_intervalo_ck CHECK (inicio < fim)
);

ALTER TABLE reserva_vaga
    ADD CONSTRAINT reserva_vaga_sem_conflito_ex
    EXCLUDE USING gist (aeroporto_id WITH =, vaga WITH =, ocupacao WITH &&);

CREATE INDEX trecho_plano_ordem_idx ON trecho (plano_voo_id, ordem);
CREATE INDEX reserva_aerovia_plano_idx ON reserva_aerovia (plano_voo_id);
CREATE INDEX reserva_vaga_plano_idx ON reserva_vaga (plano_voo_id);
CREATE INDEX plano_voo_situacao_idx ON plano_voo (situacao);
CREATE INDEX plano_voo_partida_idx ON plano_voo (partida_prevista);

CREATE TABLE livros (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    titulo VARCHAR(255),
    autor VARCHAR(255),
    ano INTEGER,
    CONSTRAINT livros_titulo_nao_vazio_ck CHECK (titulo IS NULL OR btrim(titulo) <> ''),
    CONSTRAINT livros_autor_nao_vazio_ck CHECK (autor IS NULL OR btrim(autor) <> '')
);

CREATE TABLE usuarios_sistema (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(255) NOT NULL,
    CONSTRAINT usuarios_sistema_username_nao_vazio_ck CHECK (btrim(username) <> ''),
    CONSTRAINT usuarios_sistema_senha_nao_vazia_ck CHECK (btrim(senha) <> ''),
    CONSTRAINT usuarios_sistema_perfil_nao_vazio_ck CHECK (btrim(perfil) <> '')
);

