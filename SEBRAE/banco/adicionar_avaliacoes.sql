-- Opcional: o Hibernate cria esta tabela automaticamente com ddl-auto=update.
CREATE TABLE IF NOT EXISTS avaliacoes_cursos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    nota INTEGER NOT NULL CHECK (nota BETWEEN 1 AND 5),
    comentario VARCHAR(1000),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_avaliacao_usuario_curso UNIQUE (usuario_id, curso_id),
    CONSTRAINT fk_avaliacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_avaliacao_curso FOREIGN KEY (curso_id) REFERENCES cursos(id)
);
