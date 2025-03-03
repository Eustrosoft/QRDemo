package org.eustrosoft.exceptions;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ExceptionObject<T extends JsonApiError> {
    @Getter
    private final List<T> errors;
}
