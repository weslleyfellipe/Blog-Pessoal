package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController// anotação que diz para o spring que essa é uuma controladora de rotas e acesso ao metodos 
@RequestMapping("/postagens")//rota para chegar nessa classe "insomnia"
@CrossOrigin(origins = "*",allowedHeaders = "*")//liberar acesso a outras maquinas /allowedHeaders = liberar passagem para paramentos para outras maquinas 
public class PostagemController {
	
	
	@Autowired // chamada de  dependencias -- instaciar a Classe PostagemRepository com o codigo abaixo
	private PostagemRepository postagemRepository;
	
	@Autowired
	private TemaRepository temaRepository;
	

	@GetMapping//defini o verbo http que atende esse metodo
	public ResponseEntity<List<Postagem>> getAll(){
		//ResponseEntity - classe
		return ResponseEntity.ok(postagemRepository.findAll());
	//sELECT * FROM tb_postagens
	}
    
	@GetMapping("/{id}")//localhost:8080/postagens/1
	public ResponseEntity<Postagem>getByid(@PathVariable Long id ){
		//findByid= SELECT * FROM tb_postagens WHERE id = 1
		return postagemRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // vai fazer a resposta se for falsa e vai apenas buildar e a resposta é Not_Found 
		
	}
	
	//SELECT * FROM tb_postagens WHERE titulo = "titulo";
	@GetMapping ("/titulo/{titulo}")//localhost:8080/postagens/titulo/Postagem 02
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){
		return ResponseEntity.ok(postagemRepository.findAllBytituloContainingIgnoreCase(titulo));
		
	}
      ////INSERT INTO tb_postagens (titulo, texto, data) VALUES ("Título", "Texto", "2024-12-31 14:05:01");
	 @PostMapping // vai atender o verbo post e não atende mais o metodo get.
	 public ResponseEntity<Postagem>post(@Valid @RequestBody Postagem postagem){
		 if(temaRepository.existsById(postagem.getTema().getId()))
		return ResponseEntity.status(HttpStatus.CREATED)// CREATED criando o registro no banco de dados 
				.body(postagemRepository.save(postagem));//body passando o corpo //save salvando a postagem e exibindo 
		 	
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tema não existe !",null);
	 
      }
	// return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	 @PutMapping
	  public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem){
	   if(postagemRepository.existsById(postagem.getId())){
	   	 
		   if(temaRepository.existsById(postagem.getTema().getId()))
	   		return ResponseEntity.status(HttpStatus.OK)
            .body(postagemRepository.save(postagem));
		   
	   
            
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tema não existe !", null);
		 
	 }
		
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();	
				
				
}
	 //DELETE FROM TB_POSTAGENS WHERE ID = ID;
	 @DeleteMapping ("/{id}")
	 public void  delete(@PathVariable Long id) {
		 Optional<Postagem> postagem = postagemRepository.findById(id);// USANDO O OPTIONAL PARA O CODIGO NAO QUEBRAR MESMO ELE ESTANDO VAZIO .optional retornando o objeto inteiro ou vazio.
		 
		 if(postagem.isEmpty())// VALIDANDO SE ESTA VAZIO IS.EMPTY  se caso sim ele vai executar o throw new executando o not found.
			 throw new ResponseStatusException(HttpStatus.NOT_FOUND);// meu if vai executar até o primeiro ponto e virgula ;
		 
		 postagemRepository.deleteById(id);
			 
	   
	 }
   
}

