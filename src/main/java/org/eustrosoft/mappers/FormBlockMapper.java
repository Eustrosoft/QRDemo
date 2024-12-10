package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.FormBlockChangeDto;
import org.eustrosoft.dtos.FormBlockCreationDto;
import org.eustrosoft.dtos.FormBlockDto;
import org.eustrosoft.entitites.FormBlock;
import org.eustrosoft.utils.comparators.FormFieldComparator;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FormBlockMapper extends EntityMapper {
    private final FormFieldMapper ffMapper;

    public FormBlockDto toDto(FormBlock formBlock) {
        FormBlockDto dto = new FormBlockDto();
        dto.setId(formBlock.getId());
        dto.setName(formBlock.getName());
        dto.setDescription(formBlock.getDescription());
        dto.setCreated(formBlock.getCreated());
        dto.setUpdated(formBlock.getUpdated());

        if (formBlock.getFields() != null) {
            dto.setFields(
                    formBlock.getFields().stream().map(ffMapper::toDto)
                            .sorted(new FormFieldComparator()).collect(Collectors.toList())
            );
        }
        return dto;
    }

    public FormBlock fromCreationDto(FormBlockCreationDto dto) {
        FormBlock fb = new FormBlock();
        fb.setName(dto.getName());
        fb.setDescription(dto.getDescription());
        fb.setFields(dto.getFields().stream().map(ffMapper::fromCreationDto).collect(Collectors.toList()));
        return fb;
    }

    public FormBlock fromChangeDto(FormBlockChangeDto dto) {
        FormBlock fb = new FormBlock();
        fb.setId(dto.getId());
        fb.setName(dto.getName());
        fb.setDescription(dto.getDescription());
        fb.setFields(dto.getFields().stream().map(ffMapper::fromChangeDto).collect(Collectors.toList()));
        return fb;
    }
}
