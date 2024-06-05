package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service // Indica que esta classe é um serviço do Spring
public class UsuarioService {

    @Autowired // Injeta uma instância de UsuarioRepository
    private UsuarioRepository usuarioRepository;

    @Autowired // Injeta uma instância de JwtService
    private JwtService jwtService;

    @Autowired // Injeta uma instância de AuthenticationManager
    private AuthenticationManager authenticationManager;

    // Método para cadastrar um novo usuário
    public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

        // Verifica se o usuário já existe no banco de dados
        if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
            return Optional.empty(); // Se o usuário já existe, retorna um Optional vazio

        // Criptografa a senha do usuário
        usuario.setSenha(criptografarSenha(usuario.getSenha()));

        // Salva o usuário no banco de dados e retorna um Optional com o usuário salvo
        return Optional.of(usuarioRepository.save(usuario));
    }

    // Método para atualizar um usuário existente
    public Optional<Usuario> atualizarUsuario(Usuario usuario) {

        // Verifica se o usuário existe no banco de dados pelo ID
        if (usuarioRepository.findById(usuario.getId()).isPresent()) {

            // Busca o usuário pelo nome de usuário
            Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

            // Se encontrar um usuário com o mesmo nome e ID diferente, lança uma exceção
            if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != usuario.getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

            // Criptografa a nova senha do usuário
            usuario.setSenha(criptografarSenha(usuario.getSenha()));

            // Salva as alterações no banco de dados e retorna o usuário atualizado
            return Optional.ofNullable(usuarioRepository.save(usuario));
        }

        // Se o usuário não existe, retorna um Optional vazio
        return Optional.empty();
    }

    // Método para autenticar um usuário
    public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

        // Cria um objeto de autenticação com as credenciais do usuário
        var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha());

        // Autentica o usuário
        Authentication authentication = authenticationManager.authenticate(credenciais);

        // Se a autenticação foi bem-sucedida
        if (authentication.isAuthenticated()) {

            // Busca os dados do usuário pelo nome de usuário
            Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

            // Se o usuário for encontrado
            if (usuario.isPresent()) {

                // Preenche o objeto usuarioLogin com os dados do usuário encontrado
                usuarioLogin.get().setId(usuario.get().getId());
                usuarioLogin.get().setNome(usuario.get().getNome());
                usuarioLogin.get().setFoto(usuario.get().getFoto());
                usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario())); // Gera um token JWT
                usuarioLogin.get().setSenha(""); // Limpando a senha por segurança

                // Retorna o objeto usuarioLogin preenchido
                return usuarioLogin;
            }
        }

        // Se a autenticação falhar, retorna um Optional vazio
        return Optional.empty();
    }

    // Método privado para criptografar a senha
    private String criptografarSenha(String senha) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(senha); // método encoder sendo aplicado na senha .
    }

    // Método privado para gerar um token JWT
    private String gerarToken(String usuario) {  
        return "Bearer " + jwtService.generateToken(usuario);
    }
}
