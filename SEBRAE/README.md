# SEBRAE

Projeto Spring Boot com cadastro e login de usuários, catálogo de cursos, página individual de cada curso, inscrições e manutenção administrativa de áreas e tags.

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

1. Crie uma conta em `/cadastro`. A primeira conta recebe o perfil de administrador.
2. Entre em `/login`.
3. Com a conta administradora, crie áreas em `/admin/areas`.
4. Crie tags em `/admin/tags`.
5. Cadastre um curso em `/cursos/novo`.
6. Abra o catálogo em `/cursos`.
7. Abra a página individual em `/cursos/{id}`.
8. Inscreva-se no curso.
9. Consulte as inscrições em `/minhas-inscricoes`.

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

- O painel administrativo mantém somente áreas e tags.
- Apenas usuários com perfil de administrador acessam as rotas `/admin`.
- A primeira conta cadastrada é definida como administradora. Em bancos que já possuem usuários, o usuário mais antigo é promovido quando ainda não existe administrador.
- O cadastro e o gerenciamento dos cursos ficam fora do painel administrativo e continuam restritos ao perfil de administrador.
- A exclusão de áreas e tags utilizadas por cursos é bloqueada.
- A exclusão de um curso remove suas inscrições e sua imagem.

## Navegação pelos cursos

Os cards de cursos da página inicial, do catálogo e da página de inscrições são clicáveis por inteiro e redirecionam para `/cursos/{id}`. Na tela de gerenciamento, a imagem e o nome do curso também abrem a página de detalhes.
