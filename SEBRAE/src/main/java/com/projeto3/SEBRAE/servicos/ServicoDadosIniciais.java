package com.projeto3.SEBRAE.servicos;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.Area;
import com.projeto3.SEBRAE.modelo.Curso;
import com.projeto3.SEBRAE.modelo.Nivel;
import com.projeto3.SEBRAE.modelo.Tag;
import com.projeto3.SEBRAE.repositorios.RepositorioArea;
import com.projeto3.SEBRAE.repositorios.RepositorioCurso;
import com.projeto3.SEBRAE.repositorios.RepositorioTag;

/**
 * Prepara uma base de demonstração na primeira execução da aplicação.
 *
 * A carga é idempotente: reiniciar o projeto não duplica áreas, tags ou
 * cursos. Bancos que já possuem conteúdo próprio são apenas complementados
 * até alcançar quarenta cursos no total.
 */
@Service
@Order(20)
public class ServicoDadosIniciais implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicoDadosIniciais.class);

    private static final List<String> NOMES_TAGS = List.of(
            "Empreendedorismo",
            "Finanças",
            "Marketing",
            "Vendas",
            "Gestão",
            "Inovação",
            "Tecnologia",
            "Atendimento",
            "Planejamento",
            "Sustentabilidade");

    private static final List<String> NOMES_AREAS = List.of(
            "Empreendedorismo",
            "Finanças",
            "Marketing e Vendas",
            "Gestão de Pessoas",
            "Inovação e Tecnologia",
            "Atendimento ao Cliente",
            "Planejamento Estratégico",
            "Sustentabilidade");

    private static final List<CursoInicial> CURSOS = List.of(
            curso("Comece a Empreender", "Empreendedorismo", Nivel.INICIANTE, 46, "Empreendedorismo", "Planejamento"),
            curso("Modelagem de Negócios", "Empreendedorismo", Nivel.INTERMEDIARIO, 61, "Empreendedorismo", "Gestão"),
            curso("Validação de Ideias", "Empreendedorismo", Nivel.INICIANTE, 39, "Empreendedorismo", "Inovação"),
            curso("Plano de Negócio na Prática", "Empreendedorismo", Nivel.INTERMEDIARIO, 82, "Empreendedorismo", "Planejamento"),
            curso("Crescimento Empreendedor", "Empreendedorismo", Nivel.AVANCADO, 34, "Empreendedorismo", "Gestão"),

            curso("Controle de Fluxo de Caixa", "Finanças", Nivel.INICIANTE, 113, "Finanças", "Gestão"),
            curso("Formação de Preço de Venda", "Finanças", Nivel.INTERMEDIARIO, 97, "Finanças", "Vendas"),
            curso("Gestão Financeira para Pequenos Negócios", "Finanças", Nivel.INTERMEDIARIO, 88, "Finanças", "Gestão"),
            curso("Indicadores Financeiros Essenciais", "Finanças", Nivel.AVANCADO, 58, "Finanças", "Planejamento"),
            curso("Crédito e Capital de Giro", "Finanças", Nivel.AVANCADO, 54, "Finanças", "Empreendedorismo"),

            curso("Marketing Digital para Pequenos Negócios", "Marketing e Vendas", Nivel.INICIANTE, 129, "Marketing", "Tecnologia"),
            curso("Conteúdo que Vende", "Marketing e Vendas", Nivel.INTERMEDIARIO, 77, "Marketing", "Vendas"),
            curso("Redes Sociais para Empresas", "Marketing e Vendas", Nivel.INICIANTE, 104, "Marketing", "Tecnologia"),
            curso("Estratégias de Vendas Consultivas", "Marketing e Vendas", Nivel.AVANCADO, 69, "Vendas", "Atendimento"),
            curso("Jornada do Cliente", "Marketing e Vendas", Nivel.INTERMEDIARIO, 73, "Marketing", "Atendimento"),

            curso("Liderança de Equipes", "Gestão de Pessoas", Nivel.INTERMEDIARIO, 91, "Gestão", "Planejamento"),
            curso("Gestão de Pessoas na Prática", "Gestão de Pessoas", Nivel.INICIANTE, 67, "Gestão", "Empreendedorismo"),
            curso("Feedback e Desenvolvimento", "Gestão de Pessoas", Nivel.INTERMEDIARIO, 48, "Gestão", "Atendimento"),
            curso("Cultura Organizacional", "Gestão de Pessoas", Nivel.AVANCADO, 42, "Gestão", "Inovação"),
            curso("Produtividade para Gestores", "Gestão de Pessoas", Nivel.INICIANTE, 86, "Gestão", "Planejamento"),

            curso("Transformação Digital", "Inovação e Tecnologia", Nivel.INTERMEDIARIO, 118, "Inovação", "Tecnologia"),
            curso("Ferramentas Digitais para Negócios", "Inovação e Tecnologia", Nivel.INICIANTE, 96, "Tecnologia", "Empreendedorismo"),
            curso("Inteligência Artificial para Empreendedores", "Inovação e Tecnologia", Nivel.AVANCADO, 137, "Tecnologia", "Inovação"),
            curso("Inovação em Produtos e Serviços", "Inovação e Tecnologia", Nivel.INTERMEDIARIO, 72, "Inovação", "Marketing"),
            curso("Segurança Digital para Empresas", "Inovação e Tecnologia", Nivel.AVANCADO, 65, "Tecnologia", "Gestão"),

            curso("Excelência no Atendimento", "Atendimento ao Cliente", Nivel.INICIANTE, 83, "Atendimento", "Vendas"),
            curso("Atendimento Omnichannel", "Atendimento ao Cliente", Nivel.INTERMEDIARIO, 57, "Atendimento", "Tecnologia"),
            curso("Experiência do Cliente", "Atendimento ao Cliente", Nivel.AVANCADO, 79, "Atendimento", "Marketing"),
            curso("Pós-venda e Fidelização", "Atendimento ao Cliente", Nivel.INTERMEDIARIO, 63, "Atendimento", "Vendas"),
            curso("Gestão de Reclamações", "Atendimento ao Cliente", Nivel.INICIANTE, 41, "Atendimento", "Gestão"),

            curso("Planejamento Estratégico", "Planejamento Estratégico", Nivel.INTERMEDIARIO, 109, "Planejamento", "Gestão"),
            curso("Metas e Indicadores", "Planejamento Estratégico", Nivel.INICIANTE, 74, "Planejamento", "Finanças"),
            curso("Gestão de Projetos", "Planejamento Estratégico", Nivel.INTERMEDIARIO, 89, "Planejamento", "Gestão"),
            curso("Análise de Mercado", "Planejamento Estratégico", Nivel.AVANCADO, 68, "Planejamento", "Marketing"),
            curso("Tomada de Decisão com Dados", "Planejamento Estratégico", Nivel.AVANCADO, 101, "Planejamento", "Tecnologia"),

            curso("Negócios Sustentáveis", "Sustentabilidade", Nivel.INICIANTE, 52, "Sustentabilidade", "Empreendedorismo"),
            curso("Economia Circular", "Sustentabilidade", Nivel.INTERMEDIARIO, 47, "Sustentabilidade", "Inovação"),
            curso("ESG para Pequenas Empresas", "Sustentabilidade", Nivel.AVANCADO, 56, "Sustentabilidade", "Gestão"),
            curso("Eficiência Energética", "Sustentabilidade", Nivel.INTERMEDIARIO, 38, "Sustentabilidade", "Tecnologia"),
            curso("Impacto Social e Empreendedorismo", "Sustentabilidade", Nivel.INICIANTE, 44, "Sustentabilidade", "Empreendedorismo"));

    private final RepositorioCurso repositorioCurso;
    private final RepositorioArea repositorioArea;
    private final RepositorioTag repositorioTag;
    private final Path diretorioImagens;

    public ServicoDadosIniciais(
            RepositorioCurso repositorioCurso,
            RepositorioArea repositorioArea,
            RepositorioTag repositorioTag,
            @Value("${aplicacao.upload.cursos:uploads/cursos}") String diretorioImagens) {
        this.repositorioCurso = repositorioCurso;
        this.repositorioArea = repositorioArea;
        this.repositorioTag = repositorioTag;
        this.diretorioImagens = Paths.get(diretorioImagens).toAbsolutePath().normalize();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, Tag> tags = garantirTags();
        Map<String, Area> areas = garantirAreas();
        garantirImagens();
        int criados = garantirCursos(tags, areas);

        LOGGER.info("Carga inicial conferida: {} tags, {} áreas e {} cursos no banco ({} cursos adicionados nesta execução).",
                repositorioTag.count(), repositorioArea.count(), repositorioCurso.count(), criados);
    }

    private Map<String, Tag> garantirTags() {
        Map<String, Tag> resultado = new LinkedHashMap<>();
        for (String nome : NOMES_TAGS) {
            Tag tag = repositorioTag.findByNomeIgnoreCase(nome).orElseGet(() -> {
                Tag nova = new Tag();
                nova.setNome(nome);
                return repositorioTag.save(nova);
            });
            resultado.put(nome, tag);
        }
        return resultado;
    }

    private Map<String, Area> garantirAreas() {
        Map<String, Area> resultado = new LinkedHashMap<>();
        for (String nome : NOMES_AREAS) {
            Area area = repositorioArea.findByNomeIgnoreCase(nome).orElseGet(() -> {
                Area nova = new Area();
                nova.setNome(nome);
                return repositorioArea.save(nova);
            });
            resultado.put(nome, area);
        }
        return resultado;
    }

    private void garantirImagens() {
        try {
            Files.createDirectories(diretorioImagens);
            for (int indice = 1; indice <= CURSOS.size(); indice++) {
                String nomeArquivo = nomeImagem(indice);
                Path destino = diretorioImagens.resolve(nomeArquivo).normalize();
                if (!destino.startsWith(diretorioImagens)) {
                    throw new IllegalStateException("Caminho inválido para imagem inicial.");
                }
                if (Files.exists(destino)) {
                    continue;
                }
                ClassPathResource recurso = new ClassPathResource("seed/cursos/" + nomeArquivo);
                try (InputStream entrada = recurso.getInputStream()) {
                    Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível preparar as imagens dos cursos iniciais.", e);
        }
    }

    private int garantirCursos(Map<String, Tag> tags, Map<String, Area> areas) {
        Set<String> nomesExistentes = new LinkedHashSet<>();
        repositorioCurso.findAll().forEach(curso -> nomesExistentes.add(curso.getNome().strip().toLowerCase()));

        int criados = 0;
        long totalAtual = repositorioCurso.count();
        for (int indice = 0; indice < CURSOS.size() && totalAtual < 40; indice++) {
            CursoInicial base = CURSOS.get(indice);
            if (nomesExistentes.contains(base.nome().toLowerCase())) {
                continue;
            }

            Curso curso = new Curso();
            curso.setNome(base.nome());
            curso.setDescricao(criarDescricao(base));
            curso.setArea(areas.get(base.area()));
            curso.setNivel(base.nivel());
            curso.setVisualizacoes(base.visualizacoes());
            curso.setImagem(nomeImagem(indice + 1));

            Set<Tag> tagsDoCurso = new LinkedHashSet<>();
            for (String nomeTag : base.tags()) {
                Tag tag = tags.get(nomeTag);
                if (tag != null) {
                    tagsDoCurso.add(tag);
                }
            }
            curso.setTags(tagsDoCurso);
            repositorioCurso.save(curso);
            nomesExistentes.add(base.nome().toLowerCase());
            criados++;
            totalAtual++;
        }
        return criados;
    }

    private String criarDescricao(CursoInicial base) {
        return "Aprenda " + base.nome().toLowerCase()
                + " com uma trilha prática voltada para pequenos negócios. O curso apresenta conceitos, exemplos e atividades "
                + "que ajudam a aplicar o conhecimento na rotina da empresa, com conteúdo de nível "
                + base.nivel().getDescricao().toLowerCase() + ".";
    }

    private static CursoInicial curso(String nome, String area, Nivel nivel, long visualizacoes, String... tags) {
        return new CursoInicial(nome, area, nivel, visualizacoes, List.of(tags));
    }

    private static String nomeImagem(int indice) {
        return "curso-demo-%02d.jpg".formatted(indice);
    }

    private record CursoInicial(String nome, String area, Nivel nivel, long visualizacoes, List<String> tags) {
        private CursoInicial {
            tags = new ArrayList<>(tags);
        }
    }
}
