package com.syrion.hommunity.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.syrion.hommunity.api.entity.QR;

public interface QrRepository extends JpaRepository<QR, Long> {

    @Query(value = "SELECT * FROM qr WHERE id_usuario = :idUsuario", nativeQuery = true)
    QR findByIdUsuario(@Param("idUsuario") Long idUsuario);

    @Query(value = "SELECT * FROM qr WHERE id_invitado = :idInvitado", nativeQuery = true)
    QR findByIdInvitado(@Param("idInvitado") Long idInvitado);

    @Query(value = "SELECT * FROM qr WHERE codigo = :codigo", nativeQuery = true)
    QR findByCodigo(@Param("codigo") String codigo);
}
