package org.eustrosoft.exceptions.custom;

import org.eustrosoft.exceptions.CommonException;
import org.eustrosoft.exceptions.JsonApiError;

import java.util.List;

public class IllegalActionException extends CommonException {
    public IllegalActionException(JsonApiError error) {
        super(error);
    }

    public IllegalActionException(List<JsonApiError> errors) {
        super(errors);
    }
}
