package com.syrion.hommunity.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.syrion.hommunity.api.entity.QR;
import org.springframework.data.repository.query.Param;

public interface QrRepository extends JpaRepository<QR, Long> {
    
    @Query(value = "SELECT * FROM qr WHERE vigente = true", nativeQuery = true)
    List<QR> findByActiveStatus();

    @Query("SELECT q.codigo FROM QR q WHERE q.idUsuario = :idUsuario AND q.idInvitado IS NULL")
    String findCodigoByIdUsuarioAndIdInvitadoIsNull(@Param("idUsuario") Long idUsuario);
}
