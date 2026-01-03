package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PCodeDto {
    private Long docId;
    private Long rowId;
    private Character hFields;
    private Character hFiles;
    private String p;
    private String p2;
    private String p2Mode;
    private String p2Prompt;
    private String comment;
}
