package org.eustrosoft.repositories.projections;

import org.eustrosoft.entitites.FormField;

import java.util.List;

public interface FormWithFieldsProjection extends FormSimpleProjection {
    List<FormField> getFields();
}
