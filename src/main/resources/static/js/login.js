$(document).ready(function() {
	$('#login-form').validate({
		errorClass : 'error',
		rules : {
			'm_email' : {
				required : true,
				email : true
			},
			'm_password' : {
				required : true
			}
		},
		// Specify the validation error messages
		messages : {
			'm_email' : "Please enter a valid email address",
			'm_password': "Please enter a password"
		},

		submitHandler : function(form) {
			form.submit();
		}
	});
	

	$('#password').keyboard({
		  usePreview: false,
		  appendLocally: true,
		  autoAccept: true
	});
});