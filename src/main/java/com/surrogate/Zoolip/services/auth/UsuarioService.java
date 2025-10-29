package com.surrogate.Zoolip.services.auth;

import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;

import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    @Transactional(readOnly = true)
    public List<UsuarioDto> findAvailableUsersInitialized() {
        List<UsuarioDto> users = usuarioRepository.findAvailableUserDtos();
        users.forEach(u -> {
            log.info(u.nombre());
        });
        return users;
    }
    public UsuarioDto me(String token){
    if(token == null){
        return null;
    }
        return usuarioRepository.getUserById( (long) jwtService.extractId(token));

    }
    public List<UsuarioDto> findAllByEmail(String token){
        if(token == null){
            return null;
        }
        return usuarioRepository.findAllByEmail(jwtService.extractEmail(token));
    }
    public Optional<Usuario> findById(Long uid) {
        return usuarioRepository.findById(uid);
    }
}
