# SEBRAE

Projeto Spring Boot com cadastro e login de usuários, feed dinâmico, categorias, loja de cursos, avaliações, inscrições e manutenção administrativa de áreas e tags.

## Requisitos

- Java 21
- PostgreSQL
- Maven Wrapper incluído no projeto

## Banco de dados

Crie o banco usando `banco/criar_banco.sql` ou execute:

```sql
CREATE DATABASE sebrae_db;
```

A configuração atual utiliza:

- Banco: `sebrae_db`
- Usuário: `postgres`
- Senha: `postgres`
- Porta: `5432`

Altere os dados em `ConfiguracaoBancoDeDados.java` quando necessário.

O Hibernate cria e atualiza as tabelas com `spring.jpa.hibernate.ddl-auto=update`.

## Executar

No Windows:

```bash
mvnw.cmd clean spring-boot:run
```

No Linux ou macOS:

```bash
chmod +x mvnw
./mvnw clean spring-boot:run
```

Acesse `http://localhost:8080`.

## Fluxo

1. Inicie a aplicação. A conta administradora configurada é criada automaticamente.
2. Entre em `/login`.
3. Com a conta administradora, crie áreas em `/admin/areas`.
4. Crie tags em `/admin/tags`.
5. Cadastre um curso em `/cursos/novo`.
6. Abra a Home em `/`, o feed em `/feed` ou a loja em `/loja`.
7. Explore os tópicos em `/categorias` ou a jornada guiada em `/novo-usuario`.
8. Abra a página individual em `/cursos/{id}`.
9. Inscreva-se e avalie o curso.
10. Consulte as inscrições em `/minhas-inscricoes`.

## Curso

Cada curso possui:

- Nome
- Descrição
- Imagem
- Nível
- Área existente
- Uma ou mais tags existentes

## Inscrição

A tabela `inscricoes` relaciona um usuário a um curso e registra a data e a hora da inscrição. A restrição única impede que o mesmo usuário se inscreva duas vezes no mesmo curso.

## Organização

- O painel administrativo mantém áreas, tags e estatísticas.
- Apenas usuários com perfil de administrador acessam as rotas `/admin`.
- A conta administrativa é criada automaticamente na inicialização.
- Usuários cadastrados pela tela pública recebem sempre o perfil de aluno.
- O cadastro e o gerenciamento dos cursos continuam restritos ao perfil de administrador.
- A exclusão de áreas e tags utilizadas por cursos é bloqueada.
- A exclusão de um curso remove suas inscrições, avaliações e sua imagem.

## Navegação pelos cursos

Os cards de cursos da página inicial, do catálogo e da página de inscrições são clicáveis por inteiro e redirecionam para `/cursos/{id}`. Na tela de gerenciamento, a imagem e o nome do curso também abrem a página de detalhes.

## Conta administradora automática

Ao iniciar a aplicação, o sistema garante a existência da conta administrativa configurada em `application.properties`:

```properties
aplicacao.admin.nome=${ADMIN_NAME:Administrador}
aplicacao.admin.email=${ADMIN_EMAIL:admin@gmail.com}
aplicacao.admin.password=${ADMIN_PASSWORD:admin123}
```

Com a configuração padrão, o acesso é `admin@gmail.com` / `admin123`.
Em produção, defina `ADMIN_EMAIL` e `ADMIN_PASSWORD` no ambiente. Se o e-mail já existir, a conta será promovida para administradora e a senha será sincronizada com `ADMIN_PASSWORD`.


## Integração front + backend

Este pacote foi montado usando o projeto de front como base. A página inicial, o login, o cadastro e todo o diretório `frontend/` foram preservados. O backend completo foi incorporado e as rotas principais do front agora apontam para o catálogo, busca, inscrições e painel administrativo.


## Recursos de descoberta

- `/feed`: recomendações baseadas nas áreas dos cursos em que o usuário está inscrito, novidades e conteúdos populares.
- `/categorias`: categorias por tópicos e ranking por total de visualizações.
- `/loja`: catálogo em formato de vitrine, com alternância entre grade e lista.
- `/novo-usuario`: jornada guiada com progresso salvo no navegador.

## Avaliações

Usuários autenticados podem atribuir notas de 1 a 5 estrelas e publicar um comentário opcional. Cada usuário possui uma única avaliação por curso e pode atualizá-la. A página do curso exibe média, quantidade e comentários. A tabela é criada automaticamente pelo Hibernate.
