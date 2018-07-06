$(document).ready(
	function(){
		console.log('loaded!');
	}
);

$(document).on('submit', '#forma', function(e){
	e.preventDefault();
	console.log('works!');
	let ff = JSON.stringify();
	$.post({
		url: "rest/login",
		
		data: JSON.stringify(
				{	firstName : "na", 
					lastName: "na", 
					email : "na", 
					username : $("#forma [name='username']").val(), 
					password : $("#forma [name='password']").val()}
				),
		contentType: 'application/json',
		success: function(data){
			console.log('good!');
		},
		error: function(data){
			console.log('bad!');
		}
	});
});
