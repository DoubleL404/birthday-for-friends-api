package com.doublel401.java.bff.vo;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
public class ResponseVO<T> {
    private long timestamp;
    private int status;
    private String message;
    private T data;

    public ResponseVO(int status, String message, T data) {
        this.timestamp = Instant.now().toEpochMilli();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Create 200 OK response
     *
     * @param message Message
     * @param data Data
     * @return {@link ResponseVO} OK response
     * @param <T> Generic type of data
     */
    public static <T> ResponseVO<T> ok(String message, T data) {
        return new ResponseVO<>(HttpStatus.OK.value(), message, data);
    }

    /**
     * Create 201 CREATED response
     *
     * @param message Message
     * @param data Data
     * @return {@link ResponseVO}
     * @param <T> Generic type of data
     */
    public static <T> ResponseVO<T> created(String message, T data) {
        return new ResponseVO<>(HttpStatus.CREATED.value(), message, data);
    }
}
