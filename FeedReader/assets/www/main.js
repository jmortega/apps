//used for caching
var feedCache= {};

var idFeed = "";

var onDeviceReady = function() {
    
    $('#exit_btn').click(function() {
           navigator.app.exitApp();
    });
       
    var networkstate = navigator.connection.type;
    if(networkstate == "none"){
        $(".offline").css("visibility","visible");
               
    }
};

function init() {

    document.addEventListener("deviceready", onDeviceReady, true);

    displayFeeds();
	
	//Listen for the addFeedPage so we can support adding feeds
	$("#addfeedpage").live("pageshow", function(e) {
		$("#addFeedForm").submit(function(e) {
			handleAddFeed();
			return false;
		});
	});

	//Listen for delete operations
	$(".deleteFeed").live("click",function(e) {
		idFeed = $(this).jqmData("feedid");
		var nameFeed = $(this).jqmData("feedname");
		 navigator.notification.confirm(
		         '¿Confirma eliminar el feed '+nameFeed+'?',     // mensaje (message)
		         onConfirm,      // función 'callback' a llamar con el índice del botón pulsado (confirmCallback)
		         'Borrar feed',            // titulo (title)
		          'No,Aceptar'       // botones (buttonLabels)
		         );
		 
		
	});
	
	function onConfirm(index) {

	    if(index==2) {
	          removeFeed(idFeed);
	    }
	}
	
	//Listen for the Feed Page so we can display entries
	$("#feedpage").live("pageshow", function(e) {
		//get the feed id based on query string
		var query = $(this).data("url").split("=")[1];
		//remove ?id=
		query = query.replace("?id=","");
		//assume it's a valid ID, since this is a mobile app folks won't be messing with the urls, but keep
		//in mind normally this would be a concern
		var feeds = getFeeds();
		var thisFeed = feeds[query];
		$("h1",this).text(thisFeed.name);
		
		$.mobile.showPageLoadingMsg();
		
		var networkstate = navigator.connection.type;
		
		if(networkstate == "none"){
		    navigator.notification.alert('Debe disponer de una conexión',null,'Connection','Aceptar');
	               
	    }
		if(!feedCache[thisFeed.url]) {
		    
			$("#feedcontents").html("<p>Cargando datos...</p>");
			//now use Google Feeds API
			$.get("https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=100&q="+encodeURI(thisFeed.url)+"&callback=?", {}, function(res,code) {
				//see if the response was good...
				if(res.responseStatus == 200) {
					feedCache[thisFeed.url] = res.responseData.feed.entries;
					displayFeed( thisFeed.url);
				} else {
					var error = "<p>Lo sentimos, pero el feed no puede ser cargado:</p><p>"+res.responseDetails+"</p>";
					$("#feedcontents").html(error);
					$("#feedcontents2").html("La URL introducida no tiene el formato correcto<br/><br/>"+thisFeed.url);
					navigator.notification.alert('La URL introducida no tiene el formato correcto',null,'Feed','Aceptar');
				}
			},"json");
		} else {
			displayFeed(thisFeed.url);
		}
		
	});
	
	//Listen for the Entry Page so we can display an entry
	$("#entrypage").live("pageshow", function(e) {
		//get the entry id and url based on query string
		var query = $(this).data("url").split("?")[1];
		//remove ?
		query = query.replace("?","");
		//split by &
		var parts = query.split("&");
		var entryid = parts[0].split("=")[1];
		var url = parts[1].split("=")[1];
		
		var entry = feedCache[url][entryid];
		$("h2",this).text(entry.title);
		$("#entrycontents",this).html(entry.content);
		$("#entrylink",this).attr("href",entry.link);
	});
	
	$("#botonConnection").bind("pagebeforeshow", function(e) {
	    
	    var networkState = navigator.network.connection.type;
	    var states = {};
	    states[Connection.UNKNOWN]  = 'Unknown connection';
	    states[Connection.ETHERNET] = 'Ethernet connection';
	    states[Connection.WIFI]     = 'WiFi connection';
	    states[Connection.CELL_2G]  = 'Cell 2G connection';
	    states[Connection.CELL_3G]  = 'Cell 3G connection';
	    states[Connection.CELL_4G]  = 'Cell 4G connection';
	    states[Connection.NONE]     = 'No network connection';

	    $('#conexionContent p:first').html('Connection type: ' + states[networkState]);
	});
	
	$("#loadFeeds").bind("pagebeforeshow", function(e) {
        
	    addFeed('Java Hispano','http://feeds2.feedburner.com/JHPodcasts');
	    addFeed('HtmlCssJavascript','http://feeds2.feedburner.com/HtmlCssJavascript');
	    addFeed('Web Professional Minute','http://feeds2.feedburner.com/WebProfessionalMinute');
	    
	    displayFeeds();
    });
	
}

function displayFeed(url) {
	var entries = feedCache[url];
	var s = "<ul data-role='listview' data-inset='true' id='entrylist'>";
	for(var i=0; i<entries.length; i++) {
		var entry = entries[i];
		s += "<li><a href='entry.html?entry="+i+"&url="+encodeURI(url)+"'>"+entry.title+"</a></li>";
	}
	s += "</ul>";

	$("#feedcontents").html(s);
	$("#entrylist").listview();	
	
	$.mobile.hidePageLoadingMsg();
}

function displayFeeds() {
	var feeds = getFeeds();
	if(feeds.length == 0) {
		//in case we had one form before...
		$("#feedList").html("No hay feeds registrados.Puede cargar los feeds por defecto o agregar uno manualmente");
		$("#feedList").css({"background-color":"gray","color":"black","font-size":"30px"});
		$("#introContentNoFeeds").show();
	} else {
		$("#introContentNoFeeds").hide();
		var s = "";
		for(var i=0; i<feeds.length; i++) {
			s+= "<li><a href='feed.html?id="+i+"' data-feed='"+i+"'>"+feeds[i].name+"</a> <a href='' data-role='button' id='feedAux' class='deleteFeed' data-icon='delete' data-feedname='"+feeds[i].name+"' data-feedid='"+i+"'>Delete</a>" +
					"<br/>"+feeds[i].url+"</li>";
		}
		$("#feedList").html(s);
		$("#feedList").css({"font-size":"20px"});
		$("#feedList").listview("refresh");
	}
	
	$.mobile.hidePageLoadingMsg();
}

//handles checking storage for your feeds
function getFeeds() {
	if(localStorage["feeds"]) {
		return JSON.parse(localStorage["feeds"]);
	} else return [];
}

function addFeed(name,url) {
	var feeds = getFeeds();
	feeds.push({name:name,url:url});
	localStorage["feeds"] = JSON.stringify(feeds);
	displayFeeds();
}

function removeFeed(id) {
	var feeds = getFeeds();
	feeds.splice(id, 1);
	localStorage["feeds"] = JSON.stringify(feeds);
	displayFeeds();
}

function handleAddFeed() {
	var feedname = $.trim($("#feedname").val());
	var feedurl = $.trim($("#feedurl").val());
	
	//basic error handling
	var errors = "";
	if(feedname == "") errors += "El nombre del feed es obligatorio.\n";
	if(feedurl == "") errors += "La url del feed es obligatoria.\n";
	
	if(errors != "") {
		//Create a PhoneGap notification for the error
	    navigator.notification.alert(errors,null,'New Feed','Aceptar');
	} else {
		addFeed(feedname, feedurl);
		$.mobile.changePage("index.html");
	}
}
