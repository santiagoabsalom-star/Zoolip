package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.AtiendeDTO;
import com.surrogate.Zoolip.models.bussiness.Atencion.Atiende;
import com.surrogate.Zoolip.models.bussiness.Atencion.AtiendeId;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AtencionService {
    private final MascotaRepository mascotaRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final AtiendeRepository atiendeRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final String error;
    private final String success;

    public Response empezar(Atiende atiende){
        Response response= verificarAtencion(atiende);
        if(response.getHttpCode()==200){
            atiendeRepository.save(atiende);
            return response;
        }
        return response;
    }
    public Response actualizar(Atiende atiende){
        Response response= verificarAtencion(atiende);
        if(response.getHttpCode()==200){
            atiendeRepository.save(atiende);
        }
        return response;
    }
    public Response eliminar(Atiende atiende){
        if(atiendeRepository.existsById(atiende.getId())){
            atiendeRepository.deleteById(atiende.getId());
            return new Response(success, 200, "Proceso hecho con exito");
        }
        return new Response(error, 404, "No se pudo eliminar el atencion pq no existe");
    }
    public List<AtiendeDTO> obtenerTodas(){
        List<AtiendeDTO> atiendeDTOS= atiendeRepository.getAtenciones();
        if(atiendeDTOS.isEmpty()){
            return null;
        }
        return atiendeDTOS;

        }
        public AtiendeDTO obtenerPorId(AtiendeId atiendeId){
        return atiendeRepository.getAtencionesById(atiendeId);
        }

    private Response verificarAtencion(Atiende atiende){
    if(!mascotaRepository.existsById(atiende.getId().getIdMascota())){
        return new Response(error,404, "La mascota no existe");
    }
    if(!diagnosticoRepository.existsById(atiende.getId().getIdDiagnostico())){
        return new Response(error,404, "El diagnostico no existe");
    }
    if(!veterinarioRepository.existsById(atiende.getId().getIdVeterinario())){
        return new Response(error,404, "El veterinario no existe");
    }
    if(atiende.getFecha_inicio().isAfter(LocalDateTime.now())){
        return new Response(error, 409, "La fecha de inicio no puede ser despues que la fecha actual");
    }
    if(Thread.currentThread().getStackTrace()[1].getMethodName().equals("empezar")&& atiende.getFecha_final()!=null){
        return new Response(error, 409, "La fecha final tiene que ser establecida en el metodo completar malparido mongolico como vuelvas a hacer eso te voy a borrar la cuenta");

    }
    return new Response(success, 200, "Proceso hecho con exito");
    }
    public Response completar(Atiende atiende){
        Response response = verificarAtencion(atiende);
        if(response.getHttpCode()==200){
            atiende.setFecha_inicio(LocalDateTime.now());
            atiendeRepository.save(atiende);
            return response;
        }
        return response;
    }

}
