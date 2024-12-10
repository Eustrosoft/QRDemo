package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRRangeDto extends EntityDto {
    private Long from;
    private Long to;

    public String getFrom() {
        return Long.toHexString(from);
    }

    public String getTo() {
        return Long.toHexString(to);
    }
}
