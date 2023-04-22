package com.doublel401.java.bff.exception;

import com.doublel401.java.bff.vo.FieldErrorVO;
import lombok.Getter;
import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {
    private List<FieldErrorVO> fieldErrors;

    public BadRequestException(String message, List<FieldErrorVO> fieldErrors) {
        this(message);
        this.fieldErrors = fieldErrors;
    }

    public BadRequestException(String message) {
        super(message);
    }
}
