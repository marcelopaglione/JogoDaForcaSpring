package com.elotech.forca.jogodaforca.entity;

import org.springframework.http.HttpStatus;

public class ReturnMessage {
    private String palavraEscondida;
    private String mensagem;
    private HttpStatus status;

    public ReturnMessage() {
    }

    public void setPalavraEscondida(String palavraEscondida) {
        this.palavraEscondida = palavraEscondida;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getPalavraEscondida() {
        return palavraEscondida;
    }

    public String getMensagem() {
        return mensagem;
    }

}
