package org.eustrosoft.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class JsonApiError {
    private HttpStatus status;
    private Long code;
    private String title;
    private String detail;
    private Source source;
    @JsonIgnore
    private Object[] parameters;

    public JsonApiError(HttpStatus status, String title, String detail, Throwable exception) {
        this.status = status;
        this.code = -1L;
        this.title = title;
        this.detail = detail;
        this.source = new Source(
                exception.getLocalizedMessage(),
                ServiceName.QR_DEMO
        );
    }

    public JsonApiError(HttpStatus status, Long code, String title, String detail, Source source) {
        this.status = status;
        this.code = code;
        this.title = title;
        this.detail = detail;
        this.source = source;
    }

    public JsonApiError(HttpStatus status, Long code, String title, String detail, Source source, Object... parameters) {
        this.status = status;
        this.code = code;
        this.title = title;
        this.detail = detail;
        this.source = source;
        this.parameters = parameters;
    }

    @JsonProperty("status")
    public Integer getStatusCode() {
        if (status == null) {
            return -1;
        }
        return status.value();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Source {
        private String property;
        private ServiceName service;

        public Source(String property) {
            this.property = property;
            this.service = ServiceName.QR_DEMO;
        }
    }
}
