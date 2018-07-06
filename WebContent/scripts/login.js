$('.form').find('input, textarea').on('keyup blur focus', function (e) {
  
  var $this = $(this),
      label = $this.prev('label');

	  if (e.type === 'keyup') {
			if ($this.val() === '') {
          label.removeClass('active highlight');
        } else {
          label.addClass('active highlight');
        }
    } else if (e.type === 'blur') {
    	if( $this.val() === '' ) {
    		label.removeClass('active highlight'); 
			} else {
		    label.removeClass('highlight');   
			}   
    } else if (e.type === 'focus') {
      
      if( $this.val() === '' ) {
    		label.removeClass('highlight'); 
			} 
      else if( $this.val() !== '' ) {
		    label.addClass('highlight');
			}
    }

});

$('.tab a').on('click', function (e) {
  console.log('happens');
  e.preventDefault();
  
  $(this).parent().addClass('active');
  $(this).parent().siblings().removeClass('active');
  
  target = $(this).attr('href');
  $('.tab-content > div').not(target).hide();
  
  $(target).fadeIn(600);
  
});


$(document).on('click','#loginButton', function(e){
	e.preventDefault();
	$.post({
		url: "rest/auth/login",
		data: JSON.stringify({
			username : $("#loginUsername").val(), 
			password : $("#loginPassword").val()
			}),
		contentType: 'application/json',
		success: function(data){
			window.location.href = "index.html";
		},
		error: function(data){
		}
	});
});

$(document).on('click','#registerButton', function(e){
	e.preventDefault();
	if($("#usernameRegister").val() == "" || $("#passwordRegister").val() == "" || $("#firstNameRegister").val() == "" || 
			$("#lastNameRegister").val() == "" || $("#emailRegister").val() == "" || $("#phoneRegister").val() == ""){
		console.log('no');
		return;
	} else {
		$.post({
			url: "rest/auth/register",
			data: JSON.stringify({
				"username" : $("#usernameRegister").val(), 
				"password" : $("#passwordRegister").val(),
				"firstName" : $("#firstNameRegister").val(),
				"lastName" : $("#lastNameRegister").val(),
				"email" : $("#emailRegister").val(),
				"phoneNo" : $("#phoneRegister").val()
				}),
			contentType: 'application/json',
			success: function(data){
				var str = window.location.pathname;
				str = str.substring(0, str.split('/',2).join('/').length+1)
				window.location.replace(str + 'auth.html');
			},
			error: function(data){
				alert('something went wrong');
			}
		});
	}
});


