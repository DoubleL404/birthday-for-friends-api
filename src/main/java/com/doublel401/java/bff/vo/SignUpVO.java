package com.doublel401.java.bff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignUpVO {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String birthdate;
    private Long genderId;
    private String languageCode;
}
