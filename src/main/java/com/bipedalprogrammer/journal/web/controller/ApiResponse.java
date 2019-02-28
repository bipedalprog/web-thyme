package com.bipedalprogrammer.journal.web.controller;

public class ApiResponse {
    public final static String STATUS_UNDEFINED = "undefined";
    public final static String STATUS_SUCCESSFUL = "success";
    public final static String STATUS_ERROR = "error";
    public final static String STATUS_WARNING = "warning";

    private String status;
    private String message;

    public ApiResponse() {
        status = STATUS_UNDEFINED;
        message = "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
