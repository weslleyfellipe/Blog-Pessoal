package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;

//objetivo é informar para o Security os dados de acesso a API

public class UserDetailsIlmpl implements UserDetails {
     
	//Estamos informando Userdetails que estamos usando a versão 1
	private static final long serialVersionUID = 1L;

	private String userName;
	private String password;
	
	//classe seguranção que tras todas as autorizações de acesso que o usuario tem 
	private List<GrantedAuthority> authorities;

	public UserDetailsIlmpl(Usuario user) {
		this.userName = user.getUsuario();
		this.password = user.getSenha();
	}

	public UserDetailsIlmpl() {	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        //autorização de acesso do usuario
		return authorities;
	}

	@Override
	public String getPassword() {
        //
		return password;
	}

	@Override
	public String getUsername() {

		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		//se a conta não expirou ele acessa - true
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// se a conta não está bloqueada ele acessa - true
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		//se a crendencial não estiver expirada ele acessa - true
		return true;
	}

	@Override
	public boolean isEnabled() {
		//se o usuario está habilitado ele tem passagem - true
		return true;
	}

}