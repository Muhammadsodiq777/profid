package com.profid.profid.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubjectRequestDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("credits")
    private Integer credits;

    @JsonProperty("description")
    private String description;
}
