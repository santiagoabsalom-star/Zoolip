package com.surrogate.Zoolip.services.auth;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.login.UserPrincipal;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import com.surrogate.Zoolip.utils.UserDetailsServiceWithId;
import com.surrogate.Zoolip.utils.UserDetailsWithId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUsrDtlsService implements UserDetailsServiceWithId {

    private final UsuarioRepository usuariorepository;

    public MyUsrDtlsService(UsuarioRepository usuariorepository) {
        this.usuariorepository = usuariorepository;
    }

    /*
     * Este metodo es el que se encarga de cargar el usuario desde la base de datos
     * y devolverlo a Spring Security
     * */
    @Override
    @Cacheable(value = "userDetails", key = "#username")
    public UserDetailsWithId loadUserByUsername(String username) throws UsernameNotFoundException {
        long startTime = System.currentTimeMillis();
        Usuario usuario = usuariorepository.findByNombre(username);
        long endTime = System.currentTimeMillis();
        System.out.println("Tiempo de busqueda de usuario: " + (endTime - startTime) + "ms");

        // Si no se encuentra el usuario, lanzamos una fuckin excepción

        if (usuario == null) {
            throw new UsernameNotFoundException("Nombre no encontrado: " + username);
        }
        return new UserPrincipal(usuario);
    }
}
