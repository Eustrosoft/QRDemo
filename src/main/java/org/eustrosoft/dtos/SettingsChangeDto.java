package org.eustrosoft.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class SettingsChangeDto {
    @Size(max = 1024)
    private JsonNode settings;
}
