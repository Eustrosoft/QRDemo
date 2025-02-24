package org.eustrosoft.mappers;

import lombok.SneakyThrows;
import org.eustrosoft.dtos.EntityDto;
import org.eustrosoft.entitites.DbEntity;
import org.eustrosoft.repositories.projections.EntityProjection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EntityMapper {

    public EntityDto toDto(DbEntity entity) {
        if (entity == null) {
            return null;
        }
        return new EntityDto(
                entity.getId(),
                entity.getCreated(),
                entity.getUpdated(),
                entity.getName(),
                entity.getDescription()
        );
    }

    @SneakyThrows
    public <T extends EntityDto> T toDto(DbEntity entity, Class<T> clazz) {
        if (entity == null) {
            return null;
        }
        T entityDto = clazz.getDeclaredConstructor().newInstance();
        entityDto.setId(entity.getId());
        entityDto.setCreated(entity.getCreated());
        entityDto.setUpdated(entity.getUpdated());
        entityDto.setName(entity.getName());
        entityDto.setDescription(entity.getDescription());
        entity.setParticipantId(entity.getParticipantId());
        return entityDto;
    }

    @SneakyThrows
    public <T extends EntityDto> T toDtoFromProjection(EntityProjection projection, Class<T> clazz) {
        if (projection == null) {
            return null;
        }
        T entityDto = clazz.getDeclaredConstructor().newInstance();
        entityDto.setId(projection.getId());
        entityDto.setCreated(projection.getCreated());
        entityDto.setUpdated(projection.getUpdated());
        entityDto.setName(projection.getName());
        entityDto.setDescription(projection.getDescription());
        return entityDto;
    }

    @SneakyThrows
    public <T extends DbEntity> T toEntity(EntityDto dto, Class<T> clazz) {
        if (dto == null) {
            return null;
        }
        T entity = clazz.getDeclaredConstructor().newInstance();
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated());
        entity.setUpdated(dto.getUpdated());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @SneakyThrows
    public <T extends DbEntity> T toEntity(EntityProjection projection, Class<T> clazz) {
        if (projection == null) {
            return null;
        }
        T entity = clazz.getDeclaredConstructor().newInstance();
        entity.setId(projection.getId());
        entity.setCreated(projection.getCreated());
        entity.setUpdated(projection.getUpdated());
        entity.setName(projection.getName());
        entity.setDescription(projection.getDescription());
        entity.setParticipantId(projection.getParticipantId());
        return entity;
    }

    @SneakyThrows
    public <T extends DbEntity> T toEntityFromId(Long id, Class<T> clazz) {
        if (id == null) {
            return null;
        }
        T entity = clazz.getDeclaredConstructor().newInstance();
        entity.setId(id);
        return entity;
    }

    @SneakyThrows
    public <T extends DbEntity> List<T> toListEntitiesFromIdsList(List<Long> ids, Class<T> clazz) {
        if (ids == null) {
            return null;
        }
        List<T> entities = new ArrayList<>();
        for (Long id : ids) {
            entities.add(toEntityFromId(id, clazz));
        }
        return entities;
    }

}
