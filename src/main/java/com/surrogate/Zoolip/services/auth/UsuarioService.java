package com.surrogate.Zoolip.services.auth;

import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service

public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> findAvailableUsersInitialized() {
        List<UsuarioDto> users = repo.findAvailableUserDtos();
        users.forEach(u -> {
            log.info(u.nombre());
        });
        return users;
    }

    public Optional<Usuario> findById(Long uid) {
        return repo.findById(uid);
    }
}
