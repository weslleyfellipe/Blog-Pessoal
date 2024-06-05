package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity // ckasse vau se tornar uma entidade do banco de dados
@Table(name="tb_postagens")//nomeando a tabela no banco de dados de tb_postagem

public class Postagem {

	@Id // torna o campo uma chave primaria no banco de dados 
	@GeneratedValue(strategy=GenerationType.IDENTITY)// tornando a chave primaria auto increment 
	private Long id;
	
	
	@NotBlank(message = "O atributo Titulo é obrigatorio!")// validation - validar nosso atributo NN e tambem não vazio
   @Size(min= 5, max = 100 , message = "O atributo  Titulo deve ter no minimo 5 caracteres e no maximo 100.")
	private String titulo;
	
	
	
	@NotBlank(message = "O atributo texto é Obrigatorio!")
	@Size(min = 10 , max = 1000, message = "O atributo texto deve conter no minimo e no maximo 1000 caracteres.")
		private String texto;
	
	@UpdateTimestamp //vai pegar a hora do banco de dados , e colocar automaticamente no campo 
	private LocalDateTime data;//LocalDate time é um tipo de dado que já vem com o padrão de data configurado 

	@ManyToOne
	@JsonIgnoreProperties("postagem")
	private Tema tema;
	
	
	@ManyToOne
	@JsonIgnoreProperties("postagem")
	
	private Usuario usuario;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}
	
    

}
