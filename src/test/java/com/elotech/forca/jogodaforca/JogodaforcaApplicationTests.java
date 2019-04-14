package com.elotech.forca.jogodaforca;

import com.elotech.forca.jogodaforca.entity.Jogada;
import com.elotech.forca.jogodaforca.entity.NovoJogo;
import com.elotech.forca.jogodaforca.entity.ReturnMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JogodaforcaApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	private final String URL = "/v1/forca/informar/";

	@Test
	public void contextLoads() {
	}

	@Test
	public void createInvalidNewGameShouldReturn400() {
		NovoJogo novoJogo;
		novoJogo = createNewGame();
		novoJogo.setQuantidadeDeJogadas(0);
		ResponseEntity<String> response = restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);
		assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

		novoJogo = createNewGame();
		novoJogo.setPalavra("");
		response = restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);
		assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
	}

	@Test
	public void createNewGameShouldReturn200() {
		ResponseEntity<String> response = restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(createNewGame()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(CREATED);
	}

	@Test
	public void gameShouldBeCaseInsensitive() {
		NovoJogo novoJogo = createNewGame();
		restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);

		ResponseEntity<ReturnMessage> response = restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada("b")), ReturnMessage.class);

		assertThat(getPalavraEscondida(response)).isEqualTo("_ _ B B _ _ ");
		assertThat(getMensagem(response)).contains("B").contains("inserida").contains("sucesso");
		assertThat(response.getStatusCode()).isEqualTo(OK);

		response = restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada("C")), ReturnMessage.class);
		assertThat(getPalavraEscondida(response)).isEqualTo("_ _ B B C C ");
		assertThat(getMensagem(response)).contains("C").contains("inserida").contains("sucesso");
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	@Test
	public void playerShouldInformOnlyOneLetterAtATime() {
		NovoJogo novoJogo = createNewGame();
		restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);

		ResponseEntity<String> response = restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada("bb")), String.class);

		assertThat(response.getBody()).contains("uma letra por vez");
		assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
	}

	@Test
	public void playRepeatedLetterShouldNotBeAllowed() {
		NovoJogo novoJogo = createNewGame();
		restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);

		ResponseEntity<ReturnMessage> response = restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada("b")), ReturnMessage.class);

		assertThat(getPalavraEscondida(response)).isEqualTo("_ _ B B _ _ ");
		assertThat(getMensagem(response)).contains("B").contains("inserida").contains("sucesso");
		assertThat(response.getStatusCode()).isEqualTo(OK);

		response = restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada("B")), ReturnMessage.class);
		assertThat(getPalavraEscondida(response)).isEqualTo("_ _ B B _ _ ");
		assertThat(getMensagem(response)).contains("B").contains("informada");
		assertThat(response.getStatusCode()).isEqualTo(ALREADY_REPORTED);
	}

	@Test
	public void wordWithEmptySpaceShouldNotBeAllowed() {
		NovoJogo novoJogo = createNewGame();
		novoJogo.setPalavra("AA BB CC");
		ResponseEntity<String> response = restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);

		assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
		assertThat(response.getBody()).contains("não pode conter espaço");
	}

	@Test
	public void playALetterShouldReturn200() {
		NovoJogo novoJogo = createNewGame();
		restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);

		Jogada jogada = new Jogada("a");
		ResponseEntity<ReturnMessage> response = restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(jogada), ReturnMessage.class);

		assertThat(getPalavraEscondida(response)).isEqualTo("A A _ _ _ _ ");
		assertThat(getMensagem(response)).contains("A").contains("inserida").contains("sucesso");
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	@Test
	public void winGameShouldReturn200() {
		NovoJogo novoJogo = createNewGame();
		restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);

		Jogada jogada = new Jogada("a");
		ResponseEntity<ReturnMessage> response;
		restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada("a")), ReturnMessage.class);
		restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada("b")), ReturnMessage.class);
		response = restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada("c")), ReturnMessage.class);
		System.out.println(response);

		assertThat(getPalavraEscondida(response)).isEqualTo("A A B B C C ");
		assertThat(getMensagem(response)).contains("ganhou");
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	@Test
	public void loseGameShouldReturn200() {
		NovoJogo novoJogo = createNewGame();
		restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(novoJogo), String.class);

		ResponseEntity<ReturnMessage> response = null;
		int letraD = 100;
		for (int i = 0; i < 5; i++) {
			response = restTemplate.exchange(URL + "letra/", HttpMethod.POST, createPostEntity(new Jogada(String.valueOf((char)(i+letraD)))), ReturnMessage.class);
		}

		assertThat(getPalavraEscondida(response)).isEqualTo("_ _ _ _ _ _ ");
		assertThat(getMensagem(response)).contains("Fim de jogo");
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	@Test
	public void tentativasRestantesShouldReturn200() {
		restTemplate.exchange(URL + "novoJogo/", HttpMethod.POST, createPostEntity(createNewGame()), String.class);
		ResponseEntity<String> response = restTemplate.getForEntity(URL + "tentativasRestantes/", String.class);

		assertThat(response.getBody()).isEqualTo("{\"status\":\"Você ainda tem 5 tentativas restantes\"}");
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	@Test
	public void requestStatusGameShouldReturn200() {
		NovoJogo novoJogo;
		novoJogo = createNewGame();
		novoJogo.setQuantidadeDeJogadas(0);
		ResponseEntity<String> response = restTemplate.exchange(URL + "jogoStatus/", HttpMethod.GET, createPostEntity(novoJogo), String.class);
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	private NovoJogo createNewGame(){
		return new NovoJogo("AABBCC", 5);
	}

	private HttpEntity createPostEntity(Object obj){
		return new HttpEntity<Object>(obj, null);
	}

	private String getPalavraEscondida(ResponseEntity response){
		return ((ReturnMessage)response.getBody()).getPalavraEscondida();
	}

	private String getMensagem(ResponseEntity response){
		return ((ReturnMessage)response.getBody()).getMensagem();
	}

}
