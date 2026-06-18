-- Opcional: o projeto usa spring.jpa.hibernate.ddl-auto=update e cria estas colunas/tabelas automaticamente.
ALTER TABLE cursos ADD COLUMN IF NOT EXISTS visualizacoes BIGINT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS tempos_pagina (
    id BIGSERIAL PRIMARY KEY,
    visita_id VARCHAR(64) NOT NULL UNIQUE,
    pagina VARCHAR(255) NOT NULL,
    curso_id BIGINT NULL,
    usuario_id BIGINT NULL,
    segundos BIGINT NOT NULL DEFAULT 0,
    finalizado BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_tempo_pagina ON tempos_pagina (pagina);
CREATE INDEX IF NOT EXISTS idx_tempo_curso ON tempos_pagina (curso_id);
