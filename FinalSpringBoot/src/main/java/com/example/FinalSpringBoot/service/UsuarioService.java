package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.enums.Rol;
import com.example.FinalSpringBoot.model.Perfil;
import com.example.FinalSpringBoot.model.Usuario;
import com.example.FinalSpringBoot.repository.PerfilRepository;
import com.example.FinalSpringBoot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    
    @Transactional
    public Usuario registrarUsuario(String username, String email, String password, 
                                     String nombre, String apellido, String pais) {
        
        if (existeUsername(username)) {
            throw new RuntimeException("El username ya está en uso");
        }
        if (existeEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Rol.USER);  
        
        usuario = usuarioRepository.save(usuario);

        Perfil perfil = new Perfil();
        perfil.setNombre(nombre);
        perfil.setApellido(apellido);
        perfil.setPais(pais);
        perfil.setUsuario(usuario);
        
        perfilRepository.save(perfil);
        
        usuario.setPerfil(perfil);
        
        return usuario;
    }

    
    @Transactional
    @SuppressWarnings("null")
    public Perfil actualizarPerfil(Long usuarioId, String nombre, String apellido, String pais) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Perfil perfil = usuario.getPerfil();
        if (perfil == null) {
            perfil = new Perfil();
            perfil.setUsuario(usuario);
        }
        
        perfil.setNombre(nombre);
        perfil.setApellido(apellido);
        perfil.setPais(pais);
        
        return perfilRepository.save(perfil);
    }

    @Transactional
    @SuppressWarnings("null")
    public void eliminarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Protección: No permitir eliminar usuarios con rol ADMIN
        if (usuario.getRol() == Rol.ADMIN) {
            throw new RuntimeException("No se puede eliminar un usuario administrador");
        }
        
        usuarioRepository.delete(usuario);
    }

    
    @Transactional
    @SuppressWarnings("null")
    public void cambiarPassword(Long usuarioId, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }
}
