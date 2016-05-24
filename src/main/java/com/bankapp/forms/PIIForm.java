package com.bankapp.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class PIIForm {
    @NotEmpty
    @NotBlank
    @NotNull
    @Pattern(regexp="[\\d]{9}")
    private String pii;

    public String getPii() {
        return pii;
    }

    public void setPii(String pii) {
        this.pii = pii;
    }

   
}
