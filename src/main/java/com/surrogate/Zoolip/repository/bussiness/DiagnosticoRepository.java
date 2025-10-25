package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.bussiness.Atencion.Diagnostico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
}
