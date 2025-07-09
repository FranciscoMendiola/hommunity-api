package com.syrion.hommunity.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.syrion.hommunity.api.entity.Invitado;

@Repository
public interface InvitadoRepository extends JpaRepository<Invitado, Long> {
    
}
