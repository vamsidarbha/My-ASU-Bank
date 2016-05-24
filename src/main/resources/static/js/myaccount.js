$(document).ready(function() {
	$('a.change-limit').click(function() {
		$('form.hidden-form').show();
		$('.critical-limit').hide();
	});
});