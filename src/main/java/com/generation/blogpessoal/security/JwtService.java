package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	// Define uma constante final para a chave secreta usada na assinatura do JWT
	public static final String SECRET = "3dc53f37635e12d0403e6edf293c7c2a187b2b6df3dff854105bc76eda06b52c";

	// Método privado que retorna a chave de assinatura HMAC (usada para assinar o JWT)
	private Key getSignKey() {
	    // Decodifica a chave secreta a partir do formato BASE64
	    byte[] keyBytes = Decoders.BASE64.decode(SECRET);
	    // Cria uma chave HMAC-SHA a partir dos bytes decodificados
	    return Keys.hmacShaKeyFor(keyBytes);
	}

	// Método privado que extrai todas as claims (informações) do token JWT
	private Claims extractAllClaims(String token) {
	    // Configura o parser JWT com a chave de assinatura e extrai as claims do token
	    return Jwts.parserBuilder()
	            .setSigningKey(getSignKey()).build()
	            .parseClaimsJws(token).getBody();
	}

	// Método genérico que extrai uma claim específica do token usando uma função resolver de claims
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	    final Claims claims = extractAllClaims(token);
	    return claimsResolver.apply(claims);
	}

	// Método que extrai o nome de usuário (subject) do token JWT
	public String extractUsername(String token) {
	    return extractClaim(token, Claims::getSubject);
	}

	// Método que extrai a data de expiração do token JWT
	public Date extractExpiration(String token) {
	    return extractClaim(token, Claims::getExpiration);
	}

	// Método privado que verifica se o token JWT está expirado
	private Boolean isTokenExpired(String token) {
	    return extractExpiration(token).before(new Date());
	}

	// Método que valida se o token JWT é válido comparando o nome de usuário e verificando a expiração
	public Boolean validateToken(String token, UserDetails userDetails) {
	    final String username = extractUsername(token);
	    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// Método privado que cria um token JWT com claims personalizadas e um nome de usuário
	private String createToken(Map<String, Object> claims, String userName) {
	    // Cria um token JWT configurando claims, subject, data de emissão, data de expiração e assina com a chave HMAC-SHA
	    return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(userName)
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora de validade
	                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	// Método que gera um token JWT para um nome de usuário específico, sem claims adicionais
	public String generateToken(String userName) {
	    Map<String, Object> claims = new HashMap<>();
	    return createToken(claims, userName);
	}

}	