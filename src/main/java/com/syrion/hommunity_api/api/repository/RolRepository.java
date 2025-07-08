package com.syrion.hommunity_api.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.syrion.hommunity_api.api.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

    @Query(value = "SELECT id_rol FROM rol WHERE nombre_rol = :nombreRol", nativeQuery = true)
    Optional<Long> findIdByNombreRol(@Param("nombreRol") String nombreRol);

}
