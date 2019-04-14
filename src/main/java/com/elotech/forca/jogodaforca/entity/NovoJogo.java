package com.elotech.forca.jogodaforca.entity;

public class NovoJogo {
    private String palavra;
    private int quantidadeDeJogadas;

    public NovoJogo() {
    }

    public NovoJogo(String palavra, int quantidadeDeJogadas) {
        this.palavra = palavra;
        this.quantidadeDeJogadas = quantidadeDeJogadas;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public int getQuantidadeDeJogadas() {
        return quantidadeDeJogadas;
    }

    public void setQuantidadeDeJogadas(int quantidadeDeJogadas) {
        this.quantidadeDeJogadas = quantidadeDeJogadas;
    }
}
