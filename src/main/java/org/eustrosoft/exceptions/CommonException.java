package org.eustrosoft.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonException extends RuntimeException {

    private List<JsonApiError> errors;

    public CommonException(JsonApiError error) {
        this.errors = Collections.singletonList(error);
    }
}
