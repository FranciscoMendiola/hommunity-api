package com.syrion.hommunity.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.syrion.hommunity.api.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

        @Query(value = "SELECT * FROM usuario WHERE correo = :correo", nativeQuery = true)
        Usuario findByCorreo(String correo);

        @Query(value = "SELECT * FROM usuario WHERE id_zona = :idZona", nativeQuery = true)
        List<Usuario>findByIdZona(Long idZona);

        @Query(value = "SELECT * FROM usuario WHERE id_familia = :idFamilia", nativeQuery = true)
        List<Usuario>findByIdFamilia(Long idFamilia);

        @Query(value = """ 
                SELECT u.* FROM usuario u JOIN familia f ON u.id_familia = f.id_familia
                WHERE u.estado = 'PENDIENTE' AND u.id_zona = :idZona AND f.id_usuario_registrador IS NULL
                """, nativeQuery = true)
        List<Usuario> findByFamiliaYEstadoPendiente(@Param("idZona") Long idZona);

}
