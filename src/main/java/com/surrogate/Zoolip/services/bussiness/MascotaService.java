package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.MascotaDTO;
import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.InstitucionRepository;
import com.surrogate.Zoolip.repository.bussiness.MascotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MascotaService {
    private final MascotaRepository mascotaRepository;
    private final InstitucionRepository institucionRepository;
    private final String error;
    private final String success;
    public Response agregarMascota(Mascota mascota) {
        Response response = verificarMascota(mascota);
        if (response.getHttpCode()!=200) {
            return response;
        }
        mascotaRepository.save(mascota);
        return new Response(success,200, "La mascota ha sido agregado");
    }

    public Response actualizarMascota(Mascota mascota) {
        Response response = verificarMascota(mascota);
        if (response.getHttpCode()!=200) {
            return response;
        }
        mascotaRepository.save(mascota);
        return new Response(success,200, "La mascota ha sido actualizada");
    }

    public Response eliminarMascota(Long id_mascota) {
        if (!mascotaRepository.existsById(id_mascota)) {
            return new Response(error, 404, "La mascota no existe");
        }
        mascotaRepository.deleteById(id_mascota);
        return new Response(success,200, "La mascota eliminada");

    }

    public MascotaDTO buscarById(Long id_mascota) {
        if (!mascotaRepository.existsById(id_mascota)) {
            return null;
        }
        return mascotaRepository.findMascotaDTO(id_mascota);
    }

    public List<MascotaDTO> buscarMascotasDTO() {
        if (mascotaRepository.findAllMascotasDTO().isEmpty()) {
            return null;
        }
        return mascotaRepository.findAllMascotasDTO();
    }
    public List<Mascota> buscarMascotas() {
        return mascotaRepository.buscarMascotas();
    }


    private Response verificarMascota(Mascota mascota) {
        if (mascota.getTamanio() == null) {
            log.info("La mascota no tiene tamanio");
            return new Response(error, 400, "Tamanio no puede ser nulo");

        }

        if (mascota.getRaza() == null) {
            log.info("La mascota no tiene raza");
            return new Response(error, 400, "Raza no puede ser nulo");
        }
        if (mascota.getEdad() == null) {
            log.info("La mascota no tiene edad");
            return new Response(error, 400, "Edad no puede ser nulo");
        }
        if (mascota.getEstadoAdopcion() == null) {
            log.info("La mascota no tiene estado adopcion");
            return new Response(error, 400, "Estado adopcion no puede ser nulo");
        }
        if (mascota.getEstadoSalud() == null) {
            log.info("La mascota no tiene estado salud");
            return new Response(error, 400, "Estado salud no puede ser nulo");
        }
        if (mascota.getEspecie() == null) {
            log.info("La mascota no tiene especie");
            return new Response(error, 400, "Especie no puede ser nulo");
        }
        if (mascota.getId_institucion() != null && institucionRepository.existsById(mascota.getId_institucion().getId_institucion())) {
            mascota.setId_institucion(institucionRepository.findById(mascota.getId_institucion().getId_institucion()).orElse(null));
            mascotaRepository.save(mascota);
            log.info("La institucion se ha agregado");
            return new Response(success,200 ,"La institucion ha sido encontrada y encontrada");

        }

        log.info("La institucion no existe");
        return new Response(error, 404, "Institucion no encontrada");


    }


}
