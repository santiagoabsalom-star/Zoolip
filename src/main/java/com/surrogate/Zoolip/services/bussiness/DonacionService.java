package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.DonacionDTO;
import com.surrogate.Zoolip.models.bussiness.Donacion.Donacion;
import com.surrogate.Zoolip.models.bussiness.Institucion.Tipo;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.DonacionRepository;
import com.surrogate.Zoolip.repository.bussiness.InstitucionRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonacionService {
    private final DonacionRepository donacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstitucionRepository institucionRepository;
    private final String success;
    private final String error;

    public Response crearDonacion(Donacion donacion) {
        Response response = verifyDonacion(donacion);
        if (response.getHttpCode() == 200) {
            verifyUsuario(donacion);
            verifyInstitucion(donacion);
            donacionRepository.save(donacion);
            return response;


        }
        return response;

    }

    public Response actualizarDonacion(Donacion donacion) {
        Response response = verifyDonacion(donacion);
        if (response.getHttpCode() == 200) {
            donacionRepository.save(donacion);
        }
        return response;
    }

    public Response eliminarDonacion(Donacion donacion) {
        if (donacionRepository.existsById(donacion.getId_donacion())) {
            donacionRepository.deleteById(donacion.getId_donacion());
            return new Response(success, 200, "Donacion eliminada");

        }
        return new Response(error, 404, "Donacion no encontrada");

    }

    public List<DonacionDTO> obtenerDonaciones() {
        return donacionRepository.obtenerDonaciones();
    }

    public DonacionDTO obtenerDonacionById(long idDonacion) {
        return donacionRepository.obtenerDonacionById(idDonacion);
    }

    private Response verifyDonacion(Donacion donacion) {
        if (donacion.getId_institucion() == null) {

            return new Response(error, 404, "Institucion no puede ser nula");
        }
        if (donacion.getId_usuario() == null || !usuarioRepository.existsById(donacion.getId_usuario().getId())) {
            return new Response(error, 404, "Usuario no encontrado");
        }
        if (!institucionRepository.existsById(donacion.getId_institucion().getId_institucion())) {

            return new Response(error, 404, "Institucion no encontrado");

        }
        if (donacion.getId_institucion().getTipo().equals(Tipo.VETERINARIA)) {
            return new Response(error, 409, "Tipo de institucion tiene que ser refugio");
        }
        return new Response(success, 200, "Proceso completado");
    }

    private void verifyUsuario(Donacion donacion) {
        donacion.setId_usuario(usuarioRepository.findById(donacion.getId_usuario().getId()).orElse(null));


    }

    private void verifyInstitucion(Donacion donacion) {
        donacion.setId_institucion(institucionRepository.findById(donacion.getId_institucion().getId_institucion()).orElse(null));
    }
}
