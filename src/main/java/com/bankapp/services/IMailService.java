package com.bankapp.services;

public interface IMailService {
	void sendEmail(String recipientAddress, String subject, String textBody);
}
