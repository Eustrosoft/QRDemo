package org.eustrosoft.utils.comparators;

import org.eustrosoft.dtos.FormFieldDto;

import java.util.Comparator;
import java.util.Date;

public class FormFieldComparator implements Comparator<FormFieldDto> {
    @Override
    public int compare(FormFieldDto o1, FormFieldDto o2) {
        if (o1 == null || o2 == null) {
            return 0;
        }
        Date updated1 = o1.getCreated();
        Date updated2 = o2.getCreated();
        if (updated1 == null || updated2 == null) {
            return 0;
        }
        return updated1.compareTo(updated2);
    }
}
