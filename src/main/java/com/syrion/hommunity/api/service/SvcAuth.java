package com.syrion.hommunity.api.service;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoAuthIn;
import com.syrion.hommunity.api.dto.out.DtoAuthOut;

public interface SvcAuth {

    ResponseEntity<DtoAuthOut> login(DtoAuthIn in);
    
}
