
package com.bankapp.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bankapp.constants.Constants;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IMailService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Component
public class TemporaryListener implements ApplicationListener<OnOtpEvent>, Constants {
	@Autowired
	private IUserService userService;

	@Autowired
	private IMailService mailService;

	@Autowired
	private ITransactionService transactionService;

	@Override
	public void onApplicationEvent(OnOtpEvent event) {
		this.confirmOTP(event);
	}

	private void confirmOTP(OnOtpEvent event) {
		String resourceId = event.getResourceId();
		String resourceName = event.getResourceName();
		OneTimePassword otp = userService.generateOTP(resourceId, resourceName);
		String recipientUsername, recipientEmail = null, textBody = null;
		String subject = "My ASU Bank - OTP";
		if (resourceName.equals(R_TRANSACTION)) {
			Transaction transaction = transactionService.getTransactionsById(resourceId);
			User recipientUser = transaction.getFromAccount().getUser();
			String transactionID = transaction.getTransactionId();
			recipientUsername = recipientUser.getUsername();
			recipientEmail = recipientUser.getEmail();
			textBody = String.format("Dear valued customer <b>%s</b>, <br><br>"
			        + "Please find the One Time Password for the transaction# %d: %s<br />"
			        + "Regards,<br />My ASU Bank<br>", recipientUsername, transactionID, otp.getValue());
		} else if (resourceName.equals(R_USER)) {
			User recipientUser = userService.getUserById(resourceId);
			recipientUsername = recipientUser.getUsername();
			recipientEmail = recipientUser.getEmail();
			textBody = String.format("Dear valued customer <b>%s</b>, <br><br>"
			        + "Please find the One Time Password for the change password %s<br />"
			        + "Regards,<br />My ASU Bank<br>", recipientUsername, otp.getValue());
		}
		mailService.sendEmail(recipientEmail, subject, textBody);
	}

}