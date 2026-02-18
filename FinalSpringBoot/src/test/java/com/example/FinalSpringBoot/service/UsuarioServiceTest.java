package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.enums.Rol;
import com.example.FinalSpringBoot.model.Perfil;
import com.example.FinalSpringBoot.model.Usuario;
import com.example.FinalSpringBoot.repository.PerfilRepository;
import com.example.FinalSpringBoot.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioTest;
    private Perfil perfilTest;

    @BeforeEach
    void setUp() {
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setUsername("testuser");
        usuarioTest.setEmail("test@test.com");
        usuarioTest.setPassword("encodedPassword");
        usuarioTest.setRol(Rol.USER);

        perfilTest = new Perfil();
        perfilTest.setId(1L);
        perfilTest.setNombre("Test");
        perfilTest.setApellido("User");
        perfilTest.setPais("España");
        perfilTest.setUsuario(usuarioTest);

        usuarioTest.setPerfil(perfilTest);
    }

    @Test
    void testRegistrarUsuario_Exitoso() {
        // Given
        String username = "newuser";
        String email = "new@test.com";
        String password = "Password123!";
        String nombre = "New";
        String apellido = "User";
        String pais = "España";

        when(usuarioRepository.existsByUsername(username)).thenReturn(false);
        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(perfilRepository.save(any(Perfil.class))).thenAnswer(invocation -> {
            Perfil p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        Usuario resultado = usuarioService.registrarUsuario(username, email, password, nombre, apellido, pais);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo(username);
        assertThat(resultado.getEmail()).isEqualTo(email);
        assertThat(resultado.getRol()).isEqualTo(Rol.USER);
        assertThat(resultado.getPerfil()).isNotNull();
        assertThat(resultado.getPerfil().getNombre()).isEqualTo(nombre);
        assertThat(resultado.getPerfil().getApellido()).isEqualTo(apellido);
        assertThat(resultado.getPerfil().getPais()).isEqualTo(pais);

        verify(passwordEncoder, times(1)).encode(password);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    @Test
    void testRegistrarUsuario_UsernameDuplicado() {
        // Given
        String username = "existinguser";
        when(usuarioRepository.existsByUsername(username)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> usuarioService.registrarUsuario(
                username, "new@test.com", "Pass123!", "Test", "User", "España"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El username ya está en uso");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegistrarUsuario_EmailDuplicado() {
        // Given
        String email = "existing@test.com";
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(email)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> usuarioService.registrarUsuario(
                "newuser", email, "Pass123!", "Test", "User", "España"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El email ya está registrado");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testObtenerUsuarioPorUsername_Encontrado() {
        // Given
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuarioTest));

        // When
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorUsername("testuser");

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsername()).isEqualTo("testuser");
        verify(usuarioRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testObtenerUsuarioPorUsername_NoEncontrado() {
        // Given
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        // When
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorUsername("noexiste");

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    void testObtenerUsuarioPorEmail_Encontrado() {
        // Given
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuarioTest));

        // When
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorEmail("test@test.com");

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void testActualizarPerfil_PerfilExistente() {
        // Given
        Long usuarioId = 1L;
        String nuevoNombre = "Updated";
        String nuevoApellido = "Name";
        String nuevoPais = "Francia";

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioTest));
        when(perfilRepository.save(any(Perfil.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Perfil resultado = usuarioService.actualizarPerfil(usuarioId, nuevoNombre, nuevoApellido, nuevoPais);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo(nuevoNombre);
        assertThat(resultado.getApellido()).isEqualTo(nuevoApellido);
        assertThat(resultado.getPais()).isEqualTo(nuevoPais);
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    @Test
    void testActualizarPerfil_UsuarioSinPerfil() {
        // Given
        Usuario usuarioSinPerfil = new Usuario();
        usuarioSinPerfil.setId(2L);
        usuarioSinPerfil.setUsername("sinperfil");
        usuarioSinPerfil.setEmail("sinperfil@test.com");
        usuarioSinPerfil.setPerfil(null);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioSinPerfil));
        when(perfilRepository.save(any(Perfil.class))).thenAnswer(invocation -> {
            Perfil p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        // When
        Perfil resultado = usuarioService.actualizarPerfil(2L, "Nuevo", "Perfil", "México");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Nuevo");
        assertThat(resultado.getApellido()).isEqualTo("Perfil");
        assertThat(resultado.getPais()).isEqualTo("México");
        assertThat(resultado.getUsuario()).isEqualTo(usuarioSinPerfil);
        verify(perfilRepository, times(1)).save(any(Perfil.class));
    }

    @Test
    void testActualizarPerfil_UsuarioNoEncontrado() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> usuarioService.actualizarPerfil(999L, "Test", "User", "España"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(perfilRepository, never()).save(any(Perfil.class));
    }

    @Test
    void testExisteUsername_True() {
        // Given
        when(usuarioRepository.existsByUsername("existinguser")).thenReturn(true);

        // When
        boolean resultado = usuarioService.existeUsername("existinguser");

        // Then
        assertThat(resultado).isTrue();
    }

    @Test
    void testExisteUsername_False() {
        // Given
        when(usuarioRepository.existsByUsername("newuser")).thenReturn(false);

        // When
        boolean resultado = usuarioService.existeUsername("newuser");

        // Then
        assertThat(resultado).isFalse();
    }

    @Test
    void testExisteEmail_True() {
        // Given
        when(usuarioRepository.existsByEmail("existing@test.com")).thenReturn(true);

        // When
        boolean resultado = usuarioService.existeEmail("existing@test.com");

        // Then
        assertThat(resultado).isTrue();
    }

    @Test
    void testExisteEmail_False() {
        // Given
        when(usuarioRepository.existsByEmail("new@test.com")).thenReturn(false);

        // When
        boolean resultado = usuarioService.existeEmail("new@test.com");

        // Then
        assertThat(resultado).isFalse();
    }
}
