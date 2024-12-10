package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.FormFieldChangeDto;
import org.eustrosoft.dtos.FormFieldCreationDto;
import org.eustrosoft.dtos.FormFieldDto;
import org.eustrosoft.entitites.FormField;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormFieldMapper extends EntityMapper {

    public FormFieldDto toDto(FormField formField) {
        FormFieldDto dto = new FormFieldDto();
        dto.setId(formField.getId());
        dto.setIsPublic(formField.getIsPublic());
        dto.setName(formField.getName());
        dto.setIsStatic(formField.getIsStatic());
        dto.setType(formField.getType());
        dto.setPlaceholder(formField.getPlaceholder());
        dto.setCreated(formField.getCreated());
        dto.setUpdated(formField.getUpdated());
        return dto;
    }

    public FormField fromCreationDto(FormFieldCreationDto dto) {
        FormField formField = new FormField();
        formField.setName(dto.getName());
        formField.setType(dto.getType());
        formField.setPlaceholder(dto.getPlaceholder());
        formField.setIsPublic(dto.getIsPublic());
        formField.setIsStatic(dto.getIsStatic());
        return formField;
    }

    public FormField fromChangeDto(FormFieldChangeDto dto) {
        FormField formField = new FormField();
        formField.setId(dto.getId());
        formField.setName(dto.getName());
        formField.setType(dto.getType());
        formField.setPlaceholder(dto.getPlaceholder());
        formField.setIsPublic(dto.getIsPublic());
        formField.setIsStatic(dto.getIsStatic());
        return formField;
    }
}
