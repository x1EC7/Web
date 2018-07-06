var restaurantID = window.location.href.split("id=")[1];

$(document).ready(function(){
	
	$.get({
		url: "rest/auth/currentUser",
		success:function(data){
			var buttons = $(".tab-group-style").children();
			if(data != null){
				$("#profile").text(data.username);
			} else {
				buttons[2].setAttribute("hidden","true");
			}
			buttons[data == null ? 1 : 0].setAttribute("hidden","true");

			if(data != undefined && data.role == "ADMIN"){
				$(".tab-group-style").append('<li><a class="" href="admin.html">Admin&nbsp;panel</a></li>');
			}
			if(data != undefined && data.role == "DELIVERY"){
				$(".tab-group-style").append('<li><a class="" href="work.html">Work&nbsp;panel</a></li>');	
			}

		},
		error: function(){
			window.location.href = "auth.html";
		}
	});

	
	$.get({
		url: "rest/restaurant/" + restaurantID,
		success: function(restaurant){
			$("#name").text(restaurant.name);
			$("#address").text(restaurant.address);
			$(".title").text(restaurant.category.replace("_"," "));
		}
	});

	$("#favButton").text('Favourite');
	$.get({
		url: "rest/restaurant/favourites",
		success: function(data){
			var isFav = false;
			data.forEach(function(restaurant){
				if(isFav){
					return;
				}
				if(restaurant.id == restaurantID){
					$("#favButton").text('Unfavourite');
					isFav = true;
					return;
				}
				$("#favButton").text('Favourite');
			});
		},error: function(){
			$("#favButton").text('Favourite');
		}
	});
	
	$.get({
		url: "rest/article/restaurant/" + restaurantID,
		success: function(data){
			data.forEach(function(article){
				$("#articlesTable").append('<tr>'
						+'<td>'+article.name+'</td>'
						+'<td>'+article.type.replace('_',' ')+'</td>'
						+'<td>'+article.amount + (article.type=="FOOD" ? ' g' : ' ml') + '</td>'
						+'<td>'+article.price+'</td>'
						+'<td><input min="1" type="number"><a class="orderNum" href="rest/order/'+article.id+'">ADD TO CART</a></td>'
				+'</tr>');
			});
		}
	});
});


$(document).on('click','#favButton', function(e){
	e.preventDefault();
	var content = $(this).text();
	
	$.get({
		url: "rest/auth/currentUser",
		success:function(data){
			if(data == null){
				window.location.href = "auth.html";
			}
		},
		error: function(){
			window.location.href = "auth.html";
		}
	});
	
	$.post({
		url: "rest/restaurant/favourites/" + restaurantID,
		success: function(data){
			$("#favButton").text(content == 'Favourite' ? 'Unfavourite' : 'Favourite');
		}
	});
	
});

$(document).on('click','.orderNum', function(e){
	e.preventDefault();
	var href = $(this).attr('href');
	var num = $(this).closest('td').find('input').val();
	console.log(href +'/'+ num);
	
	$.post({
		url: href +'/'+ num,
		success:function(data){
			alert('added');
		}
	});
	
});