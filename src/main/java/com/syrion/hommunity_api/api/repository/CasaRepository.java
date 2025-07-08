package com.syrion.hommunity_api.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.syrion.hommunity_api.api.entity.Casa;

public interface CasaRepository extends JpaRepository<Casa, Long> {

    @Query(value = "SELECT * FROM casa WHERE id_zona = :idZona", nativeQuery = true)
    List<Casa> findByIdZona(Long idZona); 
}
