package com.profid.profid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GeneralResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    public GeneralResponse() {
    }

    public GeneralResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
