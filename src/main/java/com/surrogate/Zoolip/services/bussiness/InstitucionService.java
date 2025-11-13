package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.InstitucionDTO;
import com.surrogate.Zoolip.models.DTO.InstitucionSolicitudDTO;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.bussiness.Institucion.InstitucionSolicitud;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.InstitucionRepository;
import com.surrogate.Zoolip.repository.bussiness.InstitucionSolicitudRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class InstitucionService {
    private final InstitucionRepository institucionRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstitucionSolicitudRepository institucionSolicitudRepository;
    private final String error;
    private final String success;

    public Response crear(Institucion institucion) {
        Response response = comprobarInst(institucion);
        if (response.getHttpCode() == 200) {
            Usuario usuario = usuarioRepository.findById(institucion.getId_usuario().getId()).orElse(null);
            institucion.setId_usuario(usuario);
            institucionRepository.save(institucion);
            return new Response(success, 200, "Institucion creada");
        }
        return response;
    }

    public Response actualizar(Institucion institucion) {
        Response response = comprobarInst(institucion);
        if (response.getHttpCode() == 200) {
            institucionRepository.save(institucion);
            return new Response(success, 200, "Institucion actualizada");
        }
        return response;
    }

    public Response eliminar(Long id_institucion) {
        try {
            if (!institucionRepository.existsById(id_institucion)) {
                return new Response(error, 404, "La institucion no existe");
            }
            institucionRepository.deleteById(id_institucion);
            return new Response(success, 200, "Institucion eliminada");
        } catch (Exception e) {
            log.error(e);
            return new Response(error, 500, "Error eliminando institucion");
        }
    }

    public InstitucionDTO getInstitucionById_Usuario(Long id_usuario) {
        if (!usuarioRepository.existsById(id_usuario)) {
            return null;
        }
        return institucionRepository.findInstitucionDTOById_usuario(id_usuario);
    }

    public Response crearSolicitud(InstitucionSolicitud institucionSolicitud) {
        if(institucionSolicitud.getNombre_institucion() == null || institucionSolicitud.getNombre_institucion().isEmpty()) {
            return new Response(error, 400, "Nombre institucion no puede ser nulo");
        }
        if(institucionSolicitud.getRazon_solicitud() == null || institucionSolicitud.getRazon_solicitud().isEmpty()) {
            return new Response(error, 400, "La razon solicitud no puede ser nula");
        }
        if(institucionSolicitud.getEmail_contacto() == null || institucionSolicitud.getEmail_contacto().isEmpty()) {
            return new Response(error, 400, "Email contacto no puede ser nula");

        }
        if(institucionSolicitud.getTelefono_contacto() == null || institucionSolicitud.getTelefono_contacto().isEmpty()) {
            return new Response(error, 400, "El telefono de contacto no puede ser nulo");

        }
            institucionSolicitudRepository.save(institucionSolicitud);


        return new Response(success, 200, "Solicitud hecha con exito, quedas a la espera");
    }
    public List<InstitucionDTO> getInstituciones() {
        return institucionRepository.findAllDTO();


    }

    public InstitucionDTO getInstitucion(Long id) {
        if (!institucionRepository.existsById(id)) {
            return null;
        }
        return institucionRepository.findInstitucionDTOById(id);
    }

    public List<Institucion> buscarInstituciones() {
        return institucionRepository.buscarInstituciones();
    }

    public Response comprobarInst(Institucion institucion) {
        if (institucion.getId_usuario() != null) {
            if (!usuarioRepository.existsById(institucion.getId_usuario().getId())) {
                log.info("Usuario no encontrado");
                return new Response(error, "Usuario no encontrado");
            }


            institucion.setId_usuario(usuarioRepository.findById(institucion.getId_usuario().getId()).orElse(null));

        }
        assert institucion.getId_usuario() != null;
        if (institucionRepository.findByU(institucion.getId_usuario().getId()) != null) {
            log.info("Este administrador ya administra una institucion");
            return new Response(error, 409, "Este administrador ya administra una institucion");
        }

        if (institucion.getNombre() == null) {

            log.info("El nombre no puede ser vacio");
            return new Response(error, 409, "El nombre no puede ser vacio");
        }
        if (institucion.getTipo() == null) {
            log.info("El tipo no puede ser vacio");
            return new Response(error, 409, "El tipo no puede ser vacio");
        }

        if (institucionRepository.existsById(institucion.getId_institucion())) {
            log.info("{} ya existe", institucion.getNombre());
            return new Response(error, 409, "La institucion ya existe");
        }
        if (institucionRepository.existsByNombre(institucion.getNombre())) {
            log.info("{} ya existe", institucion.getNombre());
            return new Response(error, 409, "La institucion ya existe");

        }
        if (!institucion.getId_usuario().getRol().equals("ADMINISTRADOR")) {
            log.info("El rol del usuario tiene que ser ADMINISTRADOR");
            return new Response(error, 409, "El rol del usuario tiene que ser administrador");

        }
        return new Response(success, 200, "Comprobacion sin errores");
    }

    public List<InstitucionSolicitudDTO> findAllSolicitudes() {
        return institucionSolicitudRepository.findAllInstitucionSolicitud();
    }
}
