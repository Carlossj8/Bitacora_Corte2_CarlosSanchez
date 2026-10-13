package com.labrasaviva.controller;

import com.labrasaviva.dto.request.LoginRequestDTO;
import com.labrasaviva.dto.request.RegistroRequestDTO;
import com.labrasaviva.dto.response.TokenResponseDTO;
import com.labrasaviva.exception.UsuarioYaExisteException;
import com.labrasaviva.model.entity.UsuarioEntity;
import com.labrasaviva.repository.UsuarioRepository;
import com.labrasaviva.security.JwtUtil;
import com.labrasaviva.service.EventoAuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints públicos para login, registro y obtención de token JWT")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventoAuditoriaService eventoAuditoriaService;

    @PostConstruct
    public void inicializarUsuarios() {
        if (usuarioRepository.count() == 0) {
            crearUsuarioSiNoExiste("admin@labrasaviva.com", "admin123", "Administrador Principal", "ROLE_ADMIN");
            crearUsuarioSiNoExiste("mesero@labrasaviva.com", "mesero123", "Mesero de Sala", "ROLE_MESERO");
            crearUsuarioSiNoExiste("cocina@labrasaviva.com", "cocina123", "Parrillero Jefe", "ROLE_COCINA");
            crearUsuarioSiNoExiste("cajero@labrasaviva.com", "cajero123", "Cajero de Turno", "ROLE_CAJERO");
            crearUsuarioSiNoExiste("cliente@labrasaviva.com", "cliente123", "Comensal Registrado", "ROLE_CLIENTE");
            crearUsuarioSiNoExiste("parqueadero@labrasaviva.com", "parqueadero123", "Operador de Parqueadero", "ROLE_PARQUEADERO");
            log.info("Usuarios iniciales con roles precargados en base de datos.");
        }
    }

    private void crearUsuarioSiNoExiste(String email, String rawPassword, String nombre, String rol) {
        if (!usuarioRepository.existsByEmail(email)) {
            usuarioRepository.save(UsuarioEntity.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .nombreCompleto(nombre)
                    .rol(rol)
                    .build());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("Intento de inicio de sesión para: {}", dto.getEmail());

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();
        String rol = user.getAuthorities().iterator().next().getAuthority();
        String token = jwtUtil.generateToken(user.getUsername(), rol);

        eventoAuditoriaService.registrarEvento(
                "LOGIN_EXITOSO",
                "Usuario",
                0L,
                "Inicio de sesión exitoso de " + user.getUsername(),
                user.getUsername(),
                Map.of("rol", rol)
        );

        return ResponseEntity.ok(TokenResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .email(user.getUsername())
                .rol(rol)
                .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<TokenResponseDTO> register(@Valid @RequestBody RegistroRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new UsuarioYaExisteException("Ya existe un usuario registrado con el email: " + dto.getEmail());
        }

        String rol = dto.getRol().toUpperCase();
        if (!rol.startsWith("ROLE_")) {
            rol = "ROLE_" + rol;
        }

        UsuarioEntity entity = UsuarioEntity.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombreCompleto(dto.getNombreCompleto())
                .rol(rol)
                .build();

        usuarioRepository.save(entity);
        log.info("Nuevo usuario registrado: {} con rol: {}", entity.getEmail(), rol);

        String token = jwtUtil.generateToken(entity.getEmail(), rol);

        return ResponseEntity.status(HttpStatus.CREATED).body(TokenResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .email(entity.getEmail())
                .rol(rol)
                .build());
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener información del usuario autenticado actual")
    public ResponseEntity<Map<String, Object>> perfil(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(Map.of(
                "email", userDetails.getUsername(),
                "roles", userDetails.getAuthorities()
        ));
    }
}
