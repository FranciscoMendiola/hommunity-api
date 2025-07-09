package com.syrion.hommunity.common.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrResidenteIn;
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

    public QR fromDtoQrInToQrResidente(DtoQrResidenteIn in) {
        QR qr = new QR();
        qr.setCodigo(generateNumericCode());
        qr.setFechaCreacion(LocalDateTime.now());
        qr.setUsosDisponibles(-1);
        qr.setIdUsuario(in.getIdUsuario());
        qr.setVigente(true);
        return qr;
    }

    private String generateNumericCode() {
        long number = 1000000000L + (long)(Math.random() * 9000000000L);
        return String.valueOf(number);
    }
}
