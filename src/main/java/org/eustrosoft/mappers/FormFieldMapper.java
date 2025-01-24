package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.FormFieldChangeDto;
import org.eustrosoft.dtos.FormFieldCreationDto;
import org.eustrosoft.dtos.FormFieldDto;
import org.eustrosoft.entitites.FormField;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FormFieldMapper extends EntityMapper {

    public FormFieldDto toDto(FormField formField) {
        if (formField == null) {
            return null;
        }
        FormFieldDto dto = new FormFieldDto();
        dto.setId(formField.getId());
        dto.setIsPublic(formField.getIsPublic());
        dto.setName(formField.getName());
        dto.setCaption(formField.getCaption());
        dto.setIsStatic(formField.getIsStatic());
        dto.setFieldOrder(formField.getFieldOrder());
        dto.setFieldType(formField.getFieldType());
        dto.setPlaceholder(formField.getPlaceholder());
        return dto;
    }

    public List<FormFieldDto> toListDto(List<FormField> formField) {
        if (formField == null) {
            return null;
        }
        return formField.stream().map(this::toDto)
                .sorted(Comparator.nullsFirst(Comparator.comparingInt(FormFieldDto::getFieldOrder)))
                .collect(Collectors.toList());
    }

    public FormField fromCreationDto(FormFieldCreationDto dto) {
        if (dto == null) {
            return null;
        }
        FormField formField = new FormField();
        formField.setName(dto.getName());
        formField.setCaption(dto.getCaption());
        formField.setFieldType(dto.getFieldType());
        formField.setPlaceholder(dto.getPlaceholder());
        formField.setFieldOrder(dto.getFieldOrder());
        formField.setIsPublic(dto.getIsPublic());
        formField.setIsStatic(dto.getIsStatic());
        return formField;
    }

    public FormField fromChangeDto(FormFieldChangeDto dto) {
        FormField formField = fromCreationDto(dto);
        formField.setId(dto.getId());
        return formField;
    }
}
