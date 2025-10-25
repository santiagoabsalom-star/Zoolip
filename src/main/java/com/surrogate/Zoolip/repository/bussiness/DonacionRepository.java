package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.bussiness.Donacion.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
}
