package com.elotech.forca.jogodaforca.endpoint;

import com.elotech.forca.jogodaforca.controller.ForcaGame;
import com.elotech.forca.jogodaforca.entity.Jogada;
import com.elotech.forca.jogodaforca.entity.NovoJogo;
import com.elotech.forca.jogodaforca.entity.ReturnMessage;
import com.elotech.forca.jogodaforca.error.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.Normalizer;
import java.util.Optional;

@RestController
@RequestMapping("v1/forca")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:9876"})
public class ForcaEndpoint {

    private ForcaGame forcaGame;

    public ForcaEndpoint() {
        forcaGame = new ForcaGame(5);
    }

    @PostMapping(path = "informar/letra/")
    public ResponseEntity<?> play(@RequestBody Jogada jogada) {
        validRequest(jogada);
        ReturnMessage returnMessage = forcaGame.jogar(normalizar(jogada.getLetra()));
        return new ResponseEntity<>(returnMessage, returnMessage.getStatus());
    }

    @PostMapping(path = "informar/novoJogo/")
    public ResponseEntity<?> newGame(@RequestBody NovoJogo novoJogo) {
        validRequest(novoJogo);
        forcaGame.criarNovoJogo(novoJogo.getPalavra(), novoJogo.getQuantidadeDeJogadas());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "informar/tentativasRestantes/")
    public ResponseEntity<?> tentativasRestantes(){
        return new ResponseEntity<>(forcaGame.getTotalTentativasRestantes(), HttpStatus.OK);
    }

    @GetMapping(path = "informar/jogoStatus/")
    public ResponseEntity<?> jogoStatus(){
        return new ResponseEntity<>(forcaGame.jogoStatus(), HttpStatus.OK);
    }

    private void validRequest(Object object) throws RuntimeException {
        if (object instanceof Jogada) {

            if(((Jogada) object).getLetra().length()>1){
                throw new InvalidRequestException("Por favor informe apenas uma letra por vez");
            }

            char letraInformada = normalizar(((Jogada) object).getLetra());
            letraEntreAeZ(letraInformada);
        } else {
            if (((NovoJogo) object).getPalavra() == null || ((NovoJogo) object).getPalavra().length() == 0) {
                throw new InvalidRequestException("Informe uma palavra secreta");
            }
            if (((NovoJogo) object).getPalavra().contains(" ")) {
                throw new InvalidRequestException("A palavra secreta não pode conter espaço em branco");
            }
            if (((NovoJogo) object).getQuantidadeDeJogadas() == 0) {
                throw new InvalidRequestException("A quantidade total de jogas deve ser maior que 0");
            }
            for (int i = 0; i < ((NovoJogo) object).getPalavra().length(); i++) {
                letraEntreAeZ(((NovoJogo) object).getPalavra().charAt(i));
            }

        }
    }

    private void letraEntreAeZ(char letraInformada) {
        int letraA = 65;
        int letraZ = 90;
        if (!(Character.toUpperCase(letraInformada) >= letraA && Character.toUpperCase(letraInformada) <= letraZ)) {
            throw new InvalidRequestException("As letras informadas devem estar entre A e Z");
        }
    }

    private char normalizar(String letra){
        String letraNormalizada = Normalizer.normalize(letra, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        return letraNormalizada.charAt(0);
    }
}
