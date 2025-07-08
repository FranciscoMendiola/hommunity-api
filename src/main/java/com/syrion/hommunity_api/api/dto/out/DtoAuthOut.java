package com.syrion.hommunity_api.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoAuthOut {
    
    @JsonProperty("token")
    private String token;
}
