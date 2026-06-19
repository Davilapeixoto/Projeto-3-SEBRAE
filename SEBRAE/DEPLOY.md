# Deploy do Ki (Spring Boot)

O projeto foi preparado para executar com Java 21 em qualquer serviço que aceite Docker, incluindo Koyeb e Render.

## Arquivos adicionados/alterados

- `Dockerfile`: compilação Maven em duas etapas e execução com Java 21.
- `.dockerignore`: reduz o contexto enviado ao servidor.
- `application.properties`: porta e banco configuráveis por ambiente.
- `application-prod.properties`: perfil otimizado para hospedagem.
- `.env.example`: lista das variáveis necessárias.
- `system.properties`: informa Java 21 para serviços que utilizam buildpack.
- Removida `ConfiguracaoBancoDeDados.java`, que obrigava o uso de PostgreSQL local.

## Variáveis obrigatórias no serviço de hospedagem

```text
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/BANCO?sslmode=require
SPRING_DATASOURCE_USERNAME=USUARIO
SPRING_DATASOURCE_PASSWORD=hjgiuiBOUVfdslf132@BRU32$NSDGF
```

## Conta administrativa de demonstração

```text
ADMIN_NAME=Administrador
ADMIN_EMAIL=admin@gmail.com
ADMIN_PASSWORD=admin123
```

A conta é criada na primeira inicialização. Se o e-mail já existir, o sistema o promove para administrador e sincroniza a senha.

A senha `admin123` pertence à conta administradora da aplicação. A senha `hjgiuiBOUVfdslf132@BRU32$NSDGF` pertence ao PostgreSQL/Supabase.

## Variáveis recomendadas para instância gratuita

```text
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=70 -XX:+UseSerialGC
DB_POOL_MAX=5
DB_POOL_MIN=1
```

## Koyeb usando Docker

1. Envie esta pasta para um repositório GitHub.
2. No Koyeb, crie um `Web Service` usando o repositório.
3. Selecione o construtor `Dockerfile`.
4. Adicione as variáveis de ambiente acima.
5. Configure a porta HTTP `8080` caso ela não seja detectada.
6. Faça o deploy.

A aplicação usa `PORT` automaticamente quando a plataforma fornece outro valor.

## Teste local com PostgreSQL

Com um banco `sebrae_db`, usuário `postgres` e senha `postgres`:

```bash
./mvnw clean package -DskipTests
java -jar target/SEBRAE-0.0.1-SNAPSHOT.jar
```

Para testar o perfil de produção:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/sebrae_db'
export SPRING_DATASOURCE_USERNAME='postgres'
export SPRING_DATASOURCE_PASSWORD='postgres'
./mvnw spring-boot:run
```

## Imagens dos cursos

As quarenta imagens iniciais estão dentro do JAR e são restauradas na inicialização. Novas imagens enviadas pelo painel ficam em `/tmp/ki/uploads/cursos` no perfil de produção.

Em hospedagem gratuita esse diretório normalmente é temporário. Assim, imagens novas podem desaparecer após um redeploy, embora o restante dos dados continue no PostgreSQL. Para uma demonstração isso funciona; para armazenamento permanente, migre as imagens para Supabase Storage, Cloudinary ou S3.
