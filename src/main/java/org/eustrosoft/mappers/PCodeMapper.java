package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.PCodeChangeDto;
import org.eustrosoft.dtos.PCodeCreationDto;
import org.eustrosoft.dtos.PCodeDto;
import org.eustrosoft.entitites.PCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PCodeMapper {

    public PCode toModel(PCodeCreationDto dto) {
        if (dto == null) {
            return null;
        }
        PCode pCode = new PCode();
        pCode.setP(dto.getP());
        pCode.setP2(dto.getP2());
        pCode.setComment(dto.getComment());
        pCode.setHFields(dto.getHFields());
        pCode.setHFiles(dto.getHFiles());
        pCode.setP2Prompt(dto.getP2Prompt());
        pCode.setDocId(dto.getDocId());
        pCode.setRowId(dto.getRowId());
        pCode.setP2Mode(dto.getP2Mode());
        return pCode;
    }

    public PCode toModel(PCodeChangeDto dto) {
        if (dto == null) {
            return null;
        }
        PCode pCode = new PCode();
        pCode.setP(dto.getP());
        pCode.setP2(dto.getP2());
        pCode.setComment(dto.getComment());
        pCode.setHFields(dto.getHFields());
        pCode.setHFiles(dto.getHFiles());
        pCode.setP2Prompt(dto.getP2Prompt());
        pCode.setDocId(dto.getDocId());
        pCode.setRowId(dto.getRowId());
        pCode.setP2Mode(dto.getP2Mode());
        return pCode;
    }

    public PCodeDto toDto(PCode pCode) {
        if (pCode == null) {
            return null;
        }
        PCodeDto dto = new PCodeDto();
        dto.setComment(pCode.getComment());
        dto.setHFields(pCode.getHFields());
        dto.setHFiles(pCode.getHFiles());
        dto.setP(pCode.getP());
        dto.setP2(pCode.getP2());
        dto.setP2Mode(pCode.getP2Mode());
        dto.setP2Prompt(pCode.getP2Prompt());
        dto.setDocId(pCode.getDocId());
        dto.setRowId(pCode.getRowId());
        return dto;
    }

    public List<PCodeDto> toListDto(List<PCode> pCodes) {
        if (pCodes == null) {
            return Collections.emptyList();
        }
        List<PCodeDto> listDtos = new ArrayList<>();
        for (PCode code : pCodes) {
            if (code != null) {
                listDtos.add(toDto(code));
            }
        }
        return listDtos;
    }
}
