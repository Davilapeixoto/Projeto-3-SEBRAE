# Validação da integração

- 34 arquivos Java do backend foram incorporados sem alteração de conteúdo.
- 13 templates Thymeleaf foram conferidos.
- Todas as views retornadas pelos controladores possuem arquivo HTML correspondente.
- CSS e JavaScript locais referenciados pelos templates estão presentes.
- `frontend/src/main.jsx` foi preservado com o mesmo SHA-256 do projeto de front:
  `ae81f1c01f2c8018a1ba5cf73171921c8982d90b049bb6b039ea82f8e1dec958`.
- A pasta `target/` antiga foi removida para evitar classes compiladas desatualizadas.

## Limites da validação neste ambiente

O teste Maven não pôde ser reexecutado porque o ambiente não possui Maven instalado e não conseguiu baixar a distribuição do Maven Wrapper. O código Java incorporado é exatamente o do backend enviado, cujo ZIP continha as classes compiladas correspondentes.

O subprojeto React/Vite em `frontend/` foi preservado como enviado. Ele já continha imports de componentes ausentes no arquivo original e não participa da execução da aplicação Spring Boot com Thymeleaf.
