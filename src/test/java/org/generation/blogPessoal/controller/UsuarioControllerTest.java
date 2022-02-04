package org.generation.blogPessoal.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.generation.blogPessoal.model.Usuario;
import org.generation.blogPessoal.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 
 * @author Guilherme Rodrigues
 * 
 * @TestMethodOrder = ndica em qual ordem os testes serão executados
 * -- MethodOrderer.OrderAnnotation.class = s indica que os testes serão executados na ordem indicada pela anotação @Order inserida em cada teste. --
 * -- TestRestTemplate =  para enviar as requisições para a nossa aplicação. -- 
 * -- UsuarioService = para persistir os objetos no Banco de dados de testes com a senha criptografada. --
 * -- HttpEntity = Classe chamado requisicao, recebendo um objeto da Classe Usuario.
 * -- exchange() = A Request HTTP ser através deste método. Ex= (String url, HttpMethod method, HttpEntity<?> requestEntity, Class<Usuario>)
 * 
 * 
 * 
 */


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	UsuarioService usuarioService;
	
	@Test
	@Order(1)
	@DisplayName("Cadastrar usuário")
	public void deveCadastrarUser() {
		HttpEntity<Usuario> request = new HttpEntity<Usuario>(new Usuario(0L,"Melissa Lima", "mel@email.com.br", "123456"));
		ResponseEntity<Usuario> response = testRestTemplate.exchange("/usuario/cadastrar", HttpMethod.POST, request, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		assertEquals(request.getBody().getNome(), response.getBody().getNome());
		assertEquals(request.getBody().getUsuario(), response.getBody().getUsuario());
	
	}
	@Test
	@Order(2)
	@DisplayName("Não deve permitir a duplicação de usuários")
	public void naoDeveDuplicarUsuario() {
		usuarioService.cadastrarUsuario(new Usuario(0L,"Jonas Costa", "jc@email.com.br", "123456"));
		HttpEntity<Usuario> request = new HttpEntity<Usuario>(new Usuario(0L,"Jonas Costa", "jc@email.com.br", "123456"));
		ResponseEntity<Usuario> response = testRestTemplate.exchange("/usuario/cadastrar", HttpMethod.POST, request, Usuario.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	@Test
	@Order(3)
	@DisplayName("Alterar um usuário")
	public void deveAtualizarUmUsuario() {
		Optional<Usuario> usuarioCreate = usuarioService.cadastrarUsuario(new Usuario(0L,"Janaina Lopes", "jana@email.com.br", "123456"));
		Usuario usuarioUpdate = new Usuario(usuarioCreate.get().getId(),"Janaina Lopes Alterado", "jana@email.com.br", "123456");
		HttpEntity<Usuario> request = new HttpEntity<Usuario>(usuarioUpdate);
		ResponseEntity<Usuario> response = testRestTemplate.withBasicAuth("jana@email.com.br", "123456").exchange("/usuario/atualizar", HttpMethod.PUT, request, Usuario.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(usuarioUpdate.getNome(), response.getBody().getNome());
	}
	@Test
	@Order(4)
	@DisplayName("Listar todos os usuários")
	public void deveMostrarTodosUsuarios() {
		usuarioService.cadastrarUsuario(new Usuario(0L,"Matias Santos", "matias@email.com.br", "123456"));
		usuarioService.cadastrarUsuario(new Usuario(0L,"Bruna Marques", "bruma@email.com.br", "123456"));
		ResponseEntity<String> response = testRestTemplate.withBasicAuth("matias@email.com.br", "123456").exchange("/usuario/all", HttpMethod.GET, null, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}