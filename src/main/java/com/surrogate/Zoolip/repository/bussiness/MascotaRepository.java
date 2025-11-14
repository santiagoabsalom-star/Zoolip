package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.MascotaDTO;
import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {


    @Query("select m.id as id_Mascota, m.estadoAdopcion as estadoAdopcion, m.tamanio as tamanio, m.estadoSalud as estadoSalud, m.especie as especie, m.raza as raza, m.edad as edad, m.id_institucion.nombre as nombreInstitucion  from Mascota m where m.id =  :idMascota")
    MascotaDTO findMascotaDTO(Long idMascota);

    @Query("select m.id as id_Mascota, m.estadoAdopcion as estadoAdopcion, m.tamanio as tamanio, m.estadoSalud as estadoSalud, m.especie as especie, m.raza as raza, m.edad as edad, m.id_institucion.nombre as nombreInstitucion  from Mascota m")
    List<MascotaDTO> findAllMascotasDTO();
    @Query("select m.id as id_Mascota, m.estadoAdopcion as estadoAdopcion, m.tamanio as tamanio, m.estadoSalud as estadoSalud, m.especie as especie, m.raza as raza, m.edad as edad, m.id_institucion.nombre as nombreInstitucion  from Mascota m where m.id_institucion.id_institucion= :id_institucion")
    List<MascotaDTO> findMascotasByInstitucionId(long id_institucion);

    @Query("Select m from Mascota m LEFT JOIN FETCH m.id_institucion")
    @NotNull List<Mascota> buscarMascotas();
    @Query("select m.id as id_Mascota, m.estadoAdopcion as estadoAdopcion, m.tamanio as tamanio, m.estadoSalud as estadoSalud, m.especie as especie, m.raza as raza, m.edad as edad, m.id_institucion.nombre as nombreInstitucion  from Mascota m join SolicitudAdopcion sp where m.id=sp.mascota.id and sp.id_adoptante.id=:id_usuario")
    List<MascotaDTO> findMisMascotasDTO(Long id_usuario);

}
