package com.syrion.hommunity.api.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;
import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoQrInvitadoOut;
import com.syrion.hommunity.api.dto.out.DtoQrUsuarioOut;
import com.syrion.hommunity.api.entity.Invitado;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.api.repository.InvitadoRepository;
import com.syrion.hommunity.api.repository.QrRepository;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.common.mapper.MapperQR;
import com.syrion.hommunity.common.util.QrCodeGenerator;
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
    public ResponseEntity<DtoQrInvitadoOut> getCodigoInvitado(Long id) {
        try {
            QR qr = qrRepository.findByIdInvitado(id);

            if (qr == null)
                throw new ApiException(HttpStatus.BAD_REQUEST, "El id de invitado indicado no esta asociado a ningún código Qr");

            Invitado invitado = invitadoRepository.findById(qr.getIdInvitado()).orElse(null);
            DtoQrInvitadoOut qrOut = mapper.fromQrToDtoQrInvitadoOut(qr, invitado);
                
            byte[] qrImageBytes = QrCodeGenerator.generateQrImageAsBytes(qr.getCodigo(), 300, 300);
            
            qrOut.setQrImageBytes(qrImageBytes);
                
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(qrOut, headers, HttpStatus.CREATED);
        } catch (WriterException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se pudo codificar el contenido del QR.");
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar la imagen del QR.");
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<DtoQrUsuarioOut> getCodigoUsuario(Long idUsuario) {
        try {
            QR qr = qrRepository.findByIdUsuario(idUsuario);

            if (qr == null)
                throw new ApiException(HttpStatus.BAD_REQUEST, "El id de usuario indicado no esta asociado a ningún código Qr");
            
            byte[] qrImageBytes = QrCodeGenerator.generateQrImageAsBytes(qr.getCodigo(), 300, 300);
            DtoQrUsuarioOut qrOut = mapper.fromQrToDtoQrUsuarioOut(qr);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(qrOut, headers, HttpStatus.CREATED);
        } catch (WriterException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se pudo codificar el contenido del QR.");
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar la imagen del QR.");
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }


    @Override
    public ResponseEntity<DtoQrInvitadoOut> createCodigoInvitado(DtoQrInvitadoIn in) {
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

            DtoQrInvitadoOut qrOut = mapper.fromQrToDtoQrInvitadoOut(qr, invitado);
                
            byte[] qrImageBytes = QrCodeGenerator.generateQrImageAsBytes(qr.getCodigo(), 300, 300);
            
            qrOut.setQrImageBytes(qrImageBytes);
                
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            qrRepository.save(qr);

            return new ResponseEntity<>(qrOut, headers, HttpStatus.CREATED);
        } catch (WriterException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se pudo codificar el contenido del QR.");
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar la imagen del QR.");
        } catch (DataAccessException e) {
            // Esta excepción no ocurrirá por la separación de generación de códigos en dos
            // endpoints
            if (e.getLocalizedMessage().contains("chk_qr_invitado_o_usuario"))
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El qr solo debe contener solo el id de usuario, o de invitado");

            // Esta excepción puede llegar a ocurrir si no se maneja bien la generación de
            // códigos
            if (e.getLocalizedMessage().contains("ux_qr_codigo"))
                throw new ApiException(HttpStatus.CONFLICT, "El código ya esta registrado");

            // ESta excepción no ocurrirá por la verificación de arriba
            if (e.getLocalizedMessage().contains("fk_qr_id_invitado"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id del invitado no esta registrado");

            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createCodigoUsuario(DtoQrUsuarioIn in) {
        try {
            QR qr = mapper.fromDtoQrInToQrResidente(in);

            qrRepository.save(qr);

            return new ResponseEntity<>(new ApiResponse("Código QR para residente creado correctamente"),
                    HttpStatus.CREATED);
        } catch (DataAccessException e) {
            // Esta excepción no ocurrirá por la separación de generación de códigos en dos
            // endpoints
            if (e.getLocalizedMessage().contains("chk_qr_invitado_o_usuario"))
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El qr solo debe contener solo el id de usuario, o de invitado");

            // Esta excepción puede llegar a ocurrir si no se maneja bien la generación de
            // códigos
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
    public ResponseEntity<ApiResponse> scanQr(String codigo) {
        try {
            QR qr = qrRepository.findByCodigo(codigo);

            if (qr == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Código QR no encontrado.");
            }

            // Validar vigencia
            if (!qr.getVigente()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ha expirado o ya no es válido.");
            }

            // Validar usos disponibles si es invitado
            if (qr.getIdInvitado() != null) {
                if (qr.getUsosDisponibles() <= 0) {
                    qr.setVigente(false);
                    qrRepository.save(qr);
                    throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ya no tiene usos disponibles.");
                }
                qr.setUsosDisponibles(qr.getUsosDisponibles() - 1);
            }

            // Si es residente no descontamos usos, solo confirmamos vigencia
            qrRepository.save(qr);

            return new ResponseEntity<>(new ApiResponse("El código QR es válido."), HttpStatus.OK);

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
}
