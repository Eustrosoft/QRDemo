package org.eustrosoft.mappers;

import lombok.SneakyThrows;
import org.eustrosoft.dtos.EntityDto;
import org.eustrosoft.entitites.DbEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public EntityDto toDto(DbEntity entity) {
        if (entity == null) {
            return null;
        }
        return new EntityDto(
                entity.getId(),
                entity.getCreated(),
                entity.getUpdated()
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
        return entityDto;
    }

    public DbEntity toEntity(EntityDto dto) {
        if (dto == null) {
            return null;
        }
        DbEntity entity = new DbEntity();
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated());
        entity.setUpdated(dto.getUpdated());
        return entity;
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
        return entity;
    }

}
