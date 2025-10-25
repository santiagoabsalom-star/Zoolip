package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.bussiness.Atencion.Atiende;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtiendeRepository extends JpaRepository<Atiende, Long> {
}
