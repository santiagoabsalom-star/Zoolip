package com.surrogate.Zoolip.models.bussiness.Atencion;

import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Diagnostico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_diagnostico", nullable = false)
    private Long id_diagnostico;
    @JoinColumn(name="id_mascota", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Mascota id_mascota;
    @Column(name="examen_fisico", nullable = false)
    private String examen_fisico;
}
