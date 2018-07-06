var type = "";
var mode = "";
var id = "";
$(document).ready(function(){

	var tmp = window.location.href.split('?')[1].split("&");
	
	

	type = tmp[0].split("=")[1];
	mode = tmp[1].split("=")[1];
	id = tmp[2].split("=")[1];
	
	if(type == "user"){
		type = "auth";
	}
	
	if(type == 'article'){
		$.get({
			url: "rest/restaurant/admin",
			success: function(data){
				data.forEach(function(restaurant){
					$("#articleRestaurant").append('<option value="'+restaurant.id+'">'+restaurant.name+'</option>');
				});
			}
		})
	}
	
	if(type == "order"){
		$.get({
			url: "rest/auth/admin",
			success:function(data){
				data.forEach(function(user){
					if(user.role == "DELIVERY"){
						$("#orderDelivery").append('<option value="'+user.id+'">'+user.username+'</option>');
					}
					$("#orderClient").append('<option value="'+user.id+'">'+user.username+'</option>');
				});
			}
		});
	}
	
	if (mode == 'edit'){
		$("#add"+type.charAt(0).toUpperCase()+type.substring(1)).text('Edit ' + type.charAt(0).toUpperCase()+type.substring(1));
		console.log(mode);
		
		$("label").addClass('active');
		
		$.get({
			url: "rest/"+type+"/"+id,
			success: function(data){
				console.log(data);
				if(type == "auth" || type == "user"){
					$("#userUsername").val(data.username);
					$("#userFirstName").val(data.firstName);
					$("#userLastName").val(data.lastName);
					$("#userRole").val(data.role);
				} else if (type == "restaurant"){
					$("#restaurantName").val(data.name);
					$("#restaurantAddress").val(data.address);
					$("#restaurantCategory").val(data.category);
				} else if (type == "article"){
					$("#articleName").val(data.name);
					$("#articleDescription").val(data.description);
					$("#articlePrice").val(data.price);
					$("#articleAmount").val(data.amount);
					$("#articleRestaurant").val(data.restaurantID);
					$("#articleType").val(data.type);
				} else if (type == "vehicle"){
					$("#vehicleBrand").val(data.brand);
					$("#vehicleModel").val(data.model);
					$("#vehiclePlate").val(data.regPlate);
					$("#vehicleYear").val(data.yearOfManufacture);
					$("#vehicleNote").val(data.note);
					
				} else if (type == "order") {
					$("#orderClient").val(data.clientId);
					$("#orderDelivery").val(data.deliveryId);
					$("#ordernote").val(data.note);
					
				}
			}
		});
		
	}
	
	if(type =="auth"){
		type = "user";
	}
		
	$(".topContainer").css('display','none');
	
	$("#"+type).css('display','block');
	
});



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

$('.sendData').on('click', function(e){
	e.preventDefault();
	var txt = $(this).text();
	console.log(txt);
	
	var data = null;
	
	if(type == "restaurant"){
		data = JSON.stringify({
			"id" : mode == "edit" ? id : null,
			"name" : $("#restaurantName").val(),
			"address" : $("#restaurantAddress").val(),
			"category" : $("#restaurantCategory").val()
		});
	} else if (type == "article"){
		data = JSON.stringify({
			"id" : mode == "edit" ? id : null,
			"name" : $("#articleName").val(),
			"description" : $("#articleDescription").val(),
			"price" : $("#articlePrice").val(),
			"amount" : $("#articleAmount").val(),
			"restaurantID" : $("#articleRestaurant").val(),
			"type" : $("#articleType").val()
		});
	} else if (type == "vehicle"){
		data = JSON.stringify({
			"id" : mode == "edit" ? id : null,
			"brand" : $("#vehicleBrand").val(),
			"model" : $("#vehicleModel").val(),
			"regPlate" : $("#vehiclePlate").val(),
			"yearOfManufacture" : $("#vehicleYear").val(),
			"note" : $("#vehicleNote").val(),
			"vehicleType" : $("#vehicleType").val()
		});
	} else if (type == "order"){
		data = JSON.stringify({
			"id" : mode == "edit" ? id : null,
			"clientID" : $("#orderClient").val(),
			"deliveryID" : $("#orderDelivery").val(),
			"note" : $("#orderNote").val()
		});
	} else if (type == "user") {
		data = JSON.stringify({
			"id" : mode == "edit" ? id : null,
			"username" : $("#userUsername").val(),
			"firstName" : $("#userFirstName").val(),
			"lastName" : $("#userLastName").val(),
			"role" : $("#userRole").val()
		});
	}
	console.log(data);
	if(mode == "edit"){
		$.ajax({
			type: "PUT",
			url: "rest/"+(type=="user"?"auth":type),
			data: data,
			contentType: 'application/json',
			success: function(data){
				alert('EDITED');
			}
		});
	} else {
		$.post({
			url: "rest/"+type+"/add",
			data: data,
			contentType: 'application/json',
			success: function(data){
				alert('ADDED');
			},
			error: function(data){
				if(type == "order")
					alert('User already has an ongoing order, cannot create new one');
			}
		});
	}
	
});