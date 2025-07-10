package com.syrion.hommunity.api.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrResidenteIn;
import com.syrion.hommunity.api.entity.Invitado;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.api.repository.InvitadoRepository;
import com.syrion.hommunity.api.repository.QrRepository;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.common.mapper.MapperQR;
import com.syrion.hommunity.exception.ApiException;
import com.syrion.hommunity.exception.DBAccessException;

@Service
public class SvcQrImp implements SvcQr {

    @Autowired
    private QrRepository qrRepository;
    
    @Autowired
    private InvitadoRepository invitadoRepository;
    
    @Autowired
    private MapperQR mapper;

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
            List<QR> qrs = qrRepository.findAll();

            return new ResponseEntity<>(qrs, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<String> getCodigoUsuario(Long idUsuario) {
        try {
            String codigo = qrRepository.findCodigoByIdUsuarioAndIdInvitadoIsNull(idUsuario);

            if (codigo == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(codigo);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }


    @Override
    public ResponseEntity<List<QR>> getCodigosActivos() {
        try {
            List<QR> qrs = qrRepository.findByActiveStatus();

            return new ResponseEntity<>(qrs, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createCodigoInvitado(DtoQrInvitadoIn in) {
        try {
            Invitado invitado = invitadoRepository.findById(in.getIdInvitado())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El invitado no está registrado."));

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

            QR qr = mapper.fromDtoQrInToQrInvitado(in);

            qrRepository.save(qr);

            return new ResponseEntity<>(new ApiResponse("Código QR registrado correctamente"), HttpStatus.CREATED);
        } catch (DataAccessException e) {
            // Esta excepción no ocurrirá por la separación de generación de códigos en dos endpoints
            if (e.getLocalizedMessage().contains("chk_qr_invitado_o_usuario"))
                throw new ApiException(HttpStatus.BAD_REQUEST, "El qr solo debe contener solo el id de usuario, o de invitado");

            // Esta excepción puede llegar a ocurrir si no se maneja bien la generación de códigos
            if (e.getLocalizedMessage().contains("ux_qr_codigo"))
                throw new ApiException(HttpStatus.CONFLICT, "El código ya esta registrado");

            // ESta excepción no ocurrirá por la verificación de arriba
            if (e.getLocalizedMessage().contains("fk_qr_id_invitado"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id del invitado no esta registrado");

            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createCodigoResidente(DtoQrResidenteIn in) {
        try {
            QR qr = mapper.fromDtoQrInToQrResidente(in);

            qrRepository.save(qr);

            return new ResponseEntity<>(new ApiResponse("Código QR para residente creado correctamente"),
                    HttpStatus.CREATED);
        } catch (DataAccessException e) {
            // Esta excepción no ocurrirá por la separación de generación de códigos en dos endpoints
            if (e.getLocalizedMessage().contains("chk_qr_invitado_o_usuario"))
                throw new ApiException(HttpStatus.BAD_REQUEST, "El qr solo debe contener solo el id de usuario, o de invitado");

            // Esta excepción puede llegar a ocurrir si no se maneja bien la generación de códigos
            if (e.getLocalizedMessage().contains("ux_qr_codigo"))
                throw new ApiException(HttpStatus.CONFLICT, "El código ya esta registrado");

            if (e.getLocalizedMessage().contains("ux_qr_id_usuario"))
                throw new ApiException(HttpStatus.CONFLICT, "El usuario residente ya tiene un código qr asociado");

            if (e.getLocalizedMessage().contains("ux_qr_id_invitado"))
                throw new ApiException(HttpStatus.CONFLICT, "El invitado ya tiene un código qr asociado");

            if (e.getLocalizedMessage().contains("fk_qr_id_usuario"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id del residente no esta registrado");

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
                return new ResponseEntity<>(new ApiResponse("Código QR validado correctamente"), HttpStatus.OK);

            if (qr.getIdInvitado() != null) {
                Invitado invitado = invitadoRepository.findById(qr.getIdInvitado()).orElse(null);
                LocalDate today = LocalDate.now();
                LocalDate fechaSalida = invitado.getFechaSalida().toLocalDate();
                if (fechaSalida.isBefore(today)) {
                    qr.setVigente(false);
                    qrRepository.save(qr);
                    throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ha expirado.");
                }

                if (qr.getUsosDisponibles() < 1) {
                    qr.setVigente(false);
                    qrRepository.save(qr);
                    throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ya no tiene usos. Debes generar uno nuevo.");
                }                 
            }
            
            qr.setUsosDisponibles(qr.getUsosDisponibles() - 1);
            qrRepository.save(qr);

            return new ResponseEntity<>(new ApiResponse("Codigo QR validado correctamente"), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    private QR validateId(Long id) {

        QR qr = qrRepository.findById(id).orElse(null);

        if (qr == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "El id del QR no esta registrado.");

        return qr;
    }

    @Override
    public ResponseEntity<String> getCodigoQrPorInvitado(Long idInvitado) {
        try {
            QR qr = qrRepository.findByIdInvitado(idInvitado);

            if (qr == null)
                throw new ApiException(HttpStatus.NOT_FOUND, "El invitado no tiene un QR registrado");

            return new ResponseEntity<>(qr.getCodigo(), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }
  
    @Override
    public ResponseEntity<String> getCodigoUsuario(Long idUsuario) {
        try {
            String codigo = qrRepository.findCodigoByIdUsuarioAndIdInvitadoIsNull(idUsuario);

            if (codigo == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(codigo);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

}
