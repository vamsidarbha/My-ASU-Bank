package com.bankapp.forms;

import org.hibernate.validator.constraints.NotEmpty;

public abstract class RecaptchaForm {

    @NotEmpty
    private String recaptchaResponse;

    public String getRecaptchaResponse() {
        return recaptchaResponse;
    }

    public void setRecaptchaResponse(String recaptchaResponse) {
        this.recaptchaResponse = recaptchaResponse;
    }

}
