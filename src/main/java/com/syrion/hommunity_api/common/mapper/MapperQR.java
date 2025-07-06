package com.syrion.hommunity_api.common.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity_api.api.dto.in.DtoQrResidenteIn;
import com.syrion.hommunity_api.api.entity.QR;

@Service
public class MapperQR {

    public QR fromQR(DtoQrInvitadoIn in) {
        QR qr = new QR();
        qr.setCodigo(generateNumericCode());
        qr.setFechaCreacion(LocalDateTime.now());
        qr.setUsosDisponibles(in.getUsosDisponibles() * 2);
        qr.setVigente(true);
        return qr;
    }

    public QR fromQR(DtoQrResidenteIn in) {
        QR qr = new QR();
        qr.setCodigo(generateNumericCode());
        qr.setFechaCreacion(LocalDateTime.now());
        qr.setUsosDisponibles(-1);
        qr.setVigente(true);
        
        return qr;
    }

    private String generateNumericCode() {
        long number = 1000000000L + (long)(Math.random() * 9000000000L);
        return String.valueOf(number);
    }
}
