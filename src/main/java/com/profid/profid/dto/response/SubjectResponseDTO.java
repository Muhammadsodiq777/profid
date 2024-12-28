package com.profid.profid.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectResponseDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("credits")
    private Integer credits;

    @JsonProperty("description")
    private String description;

    public SubjectResponseDTO() {
    }

    public SubjectResponseDTO(String name, Integer credits, String description) {
        this.name = name;
        this.credits = credits;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
