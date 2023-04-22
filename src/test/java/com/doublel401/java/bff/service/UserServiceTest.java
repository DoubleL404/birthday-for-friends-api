package com.doublel401.java.bff.service;

import com.doublel401.java.bff.entity.Gender;
import com.doublel401.java.bff.entity.Language;
import com.doublel401.java.bff.entity.Role;
import com.doublel401.java.bff.entity.User;
import com.doublel401.java.bff.enums.RoleEnum;
import com.doublel401.java.bff.exception.BadRequestException;
import com.doublel401.java.bff.repository.GenderRepository;
import com.doublel401.java.bff.repository.LanguageRepository;
import com.doublel401.java.bff.repository.RoleRepository;
import com.doublel401.java.bff.repository.UserRepository;
import com.doublel401.java.bff.utils.DateTimeUtils;
import com.doublel401.java.bff.vo.SignUpVO;
import com.doublel401.java.bff.vo.UserResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private GenderRepository genderRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Test
    public void testCreatUserSuccess() {
        SignUpVO signUpVO = SignUpVO.builder()
                .email("test@test.test")
                .password("Password123@")
                .firstName("First")
                .lastName("Last")
                .birthdate("2000-02-23")
                .genderId(1L)
                .languageCode("vi")
                .build();

        Mockito.when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(genderRepository.findById(anyLong())).thenReturn(Optional.of(new Gender()));
        Mockito.when(roleRepository.findByName(any(RoleEnum.class))).thenReturn(Optional.of(new Role()));
        Mockito.when(languageRepository.findByCode(anyString())).thenReturn(Optional.of(new Language()));
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encode_password");
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(UUID.randomUUID());
            return savedUser;
        });

        UserResponseVO userResponseVO = userService.createUser(signUpVO);

        Mockito.verify(userRepository, Mockito.times(1)).save(any());
        assertNotNull(userResponseVO);
    }

    @Test
    public void testCreatUserWithEmailExisted() {
        SignUpVO signUpVO = SignUpVO.builder()
                .email("test@test.test")
                .password("Password123@")
                .firstName("First")
                .lastName("Last")
                .birthdate("2000-02-23")
                .genderId(1L)
                .languageCode("vi")
                .build();

        Mockito.when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(genderRepository.findById(anyLong())).thenReturn(Optional.of(new Gender()));
        Mockito.when(languageRepository.findByCode(anyString())).thenReturn(Optional.of(new Language()));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(signUpVO));

        Mockito.verify(userRepository, Mockito.times(0)).save(any());
        assertEquals(1, exception.getFieldErrors().size());
    }

    @Test
    public void testCreatUserWithBirthNotInThePast() {
        String invalidBirthdate = DateTimeUtils.formatBirthdateToString(LocalDate.now().plusDays(1));
        SignUpVO signUpVO = SignUpVO.builder()
                .email("test@test.test")
                .password("Password123@")
                .firstName("First")
                .lastName("Last")
                .birthdate(invalidBirthdate)
                .genderId(1L)
                .languageCode("vi")
                .build();

        Mockito.when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(genderRepository.findById(anyLong())).thenReturn(Optional.of(new Gender()));
        Mockito.when(languageRepository.findByCode(anyString())).thenReturn(Optional.of(new Language()));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(signUpVO));

        Mockito.verify(userRepository, Mockito.times(0)).save(any());
        assertEquals(1, exception.getFieldErrors().size());
    }

    @Test
    public void testCreatUserInvalidFields() {
        SignUpVO signUpVO = SignUpVO.builder()
                .email("invalid_email")
                .password("invalid_password")
                .firstName("First")
                .lastName("Last")
                .birthdate("2000/02/23")
                .genderId(100L)
                .languageCode("invalid")
                .build();

        Mockito.when(genderRepository.findById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(languageRepository.findByCode(anyString())).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(signUpVO));

        Mockito.verify(userRepository, Mockito.times(0)).save(any());
        assertEquals(5, exception.getFieldErrors().size());
    }

    @Test
    public void testCreatUserEmptyFields() {
        SignUpVO signUpVO = new SignUpVO();

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(signUpVO));

        Mockito.verify(userRepository, Mockito.times(0)).save(any());
        assertEquals(7, exception.getFieldErrors().size());
    }
}
