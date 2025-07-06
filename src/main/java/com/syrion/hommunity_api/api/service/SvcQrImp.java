package com.syrion.hommunity_api.api.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity_api.api.dto.in.DtoQrResidenteIn;
import com.syrion.hommunity_api.api.entity.Invitado;
import com.syrion.hommunity_api.api.entity.QR;
import com.syrion.hommunity_api.api.entity.Usuario;
import com.syrion.hommunity_api.api.repository.InvitadoRepository;
import com.syrion.hommunity_api.api.repository.QrRepository;
import com.syrion.hommunity_api.api.repository.UsuarioRepository;
import com.syrion.hommunity_api.common.dto.ApiResponse;
import com.syrion.hommunity_api.common.mapper.MapperQR;
import com.syrion.hommunity_api.exception.ApiException;
import com.syrion.hommunity_api.exception.DBAccessException;

@Service
public class SvcQrImp implements SvcQr {

    @Autowired
    private QrRepository repoQr;

    @Autowired
    private MapperQR mapper;

    @Autowired
    private InvitadoRepository repoInvitado;

    @Autowired
    private UsuarioRepository repoUsuario;

    @Override
    public ResponseEntity <QR> getCodigo(Long id) {
        try {
            QR qr = validateId(id);

            return new ResponseEntity<>(qr , HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<List<QR>> getCodigos() {
        try {
            List<QR> qrs = repoQr.findAll();

            return new ResponseEntity<>(qrs, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<List<QR>> getCodigosActivos() {
        try {
            List<QR> qrs = repoQr.findByActiveStatus();

            return new ResponseEntity<>(qrs, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createCodigoInvitado(DtoQrInvitadoIn in) {
        try {
            Invitado invitado = repoInvitado.findById(in.getIdInvitado())
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "El invitado no está registrado."));

            LocalDate today = LocalDate.now();
            LocalDate fechaEntrada = invitado.getFechaEntrada().toLocalDate();
            LocalDate fechaSalida = invitado.getFechaSalida().toLocalDate();

            if (fechaEntrada.isBefore(today))
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El QR no se puede asociar a un invitado cuya fecha de entrada ya es anterior a la fecha actual.");

            long days = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida) + 1;
            int maxUsos = (int) (days * 20);

            if (in.getUsosDisponibles() > maxUsos)
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El número de usos excede el límite permitido de " + maxUsos + " usos para un periodo de "
                                + days + " día(s).");

            QR qr = mapper.fromQR(in);
            qr.setIdInvitado(invitado);

            repoQr.save(qr);

            return new ResponseEntity<>(new ApiResponse("Código QR registrado correctamente"), HttpStatus.CREATED);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createCodigoResidente(DtoQrResidenteIn in) {
        try {
            Usuario usuario = repoUsuario.findById(in.getIdUsuario())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El usuario residente no está registrado."));

            QR qr = mapper.fromQR(in);
            qr.setIdUsuario(usuario);

            repoQr.save(qr);

            return new ResponseEntity<>(new ApiResponse("Código QR para residente creado correctamente"),
                    HttpStatus.CREATED);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> validar(Long id) {
        try {
            QR qr = validateId(id);

            if (!qr.getVigente())
                throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ha expirado.");

            if (qr.getIdUsuario() != null)
            return new ResponseEntity<>(new ApiResponse("Codigo QR validado correctamente"), HttpStatus.OK);
            
            if (qr.getIdUsuario() == null) {
                LocalDate today = LocalDate.now();
                LocalDate fechaSalida = qr.getIdInvitado().getFechaSalida().toLocalDate();
                if (fechaSalida.isBefore(today)) {
                    qr.setVigente(false);
                    repoQr.save(qr);
                    throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ha expirado.");
                }

                if (qr.getUsosDisponibles() < 1) {
                    qr.setVigente(false);
                    repoQr.save(qr);
                    throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ya no tiene usos. Debes generar uno nuevo.");
                }                 
            }
            
            qr.setUsosDisponibles(qr.getUsosDisponibles() - 1);
            repoQr.save(qr);

            return new ResponseEntity<>(new ApiResponse("Codigo QR validado correctamente"), HttpStatus.OK);
        } catch (ApiException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    private QR validateId(Long id) {

        QR qr = repoQr.findById(id).orElse(null);

        if (qr == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "El id del QR no esta registrado.");

        return qr;
    }
}
