var points = 0;
var globalPrice = 0;
var discount = 0;

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
			points = data.points;
			$("#pointsDisplay").text('Points: ' + data.points);
			if(points != 0){
				$("#rangeDiv").append('<input id="discountRange" type="range" min="0" max="'+points+'">');
				$("#discountRange").val(0);
				
			}
		}
		
	});
	
	
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
		url: "rest/order",
		success:function(data){
			var price = 0;
			var lst = Object.keys(data.articles);

			lst.forEach(function(art){
				var tmp;
				tmp = articles.find( x => x.id == art);
				price += tmp.price * data.articles[art];
				$("#articlesTable").append('<tr>'
						+'<td>'+tmp.name+'</td>'
						+'<td>'+tmp.price+'</td>'
						+'<td>'+data.articles[art]+'</td>'
						+'<td><a class="remove" href="rest/order/remove/'+tmp.id+'">REMOVE</a></td>'
						+'</tr>');
			});
			globalPrice = price;
			$("#priceDisplay").text('Price: ' + price);
		}
	});
	
	$("#noteDiv").append('<input type="text" id="noteInput" placeholder="note">');
	$("#buttonDiv").append('<button id="orderButton" class="button">Order</button>');
});

$(document).on('click','.remove',function(e){
	e.preventDefault();
	$.post({
		url: $(this).attr('href'),
		success:function(data){
			$(this).closest('tr').remove();
			location.reload();
		}
	});
});

$(document).on('input',"#discountRange", function(){
	discount = $(this).val()*3 + "%";
	$('#priceDisplay').text('Price: ' + parseFloat(globalPrice * (1 - 3*$(this).val()/100)).toFixed(2) + ' ('+discount+') off');
});

$(document).on('click','#orderButton', function(e){
	e.preventDefault();
	
	var points = $('#discountRange').val();
	if(points == null || points == undefined) points = 0;
	var price = parseFloat(globalPrice * (1 - 3*points/100)).toFixed(2);
	
	dataOut = JSON.stringify({
		"points" : points,
		"price" : price,
		"note" : $("#noteInput").val()
	});
	$.post({
		url: "rest/order/order",
		contentType: "application/json",
		data: dataOut,
		success:function(data){
			alert('ORDERED');
			console.log(dataOut);
			$.post({
				url: "rest/auth/used/"+points,
				success: function(){
					window.location.href = "index.html";
				}
			});
			
		}
	});
	
});