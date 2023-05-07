package com.doublel401.java.bff.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FieldErrorVO {
    private String field;
    private String error;
    private String message;
}
