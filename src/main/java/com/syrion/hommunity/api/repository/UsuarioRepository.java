package com.syrion.hommunity.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.syrion.hommunity.api.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por correo (ya funciona bien)
    @Query(value = "SELECT * FROM usuario WHERE correo = :correo", nativeQuery = true)
    Usuario findByCorreo(@Param("correo") String correo);

    @Query(value = "SELECT * FROM usuario WHERE id_zona = :idZona", nativeQuery = true)
    List<Usuario> findByIdZona(@Param("idZona") Long idZona);

    @Query(value = "SELECT * FROM usuario WHERE id_familia = :idFamilia", nativeQuery = true)
    List<Usuario> findByIdFamilia(@Param("idFamilia") Long idFamilia);


    @Query("SELECT u FROM Usuario u WHERE u.estado = 'PENDIENTE' AND u.idZona = :idZona AND u.idFamilia IN (SELECT f.idFamilia FROM Familia f WHERE f.idUsuarioRegistrador IS NULL)")
    List<Usuario> findUsuariosPendientesPorZonaSinRegistrador(@Param("idZona") Long idZona);

        @Query(value = """ 
                SELECT u.* FROM usuario u JOIN familia f ON u.id_familia = f.id_familia
                WHERE u.estado = 'PENDIENTE' AND u.id_zona = :idZona AND f.id_usuario_registrador IS NULL
                """, nativeQuery = true)
        List<Usuario> findByFamiliaYEstadoPendiente(@Param("idZona") Long idZona);


    List<Usuario> findByIdFamiliaAndEstado(Long idFamilia, String estado);


    @Query("SELECT u FROM Usuario u WHERE u.idZona = :idZona AND u.estado = :estado AND u.idFamilia IN (SELECT f.idFamilia FROM Familia f WHERE f.idUsuarioRegistrador = :usuarioRegistrador)")
    List<Usuario> findByIdZonaAndEstadoAndUsuarioRegistrador(@Param("idZona") Long idZona, @Param("estado") String estado, @Param("usuarioRegistrador") Long usuarioRegistrador);

    // FIX: Crear alias para findByEmail que apunte a findByCorreo
    default Usuario findByEmail(String email) {
        return findByCorreo(email);
    }
}

        @Query("SELECT u FROM Usuario u WHERE u.idFamilia = :idFamilia AND u.estado = 'APROBADO'")
        List<Usuario> findUsuariosAprobadosPorFamilia(@Param("idFamilia") Long idFamilia);

}

