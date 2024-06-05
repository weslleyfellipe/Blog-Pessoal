package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // Indica que esta classe é uma classe de configuração do Spring
@EnableWebSecurity  // Habilita a configuração de segurança web do Spring
public class BasicSecurityConfig {

    @Autowired  // Injeta o filtro de autenticação JWT
    private JwtAuthFilter authFilter;

    @Bean  // Define um bean para o serviço de detalhes do usuário
    UserDetailsService userDetailsService() { // Ajustes de usuario e senha 
        return new UserDetailsServiceImpl();  // Retorna uma implementação do serviço de detalhes do usuário
    }

    @Bean  // Define um bean para o codificador de senhas
    PasswordEncoder passwordEncoder() { //criptografia da senha
        return new BCryptPasswordEncoder();  // Retorna uma instância de BCryptPasswordEncoder
    }

    @Bean  // Define um bean para o provedor de autenticação
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();  // Cria um provedor de autenticação baseado em DAO
        authenticationProvider.setUserDetailsService(userDetailsService());  // Configura o serviço de detalhes do usuário
        authenticationProvider.setPasswordEncoder(passwordEncoder());  // Configura o codificador de senhas
        return authenticationProvider;  // Retorna o provedor de autenticação configurado
    }

    @Bean  // Define um bean para o gerenciador de autenticação
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();  // Retorna o gerenciador de autenticação configurado
    }

    @Bean  // Define um bean para a cadeia de filtros de segurança
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(management -> management
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Configura a política de criação de sessão como sem estado (stateless)
                .csrf(csrf -> csrf.disable())  // Desabilita a proteção contra CSRF
                .cors(withDefaults());  // Configura as políticas CORS com valores padrão

        http
            .authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/usuarios/logar").permitAll()  // Permite acesso público à rota "/usuarios/logar"
                    .requestMatchers("/usuarios/cadastrar").permitAll()  // Permite acesso público à rota "/usuarios/cadastrar"
                    .requestMatchers("/error/**").permitAll()  // Permite acesso público a rotas de erro
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()  // Permite acesso público a requisições OPTIONS
                    .anyRequest().authenticated())  // Exige autenticação para todas as outras requisições
            .authenticationProvider(authenticationProvider())  // Configura o provedor de autenticação
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)  // Adiciona o filtro de autenticação JWT antes do filtro padrão de autenticação
            .httpBasic(withDefaults());  // Configura autenticação básica com valores padrão

        return http.build();  // Constrói a cadeia de filtros de segurança
    }
}
