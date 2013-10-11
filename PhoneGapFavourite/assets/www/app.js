var mapdata = null;

var cachedData = null;

var currentBusinessData = null;

var geolocationOptions = {
        timeout : 3000,
        enableHighAccuracy : true
 };

var geocoder;
var map;
var marker;
var infowindow;
var latitud;
var longitud;
var currentposition;

function isOnline(){ 
    
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(onGeolocationSuccess,onGeolocationError,geolocationOptions);
    } else {
        $("#geopos").html("No es posible obtener su posicion!");
    }
    
    
    
    inicializarFavoritos();
    
    inicializarBusqueda();
    
    inicializarBotonFavoritos();
    
    var networkstate = navigator.connection.type;
    if(networkstate == "none"){
        $(".offline").css("visibility","visible");
               
    }
    
}

function isOffline(){ 
    
    inicializarFavoritos();
    
    inicializarBotonFavoritos();
    
    var networkstate = navigator.connection.type;
    if(networkstate == "none"){
        $(".offline").css("visibility","visible");
               
    }
    
    $("#addfav").hide();
    
    $("#removefav").hide();
   
}

function getLocation() {

    if (navigator.geolocation) {
        
        //get the current location
        navigator.geolocation.getCurrentPosition(onGeolocationSuccess, onGeolocationError,geolocationOptions);
        
        $("#geopos").html("Reading location...");
    
    } else {
        $("#geopos").html("Unable to get your position!");
    }
 }

