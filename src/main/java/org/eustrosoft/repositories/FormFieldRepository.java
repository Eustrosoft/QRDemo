package org.eustrosoft.repositories;

import org.eustrosoft.entitites.FormField;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormFieldRepository extends CrudRepository<FormField, Long> {

}
