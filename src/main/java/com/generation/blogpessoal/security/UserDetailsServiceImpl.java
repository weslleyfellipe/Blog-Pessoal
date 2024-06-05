package com.generation.blogpessoal.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	// objetivo da classe é verificar se o usuario existe no banco de dados
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// esse método vai validar se o user ja existe
		
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(username);
		
		//validando que o usuario exista 
		if(usuario.isPresent()) {
			return new UserDetailsIlmpl(usuario.get()); //
		}else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN); // ERROR 403// Probed  //
			
		}
	
	
	}
	
	
}
