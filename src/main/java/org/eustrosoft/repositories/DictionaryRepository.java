package org.eustrosoft.repositories;

import org.eustrosoft.entitites.Dictionary;
import org.eustrosoft.entitites.composite.DictionaryCompositeId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryRepository extends CrudRepository<Dictionary, DictionaryCompositeId> {

    List<Dictionary> findAllByCode(String code);

    Optional<Dictionary> findByCodeAndName(String code, String name);
}
