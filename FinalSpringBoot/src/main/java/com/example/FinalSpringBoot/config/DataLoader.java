package com.example.FinalSpringBoot.config;

import com.example.FinalSpringBoot.enums.Rol;
import com.example.FinalSpringBoot.model.Usuario;
import com.example.FinalSpringBoot.repository.UsuarioRepository;
import com.example.FinalSpringBoot.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final PlanService planService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

   
    @Override
    public void run(String... args) {
        log.info("=== Iniciando carga de datos iniciales ===");

        
        planService.inicializarPlanes();
        log.info("Planes inicializados correctamente");

        
        crearAdminSiNoExiste();

        log.info("=== Carga de datos iniciales completada ===");
    }

   
    private void crearAdminSiNoExiste() {
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setEmail("admin@saas.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);

            usuarioRepository.save(admin);
            
            log.info("========================================");
            log.info("Usuario administrador creado:");
            log.info("  Username: admin");
            log.info("  Password: admin123");
            log.info("========================================");
        } else {
            log.info("Usuario administrador ya existe");
        }
    }
}
