package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.FormChangeDto;
import org.eustrosoft.dtos.FormCreationDto;
import org.eustrosoft.dtos.FormDto;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.repositories.projections.FormComplexProjection;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FormMapper extends EntityMapper {
    private final FormFieldMapper ffMapper;
    private final FileMapper fileMapper;

    public Form fromDto(FormDto dto) {
        if (dto == null) {
            return null;
        }
        Form form = super.toEntity(dto, Form.class);
        form.setData(dto.getData());
        return form;
    }

    public FormDto toDto(Form form) {
        if (form == null) {
            return null;
        }
        FormDto dto = super.toDto(form, FormDto.class);
        if (form.getData() != null) {
            dto.setData(form.getData());
        }
        if (form.getFields() != null) {
            dto.setFields(ffMapper.toListDto(form.getFields()));
        }
        if (form.getFiles() != null) {
            dto.setFiles(fileMapper.toListDto(form.getFiles()));
        }
        return dto;
    }

    public FormDto toDto(FormSimpleProjection form) {
        if (form == null) {
            return null;
        }
        return super.toDtoFromProjection(form, FormDto.class);
    }

    public FormDto toDto(FormComplexProjection form) {
        if (form == null) {
            return null;
        }
        FormDto dto = super.toDtoFromProjection(form, FormDto.class);
        if (form.getData() != null) {
            dto.setData(form.getData());
        }
        if (form.getFields() != null) {
            dto.setFields(ffMapper.toListDto(form.getFields()));
        }
        if (form.getFiles() != null) {
            dto.setFiles(fileMapper.toListDto(form.getFiles()));
        }
        return dto;
    }

    public Form fromCreationDto(FormCreationDto dto) {
        if (dto == null) {
            return null;
        }
        Form form = new Form();
        form.setName(dto.getName());
        form.setDescription(dto.getDescription());
        if (dto.getData() != null) {
            form.setData(dto.getData().toString());
        }
        if (dto.getFields() != null) {
            form.setFields(
                    dto.getFields().stream()
                            .map(ffMapper::fromCreationDto).collect(Collectors.toList())
            );
        }
        if (dto.getFiles() != null) {
            form.setFiles(dto.getFiles());
        }
        return form;
    }

    public Form fromChangeDto(FormChangeDto dto) {
        if (dto == null) {
            return null;
        }
        Form form = new Form();
        form.setId(dto.getId());
        form.setName(dto.getName());
        form.setDescription(dto.getDescription());
        if (dto.getData() != null) {
            form.setData(dto.getData().toString());
        }
        if (dto.getFields() != null) {
            form.setFields(
                    dto.getFields().stream()
                            .map(ffMapper::fromChangeDto).collect(Collectors.toList())
            );
        }
        if (dto.getFiles() != null) {
            form.setFiles(dto.getFiles());
        }
        return form;
    }

    public Form toEntity(FormComplexProjection formComplexProjection) {
        if (formComplexProjection == null) {
            return null;
        }
        Form form = new Form();
        form.setId(formComplexProjection.getId());
        form.setName(formComplexProjection.getName());
        form.setDescription(formComplexProjection.getDescription());
        form.setCreated(formComplexProjection.getCreated());
        form.setUpdated(formComplexProjection.getUpdated());
        form.setParticipantId(formComplexProjection.getParticipantId());
        form.setData(formComplexProjection.getData());
        if (formComplexProjection.getFields() != null) {
            form.setFields(formComplexProjection.getFields());
        }
        if (formComplexProjection.getFiles() != null) {
            form.setFiles(fileMapper.toListEntity(formComplexProjection.getFiles()));
        }
        return form;
    }
}
