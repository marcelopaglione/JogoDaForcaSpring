package com.elotech.forca.jogodaforca.controller;

import com.elotech.forca.jogodaforca.entity.Forca;
import com.elotech.forca.jogodaforca.entity.JogoStatus;
import com.elotech.forca.jogodaforca.entity.ReturnMessage;
import org.springframework.http.HttpStatus;

public class ForcaGame {

    private Forca forca;
    private final char letraParaAdivinhar = '_';
    private String id;

    public ForcaGame(int totalDeJogadas) {
        forca = new Forca();
        forca.setTotalDeJogadas(totalDeJogadas);
        forca.setPalavraSecreta("AABBAACDE");
    }

    private boolean jogadaValida(char letra){
        return !forca.getLetrasJogadas().contains(letra);
    }

    private void addLetra(char letra){
        forca.getLetrasJogadas().add(letraNormalizada(letra));
    }

    private void somarUmaTentativa(){
        forca.setTotalTentativas(forca.getTotalTentativas() + 1);
    }

    private boolean palavraSecretaContemLetra(char letra){
        return forca.getPalavraSecreta().indexOf(letraNormalizada(letra)) >= 0;
    }

    public ReturnMessage jogar(char letra){
        letra = letraNormalizada(letra);
        String mensagem;
        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setStatus(HttpStatus.OK);

        if(!fimDeJogo()){
            if(jogadaValida(letra)) {
                addLetra(letra);
                if (!palavraSecretaContemLetra(letra)) {
                    somarUmaTentativa();
                    mensagem = "Letra " + letra + " inexistente - Você ainda possui " + totalTentativasRestantes() + " tentativas.";
                } else {
                    mensagem = "Letra " + letra + " inserida com sucesso.";
                }
            }else{
                mensagem = "Letra " + letra + " já foi informada!";
                returnMessage.setStatus(HttpStatus.ALREADY_REPORTED);
            }
            if(fimDeJogo()){
                mensagem = "Fim de jogo, você perdeu!";
            }

        }else{
            mensagem = "Fim de jogo, você perdeu!";
        }

        if(ganhouJogo()){
            mensagem = "Você ganhou!";
        }

        returnMessage.setMensagem(mensagem);
        returnMessage.setPalavraEscondida(mostrarJogada());

        return returnMessage;
    }

    private int totalTentativasRestantes(){
        return forca.getTotalDeJogadas() - forca.getTotalTentativas();
    }

    public JogoStatus getTotalTentativasRestantes() {
        return new JogoStatus(totalTentativasRestantes()+"");
    }

    public void criarNovoJogo(String palavraSecreta, int totalDeJogadas){
        this.forca = new Forca();
        this.forca.setPalavraSecreta(palavraSecreta);
        this.forca.setTotalDeJogadas(totalDeJogadas);
    }

    private boolean fimDeJogo(){
        return forca.getPalavraSecreta()==null || perdeuJogo() || ganhouJogo();
    }

    private boolean ganhouJogo(){
        return !mostrarJogada().contains(String.valueOf(letraParaAdivinhar));
    }

    private boolean perdeuJogo(){
        return totalTentativasRestantes() == 0;
    }

    public JogoStatus jogoStatus() {
        return new JogoStatus(mostrarJogada());
    }

    private String mostrarJogada(){
        String palavraSecreta = "";

        for (int i = 0; i < forca.getPalavraSecreta().length(); i++) {
            char letra = forca.getPalavraSecreta().charAt(i);
            if(forca.getLetrasJogadas().contains(letraNormalizada(letra))){
                palavraSecreta+=letra+" ";
            }else{
                palavraSecreta+= letraParaAdivinhar +" ";
            }
        }

        return  palavraSecreta;
    }

    private char letraNormalizada(char letra){
        return Character.toUpperCase(letra);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
