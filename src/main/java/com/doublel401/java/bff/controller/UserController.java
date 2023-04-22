package com.doublel401.java.bff.controller;

import com.doublel401.java.bff.service.UserService;
import com.doublel401.java.bff.vo.ResponseVO;
import com.doublel401.java.bff.vo.SignUpVO;
import com.doublel401.java.bff.vo.UserResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("${bff.user.sign.up.url}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseVO<UserResponseVO> signup(@RequestBody SignUpVO signUpVO) {
        return ResponseVO.created("Sign up user successfully.", userService.createUser(signUpVO));
    }
}
