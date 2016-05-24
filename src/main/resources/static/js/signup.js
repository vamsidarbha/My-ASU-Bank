$(document).ready(function() {
	$('#signup-form').validate({
		errorClass : 'error',
		rules : {
			'username' : {
				required : true
			},
			'email' : {
				required : true,
				email : true
			},
			'password' : {
				required : true,
				minlength : 8
			},
			'dateOfBirth': {
				required: true,
				dateFA : true,
			},
			'securityQuestion' : {
				required : true
			},
			'securityAnswer' : {
				required : true
			}
		},
		// Specify the validation error messages
		messages : {
			'username' : "Please enter your user name",
			'email' : "Please enter a valid email address",
			'password': {
				required: "Please enter a password",
				minlength: "Password should be atleast 8 characters long"
			},
			'dateOfBirth': "Please enter a past date in MM/dd/yyyy format",
			'securityQuestion' : "Please enter a security question",
			'securityAnswer' : "Please enter a security answer"
		},

		submitHandler : function(form) {
			form.submit();
		}
	});
	
	$.validator.addMethod("dateFA", function(value, element) {
		return this.optional(element)
				|| /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/([0-9][0-9])\d{2}$/
						.test(value);
	}, $.validator.messages.date);
});