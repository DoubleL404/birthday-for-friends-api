package com.doublel401.java.bff.constant;

final public class Messages {
    private Messages() {}

    public static final String MSG_EMAIL_INVALID = "Please enter a valid email address.";
    public static final String MSG_EMAIL_EXISTED = "Email is already used.";
    public static final String MSG_PASSWORD_INVALID = "Please enter a valid password (must contains at least one digit, one lowercase letter, one uppercase letter and one special character.";
    public static final String MSG_FIRST_NAME_BLANK = "Please enter your first name.";
    public static final String MSG_LAST_NAME_BLANK = "Please enter your last name.";
    public static final String MSG_BIRTHDATE_BLANK = "Please enter your birthdate.";
    public static final String MSG_BIRTHDATE_INVALID_FORMAT = "Your birthdate should follow format (yyyy/MM/dd).";
    public static final String MSG_BIRTHDATE_INVALID_PAST = "Your birthdate should from the past.";
    public static final String MSG_GENDER_ID_BLANK = "Please select your gender.";
    public static final String MSG_GENDER_NOT_FOUND = "Gender is not found.";
    public static final String MSG_LANGUAGE_BLANK = "Language should be provided.";
    public static final String MSG_LANGUAGE_NOT_FOUND = "Language is not found.";
}
