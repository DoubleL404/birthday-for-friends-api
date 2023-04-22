package com.doublel401.java.bff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserResponseVO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String birthdate;
    private GenderVO gender;
    private LanguageVO language;
}
