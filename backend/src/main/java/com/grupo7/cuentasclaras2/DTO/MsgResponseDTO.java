package com.grupo7.cuentasclaras2.DTO;

import org.springframework.http.HttpStatus;

public class MsgResponseDTO {
    private String message;
    private HttpStatus status;

    public MsgResponseDTO() {
    }

    public MsgResponseDTO(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

}
