package com.syrion.hommunity.api.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.zxing.WriterException; // Importar WriterException
import com.syrion.hommunity.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity.api.dto.out.DtoQrInvitadoOut;
import com.syrion.hommunity.api.entity.Invitado;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.api.repository.InvitadoRepository;
import com.syrion.hommunity.api.repository.QrRepository;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.common.mapper.MapperInvitado;
import com.syrion.hommunity.common.mapper.MapperQR;
import com.syrion.hommunity.common.util.QrCodeGenerator;
import com.syrion.hommunity.exception.ApiException;
import com.syrion.hommunity.exception.DBAccessException;

import jakarta.transaction.Transactional;

@Service
public class SvcInvitadoImp implements SvcInvitado {

    @Autowired
    private InvitadoRepository invitadoRepository;

    @Autowired
    private MapperInvitado mapperInvitado;

    @Autowired
    private QrRepository qrRepository;

    @Autowired
    private MapperQR mapperQR;

    // ... los métodos getInvitados y getInvitado se mantienen igual ...
    @Override
    public ResponseEntity<List<Invitado>> getInvitados(Long idUsuario) {
        try {
            List<Invitado> invitados = invitadoRepository.findTop5ByIdUsuarioOrderByFechaEntradaDesc(idUsuario);
            return new ResponseEntity<>(invitados, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<Invitado> getInvitado(Long id) {
        try {
            Invitado invitado = validateId(id);
            return new ResponseEntity<>(invitado, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }


    // ✅ MÉTODO CORREGIDO Y FINAL
    @Override
    @Transactional // ¡Es crucial que sea transaccional para que el guardado funcione!
    public ResponseEntity<DtoQrInvitadoOut> getInvitadoDetailsByFamily(Long idUsuario, Long id) {
        try {
            // 1. Validar y obtener el invitado, asegurando que pertenece a la familia.
            Invitado invitado = invitadoRepository.findByIdInvitadoAndIdUsuario(id, idUsuario)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Invitado no encontrado o no pertenece a esta familia."));

            // 2. Obtener el QR asociado a este invitado.
            QR qr = qrRepository.findByIdInvitado(invitado.getIdInvitado())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Este invitado no tiene un código QR asociado."));

            // 3. ¡LA LÓGICA CLAVE! Actualizar la vigencia del QR antes de devolverlo.
            actualizarVigenciaQrSiEsNecesario(qr, invitado);

            // 4. Mapear al DTO de salida usando los datos ya actualizados.
            DtoQrInvitadoOut qrOut = mapperQR.fromQrToDtoQrInvitadoOut(qr, invitado);

            // 5. Generar la imagen del QR y añadirla al DTO.
            byte[] qrImageBytes = QrCodeGenerator.generateQrImageAsBytes(qr.getCodigo(), 300, 300);
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

    /**
     * ✅ MÉTODO AUXILIAR PARA ACTUALIZAR LA VIGENCIA
     * Verifica si un QR debe ser invalidado y actualiza su estado si es necesario.
     */
    private void actualizarVigenciaQrSiEsNecesario(QR qr, Invitado invitado) {
        if (qr == null || !qr.getVigente()) {
            return; // Si no hay QR o ya es inválido, no hacemos nada.
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
            qrRepository.save(qr); // La transacción se encargará de hacer el commit al final.
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> createInvitado(DtoInvitadoIn in) {
        try {
            if (in.getFechaEntrada().isBefore(LocalDateTime.now()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "La fecha de entrada no puede ser anterior a la fecha actual.");

            if (in.getFechaSalida().isBefore(in.getFechaEntrada()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "La fecha de salida no puede ser anterior a la fecha de entrada.");

            List<Invitado> invitacionesExistentes = invitadoRepository.findByIdUsuarioAndFechaEntradaBetween(
                in.getIdUsuario(),
                in.getFechaEntrada().withHour(0).withMinute(0).withSecond(0),
                in.getFechaSalida().withHour(23).withMinute(59).withSecond(59)
            );
            for (Invitado existente : invitacionesExistentes) {
                if (isOverlap(in.getFechaEntrada(), in.getFechaSalida(), existente.getFechaEntrada(), existente.getFechaSalida())) {
                    throw new ApiException(HttpStatus.CONFLICT, "Ya hay una invitación en ese horario.");
                }
            }

            Invitado invitado = mapperInvitado.fromDtoInvitadoInToInvitado(in);
            invitadoRepository.save(invitado);

            // ELIMINADO: La creación automática del QR
            // Esto debe hacerse posteriormente mediante el endpoint /qr/invitado

            return new ResponseEntity<>(new ApiResponse("Invitado creado correctamente"), HttpStatus.CREATED);
        } catch (DataAccessException e) {
            if (e.getLocalizedMessage().contains("fk_invitado_id_usuario"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id del usuario residente no esta registrado");

            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> deleteInvitadoByFamily(Long idUsuario, Long id) {
        try {
            Invitado invitado = validateId(id);
            if (!invitado.getIdUsuario().equals(idUsuario)) {
                throw new ApiException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este invitado.");
            }
            QR qr = qrRepository.findByIdInvitado(id).orElse(null);
            if (qr != null) {
                qrRepository.delete(qr);
            }
            invitadoRepository.delete(invitado);
            return new ResponseEntity<>(new ApiResponse("Invitado eliminado correctamente"), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    private Invitado validateId(Long id) {
        Invitado invitado = invitadoRepository.findById(id).orElse(null);
        if (invitado == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "El id del invitado no esta registrado.");
        return invitado;
    }

    private boolean isOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}