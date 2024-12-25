package org.eustrosoft.repositories;

import org.eustrosoft.entitites.Dictionary;
import org.eustrosoft.entitites.composite.DictionaryCompositeId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRepository extends CrudRepository<Dictionary, DictionaryCompositeId> {

    List<Dictionary> findAllByCode(String code);
}
