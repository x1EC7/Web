$(document).ready(function(){
	
	$.get({
		url: "rest/auth/currentUser",
		success: function(data){
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

		}
		
	});
	
	
	
	$.get({
		url: "rest/restaurant/all",
		success: function(data){
			data.forEach(function(restaurant){
				$("#restaurantsTable").append('<tr>'
						+'<td>'+restaurant.name+'</td>'
						+'<td>'+restaurant.address+'</td>'
						+'<td>'+restaurant.category.replace('_',' ')+'</td>'
						+'<td>'+'<a href="restaurant.html?id='+restaurant.id+'">See more</a>'+'</td>'		
				+'</tr>');
				$("#restaurantIdFilter").append('<option value="'+restaurant.id+'">'+restaurant.name+'</option>');
			});
		}
	});
	
	
	$.get({
		url: "rest/article/all",
		success: function(data){
			
			$.get({
				url: "rest/article/top10",
				success: function(data){
					data.forEach(function(entity){
						var ent = '<tr>'
							+'<td>'+entity.name+'</td>'
							+'<td>'+entity.type.replace('_',' ')+'</td>'
							+'<td>'+entity.amount + (entity.type=="FOOD" ? ' g' : ' ml') + '</td>'
							+'<td>'+entity.price+'</td>'
							+'<td><input min="1" type="number"><a class="orderNum" href="rest/order/'+entity.id+'">ADD TO CART</a></td>'
					+'</tr>';
						if(entity.type == "FOOD"){
							$("#foodTable").append(ent);
						} else {
							$("#drinkTable").append(ent);
						}
						
					});
				}
				
			});	
			
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

$(document).on('click', 'a.click', function (e) {
	  e.preventDefault();
	  
	  var txt = $(this).text();
	  
	  $("#title").html(txt);

	  $(this).parent().addClass('active');
	  $(this).parent().siblings().removeClass('active');
	  
	  target = $(this).attr('href');

	  let headers = $(".tbl-header").children().children();
	  
	  if(txt === "Restaurants"){
		  headers[0].removeAttribute('hidden');
		  headers[1].setAttribute('hidden', 'true');
		  
		  $("#restaurantFilter").css('display','block');
		  $("#articleFilter").css('display','none');
		  
	  } else if (txt === "Articles"){
		  headers[0].setAttribute('hidden', 'true');
		  headers[1].removeAttribute('hidden');
		  
		  $("#restaurantFilter").css('display','none');
		  $("#articleFilter").css('display','block');
		  
	  } else {
		  headers[0].setAttribute('hidden', 'true');
		  headers[1].removeAttribute('hidden');
		  $("#restaurantFilter").css('display','none');
		  $("#articleFilter").css('display','none');
		  
	  }
	  
	  let tables = $('.tbl-content').children();
	  
	  tables[0].setAttribute('hidden', 'true');
	  tables[1].setAttribute('hidden', 'true');
	  tables[2].setAttribute('hidden', 'true');
	  tables[3].setAttribute('hidden', 'true');
		
	  
	  if(txt === "Articles"){
		  tables[1].removeAttribute('hidden');
	  } else if(txt === "Restaurants"){
		  tables[0].removeAttribute('hidden');
	  } else if(txt === "Top 10 Food"){
		  tables[2].removeAttribute('hidden');
	  } else {
		  tables[3].removeAttribute('hidden');
	  }
	  
});


$(document).on('click', "#rFilter", function(e){
	e.preventDefault();
	data = JSON.stringify({
		"name" : $("#restaurantNameFilter").val(),
		"address" : $("#restaurantAddressFilter").val(),
		"category" : $("#restaurantCategoryFilter").val()
	});

	$.post({
		url: "rest/restaurant/filter",
		data: data,
		contentType: "application/json",
		success: function(data){
			$("#restaurantsTable").empty();
			data.forEach(function(restaurant){
				$("#restaurantsTable").append('<tr>'
						+'<td>'+restaurant.name+'</td>'
						+'<td>'+restaurant.address+'</td>'
						+'<td>'+restaurant.category.replace('_',' ')+'</td>'
						+'<td>'+'<a href="restaurant.html?id='+restaurant.id+'">See more</a>'+'</td>'		
				+'</tr>');
			});
		}
	});
	
});

$(document).on('click', "#aFilter", function(e){

	e.preventDefault();
	data = JSON.stringify({
		"name" : $("#articleNameFilter").val(),
		"type" : $("#articleTypeFilter").val(),
		"restaurantId" : $("#restaurantIdFilter").val(),
		"minPrice" : $("#minPrice").val() == "" ? 0 : $("#minPrice").val(),
		"maxPrice" : $("#maxPrice").val() == "" ? 0 : $("#maxPrice").val()
	});
	console.log(data);
	$.post({
		url: "rest/article/filter",
		data: data,
		contentType: "application/json",
		success: function(data){
			$("#articlesTable").empty();
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