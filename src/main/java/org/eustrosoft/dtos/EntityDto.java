package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityDto {
    private Long id;
    private Date created;
    private Date updated;
    private String name;
    private String description;
}
