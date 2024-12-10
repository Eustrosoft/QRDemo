package org.eustrosoft.dtos;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class SettingsDto {
    @JsonRawValue
    private String settings;
}
