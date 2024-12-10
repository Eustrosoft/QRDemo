package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.FormChangeDto;
import org.eustrosoft.dtos.FormCreationDto;
import org.eustrosoft.dtos.FormDto;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FormMapper extends EntityMapper {
    private final FormBlockMapper fbMapper;

    public Form fromDto(FormDto dto) {
        Form form = new Form();
        form.setId(dto.getId());
        form.setName(dto.getName());
        form.setDescription(dto.getDescription());
        form.setData(dto.getData());
        form.setCreated(dto.getCreated());
        form.setUpdated(dto.getUpdated());
        return form;
    }

    public FormDto toDto(Form form) {
        FormDto dto = new FormDto();
        dto.setId(form.getId());
        dto.setName(form.getName());
        dto.setDescription(form.getDescription());
        dto.setCreated(form.getCreated());
        dto.setUpdated(form.getUpdated());
        if (form.getData() != null) {
            dto.setData(form.getData());
        }
        if (form.getBlocks() != null) {
            dto.setBlocks(form.getBlocks().stream().map(fbMapper::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    public FormDto toDto(FormSimpleProjection form) {
        FormDto dto = new FormDto();
        dto.setId(form.getId());
        dto.setName(form.getName());
        dto.setDescription(form.getDescription());
        dto.setCreated(form.getCreated());
        dto.setUpdated(form.getUpdated());
        return dto;
    }

    public Form fromCreationDto(FormCreationDto dto) {
        Form form = new Form();
        form.setName(dto.getName());
        form.setDescription(dto.getDescription());
        if (dto.getData() != null) {
            form.setData(dto.getData().toString());
        }
        form.setBlocks(dto.getBlocks().stream().map(fbMapper::fromCreationDto).collect(Collectors.toList()));
        return form;
    }

    public Form fromChangeDto(FormChangeDto dto) {
        Form form = new Form();
        form.setId(dto.getId());
        form.setName(dto.getName());
        form.setDescription(dto.getDescription());
        if (dto.getData() != null) {
            form.setData(dto.getData().toString());
        }
        form.setBlocks(dto.getBlocks().stream().map(fbMapper::fromChangeDto).collect(Collectors.toList()));
        return form;
    }
}
