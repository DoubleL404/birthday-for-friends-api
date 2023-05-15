package com.doublel401.java.bff.controller;

import com.doublel401.java.bff.service.UserService;
import com.doublel401.java.bff.vo.ResponseVO;
import com.doublel401.java.bff.vo.SignUpVO;
import com.doublel401.java.bff.vo.TokenResponseVO;
import com.doublel401.java.bff.vo.UserResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("${bff.user.sign.up.url}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseVO<UserResponseVO> signUp(@RequestBody SignUpVO signUpVO) {
        return ResponseVO.created("Sign up user successfully.", userService.createUser(signUpVO));
    }

    @PostMapping("/api/auth/refresh")
    public ResponseVO<TokenResponseVO> refreshToken(@RequestParam("token") String token,
                                                    HttpServletRequest request) {
        TokenResponseVO tokenResponse =  userService.refreshToken(token, request);
        return ResponseVO.ok("Refresh token successfully", tokenResponse);
    }
}
