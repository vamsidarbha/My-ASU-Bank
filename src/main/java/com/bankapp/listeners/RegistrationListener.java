package com.bankapp.listeners;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bankapp.models.User;
import com.bankapp.services.IMailService;
import com.bankapp.services.IUserService;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
	@Autowired
	private IUserService service;

	@Autowired
	private IMailService mailService;

	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event) {
		this.confirmRegistration(event);
	}

	private void confirmRegistration(OnRegistrationCompleteEvent event) {
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		service.createVerificationToken(user, token);
		String recipientAddress = user.getEmail();
		String subject = "My ASU Bank - Registration Confirmation";
		String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;

		String recipientUsername = user.getUsername();
		String textBody = String.format("Dear valued customer <b>%s</b>, <br><br>"
		        + "Thank you for registering with our My ASU Bank.<br />To activate your account, "
		        + "please click on <b><a href='%s' target='_blank'>this</a></b> link.<br><br>"
		        + "Regards,<br />My ASU Bank<br>", recipientUsername, confirmationUrl);

		mailService.sendEmail(recipientAddress, subject, textBody);
	}
}