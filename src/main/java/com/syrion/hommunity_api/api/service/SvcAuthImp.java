package com.syrion.hommunity_api.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoAuthIn;
import com.syrion.hommunity_api.api.dto.out.DtoAuthOut;
import com.syrion.hommunity_api.api.entity.Usuario;
import com.syrion.hommunity_api.api.repository.UsuarioRepository;
import com.syrion.hommunity_api.config.jwt.JwtUtil;
import com.syrion.hommunity_api.exception.ApiException;
import com.syrion.hommunity_api.exception.DBAccessException;

@Service
public class SvcAuthImp implements SvcAuth {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Override
    public ResponseEntity<DtoAuthOut> login(DtoAuthIn in) {
        try {
            Usuario usuario = usuarioRepository.findByCorreo(in.getCorreo());

            if (usuario == null)
                throw new ApiException(HttpStatus.NOT_FOUND, "Usuario no registrado");

            if (!passwordEncoder.matches(in.getContraseña(), usuario.getContraseña()))
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");

            if (!usuario.getEstado().toLowerCase().equals("aprobado"))
                throw new ApiException(HttpStatus.FORBIDDEN, "Verifica con tu administrador de zona el estado de tu cuenta");

            DtoAuthOut token = new DtoAuthOut();
            token.setToken(jwtUtil.generateToken(usuario));
            
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }
}
