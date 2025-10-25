package com.surrogate.Zoolip.models.bussiness.Atencion;

import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;

import jakarta.persistence.OneToOne;

import java.io.Serializable;

@Embeddable
public class AtiendeId implements Serializable {
    @OneToOne(fetch = FetchType.LAZY)
    private Veterinario id_veterinario;
    @OneToOne(fetch = FetchType.LAZY)
    private Diagnostico id_diagnostico;
    @OneToOne(fetch = FetchType.LAZY)
    private Mascota id_mascota;

}
