package com.demo.alkolicznik.exceptions;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIncludeProperties({"timestamp", "status", "error", "message", "path"})
@JsonPropertyOrder({"timestamp", "status", "error", "message", "path"})
public class ApiException extends RuntimeException {

    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiException(HttpStatus status, String message, String path) {
		// TODO: adjust zone id to user
		this.timestamp = ZonedDateTime.now(ZoneId.of("Poland"));
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
