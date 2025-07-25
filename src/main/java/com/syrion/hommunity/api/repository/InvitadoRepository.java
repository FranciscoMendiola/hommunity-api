package com.syrion.hommunity.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.syrion.hommunity.api.entity.Invitado;

@Repository
public interface InvitadoRepository extends JpaRepository<Invitado, Long> {
  
    List<Invitado> findTop5ByIdUsuarioOrderByFechaEntradaDesc(Long idUsuario);

    @Query("SELECT i FROM Invitado i WHERE i.idUsuario = :idUsuario AND i.fechaEntrada BETWEEN :startOfDay AND :endOfDay")
    List<Invitado> findByIdUsuarioAndFechaEntradaBetween(
        @Param("idUsuario") Long idUsuario,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );

    Optional<Invitado> findByIdInvitadoAndIdUsuario(Long idInvitado, Long idUsuario);
}