package com.doublel401.java.bff.vo;

import lombok.Data;

@Data
public class NewUserVO {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String birthdate;
    private Long genderId;
}
