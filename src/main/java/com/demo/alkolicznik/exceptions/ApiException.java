package com.demo.alkolicznik.exceptions;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Getter
@Setter
@ToString
// the annotation below prevents from showing additional fields of 'RuntimeException' class
@JsonIncludeProperties({"timestamp", "status", "error", "message", "path"})
@JsonPropertyOrder({"timestamp", "status", "error", "message", "path"})
public class ApiException extends RuntimeException {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    @Autowired
    public ApiException(HttpStatus status, String message, String path) {
        this.timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now(Clock.systemUTC()));
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
