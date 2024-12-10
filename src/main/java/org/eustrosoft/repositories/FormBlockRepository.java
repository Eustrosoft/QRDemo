package org.eustrosoft.repositories;

import org.eustrosoft.entitites.FormBlock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormBlockRepository extends CrudRepository<FormBlock, Long> {

}
