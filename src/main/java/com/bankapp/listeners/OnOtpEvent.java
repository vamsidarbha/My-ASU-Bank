package com.bankapp.listeners;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class OnOtpEvent extends ApplicationEvent {
    private final String resourceName;
    private final String resourceId;

    public OnOtpEvent(String resourceId, String resourceName) {
        super(resourceName);

        this.resourceId = resourceId;
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceId() {
        return resourceId;
    }

}
