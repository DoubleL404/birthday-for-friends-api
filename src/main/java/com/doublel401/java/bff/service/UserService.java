package com.doublel401.java.bff.service;

import com.doublel401.java.bff.constant.AppConstants;
import com.doublel401.java.bff.constant.FieldConstants;
import com.doublel401.java.bff.constant.MessageCodes;
import com.doublel401.java.bff.constant.Messages;
import com.doublel401.java.bff.entity.*;
import com.doublel401.java.bff.enums.RoleEnum;
import com.doublel401.java.bff.exception.BadRequestException;
import com.doublel401.java.bff.exception.IllegalArgumentException;
import com.doublel401.java.bff.exception.InternalServerException;
import com.doublel401.java.bff.repository.*;
import com.doublel401.java.bff.utils.DateTimeUtils;
import com.doublel401.java.bff.utils.TokenUtils;
import com.doublel401.java.bff.vo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final LanguageRepository languageRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenUtils tokenUtils;

    public UserService(UserRepository userRepository, GenderRepository genderRepository,
                       LanguageRepository languageRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository, TokenUtils tokenUtils)
    {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.languageRepository = languageRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenUtils = tokenUtils;
    }

    /**
     * Signup new user
     *
     * @param signUpVO sign up data
     * @return user response VO
     */
    @Transactional
    public UserResponseVO createUser(SignUpVO signUpVO) {
        List<FieldErrorVO> fieldErrors = validateSignUpData(signUpVO);
        if (!fieldErrors.isEmpty()) {
            throw new BadRequestException("There are invalid fields in the request", fieldErrors);
        }

        // Build user model and save to DB
        User user = buildFromVO(signUpVO);
        userRepository.save(user);

        return buildFromUser(user);
    }

    /**
     * Refresh jwt access token
     * @param token refresh token
     * @param request http servlet request
     * @return TokenResponseVO
     */
    public TokenResponseVO refreshToken(String token, HttpServletRequest request) {
        UUID uuidToken = getTokenFromString(token);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(uuidToken)
                .orElseThrow(() -> new BadRequestException("Refresh token is invalid"));

        if (Instant.now().isAfter(refreshToken.getExpiredTime())) {
            throw new BadRequestException("Refresh token is expired");
        }

        // Get issuer -> the request URL
        String issuer = request.getRequestURL().toString();

        String accessToken = tokenUtils.generateAccessToken(refreshToken.getUsername(), issuer);

        return new TokenResponseVO(accessToken);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    /**
     * Validate user data before save to DB
     *
     * @param signUpVO user sign up data
     * @return list of field error VOs
     */
    private List<FieldErrorVO> validateSignUpData(SignUpVO signUpVO) {
        List<FieldErrorVO> fieldErrors = new ArrayList<>();

        // Validate email process
        if (StringUtils.isBlank(signUpVO.getEmail())
                || !signUpVO.getEmail().matches(AppConstants.EMAIL_REGEX)) {
            fieldErrors.add(new FieldErrorVO(
                    FieldConstants.EMAIL,
                    MessageCodes.CODE_EMAIL_INVALID,
                    Messages.MSG_EMAIL_INVALID));
        } else if(userRepository.existsByEmail(signUpVO.getEmail())) {
            fieldErrors.add(new FieldErrorVO(
                    FieldConstants.EMAIL,
                    MessageCodes.CODE_EMAIL_EXISTED,
                    Messages.MSG_EMAIL_EXISTED));
        }

        // Validate password process
        if (StringUtils.isBlank(signUpVO.getPassword())
                || !signUpVO.getPassword().matches(AppConstants.PASSWORD_REGEX))
        {
            fieldErrors.add(new FieldErrorVO(
                    FieldConstants.PASSWORD,
                    MessageCodes.CODE_PASSWORD_INVALID,
                    Messages.MSG_PASSWORD_INVALID));
        }

        // Validate first name process
        if (StringUtils.isBlank(signUpVO.getFirstName())) {
            fieldErrors.add(new FieldErrorVO(
                    FieldConstants.FIRST_NAME,
                    MessageCodes.CODE_FIRST_NAME_BLANK,
                    Messages.MSG_FIRST_NAME_BLANK));
        }

        // Validate last name process
        if (StringUtils.isBlank(signUpVO.getLastName())) {
            fieldErrors.add(new FieldErrorVO(
                    FieldConstants.LAST_NAME,
                    MessageCodes.CODE_LAST_NAME_BLANK,
                    Messages.MSG_LAST_NAME_BLANK));
        }

        // Validate birthdate process
        if (StringUtils.isBlank(signUpVO.getBirthdate())) {
            fieldErrors.add(new FieldErrorVO(
                    FieldConstants.BIRTHDATE,
                    MessageCodes.CODE_BIRTHDATE_BLANK,
                    Messages.MSG_BIRTHDATE_BLANK));
        } else {
            try {
                LocalDate birthdate = LocalDate.parse(signUpVO.getBirthdate(),
                        DateTimeFormatter.ofPattern(DateTimeUtils.LOCAL_DATE_FORMAT));

                // Check birthdate is in the past
                if (birthdate.isAfter(LocalDate.now())) {
                    fieldErrors.add(new FieldErrorVO(
                            FieldConstants.BIRTHDATE,
                            MessageCodes.CODE_BIRTHDATE_INVALID_PAST,
                            Messages.MSG_BIRTHDATE_INVALID_PAST));
                }
            } catch (DateTimeParseException e) {
                fieldErrors.add(new FieldErrorVO(
                        FieldConstants.BIRTHDATE,
                        MessageCodes.CODE_BIRTHDATE_INVALID_FORMAT,
                        Messages.MSG_BIRTHDATE_INVALID_FORMAT));
            }
        }

        // Validate gender process
        if (Objects.isNull(signUpVO.getGenderId())) {
            fieldErrors.add(new FieldErrorVO(
                    FieldConstants.GENDER_ID,
                    MessageCodes.CODE_GENDER_ID_BLANK,
                    Messages.MSG_GENDER_ID_BLANK));
        } else {
            Optional<Gender> genderOptional = genderRepository.findById(signUpVO.getGenderId());
            if (genderOptional.isEmpty()) {
                fieldErrors.add(new FieldErrorVO(
                        FieldConstants.GENDER_ID,
                        MessageCodes.CODE_GENDER_NOT_FOUND,
                        Messages.MSG_GENDER_NOT_FOUND));
            }
        }

        // Validate language preference process
        if (StringUtils.isBlank(signUpVO.getLanguageCode())) {
            fieldErrors.add(new FieldErrorVO(
                    FieldConstants.LANGUAGE_CODE,
                    MessageCodes.CODE_LANGUAGE_BLANK,
                    Messages.MSG_LANGUAGE_BLANK));
        } else {
            Optional<Language> languageOptional = languageRepository.findByCode(signUpVO.getLanguageCode());
            if (languageOptional.isEmpty()) {
                fieldErrors.add(new FieldErrorVO(
                        FieldConstants.LANGUAGE_CODE,
                        MessageCodes.CODE_LANGUAGE_NOT_FOUND,
                        Messages.MSG_LANGUAGE_NOT_FOUND));
            }
        }

        return fieldErrors;
    }

    /**
     * Helper method for building user entity from user sign up VO
     *
     * @param signUpVO user request data
     * @return user model
     */
    private User buildFromVO(SignUpVO signUpVO) {
        User user = User.builder()
                .email(signUpVO.getEmail())
                .password(passwordEncoder.encode(signUpVO.getPassword()))
                .firstName(signUpVO.getFirstName())
                .lastName(signUpVO.getLastName())
                .birthdate(DateTimeUtils.parseBirthdateFromString(signUpVO.getBirthdate()))
                .isAccountNonExpired(Boolean.TRUE)
                .isAccountNonLocked(Boolean.TRUE)
                .isCredentialsNonExpired(Boolean.TRUE)
                .isEnabled(Boolean.TRUE)
                .build();

        // Assign gender to user
        Gender gender = genderRepository.findById(signUpVO.getGenderId())
                .orElseThrow(() -> new InternalServerException("Gender is not found in the system."));
        user.setGender(gender);

        // Assign ROLE_USER to normal user
        Role role = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new InternalServerException("User role is not found in the system."));
        user.setRole(role);

        // If user provide language code, assign to user
        Language language = languageRepository.findByCode(signUpVO.getLanguageCode())
                .orElseThrow(() -> new InternalServerException("Language is not found in the system."));
        user.setLanguage(language);

        return user;
    }

    /**
     * Helper method for building user response VO from user entity
     *
     * @param user user entity
     * @return user response VO
     */
    private UserResponseVO buildFromUser(User user) {
        return UserResponseVO.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthdate(DateTimeUtils.formatBirthdateToString(user.getBirthdate()))
                .gender(new GenderVO(user.getGender()))
                .language(new LanguageVO(user.getLanguage()))
                .build();
    }

    /**
     * Helper method for getting uuid token from string
     *
     * @param token string of token
     * @return uuid token
     */
    private UUID getTokenFromString(String token) {
        if (StringUtils.isBlank(token)) return null;

        try {
            return UUID.fromString(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("Refresh token is invalid");
        }
    }
}
