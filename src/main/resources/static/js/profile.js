$(document).ready(function() {
	$('#profile-form').validate({
		errorClass : 'error',
		rules : {
			'address' : {
				required : true
			},
			'phoneNumber' : {
				required : true,
				digits : true,
				phoneNum : true
				
			},
			'dateOfBirth' : {
				required : true,
				dateFA : true,
				maxDate : true
				
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
			'address' : "Address field cannot be empty",
			'phoneNumber' : {
				required : "phoneNumber cannot be empty",
				digits : "Please enter a valid phone number",
				phoneNum : "Phone Number should be 10 digits long"
			},
			'dateOfBirth' : {
				dateFA : "Please enter Date of Birth in mm/dd/yyyy format",
				required : "Date of Birth cannot be empty",
				maxDate : "Date of Birth cannot be Future Date"
			}
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

	$.validator.addMethod("maxDate", function(value, element) {
		var now = new Date();
		var myDate = new Date(value);
		return this.optional(element) || myDate <= now;

	});
	$.validator.addMethod("phoneNum", function(value, element) {
		return this.optional(element)
				|| /[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]/
						.test(value);
	});

});