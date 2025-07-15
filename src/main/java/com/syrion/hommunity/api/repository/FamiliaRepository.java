package com.syrion.hommunity.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.syrion.hommunity.api.entity.Familia;

@Repository
public interface FamiliaRepository extends JpaRepository<Familia, Long> {

    @Query("SELECT f FROM Familia f JOIN Casa c ON f.idCasa = c.idCasa WHERE c.idZona = :idZona")
    List<Familia> findFamiliasByIdZona(@Param("idZona") Long idZona);

}
