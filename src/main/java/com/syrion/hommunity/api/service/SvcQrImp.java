package com.syrion.hommunity.api.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import com.syrion.hommunity.api.dto.out.DtoQrUsuarioCodigoOut;
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

import jakarta.transaction.Transactional;

@Service
public class SvcQrImp implements SvcQr {

    @Autowired
    private QrRepository qrRepository;

    @Autowired
    private InvitadoRepository invitadoRepository;

    @Autowired
    private MapperQR mapper;

    @Override
    @Transactional
    public ResponseEntity<DtoQrInvitadoOut> getCodigoInvitado(Long id) {
        try {
            // 1. Buscar el QR asociado al ID del invitado
            QR qr = qrRepository.findByIdInvitado(id).orElseThrow(() -> 
                new ApiException(HttpStatus.NOT_FOUND, "El id de invitado indicado no está asociado a ningún código QR."));

            // 2. Buscar al invitado
            Invitado invitado = invitadoRepository.findById(qr.getIdInvitado()).orElse(null);
            
            // 3. Actualizar el estado de vigencia del QR EN MEMORIA y persistirlo.
            // La instancia 'qr' quedará con el estado correcto ('vigente' = false si expiró).
            actualizarVigenciaQrSiEsNecesario(qr, invitado);
            
            // 4. Mapear el DTO usando la instancia 'qr' ya actualizada.
            // ¡¡NO RECARGAR DESDE LA BASE DE DATOS!! Usamos el objeto que ya tenemos.
            DtoQrInvitadoOut qrOut = mapper.fromQrToDtoQrInvitadoOut(qr, invitado);
                
            // 5. Generar la imagen y devolver la respuesta
            byte[] qrImageBytes = QrCodeGenerator.generateQrImageAsBytes(qr.getCodigo(), 300, 300);
            qrOut.setQrImageBytes(qrImageBytes);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(qrOut, headers, HttpStatus.OK);
        } catch (WriterException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se pudo codificar el contenido del QR.");
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar la imagen del QR.");
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    // ... (El resto de tus métodos como getCodigoUsuario, createCodigoInvitado, etc., se mantienen igual)
    
    @Override
    public ResponseEntity<DtoQrUsuarioOut> getCodigoUsuario(Long idUsuario) {
        try {
            QR qr = qrRepository.findByIdUsuario(idUsuario);

            if (qr == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "El id de usuario indicado no está asociado a ningún código QR");
            }
            
            byte[] qrImageBytes = QrCodeGenerator.generateQrImageAsBytes(qr.getCodigo(), 300, 300);
            
            DtoQrUsuarioOut qrOut = mapper.fromQrToDtoQrUsuarioOut(qr);
            qrOut.setQrImageBytes(qrImageBytes); 

            return new ResponseEntity<>(qrOut, HttpStatus.OK);
        } catch (WriterException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se pudo codificar el contenido del QR.");
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar la imagen del QR.");
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }


    @Override
    @Transactional
    public ResponseEntity<DtoQrInvitadoOut> createCodigoInvitado(DtoQrInvitadoIn in) {
        try {
            // Verificar si ya existe un QR para este invitado
            QR existingQr = qrRepository.findByIdInvitado(in.getIdInvitado()).orElse(null);
            if (existingQr != null) {
                throw new ApiException(HttpStatus.CONFLICT, "Ya existe un código QR asociado a este invitado.");
            }

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

            DtoQrInvitadoOut qrOut = mapper.fromQrToDtoQrInvitadoOut(qr, invitado);
            byte[] qrImageBytes = QrCodeGenerator.generateQrImageAsBytes(qr.getCodigo(), 300, 300);
            qrOut.setQrImageBytes(qrImageBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // Cambiado a JSON

            return new ResponseEntity<>(qrOut, headers, HttpStatus.CREATED);
        } catch (WriterException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se pudo codificar el contenido del QR.");
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar la imagen del QR.");
        } catch (DataAccessException e) {
            if (e.getLocalizedMessage().contains("chk_qr_invitado_o_usuario"))
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El qr solo debe contener solo el id de usuario, o de invitado");

            if (e.getLocalizedMessage().contains("ux_qr_codigo"))
                throw new ApiException(HttpStatus.CONFLICT, "El código ya esta registrado");

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
            if (e.getLocalizedMessage().contains("chk_qr_invitado_o_usuario"))
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El qr solo debe contener solo el id de usuario, o de invitado");

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

            if (!qr.getVigente()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ha expirado o ya no es válido.");
            }

            if (qr.getIdInvitado() != null) {
                if (qr.getUsosDisponibles() <= 0) {
                    qr.setVigente(false);
                    qrRepository.save(qr);
                    throw new ApiException(HttpStatus.BAD_REQUEST, "El código QR ya no tiene usos disponibles.");
                }
                qr.setUsosDisponibles(qr.getUsosDisponibles() - 1);
            }

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

    @Override
    public ResponseEntity<DtoQrUsuarioCodigoOut> getCodigoUsuarioSimple(Long idUsuario) {
        try {
            QR qr = qrRepository.findByIdUsuario(idUsuario);

            if (qr == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "El id de usuario indicado no está asociado a ningún código QR");
            }

            DtoQrUsuarioCodigoOut codigoOut = new DtoQrUsuarioCodigoOut(qr.getCodigo());
            return new ResponseEntity<>(codigoOut, HttpStatus.OK);

        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    /**
     * ✅ MÉTODO REFACTORIZADO
     * Verifica si un QR debe ser invalidado y actualiza su estado si es necesario.
     * Esta operación modifica la instancia del objeto QR directamente.
     */
    private void actualizarVigenciaQrSiEsNecesario(QR qr, Invitado invitado) {
        // Si ya no es vigente, no hay nada que hacer.
        if (qr == null || !qr.getVigente()) {
            return;
        }

        boolean debeInvalidarse = false;
        String motivo = "";

        // Criterio 1: Usos agotados
        if (qr.getUsosDisponibles() != null && qr.getUsosDisponibles() <= 0) {
            debeInvalidarse = true;
            motivo = "Usos disponibles agotados.";
        }

        // Criterio 2: Fecha expirada (solo si no se ha invalidado ya por usos)
        if (!debeInvalidarse && invitado != null) {
            ZoneId zonaCST = ZoneId.of("America/Mexico_City");
            LocalDateTime ahora = LocalDateTime.now(zonaCST);
            LocalDateTime fechaSalida = invitado.getFechaSalida();

            if (fechaSalida != null && ahora.isAfter(fechaSalida)) {
                debeInvalidarse = true;
                motivo = "La fecha de salida (" + fechaSalida + ") ha pasado.";
            }
        }
        
        // Si se cumple una condición, se actualiza el objeto y se guarda.
        if (debeInvalidarse) {
            System.out.println("Invalidando QR " + qr.getCodigo() + ". Motivo: " + motivo);
            qr.setVigente(false);
            qrRepository.save(qr); // La transacción se encargará de hacer el commit.
        }
    }
}