package com.demo.alkolicznik.exceptions;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.http.HttpStatus;

@JsonPropertyOrder({"timestamp", "status", "error", "message", "path"})
@NoArgsConstructor
@Getter
@Setter
public class ApiError {

    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiError(HttpStatus status, String message, String path) {
		// TODO: adjust zone id to user
		this.timestamp = ZonedDateTime.now(ZoneId.of("Poland"));
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
