package com.example.FinalSpringBoot.security;

import com.example.FinalSpringBoot.model.Usuario;
import com.example.FinalSpringBoot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Spring Security llama a este método cuando un usuario intenta hacer login
     * @param username Username ingresado en el formulario
     * @return UserDetails con la info del usuario y sus permisos
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());
       
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())  // Ya está encriptada con BCrypt
                .authorities(Collections.singletonList(authority))
                .build();
    }
}
