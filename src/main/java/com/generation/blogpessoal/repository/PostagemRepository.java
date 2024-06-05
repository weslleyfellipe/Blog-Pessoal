package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import com.generation.blogpessoal.model.Postagem;


//JpaRepository-classe Jpa - metodos que vão realizar query no banco
public interface PostagemRepository extends JpaRepository<Postagem,Long> {
	// Select *From tb_postagens
	
	
	// Select *From tb_postagens WHERE titulo LIKE "%POST%"; =  findAllBytituloContainingIgnoreCase
	public List <Postagem> findAllBytituloContainingIgnoreCase(@Param("titulo") String titulo);
	
}
