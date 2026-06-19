package com.projeto3.SEBRAE;

import org.springframework.context.annotation.Configuration;

/**
 * A configuração do DataSource é gerenciada pelo Spring Boot por meio das
 * propriedades spring.datasource.* e das variáveis de ambiente do serviço.
 *
 * Esta classe permanece intencionalmente sem beans para substituir versões
 * antigas que fixavam a conexão em localhost:5432.
 */
@Configuration(proxyBeanMethods = false)
public class ConfiguracaoBancoDeDados {
}
