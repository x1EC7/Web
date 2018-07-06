$(document).ready(function(){
	
	$.get({
		url: "rest/auth/currentUser",
		success: function(data){
			if(data == undefined || data == null || data.role != "ADMIN"){
				
				$.post({
					url: "rest/auth/logout",
					success: function(data){
						window.location.href = "index.html";
					}
				});	
			}

			$.get({
				url: "rest/order/admin",
				success: function(data){
					data.forEach(function(order){
						let text = ((order.deleted == true) ? "UN" : "") + "DELETE";
						let edit = '<a href="new.html?type=order&mode=edit&id="'+order.id+'">EDIT</a>';
						$("#ordersTable").append('<tr><td>'+order.note+'</td><td>'+edit+'</td><td><a class="deleteEntity" href="rest/order/'+order.id+'">'+text+'</a></td></tr>');
					});
				}
			});
			
			$.get({
				url: "rest/auth/admin",
				success: function(data){
					data.forEach(function(user){
						let edit = '<a href="new.html?type=user&mode=edit&id=' + user.id +'">EDIT</a>';
						$("#usersTable").append('<tr><td>'+user.username+'</td><td>'+edit+'</td></tr>');
					});
				}
			});
			
			$.get({
				url: "rest/restaurant/admin",
				success: function(data){
					data.forEach(function(restaurant){
						let text = ((restaurant.deleted == true) ? "UN" : "") + "DELETE";
						let edit = '<a href="new.html?type=restaurant&mode=edit&id=' + restaurant.id +'">EDIT</a>';
						$("#restaurantsTable").append('<tr><td>'+restaurant.name+'</td><td>'+edit+'</td><td>'+'<a class="deleteEntity" href="rest/restaurant/'+restaurant.id+'">'+text+'</a>'+'</td></tr>');
					});
				}
			});
			
			$.get({
				url: "rest/article/admin",
				success: function(data){
					data.forEach(function(article){
						let text = ((article.deleted == true) ? "UN" : "") + "DELETE";
						let edit = '<a href=new.html?type=article&mode=edit&id=' + article.id +'>EDIT</a>';
						$("#articlesTable").append('<tr><td>'+article.name+'</td><td>'+edit+'</td><td>'+'<a class="deleteEntity" href="rest/article/'+article.id+'">'+text+'</a>'+'</td></tr>');
					});
				}
			});
			
			$.get({
				url: "rest/vehicle/admin",
				success: function(data){
					data.forEach(function(vehicle){
						let text = ((vehicle.deleted == true) ? "UN" : "") + "DELETE";
						let edit = '<a href=new.html?type=vehicle&mode=edit&id=' + vehicle.id +'>EDIT</a>';
						$("#vehiclesTable").append('<tr><td>'+vehicle.regPlate+'</td><td>'+edit+'</td><td>'+'<a class="deleteEntity" href="rest/vehicle/'+vehicle.id+'">'+text+'</a>'+'</td></tr>');
					});
				}
			});
			
			
		}
	});
	
});

$(document).on('click', ".tab a.click", function(e){
	e.preventDefault();
	
	var txt = $(this).attr('href');
	
	$(this).parent().addClass('active');
    $(this).parent().siblings().removeClass('active');
	
	var tables = $(".tbl-content").children();


	tables[0].setAttribute('hidden','true');
	tables[1].setAttribute('hidden','true');
	tables[2].setAttribute('hidden','true');
	tables[3].setAttribute('hidden','true');
	tables[4].setAttribute('hidden','true');
	
	if(txt == "restaurantsTable"){
		tables[0].removeAttribute('hidden');
	} else if(txt == "articlesTable"){
		tables[1].removeAttribute('hidden');
	} else if(txt == "vehiclesTable"){
		tables[2].removeAttribute('hidden');
	} else if(txt == "usersTable"){
		tables[3].removeAttribute('hidden');
	} else if(txt == "ordersTable"){
		tables[4].removeAttribute('hidden');
	}
	
	var headers = $("#header").children();
	
	headers[0].setAttribute('hidden','true');
	headers[1].setAttribute('hidden','true');
	
	if(txt == "usersTable"){
		headers[1].removeAttribute('hidden');
	} else {
		headers[0].removeAttribute('hidden');
	}
});

$(document).on('click','a.deleteEntity',function(e){
	e.preventDefault();
	
	var t = $(this);
	
	var text = $(this).text();
	var url = $(this).attr('href');
	$.ajax({
		type: "DELETE",
		url: url,
		success: function(data){
			$(t).text(text == "DELETE" ? "UNDELETE" : "DELETE");
			if(url.includes('restaurant')){
				var id = url.split('/').pop();
				$.ajax({
					type: ((text == "DELETE") ? "DELETE" : "PUT"),
					url: "rest/article/restaurant/"+id,
					success: function(data){
						window.location.href = "admin.html";					
					}
				});
			}
		}
	});
});


$(document).on('click','a.addEntity',function(e){
	e.preventDefault();
	
	console.log($(this).text());
	
	window.open('new.html?type='+$(this).attr('href')+'&mode=add&id=');
	
});

