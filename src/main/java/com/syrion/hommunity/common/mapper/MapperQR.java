package com.syrion.hommunity.common.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoQrInvitadoOut;
import com.syrion.hommunity.api.dto.out.DtoQrUsuarioOut;
import com.syrion.hommunity.api.entity.Invitado;
import com.syrion.hommunity.api.entity.QR;

@Service
public class MapperQR {

    public QR fromDtoQrInToQrInvitado(DtoQrInvitadoIn in) {
        QR qr = new QR();
        qr.setCodigo(generateNumericCode());
        qr.setFechaCreacion(LocalDateTime.now());
        qr.setUsosDisponibles(in.getUsosDisponibles() * 2);
        qr.setIdInvitado(in.getIdInvitado());
        qr.setVigente(true);
        return qr;
    }

    public QR fromDtoQrInToQrResidente(DtoQrUsuarioIn in) {
        QR qr = new QR();
        qr.setCodigo(generateNumericCode());
        qr.setFechaCreacion(LocalDateTime.now());
        qr.setUsosDisponibles(-1);
        qr.setIdUsuario(in.getIdUsuario());
        qr.setVigente(true);
        return qr;
    }

    public String generateNumericCode() {
        long number = 1000000000L + (long)(Math.random() * 9000000000L);
        return String.valueOf(number);
    }

    public DtoQrUsuarioOut fromQrToDtoQrUsuarioOut(QR qr) {
        DtoQrUsuarioOut qrOut = new DtoQrUsuarioOut();

        qrOut.setIdQr(qr.getIdQr());
        qrOut.setCodigo(qr.getCodigo());
        qrOut.setFechaCreacion(qr.getFechaCreacion());
        qrOut.setIdUsuario(qr.getIdUsuario());
        qrOut.setVigente(qr.getVigente());
        qrOut.setUsosDisponibles(qr.getUsosDisponibles());

        return qrOut;
    }

    public DtoQrInvitadoOut fromQrToDtoQrInvitadoOut(QR qr, Invitado invitado) {
        DtoQrInvitadoOut qrOut = new DtoQrInvitadoOut();

        qrOut.setIdQr(qr.getIdQr());
        qrOut.setCodigo(qr.getCodigo());
        qrOut.setVigente(qr.getVigente());
        qrOut.setUsosDisponibles(qr.getUsosDisponibles());
        qrOut.setFechaEntrada(invitado.getFechaEntrada());
        qrOut.setFechaSalida(invitado.getFechaSalida());
        
        // Corregir la concatenación del nombre con espacios apropiados
        StringBuilder nombreCompleto = new StringBuilder();
        nombreCompleto.append(invitado.getNombre() != null ? invitado.getNombre() : "");
        
        if (invitado.getApellidoPaterno() != null && !invitado.getApellidoPaterno().isEmpty()) {
            if (nombreCompleto.length() > 0) nombreCompleto.append(" ");
            nombreCompleto.append(invitado.getApellidoPaterno());
        }
        
        if (invitado.getApellidoMaterno() != null && !invitado.getApellidoMaterno().isEmpty()) {
            if (nombreCompleto.length() > 0) nombreCompleto.append(" ");
            nombreCompleto.append(invitado.getApellidoMaterno());
        }
        
        qrOut.setNombreInvitado(nombreCompleto.toString().trim());

        return qrOut;
    }
}