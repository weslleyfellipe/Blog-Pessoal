package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component  // Define esta classe como um componente do Spring
public class JwtAuthFilter extends OncePerRequestFilter {  // Extende OncePerRequestFilter para garantir que o filtro seja executado uma vez por requisição

    @Autowired
    private JwtService jwtService;  // Injeção do serviço JWT para manipular tokens

    @Autowired
    private UserDetailsServiceImpl userDetailsService;  // Injeção do serviço de detalhes do usuário para carregar dados do usuário

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        // Obtém o cabeçalho de autorização da requisição
        String authHeader = request.getHeader("Authorization");
        //inicio null
        String token = null;
        //inicio user
        String username = null;

        try {
            // Verifica se o cabeçalho está presente e começa com "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);  // Extrai o token JWT
                username = jwtService.extractUsername(token);  // Extrai o nome de usuário do token
            }

            // Se o nome de usuário foi extraído e o usuário ainda não está autenticado
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // Carrega os detalhes do usuário
                
                // Valida o token
                if (jwtService.validateToken(token, userDetails)) {
                    // Cria um token de autenticação
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  // Define os detalhes da autenticação
                    SecurityContextHolder.getContext().setAuthentication(authToken);  // Define o contexto de segurança
                }
            }
            filterChain.doFilter(request, response);  // Continua a cadeia de filtros

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
                | SignatureException | ResponseStatusException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());  // Define o status da resposta como 403 (Proibido) se houver um erro
            return;  // Interrompe o processamento
        }
    }
}
