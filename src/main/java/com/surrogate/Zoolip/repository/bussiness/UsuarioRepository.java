package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("SELECT u FROM Usuario u WHERE (u.nombre) = (:nombre)")
    Optional<Usuario> findByNombreas(String nombre);


    @Query("SELECT u.id FROM Usuario u WHERE LOWER(u.nombre) = LOWER(:nombre)")
    Long getIdUsuario(@Param("nombre") String nombre);

    @Query("SELECT new com.surrogate.Zoolip.models.DTO.UsuarioDto(u.id, u.nombre, u.rol, u.email) " +
            "FROM Usuario u " +
            "WHERE NOT EXISTS (SELECT i FROM Institucion i WHERE i.id_usuario = u)")
    List<UsuarioDto> findAvailableUserDtos();

    @Query("Select new com.surrogate.Zoolip.models.DTO.UsuarioDto(u.id, u.nombre, u.rol, u.email) from Usuario u where u.id =:id")
    Optional<UsuarioDto> getUserById(Long id);

    @Query("Select new com.surrogate.Zoolip.models.DTO.UsuarioDto(u.id,u.nombre,u.rol,u.email) from Usuario u where u.email=:email")
    List<UsuarioDto> findAllByEmail(String email);

    Usuario getUsuarioById(Long idUsuario);

    Usuario findByNombre(String nombre);

    boolean existsByNombre(String nombre);
    boolean existsByEmail(String email);
    @Query("Select new com.surrogate.Zoolip.models.DTO.UsuarioDto(u.id,u.nombre,u.rol,u.email) from Usuario u where u.id=:id_usuario")
    UsuarioDto findDTOById(long id_usuario);
    @Query("Select new com.surrogate.Zoolip.models.DTO.UsuarioDto(u.id,u.nombre,u.rol,u.email) from Usuario u where u.id >=:id_usuario ORDER BY u.id LIMIT 10  " )
    List<UsuarioDto> findAllDTosWithLimit(long id_usuario);

}