package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.DTO.EpisodioDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String ator, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer maxTemp, Double avalMin);

    //@Query(value = "select * from serie WHERE serie.total_temporadas <= 5 AND serie.avaliacao >= 7.5", nativeQuery = true)
    @Query("Select s from Serie s Where s.totalTemporadas <= :maxTemp And s.avaliacao >= :avalMin")
    List<Serie> seriesPorTemporadaEAvaliacao(Integer maxTemp, Double avalMin);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trecho%")
    List<Episodio> episodioPorTrecho(String trecho);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> top5EpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> episodioPorSerieEAno(Serie serie, String anoLancamento);

    //List<Serie> findTop5ByOrderByEpisodiosDataLancamentoDesc(); //repete as series

    @Query("SELECT s FROM Serie s " +
            "JOIN s.episodios e " +
            "GROUP BY s " +
            "ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> lancamentosMaisRecentes();

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND temporada = :numero")
    List<Episodio> obterEpisodiosPorTemporadas(Long id, Long numero);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    //@Query(value = "SELECT * FROM Episodio WHERE serie_id = 1 ORDER BY avaliacao DESC LIMIT 5", nativeQuery = true)
    List<Episodio> obterTopEpisodiosPorSerie(Serie serie);
}
