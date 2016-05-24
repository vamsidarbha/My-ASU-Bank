package com.bankapp.forms;

import javax.validation.constraints.Size;

public class OTPForm {
    @Size(min = 1, max = 50)
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
