$(document).ready(function(){
	$.get({
		url: "rest/auth/currentUser",
		success: function(data){
			if(data == null) {
				window.location.href = "auth.html";
			}
			
			var articles = [];
			
			$.get({
				url: "rest/article/all",
				success: function(data){
					data.forEach(function(article){
						articles.push(article);
					});

				}
			})
			$.get({
				url: "rest/restaurant/favourites",
				success: function(data){
					data.forEach(function(restaurant){
						$("#restaurantsTable").append('<tr><td>'+restaurant.name+'</td>'
								+'<td>'+restaurant.address+'</td></tr>');
					});
				}
			});
			
			$.get({
				url: "rest/order/history",
				success: function(data){
					data.forEach(function(order){
						var name = "";
						var lst = Object.keys(order.articles);
						var first = true;

						lst.forEach(function(art){
							var tmp;
							try {
								tmp = articles.find( x => x.id == art);
								name += ((first == true) ? "" : "<br>") + tmp.name + " (" + order.articles[art] + ")";
							} catch(err) {
								tmp = null;
								name += ((first == true) ? "" : "<br>") + "[REDACTED]" + " (" + order.articles[art] + ")";
							}
							
							first = first == true ? false : first;
						});
						$("#ordersTable").append('<tr><td>'+name+'</td><td>'+order.dateOfOrder+'</td><td>'+order.price+'</td></tr>');
					});
				}
			});
			$(".title").text(data.role);
			$("#points").text('Points: ' + data.points);
			$("#email").text('Email: ' + data.email);
			$("#address").text('Address: ' + data.address);
			$("#phoneNo").text('Phone number: ' + data.phoneNo);
			$("#name").text(data.firstName + ' ' + data.lastName);
			$("#date").text('Member since: ' + data.dateOfRegistration);
		}
	});
});


$(document).on('click','a.click',function(e){
	e.preventDefault();
	
	var txt = $(this).text();
	
	var index = txt == "Favourite Restaurants" ? 0 : 1;

    $(this).parent().addClass('active');
    $(this).parent().siblings().removeClass('active');

    var headers = $("#header").children();
    
    headers[1 - index].setAttribute('hidden','true');
    headers[index].removeAttribute('hidden');
    
    var tables = $(".tbl-content").children();
    
    tables[1 - index].setAttribute('hidden','true');
    tables[index].removeAttribute('hidden');
	
});