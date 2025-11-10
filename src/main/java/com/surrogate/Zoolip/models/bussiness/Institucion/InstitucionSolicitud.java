package com.surrogate.Zoolip.models.bussiness.Institucion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class InstitucionSolicitud {
    @Id
    @Column(name="id_institucion_solicitud", nullable = false, unique = true)
    private Long id_institucion_solicitud;
    @Column(name="nombre_institucion", nullable = false)
    private String nombre_institucion;
    @Column(name="tipo", nullable = false)
    private Tipo tipo;
    @Column(name="estado", nullable = false)
    private Estado estado;
    @Column(name="email_contacto", nullable = false)
    private String email_contacto;
    @Column(name="telefono_contacto", nullable = false)
    private String telefono_contacto;
    @Column(name="razon_solicitud", nullable = false)
    private String razon_solicitud;

}
