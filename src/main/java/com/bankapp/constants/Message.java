package com.bankapp.constants;

public class Message {

    String status;

    String msg;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String message) {
        this.msg = message;
    }

    public Message(String status, String message) {
        super();
        this.status = status;
        this.msg = message;
    }

}