function onDeviceReady(){ 

    $('#homepage').click( function() { $.mobile.changePage("#auxMainPage"); return true; } );
    $('#homepageAux').click( function() { $.mobile.changePage("#auxMainPage"); return true; } );
    $('#homepage1').click( function() { $.mobile.changePage("#auxMainPage"); return true; } );
    
    //personalizar el mensaje que sale cuando se esperan datos AJAX
    $.mobile.loadingMessage = "Cargando datos";
    //personalizar el texto del botón para ir a la página anterior
    $.mobile.page.prototype.options.backBtnText = "Atr&aacute;s"
     
    
    $('#auxMainPage').bind('pageinit', function() {
        
        $('#botonConnection').click(checkConnection);
        $.mobile.fixedToolbars.show(true)
    })
    
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

        $('#conexionContent p:first').html('Tipo de conexi&oacute;n: ' + states[networkState]);
    });
    
    //exit button
    $('#exit_btn').click(function() {
        navigator.app.exitApp();
    });
 
}
    
    
    /**
     * Fetch the details of a place/business. This function is called before user navigates to details page
     * @param {Object} reference
     */
    function fetchDetails(entry){
		
        currentBusinessData = null; 
        
        
        $.mobile.pageLoading();
        $('.ui-loader h1').text('Cargando detalles....');
        var detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?reference=" + entry.reference + "&sensor=true&key=AIzaSyCami1I8wWyM5-VePBi7Dj6in7W-coqGkU";
        $("#name").html("Sin información.Es necesaria la conexión para obtener los detalles");
        $("#address").html("");
        $("#phone").html("");
        $("#rating").html("");
        $("#homepage").attr("href", "");
        
        if(navigator.onLine){  
            geolocationDetalles(entry.geometry.location.lat, entry.geometry.location.lng);
        }
        
        $.getJSON(detailsUrl, function(data){
            if (data.result) {
                currentBusinessData = data.result;
                
                esFavorito(currentBusinessData, function(isPlaceFav){
					console.log(currentBusinessData.name+" is fav "+isPlaceFav);
                    if (!isPlaceFav) {
                    
                        $("#addfav").show();
                        $("#removefav").hide();
                        $("#favstar").hide();
                    }
                    else {
                    
                        $("#addfav").hide();
                        $("#removefav").show();
                        $("#favstar").show();
                    }
                    
                    
                    
                    $("#name").html(data.result.name);
                    $("#address").html(data.result.formatted_address);
                    $("#phone").html(data.result.formatted_phone_number);
                    $("#rating").html(data.result.rating);
                    $("#homepage").attr("href", data.result.url);
                    
                });
                
            }
        }).error(function(err){
            console.log("Got Error while fetching details of Business " + err);
        }).complete(function(){
            $.mobile.pageLoading(true); //ocultar page loading
        });
        
    }
    
    function fetchDetailsFavoritos(reference,latitudAux,longitudAux){
        
        currentBusinessData = null; 
        
        
        $.mobile.pageLoading();
        $('.ui-loader h1').text('Cargando detalles....');
        var detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?reference=" + reference + "&sensor=true&key=AIzaSyCami1I8wWyM5-VePBi7Dj6in7W-coqGkU";
        $("#name").html("Sin información.Es necesaria la conexión para obtener los detalles");
        $("#address").html("");
        $("#phone").html("");
        $("#rating").html("");
        $("#homepage").attr("href", "");
        
        if(navigator.onLine){  
            geolocationDetalles(latitudAux, longitudAux);
        }
        
        $.getJSON(detailsUrl, function(data){
            if (data.result) {
                currentBusinessData = data.result;
                
                esFavorito(currentBusinessData, function(isPlaceFav){
                    console.log(currentBusinessData.name+" is fav "+isPlaceFav);
                    if (!isPlaceFav) {
                    
                        $("#addfav").show();
                        $("#removefav").hide();
                        $("#favstar").hide();
                    }
                    else {
                    
                        $("#addfav").hide();
                        $("#removefav").show();
                        $("#favstar").show();
                    }
                    
                    
                    
                    $("#name").html(data.result.name);
                    $("#address").html(data.result.formatted_address);
                    $("#phone").html(data.result.formatted_phone_number);
                    $("#rating").html(data.result.rating);
                    $("#homepage").attr("href", data.result.url);
                    
                });
                
            }
        }).error(function(err){
            console.log("Got Error while fetching details of Business " + err);
        }).complete(function(){
            $.mobile.pageLoading(true); //ocultar page loading
        });
        
        $.mobile.changePage("#details");
        
    }
    
   
    /**
     * Called to initiate Map page
     */
    function inicializarMapa(){
        $("#map").bind("pagebeforeshow", function(){
            try {
            
                $('#map_canvas').gmap({
                    panControl: true,
                    zoomControl: true,
                    'zoom': 12,
                    'callback': function(map){
                        $(cachedData.results).each(function(index, entry){
                            $('#map_canvas').gmap('addMarker', {
                                'position': new google.maps.LatLng(entry.geometry.location.lat, entry.geometry.location.lng),
                            }, function(map, marker){
                                $('#map_canvas').gmap('addInfoWindow', {
                                    'position': marker.getPosition(),
                                    'content': entry.name
                                }, function(iw){
                                    $(marker).click(function(){
                                        iw.open(map, marker);
                                    });
                                });
                            });
                            
                        });
                    }

                });
                $('#map_canvas').gmap('clearMarkers');
                
                $('#map_canvas').gmap({
                    'center': new google.maps.LatLng(latitud,longitud),
                    'zoom': 12,
                    'callback': function(map){
                        $(cachedData.results).each(function(index, entry){
                            $('#map_canvas').gmap('addMarker', {
                                'position': new google.maps.LatLng(entry.geometry.location.lat, entry.geometry.location.lng),
                            }, function(map, marker){
                                $('#map_canvas').gmap('addInfoWindow', {
                                    'position': marker.getPosition(),
                                    'content': entry.name
                                }, function(iw){
                                    $(marker).click(function(){
                                        iw.open(map, marker);
                                    });
                                });
                            });
                            
                        });
                        
                        
                        $('#map_canvas').gmap('addMarker', {
                            'position':  new google.maps.LatLng(latitud,longitud),
                        }, function(map, marker){
                            $('#map_canvas').gmap('addInfoWindow', {
                                'position': marker.getPosition(),
                                'content': 'Mi posición'
                            }, function(iw){
                                $(marker).click(function(){
                                    iw.open(map, marker);
                                    map.panTo(marker.getPosition());
                                });
                            });
                        });
                        
                    }

                });
                console.log("Map initialized");

            } 
            catch (err) {
                console.log("Got error while initializing map " + err);
            }
            
        });
        
        
    }
    
    //confirmar eliminar favorito
    function onConfirm(index) {

        if(index==2) {
             
            try {
                if (currentBusinessData != null) {
                    eliminarFavorito(currentBusinessData);
                    $("#add").show();
                    $("#remove").hide();
                    
                }
            } 
            catch (err) {
                console.log("Got Error while removing " + currentBusinessData.name + " error " + err);
            }
            
        }
    }
    
    

    function inicializarBotonFavoritos(){
        $("#removefav").click(function(){
        
            navigator.notification.confirm(
                    'Confirma eliminar de favoritos '+currentBusinessData.name+'?',     // mensaje (message)
                    onConfirm,      // función 'callback' a llamar con el índice del botón pulsado (confirmCallback)
                    'Eliminar favorito',            // titulo (title)
                     'No,Aceptar'       // botones (buttonLabels)
                    );

        });
        $("#addfav").click(function(){
            try {
                if (currentBusinessData != null) {
                    addToFavorite(currentBusinessData);
                    $("#add").hide();
                    $("#remove").show();
                }
            } 
            catch (err) {
                console.log("Got Error while adding " + currentBusinessData.name + " error " + err);
            }
            
        });
        
        
    }
    
    /**
     * Called each time before user navigates to Favourites
     */
    function inicializarFavoritos(){
        $("#fav").live("pagebeforeshow", function(){
        
            var db = window.openDatabase("Favourites", "1.0", "Favourites", 200000);
            try {
                db.transaction(function(tx){
					tx.executeSql('CREATE TABLE IF NOT EXISTS Favourite (id unique, reference, name,address,phone,rating,icon,vicinity,latitud,longitud)');
                    tx.executeSql('SELECT * FROM Favourite', [], function(tx, results){
                       
                        $("#fav-list").html("");
                        if (results != null && results.rows != null && results.rows.length>0) {
                            for (var index = 0; index < results.rows.length; index++) {
                                
                                var entry = results.rows.item(index);
                                
                                var htmlData = "<li><a class=\"detallesli\" reference=\"" + entry.reference + "\" latitud=\"" + entry.latitud + "\" longitud=\"" + entry.longitud + "\" id=\"" + entry.id + "\"><img src=\"" + entry.icon + "\" class=\"ui-li-icon\"></img><h3 class=\"detalles\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + entry.name + "</h3><p class=\"detalles2\" ><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Localizaci&oacute;n:" + entry.vicinity + "</strong><img src=\"favorito2.png\"></p></a></li>";
                                
                                $("#fav-list").append(htmlData);
                                
                                $('.detallesli').bind("click", function(event){
                                    event.preventDefault();
                                    //alert($(this).attr('id'));
                                    fetchDetailsFavoritos($(this).attr('reference'),$(this).attr('latitud'),$(this).attr('longitud'));
                                    return true;
                                });
                                
                            }

                        }else{
                           
                            var htmlData = "<div align='center' class='detalles3'>No hay favoritos registrados</div>";
                            
                            var liElem = $(document.createElement('li'));
                            
                            $("#fav-list").append(liElem.html(htmlData));
                              
                            
                        }
                        
                        $("#fav-list").listview('refresh');

                        
                    }, function(error){
                        console.log("Got error fetching favourites " + error.code + " " + error.message);
                    });
                });
            } 
            catch (err) {
                console.log("Got error while reading favourites " + err);
            }
            
        });
    }
    
    /**
     * Ensure we have the table before we use it
     * @param {Object} tx
     */
    function ensureTableExists(tx){
        tx.executeSql('CREATE TABLE IF NOT EXISTS Favourite (id unique, reference, name,address,phone,rating,icon,vicinity,latitud,longitud)');
    }
    
    /**
     * Add current business data to favourite
     * @param {Object} data
     */
    function addToFavorite(data){
        var db = window.openDatabase("Favourites", "1.0", "Favourites", 20000000);
        
        db.transaction(function(tx){
            ensureTableExists(tx);
            var id = (data.id != null) ? ('"' + data.id + '"') : ('""');
			var reference = (data.reference != null) ? ('"' + data.reference + '"') : ('""');
            var name = (data.name != null) ? ('"' + data.name + '"') : ('""');
            var address = (data.formatted_address != null) ? ('"' + data.formatted_address + '"') : ('""');
            var phone = (data.formatted_phone_number != null) ? ('"' + data.formatted_phone_number + '"') : ('""');
            var rating = (data.rating != null) ? ('"' + data.rating + '"') : ('""');
            var icon = (data.icon != null) ? ('"' + data.icon + '"') : ('""');
            var vicinity = (data.vicinity != null) ? ('"' + data.vicinity + '"') : ('""');
            var latitud = (data.geometry.location.lat != null) ? ('"' + data.geometry.location.lat + '"') : ('""');
            var longitud = (data.geometry.location.lng != null) ? ('"' + data.geometry.location.lng + '"') : ('""');

            var insertStmt = 'INSERT INTO Favourite (id,reference, name,address,phone,rating,icon,vicinity,latitud,longitud) VALUES (' + id + ',' + reference+ ',' + name + ',' + address + ',' + phone + ',' + rating + ',' + icon + ',' + vicinity + ',' + latitud + ',' + longitud + ')';
            tx.executeSql(insertStmt);
            
        }, function(error){
            console.log("Data insert failed " + error.code + "   " + error.message);
        }, function(){
            console.log("Data insert successful");
            
            navigator.notification.alert('El lugar se ha añadido a la lista de favoritos',null,'Favoritos','Aceptar');
            inicializarFavoritos();
            
          //cambiar a página de favoritos 
            $.mobile.changePage("#fav"); 
        });
        
    }
    
    

    function eliminarFavorito(data){
        try {
            var db = window.openDatabase("Favourites", "1.0", "Favourites", 20000000);
            
            db.transaction(function(tx){
                ensureTableExists(tx);
                var deleteStmt = "DELETE FROM Favourite WHERE id = '" + data.id + "'";
                console.log(deleteStmt);
                tx.executeSql(deleteStmt);
                
            }, function(error){
                console.log("Data Delete failed " + error.code + "   " + error.message);
            }, function(){
                console.log("Data Delete successful");
                navigator.notification.alert('Lugar elininado de la lista de favoritos',null,'Favorito','Aceptar');
                inicializarFavoritos();
                
                //cambiar a página de favoritos 
                $.mobile.changePage("#fav"); 
            });
        } 
        catch (err) {
            console.log("Caught exception while deleting favourite " + data.name);
        }
        
    }
    

    function esFavorito(data, callback){
        var db = window.openDatabase("Favourites", "1.0", "Favourites", 200000);
        try {
            db.transaction(function(tx){
                ensureTableExists(tx);
                var sql = "SELECT * FROM Favourite where id='" + data.id + "'";
                tx.executeSql(sql, [], function(tx, results){
                
                    var result = (results != null && results.rows != null && results.rows.length > 0);
                    
                    callback(result);
                }, function(tx, error){
                
                    console.log("Got error in isFav error.code =" + error.code + " error.message = " + error.message);
                    callback(false);
                    
                    
                });
            });
        } 
        catch (err) {
            console.log("Got error in isFav " + err);
            callback(false);
        }
        
        
    }
    
    function esFavoritoAux(data){
        var db = window.openDatabase("Favourites", "1.0", "Favourites", 200000);
        
        var resultado=false;
        
        try {
            db.transaction(function(tx){
                ensureTableExists(tx);
                var sql = "SELECT * FROM Favourite where id='" + data.id + "'";
                tx.executeSql(sql, [], function(tx, results){
                
                    resultado = (results != null && results.rows != null && results.rows.length > 0);
                    
                    return resultado;

                }, function(tx, error){
                
                    console.log("Got error in isFav error.code =" + error.code + " error.message = " + error.message);
                    return false;
                });
            });
        } 
        catch (err) {
            console.log("Got error in isFav " + err);

            return false;
        }
        
        return resultado;
        
    }
    

    function inicializarBusqueda(){
        $("#search").click(function(){
            try {
                
               if(navigator.onLine){  
                    
                
                $.mobile.pageLoading();
                
                if($("#searchbox").val()!=null ){
                    
                    $('.ui-loader h1').text('Buscando lugares '+$("#searchbox").val());
                }
                
                navigator.geolocation.getCurrentPosition(function(position){
                
                    var radius = $("#range").val() * 1000;
                    mapdata = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                    var url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + position.coords.latitude + "," + position.coords.longitude + "&radius=" + radius + "&name=" + $("#searchbox").val() + "&sensor=true&key=AIzaSyCami1I8wWyM5-VePBi7Dj6in7W-coqGkU";

                    $.getJSON(url, function(data){
                        cachedData = data;
                        $("#result-list").html("");
                        try {
                            
                            if($("#searchbox").val()!=null &&  $("#searchbox").val()!=''){
                                var cadenaBusqueda = "<div align=\"center\" class=\"detalles4\" data-theme=\"e\" >Criterio de b&uacute;squeda   " +$('#searchbox').val()+ "</div>";
                                var liElem = $(document.createElement('li'));
                                $("#result-list").append(liElem.html(cadenaBusqueda));
                            }
                            
                            if (data.results != null && data.results.length>0) {
                                
                            
                            $(data.results).each(function(index, entry){
                            
                                
                                var htmlData;

                                var favorito= esFavoritoAux(entry);

                                if (favorito) {

                                    htmlData = "<a href=\"#details\" id=\"" + entry.reference + "\"><img src=\"" + entry.icon + "\" class=\"ui-li-icon\"></img><h3 class=\"detalles\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + entry.name + "</h3><p><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Localizaci&oacute;n:" + entry.vicinity + "</strong><img src=\"favorito2.png\"></p></a>";
                                    
                                }
                                else {

                                    htmlData = "<a href=\"#details\" id=\"" + entry.reference + "\"><img src=\"" + entry.icon + "\" class=\"ui-li-icon\"></img><h3 class=\"detalles\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + entry.name + "</h3><p><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Localizaci&oacute;n:" + entry.vicinity + "</strong></p></a>";
                                 
                                }
                                
                                var liElem = $(document.createElement('li'));
                                
                                $("#result-list").append(liElem.html(htmlData));
                                
                                
                                $(liElem).bind("tap", function(event){
                                    event.stopPropagation();
                                    fetchDetails(entry);
                                    return true;
                                });
                                
                            });
                            
                            }else{
                                
                                var htmlData = "<div align='center' class='detalles3'>No hay resultados</div>";
                            
                                var liElem = $(document.createElement('li'));
                            
                                $("#result-list").append(liElem.html(htmlData));
                            }
                            
							$("#result-list").listview('refresh');
                        } 
                        catch (err) {
                            console.log("Got error while putting search result on result page " + err);
                        }
                        
                        
                       
                        inicializarMapa();
                        
                        $.mobile.changePage("#list");
                        
                        $.mobile.pageLoading(true);
                        
                        
                        
                    }).error(function(xhr, textStatus, errorThrown){
                        console.log("Got error while fetching search result : xhr.status=" + xhr.status);
                        
                    }).complete(function(error){
                        $.mobile.pageLoading(true);
                    });
                }, function(error){
                    console.log("Got Error fetching geolocation " + error);
                });
                
                
            }
            else{
                
                navigator.notification.alert('Debe disponer de una conexión',null,'Search','Aceptar');
                
            }
            }
            catch (err) {
                console.log("Got error on clicking search button " + err);
            }
            
            
            
        });
        
    }
    

    function onGeolocationSuccess(position) {

        var geostr = "<h3>Localización</h3><p>Latitud (deg): " + position.coords.latitude
            + "</p><p>Longitud (deg): " + position.coords.longitude
            
        $("#geopos").html(geostr);
        
        latitud = position.coords.latitude;
        longitud = position.coords.longitude;
        
        currentposition = new google.maps.LatLng(latitud,longitud);
        
        var mapoptions = {
            zoom: 12,
            panControl: true,
            zoomControl: true,
            center: currentposition,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        
        map = new google.maps.Map(document.getElementById('map_holder'), mapoptions);

        marker = new google.maps.Marker({
                position: currentposition,
                map: map,
                title: 'My Location'
        });
        
        codeLatLng(currentposition);
        
        var mapOutput = '<img src="http://maps.googleapis.com/maps/api/staticmap?markers='+position.coords.latitude+','+position.coords.longitude+'&zoom=12&size=200x200&scale=2&sensor=true">';
        var element = document.getElementById('map_holder');
        element.innerHTML = mapOutput;
      }
    
    function onGeolocationError(error) {
        
        var errString   =   '';
        
        // Check to see if we have received an error code     
        if(error.code) {
            // If we have, handle it by case
            switch(error.code)
            {
                case 1: // PERMISSION_DENIED
                    errString   =   
                        'Unable to obtain the location information ' + 
                        'because the device does not have permission '+
                        'to the use that service.';
                break;
                case 2: // POSITION_UNAVAILABLE
                    errString   =   
                        'Unable to obtain the location information ' +
                        'because the device location could not be ' +
                        'determined.';
                break;
                case 3: // TIMEOUT
                    errString   =   
                        'Unable to obtain the location within the ' +
                        'specified time allocation.';
                break;
                default: // UNKOWN_ERROR
                    errString   =   
                        'Unable to obtain the location of the ' +
                        'device to an unknown error.';
                break;
            }
            
            navigator.notification.alert(errString,alert,'Location','Aceptar');
            
        }
    }
  
  function codeLatLng(currentposition) {
      
      infowindow = new google.maps.InfoWindow();
      
      geocoder = new google.maps.Geocoder();
      
      geocoder.geocode({'latLng': currentposition}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
           
           var geostr = "<h3>Localización</h3><p>"+results[0].formatted_address+"<br/>"+
           results[1].formatted_address+"</p>";

           $("#geopos").html(geostr);
           
          if (results[1]) {
            map.setZoom(11);
            marker = new google.maps.Marker({
                position: currentposition,
                map: map
            });
            infowindow.setContent(results[1].formatted_address);
            infowindow.open(map, marker);
          } else {
            //alert('No results found');
          }
        } else {
          //alert('Geocoder failed due to: ' + status);
        }
      });
    }
  
  function codeLatLngAux(currentposition) {
      
      infowindow = new google.maps.InfoWindow();
      
      geocoder = new google.maps.Geocoder();
      
      geocoder.geocode({'latLng': currentposition}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {

            if (results[1]) {
            map.setZoom(11);
            marker = new google.maps.Marker({
                position: currentposition,
                map: map
            });
            infowindow.setContent(results[1].formatted_address);
            infowindow.open(map, marker);
          } else {
            //alert('No results found');
          }
        } else {
          //alert('Geocoder failed due to: ' + status);
        }
      });
    }
  
  function geolocationDetalles(latitudAux,longitudAux) {
      
      var currentpositionAux = new google.maps.LatLng(latitudAux,longitudAux);
      
      var mapoptions = {
          zoom: 12,
          panControl: true,
          zoomControl: true,
          center: currentpositionAux,
          mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      
      map = new google.maps.Map(document.getElementById('map_canvas_detalle'), mapoptions);

      marker = new google.maps.Marker({
              position: currentpositionAux,
              map: map,
              title: 'Location'
      });
      
      codeLatLngAux(currentpositionAux);
      
      var mapOutput = '<img src="http://maps.googleapis.com/maps/api/staticmap?markers='+latitudAux+','+longitudAux+'&zoom=12&size=250x250&scale=2&sensor=true">';
      var element = document.getElementById('map_canvas_detalle');
      element.innerHTML = mapOutput;
    }
    
   


document.addEventListener("deviceready", onDeviceReady);

document.addEventListener("online", isOnline, false);  

document.addEventListener("offline", isOffline, false);  
