package com.syrion.hommunity.common.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoUsuarioOut;
import com.syrion.hommunity.api.entity.Familia;
import com.syrion.hommunity.api.entity.Usuario;
import com.syrion.hommunity.api.repository.FamiliaRepository;
import com.syrion.hommunity.common.util.ConverterRol;

@Service
public class MapperUsuario {

    @Autowired
    private ConverterRol converterRol;

    @Autowired
    private FamiliaRepository familiaRepository;

    /**
     * Convierte un Usuario a DtoUsuarioOut,
     * incluyendo el apellido de la familia si existe.
     */
    public DtoUsuarioOut fromUsuarioToDtoUsuarioOut(Usuario usuario) {
    DtoUsuarioOut out = new DtoUsuarioOut();
    
    out.setIdUsuario(usuario.getIdUsuario());
    out.setNombre(usuario.getNombre());
    out.setApellidoMaterno(usuario.getApellidoMaterno());
    out.setApellidoPaterno(usuario.getApellidoPaterno());
    out.setCorreo(usuario.getCorreo());
    out.setIdFamilia(usuario.getIdFamilia());
    out.setIdZona(usuario.getIdZona());
    out.setEstado(usuario.getEstado());
    out.setFotoIdentificacion(usuario.getFotoIdentificacion());
    out.setIdRol(usuario.getIdRol());

    String apellidoFamilia;
    if (usuario.getIdFamilia() != null) {
        apellidoFamilia = familiaRepository.findById(usuario.getIdFamilia())
                .map(Familia::getApellido)
                .orElse("Sin familia");
    } else {
        apellidoFamilia = "Sin familia"; // Valor por defecto para admins
    }
    out.setApellidoFamilia(apellidoFamilia);

    return out;
}

    /**
     * Convierte un DtoUsuarioIn a Usuario al registrar.
     */
    public Usuario fromDtoUsuarioInToUsuario(DtoUsuarioIn in) {
        Usuario usuario = new Usuario();

        usuario.setNombre(in.getNombre());
        usuario.setApellidoPaterno(in.getApellidoPaterno());
        usuario.setApellidoMaterno(in.getApellidoMaterno());
        usuario.setCorreo(in.getCorreo());
        usuario.setEstado("PENDIENTE");
        usuario.setIdFamilia(in.getIdFamilia());
        usuario.setContraseña(in.getContrasena());
        usuario.setIdRol(converterRol.getIdRol("RESIDENTE"));
        usuario.setIdZona(in.getIdZona());

        return usuario;
    }

    /**
     * Convierte una lista de Usuario a una lista de DtoUsuarioOut.
     */
    public List<DtoUsuarioOut> fromListUsuarioToDtoUsuarioOut(List<Usuario> usuarios) {
        List<DtoUsuarioOut> out = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            out.add(fromUsuarioToDtoUsuarioOut(usuario));
        }
        return out;
    }
}
