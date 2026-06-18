-- Substitua o e-mail abaixo pelo e-mail da conta que deve acessar o painel.
-- Depois, saia da conta e entre novamente para renovar a sessão.
UPDATE usuarios
SET perfil = 'ADMINISTRADOR'
WHERE LOWER(email) = LOWER('seu-email@exemplo.com');
