package com.doublel401.java.bff.utils;


import com.doublel401.java.bff.exception.IllegalArgumentException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

final public class DateTimeUtils {
    private DateTimeUtils() {}

    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Helper method for parsing string to LocalDate
     *
     * @param birthdate the string to parse
     * @return the parsed local date
     */
    public static LocalDate parseBirthdateFromString(String birthdate) {
        try {
            return LocalDate.parse(birthdate, DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Cannot parse birthdate", e);
        }
    }

    /**
     * Helper method for formatting birthdate to string
     *
     * @param birthdate the local date to format
     * @return the formatted date string
     */
    public static String formatBirthdateToString(LocalDate birthdate) {
        try {
            return birthdate.format(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Cannot format birthdate", e);
        }
    }
}
