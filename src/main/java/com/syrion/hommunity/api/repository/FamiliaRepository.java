package com.syrion.hommunity.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.syrion.hommunity.api.entity.Familia;

@Repository
public interface FamiliaRepository extends JpaRepository<Familia, Long> {

    // path: Familia.idCasa.idZona.idZona
    @Query(value = "SELECT * FROM ZONA WHERE id_zona = :idZona", nativeQuery = true)
    List<Familia> findByIdZona(Long idZona);
}
