package com.projeto3.SEBRAE.configuracao;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracaoArquivos implements WebMvcConfigurer {

    private final String diretorioCursos;

    public ConfiguracaoArquivos(@Value("${aplicacao.upload.cursos:uploads/cursos}") String diretorioCursos) {
        this.diretorioCursos = diretorioCursos;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeamentos explícitos para garantir que os recursos adicionados ao projeto
        // sejam encontrados também ao executar pelo Eclipse.
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        String local = Paths.get(diretorioCursos).toAbsolutePath().normalize().toUri().toString();
        if (!local.endsWith("/")) {
            local += "/";
        }
        registry.addResourceHandler("/uploads/cursos/**").addResourceLocations(local);
    }
}
