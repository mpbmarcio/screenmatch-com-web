package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    //private List<DadosSerie> dadosSerie = new ArrayList<>();
    private List<Serie> series;

    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Buscar séries buscadas
                    4 - Buscar séries por título
                    5 - Buscar séries por ator
                    6 - Buscar Top 5 séries
                    7 - Buscar séries por categoria
                    8 - Buscar Qt de Temp. e Avaliação Mínima
                    9 - Buscar Episódio por Trecho
                    10 - Buscar Top 5 Episódios
                    11 - Buscar Episódios a partir de uma Data
                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    buscarMaxTempAvalMin();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTopEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarEpisodiosDepoisDeUmaData() {
        System.out.println("Digite o nome da série para busca:");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serieBuscada= repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("Digite o ano limite de lançamento:");
            String anoLancamento = leitura.nextLine();

            Serie serie = serieBuscada.get();
            List<Episodio> episodios = repositorio.episodioPorSerieEAno(serie, anoLancamento);
            episodios.forEach(System.out::println);
        }

    }

    private void buscarTopEpisodiosPorSerie() {
        System.out.println("Digite o nome da série para busca:");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serieBuscada= repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
           Serie serie = serieBuscada.get();
           List<Episodio> top5Episodios = repositorio.top5EpisodiosPorSerie(serie);
           top5Episodios.forEach(e ->
                    System.out.printf("Série %s Temporadas %s - Episódio %s - %s Avaliação %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        } else {
            System.out.println("Série não encontrada!");
        }

    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite o nome do Episódio");
        String trecho = leitura.nextLine();
        List<Episodio> episodios = repositorio.episodioPorTrecho(trecho);
        episodios.forEach(e ->
                            System.out.printf("Série %s Temporadas %s - Episódio %s - %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                                    e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void buscarMaxTempAvalMin() {
        System.out.println("Digite o valor máximo para temporadas:");
        Integer maxTemp = leitura.nextInt();
        System.out.println("Digite a avaliação Mínima:");
        Double avalMin = leitura.nextDouble();

        //List<Serie> maxTempAvalMin = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(maxTemp, avalMin);
        List<Serie> maxTempAvalMin = repositorio.seriesPorTemporadaEAvaliacao(maxTemp, avalMin);
        System.out.println("As séries com máximo de: " + maxTemp + " temporadas e avaliação Mínima de: " + avalMin);
        maxTempAvalMin.forEach(System.out::println);

    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Digite o nome da categoria/gênero");
        String nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriePorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria: " + nomeGenero);
        seriePorCategoria.forEach(System.out::println);
    }

    private void buscarTop5Series() {
        List<Serie> top = repositorio.findTop5ByOrderByAvaliacaoDesc();

        top.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }


    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome do ator:");
        String ator = leitura.nextLine();
        System.out.println("Digite o valor da avaliação:");
        Double avaliacao = leitura.nextDouble();

        List<Serie> series = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(ator, avaliacao);
        System.out.println("Séries que o ator: " + ator + " trabalhou.");
        //System.out.println(series);
        series.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    /*private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        dadosSerie.add(dados);
        System.out.println(dados);
    }*/

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome:");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serie= repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        /*Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();*/

        if (serie.isPresent()) {
            Serie serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios =  temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        } else {
            System.out.println("Série não encontrada!");
        }
    }

    /*private void buscarEpisodioPorSerie(){
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }*/

    public void buscarSeriesPorTitulo() {
        System.out.println("Digite o nome da série para busca:");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serieBuscada= repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    public void listarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    /*public void listarSeriesBuscadas() {
        List<Serie> series = new ArrayList<>();
        series = dadosSerie.stream()
                .map(s -> new Serie(s))
                        .collect(Collectors.toList());

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

        //dadosSerie.forEach(System.out::println);
    }*/
}