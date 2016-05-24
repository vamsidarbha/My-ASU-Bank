package com.bankapp.constants;

public interface Constants {

	public String SUCCESS = "success";
	public String ERROR = "error";
	public String CRITICAL = "Its a critical transaction, so it will be handled by our employees shortly";
	public String PII_EXISTS = "Pii already added";
	
	public String ERR_ACCOUNT_EXISTS = "An account exists with this email";
	public String ERR_LESS_BALANCE = "You are low on balance, the transaction cannot go through.";
	public String ERR_ACCOUNT_NOT_EXISTS = "Account does not exist!";
	public String ERR_SAME_USER = "Same user transfer is not valid";
	public String ERR_EMAIL_NOT_EXISTS = "email not present with agency";
	public String ERR_PII_NOT_ADDED = "The user has not added the pii yet please tell the user to put their pii";
	
	public String ERR_PROFILE_UPDATE = "Could not authorize the request";
	public String ERR_PROFILE_DOB = "Date of Birth must be in MM/dd/yyyy format";
	public String ERR_PROFILE_PHONE = "Phone number must be 10 digits.";
	
	public String ERR_TRANS_LIMIT = "Transaction amount not in our bank's transaction bounds ($0 - $100000). Please try with something in between!";
	public String ERR_TRANS_DECODE = "Could not decode your input. Please try again!";
	public String ERR_TRANS_DECRYPTION = "Invalid encypted input. Please try again!";
	public String ERR_TRANS_EXPIRED = "Transaction encryption has expired. Please retry using the applet!";
	public String ERR_TRANS_INCORRECT_FORMAT = "Incorrect amount specified. Please try again!";
	public String ERR_UNHANDLED = "Oops, something unexpected happened. Please contact the administrator";
	//Status
	public String S_PENDING = "P"; //pending for request like transfer money and profile
	public String S_OTP_PENDING = "OP"; //critical transaction
	public String S_VERIFIED = "V"; //normal request verified
	public String S_DECLINED = "D"; //normal request verified
	public String S_OTP_VERIFIED = "OV"; //critical transaction verified
	public String S_PENDING_CUSTOMER_VERIFICATION = "PCV"; //merchant request for customer money pending 
	public String S_CUSTOMER_VERIFIED = "CV"; //merchant request verified
	public String S_CUSTOMER_DECLINED = "CD"; //merchant request Declined
	public String S_PROFILE_UPDATE_PENDING = "PUP"; //Profile Changes pending
	public String S_PROFILE_UPDATE_VERIFIED = "PUV";//profile changes verified
	public String S_PROFILE_UPDATE_DECLINED = "PUD";//profile changes decline
	public String S_PII_PENDING = "PIIP";//pii verification
	public String S_PII_AUTHORIZED = "PIIA";//pii verification
	public String S_PII_DECLINED = "PIID";//pii request pending
	public String S_PII_REQUEST_PENDING = "PIIRP";//pii request pending
	public String S_PII_REQUEST_DONE = "PIIRD";//pii request pending
	public String A_CREDIT = "ACR";
	public String A_DEBIT = "ADT";
	public String S_CREDIT_VERIFIED = "CREDIT_V"; //credit
	public String S_DEBIT_VERIFIED = "DEBIT_V"; //debit
	
	
	//OTP Resource Name
	public String R_TRANSACTION  = "TXN";
	public String R_ACCOUNT  = "ACC";
	public String R_USER  = "USER";
	
}