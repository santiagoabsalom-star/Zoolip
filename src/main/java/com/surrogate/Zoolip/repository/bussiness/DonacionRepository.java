package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.DonacionDTO;
import com.surrogate.Zoolip.models.bussiness.Donacion.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    @Query("select new com.surrogate.Zoolip.models.DTO.DonacionDTO(u.id_donacion,u.monto, u.fecha_inicio, u.status, u.id_usuario.nombre, u.id_institucion.nombre ) from Donacion u")
     List<DonacionDTO> obtenerDonaciones();
    @Query("select new com.surrogate.Zoolip.models.DTO.DonacionDTO(u.id_donacion,u.monto, u.fecha_inicio, u.status, u.id_usuario.nombre, u.id_institucion.nombre ) from Donacion u where u.id_donacion=:idDonacion")
     DonacionDTO obtenerDonacionById(long idDonacion);
}
