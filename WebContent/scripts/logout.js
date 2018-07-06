$(document).on('click','.logout', function(e){
	e.preventDefault();
	
	var url = $(this).attr('href');
	console.log(url);
	$.post({
		url: url,
		success: function(data){
			window.location.href = "auth.html";
		}
	});
	
});