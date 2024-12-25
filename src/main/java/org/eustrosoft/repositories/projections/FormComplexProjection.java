package org.eustrosoft.repositories.projections;

import org.eustrosoft.entitites.FormField;

import java.util.List;

public interface FormComplexProjection extends EntityProjection {

    String getData();

    List<FormField> getFields();

    List<FileProjection> getFiles();
}
