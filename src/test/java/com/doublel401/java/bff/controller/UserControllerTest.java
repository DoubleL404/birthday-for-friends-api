package com.doublel401.java.bff.controller;

import com.doublel401.java.bff.exception.BadRequestException;
import com.doublel401.java.bff.exception.GlobalExceptionHandler;
import com.doublel401.java.bff.service.UserService;
import com.doublel401.java.bff.utils.JsonUtils;
import com.doublel401.java.bff.vo.FieldErrorVO;
import com.doublel401.java.bff.vo.SignUpVO;
import com.doublel401.java.bff.vo.UserResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private final String signUpUrl = "/api/auth/signUp";

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addPlaceholderValue("bff.user.sign.up.url", signUpUrl)
                .build();
    }

    @Test
    public void testSignUpUserSuccess() throws Exception {
        SignUpVO signUpVO = new SignUpVO();
        UserResponseVO userResponseVO = new UserResponseVO();

        Mockito.when(userService.createUser(any(SignUpVO.class))).thenReturn(userResponseVO);

        mockMvc.perform(post(signUpUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(signUpVO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    public void testSignUpUserNotSuccess() throws Exception {
        SignUpVO signUpVO = new SignUpVO();

        Mockito.doThrow(new BadRequestException("There are invalid fields in the request", List.of(new FieldErrorVO())))
                .when(userService).createUser(any(SignUpVO.class));

        mockMvc.perform(post(signUpUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(signUpVO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("There are invalid fields in the request")))
                .andExpect(jsonPath("$.path", is(signUpUrl)));
    }
}
