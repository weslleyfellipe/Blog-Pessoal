package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

import jakarta.validation.Valid;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping("/usuarios") // Define a URL base para todos os endpoints deste controlador
@CrossOrigin(origins = "*", allowedHeaders = "*") // Permite solicitações de qualquer origem
public class UsuarioController {

    @Autowired // Injeta uma instância de UsuarioService
    private UsuarioService usuarioService;

    @Autowired // Injeta uma instância de UsuarioRepository
    private UsuarioRepository usuarioRepository;
    
    // Endpoint para obter todos os usuários
    @GetMapping("/all")
    public ResponseEntity<List<Usuario>> getAll() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // Endpoint para obter um usuário pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) {
        return usuarioRepository.findById(id)
            .map(resposta -> ResponseEntity.ok(resposta)) // Se o usuário for encontrado, retorna 200 OK com o usuário
            .orElse(ResponseEntity.notFound().build()); // Se não for encontrado, retorna 404 Not Found
    }
    
    // Endpoint para autenticar um usuário
    @PostMapping("/logar")
    public ResponseEntity<UsuarioLogin> autenticarUsuario(@RequestBody Optional<UsuarioLogin> usuarioLogin) {
        return usuarioService.autenticarUsuario(usuarioLogin)
                .map(resposta -> ResponseEntity.status(HttpStatus.OK).body(resposta)) // Se autenticado com sucesso, retorna 200 OK com os dados do usuário
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()); // Se falhar a autenticação, retorna 401 Unauthorized
    }
    
    // Endpoint para cadastrar um novo usuário
    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> postUsuario(@RequestBody @Valid Usuario usuario) {
        return usuarioService.cadastrarUsuario(usuario)
            .map(resposta -> ResponseEntity.status(HttpStatus.CREATED).body(resposta)) // Se o cadastro for bem-sucedido, retorna 201 Created com o novo usuário
            .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()); // Se o cadastro falhar, retorna 400 Bad Request
    }

    // Endpoint para atualizar um usuário existente
    @PutMapping("/atualizar")
    public ResponseEntity<Usuario> putUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.atualizarUsuario(usuario)
            .map(resposta -> ResponseEntity.status(HttpStatus.OK).body(resposta)) // Se a atualização for bem-sucedida, retorna 200 OK com o usuário atualizado
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Se o usuário não for encontrado, retorna 404 Not Found
    }
}
