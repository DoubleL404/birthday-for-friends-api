package com.doublel401.java.bff.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignInVO {
    private String accessToken;
    private String refreshToken;
}
