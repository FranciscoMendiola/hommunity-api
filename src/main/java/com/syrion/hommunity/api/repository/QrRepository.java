package com.syrion.hommunity.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.syrion.hommunity.api.entity.QR;

public interface QrRepository extends JpaRepository<QR, Long> {

    @Query(value = "SELECT * FROM qr WHERE vigente = true", nativeQuery = true)
    List<QR> findByActiveStatus();

    QR findByIdInvitado(Long idInvitado);
}