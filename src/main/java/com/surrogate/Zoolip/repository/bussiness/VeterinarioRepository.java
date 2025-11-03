package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.VeterinarioDTO;
import com.surrogate.Zoolip.models.bussiness.Atencion.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    @Query("Select new com.surrogate.Zoolip.models.DTO.VeterinarioDTO(v.id_veterinario, v.nombre, v.id_institucion.nombre) from Veterinario v")
    List<VeterinarioDTO> findVeterinariosDTO();

    @Query("Select new com.surrogate.Zoolip.models.DTO.VeterinarioDTO(v.id_veterinario, v.nombre, v.id_institucion.nombre) from Veterinario v where v.id_veterinario = :id_veterinario")
    VeterinarioDTO findVeterinarioDTO(Long id_veterinario);
}
