package com.surrogate.Zoolip.models.bussiness.Atencion;

import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Veterinario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_veterinario", nullable = false)
  private Long id_veterinario;
  @Column(name = "nombre", nullable = false)
  private String nombre;
  @JoinColumn(name = "id_institucion", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Institucion id_institucion;
}
