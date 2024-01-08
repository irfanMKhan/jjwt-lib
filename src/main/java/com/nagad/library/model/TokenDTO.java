package com.nagad.library.model;

public class TokenDTO {
    private String subject;
    private String message;
    private Boolean status;

    public TokenDTO() {
        this.subject = "";
        this.message = "";
        this.status = false;
    }

    public TokenDTO(String subject, String message, Boolean status) {
        this.subject = subject;
        this.message = message;
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
