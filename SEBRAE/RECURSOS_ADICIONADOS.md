# Recursos adicionados

## Já existentes e preservados
- Modo noturno persistente.
- Opção Home.
- Catálogo, busca, cursos mais visitados e inscrições.
- Confirmação de senha, login automático após cadastro e toast de sucesso.
- Visual antigo das páginas de login e cadastro.

## Novos recursos
- Feed dinâmico em `/feed` com recomendações, novidades, conteúdos populares e tópicos em alta.
- Jornada guiada “New User” em `/novo-usuario`, com progresso salvo no navegador.
- Categorias por tópicos em `/categorias`.
- Ranking de categorias mais acessadas calculado pelas visualizações dos cursos.
- Modo loja em `/loja`, com vitrine, indicação de gratuidade e alternância entre vitrine e lista.
- Avaliação de cursos de 1 a 5 estrelas, comentário opcional, média, total de avaliações e edição da própria avaliação.

## Banco de dados
A tabela `avaliacoes_cursos` é criada automaticamente por `spring.jpa.hibernate.ddl-auto=update`.
O arquivo `banco/adicionar_avaliacoes.sql` foi incluído apenas para criação manual, se necessário.
