package com.elotech.forca.jogodaforca.entity;

import java.util.ArrayList;
import java.util.List;

public class Forca {
    private String palavraSecreta;
    private int totalTentativas;
    private int totalDeJogadas;
    private List<Character> letrasJogadas;

    public Forca() {
        letrasJogadas = new ArrayList<>();
        totalTentativas = 0;
        totalDeJogadas = 0;
        palavraSecreta = "";
    }

    public String getPalavraSecreta() {
        return palavraSecreta.toUpperCase();
    }

    public void setPalavraSecreta(String palavraSecreta) {
        this.palavraSecreta = palavraSecreta;
    }

    public int getTotalTentativas() {
        return totalTentativas;
    }

    public void setTotalTentativas(int totalTentativas) {
        this.totalTentativas = totalTentativas;
    }

    public int getTotalDeJogadas() {
        return totalDeJogadas;
    }

    public void setTotalDeJogadas(int totalDeJogadas) {
        this.totalDeJogadas = totalDeJogadas;
    }

    public List<Character> getLetrasJogadas() {
        return letrasJogadas;
    }

}
