package com.surrogate.Zoolip.models.bussiness.Atencion;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.util.Objects;
@Getter
@Setter
@Embeddable
public class AtiendeId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "id_diagnostico")
    private Long idDiagnostico;

    @Column(name = "id_veterinario")
    private Long idVeterinario;

    @Column(name = "id_mascota")
    private Long idMascota;

    public AtiendeId() {}

    public AtiendeId(Long idDiagnostico, Long idVeterinario, Long idMascota) {
        this.idDiagnostico = idDiagnostico;
        this.idVeterinario = idVeterinario;
        this.idMascota = idMascota;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtiendeId)) return false;
        AtiendeId that = (AtiendeId) o;
        return Objects.equals(idDiagnostico, that.idDiagnostico) &&
                Objects.equals(idVeterinario, that.idVeterinario) &&
                Objects.equals(idMascota, that.idMascota);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDiagnostico, idVeterinario, idMascota);
    }
}
