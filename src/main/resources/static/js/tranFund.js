$(document).ready(function() {
	$('#tF-form').validate({
		errorClass : 'error',
		rules : {
			'email' : {
				required : true,
				email : true
			},
			'amount' : {
				required : true
			},
			'comment' : {
				required : false
			},

		},
		// Specify the validation error messages
		messages : {
			'email' : {
				required : "Recipient email address cannot be empty",
				email: "Recipient email address is not a valid email"
			},
			'amount' : {
				required : "Recepient Account number cannot be empty"
			}
		},

		submitHandler : function(form) {
			form.submit();
		}
	});
});