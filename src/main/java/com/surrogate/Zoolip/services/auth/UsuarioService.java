package com.surrogate.Zoolip.services.auth;

import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JWTService jwtService;


    @Transactional
    public Response actualizar(Usuario usuario) {
        if (usuarioRepository.existsById(usuario.getId())) {
            usuarioRepository.saveAndFlush(usuario);
            return new Response("Usuario actualizado correctamente", 200, "Success");
        } else {
            return new Response("El usuario no existe", 404, "Error");
        }
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> findAvailableUsersInitialized() {

        return usuarioRepository.findAvailableUserDtos();
    }

    @Cacheable(cacheNames="meCache", key="#token")
    public Optional<UsuarioDto> me(String token) {
        if (token == null) {
            return Optional.empty();
        }
        if(!jwtService.isTokenStored(token)) {
            return Optional.of(403).map(m -> null);
        }
        return usuarioRepository.getUserById((long) jwtService.extractId(token));

    }
    @Cacheable(cacheNames="usersByEmailCache", key="#token")
    public List<UsuarioDto> findAllByEmail(String token) {
        if (token == null) {
            return null;
        }
        return usuarioRepository.findAllByEmail(jwtService.extractEmail(token));
    }

    public Optional<Usuario> findById(Long uid) {
        return usuarioRepository.findById(uid);
    }
}
