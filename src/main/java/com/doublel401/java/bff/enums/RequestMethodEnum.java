package com.doublel401.java.bff.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
public enum RequestMethodEnum {
    READ("read", "GET"),
    CREATE("create", "POST"),
    UPDATE("update", "PUT"),
    DELETE("delete", "DELETE"),
    ANY("*", "*");

    private final String action;
    private final String method;

    private RequestMethodEnum(String action, String method) {
        this.action = action;
        this.method = method;
    }

    /**
     * Get RequestMethodEnum by action
     *
     * @param action The action of RequestMethodEnum
     * @return RequestMethodEnum
     */
    public static RequestMethodEnum getByAction(String action) {
        if (StringUtils.isBlank(action)) return null;

        return Arrays.stream(RequestMethodEnum.values())
                .filter(item -> StringUtils.equalsAnyIgnoreCase(item.getAction(), action))
                .findFirst().orElse(null);
    }

    /**
     * Get RequestMethodEnum by method
     * @param method The method of RequestMethodEnum
     * @return RequestMethodEnum
     */
    public static RequestMethodEnum getByMethod(String method) {
        if (StringUtils.isBlank(method)) return null;

        return Arrays.stream(RequestMethodEnum.values())
                .filter(item -> StringUtils.equalsAnyIgnoreCase(item.getMethod(), method))
                .findFirst().orElse(null);
    }
}
