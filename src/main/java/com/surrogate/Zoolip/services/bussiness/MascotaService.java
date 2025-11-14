package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.MascotaDTO;
import com.surrogate.Zoolip.models.DTO.SolicitudAdopcionDTO;
import com.surrogate.Zoolip.models.bussiness.Mascota.EstadoAdopcion;
import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import com.surrogate.Zoolip.models.bussiness.Mascota.SolicitudAdopcion.EstadoSolicitud;
import com.surrogate.Zoolip.models.bussiness.Mascota.SolicitudAdopcion.SolicitudAdopcion;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.login.UserPrincipal;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.InstitucionRepository;
import com.surrogate.Zoolip.repository.bussiness.MascotaRepository;
import com.surrogate.Zoolip.repository.bussiness.SolicitudAdopcionRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MascotaService {
    private final MascotaRepository mascotaRepository;
    private final InstitucionRepository institucionRepository;
    private final String error;
    private final String success;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudAdopcionRepository solicitudAdopcionRepository;


    public Response agregarMascota(Mascota mascota) {
        Response response = verificarMascota(mascota);
        if (response.getHttpCode() != 200) {
            return response;
        }
        mascotaRepository.save(mascota);
        return new Response(success, 200, "La mascota ha sido agregada");
    }

    public Response actualizarMascota(Mascota mascota) {
        Response response = verificarMascota(mascota);
        if (response.getHttpCode() != 200) {
            return response;
        }
        mascotaRepository.save(mascota);
        return new Response(success, 200, "La mascota ha sido actualizada");
    }

    public Response eliminarMascota(Long id_mascota) {
        if (!mascotaRepository.existsById(id_mascota)) {
            return new Response(error, 404, "La mascota no existe");
        }
        mascotaRepository.deleteById(id_mascota);
        return new Response(success, 200, "La mascota eliminada");

    }

    public MascotaDTO buscarById(Long id_mascota) {
        if (!mascotaRepository.existsById(id_mascota)) {
            return null;
        }
        return mascotaRepository.findMascotaDTO(id_mascota);
    }
    public Response solicitudAdopcion(SolicitudAdopcion solicitudAdopcion){
        if(usuarioRepository.existsById(getIdUsuario())){
            solicitudAdopcion.setId_adoptante(usuarioRepository.findById(getIdUsuario()).orElse(null));

        }
        if(solicitudAdopcion.getEstadoSolicitud()!=null){
            return new Response(error, 409, "No puedes declarar un estado porque ya se hace automaticamente");
        }
        if(Objects.equals(getRoleUsuario(), "ROLE_SYSTEM") || Objects.equals(getRoleUsuario(), "ROLE_ADMIN") || Objects.equals(getRoleUsuario(), "ROLE_ADMINISTRADOR"))
        {
            return new Response(error, 409, "Solo usuarios comunes pueden solicitar adopcion");
        }
        if(solicitudAdopcion.getFecha_finalizado()!=null && solicitudAdopcion.getFecha_inicio()!=null){
            return new Response(error, 409, "No puedes establecer fecha finalizada ni fecha inicio en la solicitud");
        }
        if(solicitudAdopcionRepository.existsByMascotaId(solicitudAdopcion.getMascota().getId())){
            return new Response(error, 409, "La mascota ya ha sido solicitada");
        }

        solicitudAdopcion.setFecha_inicio(LocalDateTime.now());
        if(!mascotaRepository.existsById(solicitudAdopcion.getMascota().getId())) {
            return new Response(error, 404, "La mascota no existe");
         }
        solicitudAdopcion.setEstadoSolicitud(EstadoSolicitud.SOLICITADO);
        Mascota mascota= mascotaRepository.findById(solicitudAdopcion.getMascota().getId()).orElse(null);
        assert mascota != null;
        mascota.setEstadoAdopcion(EstadoAdopcion.EN_PROCESO);

        mascotaRepository.saveAndFlush(mascota);
        solicitudAdopcionRepository.save(solicitudAdopcion);
        return new Response(success, 200, "La mascota ha sido solicitada");
    }
    public Response completarAdopcion(SolicitudAdopcion solicitudAdopcion) {
        if (!solicitudAdopcionRepository.existsById(solicitudAdopcion.getId_solicitud_adopcion())) {

            return new Response(error, 404, "La solicitud no existe");

        }
        if(isSolicitudCompleted(solicitudAdopcion.getId_solicitud_adopcion())){
            return new Response(error, 409, "La solicitud ya esta completada");
        }





        if (solicitudAdopcion.getFecha_finalizado() != null) {
            return new Response(error, 409, "No puedes establecer fecha finalizada ni fecha inicio en la solicitud");
        }
        solicitudAdopcion.setFecha_finalizado(LocalDateTime.now());

        SolicitudAdopcion solicitudAdopcionCompletada = solicitudAdopcionRepository.findById(solicitudAdopcion.getId_solicitud_adopcion()).orElse(null);
        assert solicitudAdopcionCompletada != null;
        solicitudAdopcionCompletada.setEstadoSolicitud(solicitudAdopcion.getEstadoSolicitud());

        if(!usuarioRepository.existsById(solicitudAdopcionCompletada.getId_adoptante().getId())) {
            return new Response(error, 409, "El usuario no existe");
        }
        if (!mascotaRepository.existsById(solicitudAdopcionCompletada.getMascota().getId())) {
            return new Response(error, 404, "La mascota no existe");
        }


        Mascota mascota = mascotaRepository.findById(solicitudAdopcionCompletada.getMascota().getId()).orElse(null);
        assert mascota != null;

        if (solicitudAdopcionCompletada.getEstadoSolicitud() == EstadoSolicitud.APROBADO) {
            mascota.setEstadoAdopcion(EstadoAdopcion.ADOPTADO);
        }
        mascota.setEstadoAdopcion(EstadoAdopcion.DISPONIBLE);
        mascotaRepository.saveAndFlush(mascota);

        Usuario usuario = usuarioRepository.findById(solicitudAdopcionCompletada.getId_adoptante().getId()).orElse(null);
        assert usuario != null;
        usuario.setRol("ADOPTANTE");
        usuarioRepository.saveAndFlush(usuario);
        solicitudAdopcionRepository.saveAndFlush(solicitudAdopcionCompletada);
        return new Response(success, 200, "Proceso hecho con exito");


    }


    public List<SolicitudAdopcionDTO> getAllSolicitudes(){
        return solicitudAdopcionRepository.findAllDTOs();
    }
public SolicitudAdopcionDTO getSolicitudAdopcionById(Long id) {
        return solicitudAdopcionRepository.findDTOById(id);
}





    @Cacheable(cacheNames = "mascotas", unless = "#result == null || #result.isEmpty()")
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

        String rolUsuario = getRoleUsuario();
        if((!(rolUsuario.equals("ROLE_ADMIN") || rolUsuario.equals("ROLE_ADOPTANTE") || rolUsuario.equals("ROLE_ADMINISTRADOR"))) && mascota.getNombre()!=null) {

            return new Response(error, 403, "No tienes permiso para asignar nombre a la mascota");

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
            return new Response(success, 200, "La institucion ha sido encontrada y encontrada");

        }

        log.info("La institucion no existe");
        return new Response(error, 404, "Institucion no encontrada");


    }
    public List<MascotaDTO> buscarMascotaByInstitucionId(Long id_institucion){
        return mascotaRepository.findMascotasByInstitucionId(id_institucion);
    }
    public List<MascotaDTO> buscarMisMascotasDTO() {
        return mascotaRepository.findMisMascotasDTO(getIdUsuario());

    }
    private Long getIdUsuario(){
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId();
    }

    private String getRoleUsuario(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
    }
    private boolean isSolicitudCompleted(long id_solicitud){
        SolicitudAdopcion solicitudAdopcion= solicitudAdopcionRepository.findById(id_solicitud).orElse(null);
        assert solicitudAdopcion != null;

            return solicitudAdopcion.getEstadoSolicitud() == EstadoSolicitud.APROBADO || solicitudAdopcion.getEstadoSolicitud() == EstadoSolicitud.RECHAZADO;



    }



}
