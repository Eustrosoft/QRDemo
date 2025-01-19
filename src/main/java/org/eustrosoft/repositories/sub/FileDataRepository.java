package org.eustrosoft.repositories.sub;

import org.eustrosoft.entitites.subentities.FileData;
import org.eustrosoft.entitites.subentities.ParticipantData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDataRepository extends CrudRepository<FileData, Long> {
}
