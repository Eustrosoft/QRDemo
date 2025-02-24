package org.eustrosoft.mappers;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.dtos.QRChangeDto;
import org.eustrosoft.dtos.QRCreationDto;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.repositories.projections.QRProjection;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.eustrosoft.repositories.projections.QRSimplestProjection;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QrMapper extends EntityMapper {
    private final FormMapper formMapper;
    private final FileMapper fileMapper;

    public QRDto toDto(QRSimplestProjection qr) {
        QRDto dto = super.toDtoFromProjection(qr, QRDto.class);
        dto.setCode(qr.getCode());
        return dto;
    }

    public QRDto toDto(QRSimpleProjection qr) {
        QRDto dto = toDto((QRSimplestProjection) qr);
        dto.setName(qr.getName());
        dto.setDescription(qr.getDescription());
        dto.setData(qr.getData());
        dto.setForm(formMapper.toDto(qr.getForm()));
        return dto;
    }

    public QRDto toDto(QRProjection qr) {
        QRDto dto = toDto((QRSimplestProjection) qr);
        dto.setCode(qr.getCode());
        dto.setData(qr.getData());
        dto.setForm(formMapper.toDto(qr.getForm()));
        dto.setFiles(fileMapper.toListDto(qr.getFiles()));
        return dto;
    }

    public QRDto toDto(QR qr) {
        QRDto dto = super.toDto(qr, QRDto.class);
        dto.setCode(qr.getCode());
        dto.setData(qr.getData());
        dto.setForm(formMapper.toDto(qr.getForm()));
        dto.setFiles(fileMapper.toListDto(qr.getFiles()));
        return dto;
    }

    public List<QRDto> toDtoList(Collection<QR> qrs) {
        if (Collections.isEmpty(qrs)) {
            return java.util.Collections.emptyList();
        }
        return qrs.stream().map(this::toDto).collect(Collectors.toList());
    }

    public QR fromCreationDto(QRCreationDto dto) {
        QR qr = new QR();
        qr.setName(dto.getName());
        qr.setDescription(dto.getDescription());
        qr.setCode(dto.getCode());
        if (dto.getData() != null) {
            qr.setData(dto.getData().toString());
        }
        if (dto.getFilesIds() != null) {
            qr.setFiles(fileMapper.toListEntitiesFromIdsList(dto.getFilesIds(), File.class));
        }
        if (dto.getFormId() != null) {
            qr.setFormId(dto.getFormId());
        }
        qr.setRange(toEntityFromId(dto.getRangeId(), QRRange.class));
        return qr;
    }

    public QR fromChangeDto(QRChangeDto dto) {
        QR qr = new QR();
        qr.setId(dto.getId());
        qr.setName(dto.getName());
        qr.setDescription(dto.getDescription());
        qr.setCode(dto.getCode());
        if (dto.getFormId() != null) {
            qr.setForm(formMapper.toEntityFromId(dto.getFormId(), Form.class));
        }
        if (dto.getData() != null) {
            qr.setData(dto.getData().toString());
        }
        if (dto.getFilesIds() != null) {
            qr.setFiles(fileMapper.toListEntitiesFromIdsList(dto.getFilesIds(), File.class));
        }
        return qr;
    }
}
