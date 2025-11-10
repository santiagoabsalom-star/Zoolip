package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.bussiness.Publicacion.PublicacionFavUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PublicacionFavUsuarioRepository extends JpaRepository<PublicacionFavUsuario, Long> {
    @Modifying
    @Query("INSERT INTO PublicacionFavUsuario (id_usuario, id_publicacion) VALUES (?1, ?2)")
    void addFavorite(Long id_usuario, Long id_publicacion);

    @Modifying
    @Query("DELETE FROM PublicacionFavUsuario pfu WHERE pfu.id_publicacion.id_publicacion = :idPublicacion AND pfu.id_usuario.id = :idUsuario")
    void removeFavorite(Long idPublicacion, Long idUsuario);
}
