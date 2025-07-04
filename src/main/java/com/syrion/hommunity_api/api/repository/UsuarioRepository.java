package com.syrion.hommunity_api.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.syrion.hommunity_api.api.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

        @Query(value = "SELECT * FROM usuario WHERE correo = :correo", nativeQuery = true)
        Usuario findByCorreo(String correo);

        @Query(value = "SELECT * FROM usuario WHERE id_zona = :idZona", nativeQuery = true)
        List<Usuario>findByIdZona(Long idZona);

        @Query(value = "SELECT * FROM usuario WHERE id_familia = :idFamilia", nativeQuery = true)
        List<Usuario>findByIdFamilia(Long idFamilia);
}
