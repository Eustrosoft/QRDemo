package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityDto {
    private Long id;
    private Date created;
    private Date updated;
    @Size(max = 128)
    private String name;
    @Size(max = 512)
    private String description;
    private String type;
}
