package com.demo.alkolicznik.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.ZonedDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
// the annotation below prevents from showing additional fields of 'RuntimeException' class
@JsonIncludeProperties({"timestamp", "status", "error", "message", "path"})
@JsonPropertyOrder({"timestamp", "status", "error", "message", "path"})
public class ApiException extends RuntimeException {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiException(HttpStatus status, String message, String path) {
		this.timestamp = ZonedDateTime.now(Clock.systemUTC());
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
