# Alterações visuais e de cadastro

## Identidade visual

- Todas as páginas Thymeleaf passaram a usar a paleta da página inicial:
  - preto: `#0B0C10`
  - cinza principal: `#36363E`
  - fundo claro: `#F3F4F6`
  - laranja de destaque: `#F05D23`
  - verde: `#0F978A`
  - azul escuro: `#144F63`
  - amarelo: `#E8AC00`
- Login, cadastro, catálogo, detalhes do curso, inscrições e painel administrativo foram harmonizados.
- O arquivo `recursos-plataforma.css` concentra os componentes visuais públicos.
- O arquivo `admin.css` mantém o painel administrativo alinhado à mesma identidade.

## Modo escuro

- Disponível em todas as páginas.
- A escolha é armazenada em `localStorage` usando a chave `sebrae-tema`.
- Quando ainda não existe escolha salva, a preferência do sistema operacional é respeitada.

## Cadastro

- Adicionado o campo `confirmarSenha`.
- A igualdade entre as senhas é validada no navegador e novamente no backend.
- Após salvar o cadastro, o usuário é colocado diretamente na sessão `usuarioLogado`.
- O redirecionamento passa a ser para `/?cadastro=sucesso`.
- A página inicial apresenta um toast informando que o cadastro foi concluído e que a conta já está conectada.
- Depois de exibir o toast, o parâmetro `cadastro` é removido do endereço para a notificação não reaparecer ao atualizar a página.
