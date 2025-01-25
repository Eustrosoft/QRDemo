package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.QRRangeDto;
import org.eustrosoft.entitites.QRRange;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QrRangeMapper extends EntityMapper {

    public QRRangeDto toDto(QRRange range) {
        QRRangeDto dto = new QRRangeDto(range.getFrom(), range.getTo());
        dto.setId(range.getId());
        dto.setCreated(range.getCreated());
        dto.setUpdated(range.getUpdated());
        return dto;
    }

    public List<QRRangeDto> toDtoList(Collection<QRRange> ranges) {
        if (ranges == null || ranges.isEmpty()) {
            return Collections.emptyList();
        }
        return ranges.stream()
                .map(this::toDto).collect(Collectors.toList());
    }
}
