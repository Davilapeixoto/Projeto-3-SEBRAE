# Paleta, logos e dados iniciais

## Identidade visual

A plataforma passou a usar como cores principais:

- Azul escuro: `#0F0F77`
- Vermelho vivido: `#F32C06`
- Amarelo: `#E7F118`
- Amarelo claro: `#EAE8E1`

Os quadrados com a letra **S** foram substituídos pela imagem oficial disponível em
`src/main/resources/static/assets/logo-sebrae.png`. A substituição também foi aplicada
à navegação administrativa e ao rodapé da home.

A tela de login permanece exclusivamente em modo claro. O restante da plataforma
continua com o modo escuro disponível.

## Carga automática

O serviço `ServicoDadosIniciais` é executado ao iniciar a aplicação e garante:

- 10 tags de demonstração;
- 8 áreas necessárias para organizar os cursos;
- até 40 cursos no banco, com níveis iniciante, intermediário e avançado;
- imagens locais para cada um dos 40 cursos;
- visualizações iniciais variadas para alimentar os rankings e recomendações.

A carga é idempotente: reiniciar o projeto não duplica os registros. Caso já existam
cursos próprios no banco, o serviço apenas complementa o total até quarenta.

As imagens originais da carga ficam em `src/main/resources/seed/cursos` e são copiadas
para o diretório configurado em `aplicacao.upload.cursos` durante a inicialização.

## Cadastro de curso

A imagem continua obrigatória para novos cursos, aceita JPG, PNG e WEBP até 5 MB e
agora possui pré-visualização antes do envio do formulário.
