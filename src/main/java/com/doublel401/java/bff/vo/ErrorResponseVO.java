package com.doublel401.java.bff.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;


@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseVO {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldErrorVO> fieldErrors;

    public ErrorResponseVO(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ErrorResponseVO(int status, String error, String message, String path, List<FieldErrorVO> fieldErrors) {
        this(status, error, message, path);
        this.fieldErrors = fieldErrors;
    }

}
