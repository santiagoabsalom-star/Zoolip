package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.AtiendeDTO;
import com.surrogate.Zoolip.models.bussiness.Atencion.Atiende;
import com.surrogate.Zoolip.models.bussiness.Atencion.AtiendeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtiendeRepository extends JpaRepository<Atiende, AtiendeId> {
    @Query("Select new com.surrogate.Zoolip.models.DTO.AtiendeDTO(a.diagnostico.id_diagnostico, a.mascota.id, a.veterinario.id_veterinario, a.fecha_inicio, a.motivo_consulta, a.fecha_final) from Atiende a")
    List<AtiendeDTO> getAtenciones();
    @Query("Select new com.surrogate.Zoolip.models.DTO.AtiendeDTO(a.diagnostico.id_diagnostico, a.mascota.id, a.veterinario.id_veterinario, a.fecha_inicio, a.motivo_consulta, a.fecha_final) from Atiende a where a.id=:id")
    AtiendeDTO getAtencionesById(AtiendeId id);
}
