package org.eustrosoft.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class SettingsChangeDto {
    private JsonNode settings;
}
