package br.com.alura.screenmatch.service.traducao;

import br.com.alura.screenmatch.service.traducao.DadosResposta;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosTraducao(@JsonAlias(value = "responseData") DadosResposta dadosResposta) {
}