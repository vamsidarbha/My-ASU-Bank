$(document).ready(function() {
	$(".reset").click(function() {
		$(this).closest('form').find("input[type=text], input[type=email], input[type=password], textarea").val("");
	});
	status = "Sorry, Right Click Disabled";
	$(document).on({
	    "contextmenu": function(e) {
	        alert(status); 
	        e.preventDefault();
	    },
	    "mousedown": function(e) { 
	    },
	    "mouseup": function(e) { 
	    }
	});
	
});