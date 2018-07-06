$(document).ready(function(){
	$.get({
		url: "rest/auth/currentUser",
		success: function(data){
			var buttons = $(".tab-group-style").children();
			if(data.role != "DELIVERY"){
				window.location.href = "index.html";
			}
			if(data != null){
				$("#profile").text(data.username);
			} else {
				buttons[2].setAttribute("hidden","true");
			}
			buttons[data == null ? 1 : 0].setAttribute("hidden","true");
			///////////////////////////////////////////////////////////
			
			var articles = [];
			
			$.get({
				url: "rest/article/all",
				success: function(data){
					data.forEach(function(article){
						articles.push(article);
					});

				}
			});

			
			$.get({
				url: "rest/vehicle/available",
				success: function(data){
					data.forEach(function(vehicle){
						var str = 'TAKE';
						if(vehicle.inUse){
							str = 'RETURN';
						}
						$("#vehiclesTable").append('<tr>'
								+'<td>'+vehicle.model+'</td>'
								+'<td>'+vehicle.brand+'</td>'
								+'<td>'+vehicle.regPlate+'</td>'
								+'<td><a class="vclick" href="rest/vehicle/take/'+vehicle.id+'">'+str+'</a></td>'
								+'</tr>');
					});
				}
			});
			
			$.get({
				url: "rest/order/work",
				success: function(data){
					var name = "";
					data.forEach(function(order){
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

						var text = '';
						
						$("#ordersTable").append('<tr>'
							+'<td>'+name+'</td>'
							+'<td><a class="takeDeliver" href="rest/order/change/'+order.id+'">'+(order.status == 'ORDERED' ? "TAKE ORDER" : "DELIVER") + '</a></td>'
							+'<td><a class="cancel" href="rest/order/cancel/'+order.id+'">CANCEL</a></td>'
							+'</tr>');
					});
				}
			});
			
		}
	});
});


$(document).on('click','.click',function(e){
	e.preventDefault();
	
	var id = $(this).attr('href');
	var txt = $(this).text();

	let tables = $('.tbl-content').children();
	let headers = $(".tbl-header").children().children();

	tables[0].setAttribute('hidden', 'true');
	tables[1].setAttribute('hidden', 'true');
	headers[0].setAttribute('hidden', 'true');
	headers[1].setAttribute('hidden', 'true');
	
	if(txt === "Orders"){
		tables[1].removeAttribute('hidden');
		headers[1].removeAttribute('hidden');
	} else if (txt === "Vehicles"){
		tables[0].removeAttribute('hidden');
		headers[0].removeAttribute('hidden');
	} 
	
	
});

$(document).on('click', '.vclick', function(e){
	
	e.preventDefault();
	
	var url = $(this).attr('href');
	var text = $(this).text();
	var t = $(this);
	$.post({
		url: url,
		success: function(data){
			$(t).text(text == "TAKE" ? "RETURN" : "TAKE");
		}
	});
	
	
});

$(document).on('click','.takeDeliver', function(e){
	e.preventDefault();
	
	var url = $(this).attr('href');
	var text = $(this).text();
	var t = $(this);
	$.post({
		url: url,
		success: function(data){
			if(text == "TAKE ORDER")
				$(t).text('DELIVER');
			if(text == "DELIVER"){
				
				var lst = Object.keys(data.articles);

				lst.forEach(function(id){
					$.post({
						url: "rest/article/popularity/"+id,
						success: function(data){}
					});
				});
				if(data.price >= 500)
					$.post({
						url: "rest/auth/addpoint/"+data.clientID,
						success: function(data){
							$(t).closest('tr').remove();
							//location.reload();		
						}
					});						
				
			}
				
		}
	});
	
});