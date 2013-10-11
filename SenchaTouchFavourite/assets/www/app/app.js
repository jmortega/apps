Ext.setup({
    onReady: function(){ 
    	var lastPanelId = 0;
      
    	var SEARCHPAGE=0;
    	var TABPAGE=1;
    	var FAVPAGE=2;
    	var DETAILSPAGE=3;

		var cachedDetails=null;
		
		var latitudAux = 0
        var longitudAux = 0;
		var position;
        
		var markersAux = [];
		
        var searchPanel = new Ext.form.FormPanel({
            layout: 'fit',
            fullscreen: true,
            scroll: 'vertical',
            standardSubmit: false,
            defaults:{
                styleHtmlContent: true
            },
            //Adding form field
            items: [{
                xtype: 'fieldset',
                title: 'Encuentra lugares en un rango de 20 km a partir de tu localizaci&oacute;n',
                items: [{
                    xtype: 'textfield',
                    name: 'search',
                    label: 'Criterio de b&uacute;squeda',
                    id:"field_search",
                    itemId:'field_search',
                    useClearIcon: true,
                    autoCapitalize: false,
                    style: 'font-size:25px;text-align: center;'
                }, {
                    xtype: 'sliderfield',
                    name: 'range',
                    id:"range_search",
                    itemId:'range_search',
                    label: 'Rango (0-20 Kms)',
                    value: 5,
                    minValue: 0,
                    maxValue: 20,
                    style: 'font-size:25px;text-align: center;'
                },
                {
                    xtype: 'textfield',
                    name: 'location',
                    label: 'Localización',
                    id:"field_location",
                    itemId:'field_location',
                    useClearIcon: false,
                    autoCapitalize: false,
                    style: 'font-size:25px;text-align: center;'
                },
                {
                    xtype: 'textfield',
                    name: 'location',
                    label: 'Latitud',
                    id:"field_latitud",
                    itemId:'field_latitud',
                    useClearIcon: false,
                    autoCapitalize: false,
                    style: 'font-size:25px;text-align: center;'
                },
                {
                    xtype: 'textfield',
                    name: 'location',
                    label: 'Longitud',
                    id:"field_longitud",
                    itemId:'field_longitud',
                    useClearIcon: false,
                    autoCapitalize: false,
                    style: 'font-size:25px;text-align: center;'
                }
                ]
            }]            
            //Docking a toolbar at bottom			
            ,
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'bottom',
                items: [{
                        text: 'Check Connection',
                        iconCls: 'settings',
                        title: 'Check Connection',
                        iconMask: true,
                        style: 'height:25px;font-size:25px;text-align: center;',
                        ui: 'round',
                        ui: 'confirm',
                        handler: function(){
                            
                            var isOnline = navigator.onLine;
                            
                            if(!isOnline){
                                
                                new Ext.MessageBox().show({
                                    title: 'Connection',
                                    msg: 'You are not connected any network',
                                 });
                            }
                            
                            var networkState = navigator.connection.type;

                            var states = {};
                            states[Connection.UNKNOWN]  = 'Unknown connection';
                            states[Connection.ETHERNET] = 'Ethernet connection';
                            states[Connection.WIFI]     = 'WiFi connection';
                            states[Connection.CELL_2G]  = 'Cell 2G connection';
                            states[Connection.CELL_3G]  = 'Cell 3G connection';
                            states[Connection.CELL_4G]  = 'Cell 4G connection';
                            states[Connection.NONE]     = 'No network connection';
                            
                            new Ext.MessageBox().show({
                                title: 'Connection',
                                msg: 'Connection type:'+states[networkState],
                                multiline: true,
                                style: 'width:80px;height:30px;font-size:20px;background:grey',
                             });
                            
                        }
                },
                {
                    text: 'Update Location',
                    iconCls: 'refresh',
                    title: 'Update Location',
                    iconMask: true,
                    style: 'height:25px;font-size:25px;text-align: center;',
                    ui: 'round',
                    ui: 'confirm',
                    handler: function(){
                        
                     // Prompt for user data:
                        Ext.Msg.prompt('Location', 'Introduzca la nueva localización:', function(btn, text){
                            if (btn == 'ok'){
                                
                                var geocoder = new google.maps.Geocoder();
                                var mapAux = location.map;
                                geocoder.geocode({'address': text}, function(results, status) {
                                    if (status == google.maps.GeocoderStatus.OK) {
                                        //mapAux.getGeo().suspendUpdates();
                                        mapAux.setCenter(results[0].geometry.location);
                                        latitudAux=results[0].geometry.location.lat();
                                        longitudAux=results[0].geometry.location.lng();
                                        //alert(results[0].formatted_address);
                                        Ext.getCmp('field_location').setValue(results[0].formatted_address);
                                        Ext.getCmp('field_latitud').setValue(latitudAux);
                                        Ext.getCmp('field_longitud').setValue(longitudAux);
                                        var marker = new google.maps.Marker({
                                            map: location.map,
                                            position: results[0].geometry.location,
                                            title: 'My Location',
                                        });
                                        //mapAux.getGeo().setLatitude(results[0].geometry.location.lat());
                                        //mapAux.getGeo().setLongitude(results[0].geometry.location.lng());
                                        
                                        Ext.Msg.alert('Update Location', "Nueva Localizacion:<br/>"+results[0].formatted_address+"<br/>Latitud:<br/>"+latitudAux+"<br/>Longitud:<br/>"+longitudAux);
                                        
                                    } else {
                                        Ext.Msg.alert('Error', 'No es posible actualizar la localización.');
                                    }
                                    
                                });
                                
                            }
                        });
                      
                    }
            },
                {
                    xtype: 'spacer'
                }, {
                    text: 'Buscar',
                    iconCls: 'search',
                    title: 'Buscar',
                    iconMask: true,
                    style: 'height:25px;font-size:25px;text-align: center;',
                    ui: 'round',
                    ui: 'confirm',
                    handler: function(){
                        
                        var isOnline = navigator.onLine;
                        
                        if(!isOnline){
                            
                            new Ext.MessageBox().show({
                                title: 'Conexión',
                                msg: 'Debe disponer de una conexión de datos',
                             });
                            
                        }else{
                        
                            lastPanelId=TABPAGE;
                            fetchFromGoogle(Ext.getCmp('field_search').getValue(),Ext.getCmp('range_search').getValue());
                            mainPanel.dockedItems.items[0].setTitle('Resultados de b&uacute;squeda');
                            mainPanel.setActiveItem(lastPanelId);
                        }
                    }
                }]
            }]
        });
        
    	var detailClickHandler = function(event){
		   var reference = event.getTarget(".place").id;
           fetchDetails(reference);
		   mainPanel.dockedItems.items[0].setTitle('Detalles');
           mainPanel.setActiveItem(DETAILSPAGE, "slide");
        }
    	

        var result = new Ext.Component({
        
            xtype: 'list',
            title: 'Resultados de búsquedas',
            iconMask: true,
            iconCls: 'organize',
            onItemDisclosure: true,
            cls: 'timeline',
            scroll: 'vertical',
            emptyText: "<div>No se han encontrado datos para el criterio introducido</div>",
            scrollable: true,
            loadingText: "<div><br/><br/><br/>Cargando datos....</div>",
            loadmask: true,
            defaults:{
                styleHtmlContent: true
            },
            tpl: [	
                  	'<tpl for=".">', 
						'<div class="place" id="{reference}">', 
							'<div class="icon"><img  src="{icon}" /></div>', 
							'<div class="list-item-titulo">', 
								'<h2 >{name}</h2>', 
								'<p>{vicinity}</p>', 
							'</div>', 
						'</div>', 
					'</tpl>',

					
					
					],
			listeners: {
     				   el: {
            				tap: detailClickHandler,
            				delegate: '.place'
            				
        					}
   					 }
				
        });
		
        var favorites = new Ext.Component({
            title: 'Favotitos',
            iconMask: true,
            iconCls: 'organize',
            scroll: 'vertical',
            emptyText: "<div>No hay favoritos registrados </div>",
            scrollable: true,
            loadingText: "<div><br/><br/><br/>Cargando datos....</div>",
            loadmask: true,
            defaults:{
                styleHtmlContent: true
            },
            cls: 'timeline',
            scroll: 'vertical',
            tpl: [	'<tpl for=".">', 
					'<div class="place" id="{reference}">', 
						'<div class="icon"><img  src="{icon}" /></div>', 
						'<div class="imgFavorito">', 
							'<h2>{name}</h2>', 
							'<p>{vicinity}</p>', 
						'</div>', 
					'</div>', 
				'</tpl>'],
			listeners: {
     				   el: {
            				tap: detailClickHandler,
            				delegate: '.place'
            				
        					}
   					 }
			
        });
        
       
        var infowindow = new google.maps.InfoWindow({
            content: 'My location'
        });
        
        var map = new Ext.Map({
            iconMask: true,
            iconCls: 'maps',
            title: 'Mapa', // Name that appears on this tab
            fullscreen:true,
            
            mapOptions: { // Used in rendering map
                zoom: 12,
                mapTypeId: google.maps.MapTypeId.HYBRID,
                backgroundColor: 'transparent',
                disableDoubleClickZoom: true,
                keyboardShortcuts: false,
                scrollwheel: false,
                streetViewControl : false,
                navigationControl: true,
                navigationControlOptions: {
                    style: google.maps.NavigationControlStyle.DEFAULT
                }
            },
            listeners: {
                maprender : function(comp, map){
                    var marker = new google.maps.Marker({
                         position: map.center,
                         title : 'Location',
                         map: map
                    });

                    //infowindow.open(map, marker);

                    google.maps.event.addListener(marker, 'click', function() {
                        new google.maps.InfoWindow({
                            content: 'Location'
                            }).open(map, marker);
                    });
                }
            }
            
        });
        
       
        var location = new Ext.Map( {
            
            iconMask: true,
            iconCls: 'maps',
            title: 'Localización', // Name that appears on this tab
            fullscreen:true,
            mapOptions: { // Used in rendering map
                zoom: 12,
                mapTypeId : google.maps.MapTypeId.ROADMAP,
                navigationControl: true,
                backgroundColor: 'transparent',
                disableDoubleClickZoom: true,
                keyboardShortcuts: false,
                scrollwheel: false,
                streetViewControl : false,
                navigationControl: true,
                navigationControlOptions: {
                    style: google.maps.NavigationControlStyle.DEFAULT
                }
            },
            listeners: {
                maprender: function(comp, location) {
                    var marker = new google.maps.Marker({
                        position: position,
                        title : 'My location',
                        map: location
                    });


                    google.maps.event.addListener(marker, 'click', function() {
  
                        new google.maps.InfoWindow({
                        content: 'My location'
                        }).open(location, marker);
                    });

                }
            }
        });
        
        
        navigator.geolocation.getCurrentPosition(
                function(positionAux){
                    latitudAux = positionAux.coords.latitude;
                    longitudAux = positionAux.coords.longitude;
                    
                    location.update({
                                latitude: latitudAux,
                                longitude: longitudAux
                            });
                    
                    position = new google.maps.LatLng(latitudAux, longitudAux);
                    //alert(position);
                    
                    var geocoder = new google.maps.Geocoder();
                    var mapAux = location.map;
                    geocoder.geocode({'latLng': position}, function(results, status) {
                        if (status == google.maps.GeocoderStatus.OK) {
                            //mapAux.getGeo().suspendUpdates();
                            mapAux.setCenter(results[0].geometry.location);
                            //alert(results[0].geometry.location);
                            //alert(results[0].formatted_address);
                            Ext.getCmp('field_location').setValue(results[0].formatted_address);
                            Ext.getCmp('field_latitud').setValue(latitudAux);
                            Ext.getCmp('field_longitud').setValue(longitudAux);
                            var marker = new google.maps.Marker({
                                map: location.map,
                                position: results[0].geometry.location,
                                title: 'My Location',
                            });
                            //mapAux.getGeo().setLatitude(results[0].geometry.location.lat());
                            //mapAux.getGeo().setLongitude(results[0].geometry.location.lng());
                        } else {
                            Ext.Msg.alert('Error', 'Unable to find address.');
                        }
                        
                    });
                }
                
        );
        
        
        
     
        var tabResultPanel = new Ext.TabPanel({
            layout: 'fit',
            tabBar: {
                dock: 'bottom',
                layout: {
                    pack: 'center'
                }
            },
            items: [result,map,location],
        
        
        });
        
        var placeDetailsPanel = new Ext.Panel({
            layout: 'fit',
            defaults:{
                styleHtmlContent: true
            },
            tpl: [				
					'<div class="descrip">', 
					   '<div>',					   
					   	'<h1 class="bold">Lugar</h1>',
					   '</div>',
					   '<div>',					   
					      '<h1><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{name}</h1>',
					   '</div>',
					   
					'</div><br/>', 
					
					'<div class="descrip">', 
					   '<div>',					   
					   	'<h1 class="bold">Direcci&oacute;n</h1>',
					   '</div>',
					   '<div>',					   
					      '<h1><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{formatted_address}</h1>',
					   '</div>',
					   
					'</div><br/>', 						
					'<div class="descrip">', 
					   '<div>',					   
					   	'<h1 class="bold">Tel&eacute;fono</h1>',
					   '</div>',
					   '<div>',					   
					      '<h1><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{formatted_phone_number}</h1>',
					   '</div>',
					   
					'</div><br/>', 						
					'<div class="descrip">', 
					   '<div>',					   
					   	'<h1 class="bold">Rating</h1>',
					   '</div>',
					   '<div>',					   
					      '<h1><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{rating}</h1>',
					   '</div>',
					'</div><br/>',
					
					'<div class="descrip">', 
					   '<div>',					   
					   	'<h1 class="bold">URL</h1>',
					   '</div>',
					   '<div>',					   
					      '<a href="{url}" target="_blank"><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Home Page</a>',
					   '</div>',
					'</div>',
					
				
			]
		});
		
        var resultDetailPanel = new Ext.Panel({
            layout: {
            	type: 'vbox',
            },
            items:[
            	placeDetailsPanel,
            	{
            		xtype:'button',
            		id:"button_favorito",
                    itemId:'button_favorito',
            		text: 'Añadir a favoritos',
            		handler:function(button,event){
						if(button.text=="Añadir a favoritos"){
							addCurrentToFav();
							button.setText("Eliminar de favoritos");
						}
						else{
							removeCurrentFromFav();				
						}
						
            		}
            		
            	}
            ],
        	dockedItems: [{
                xtype: 'toolbar',
                dock: 'bottom',
                items: [
                	{ 
                    ui: 'round',
                    text: 'Back',
                    style: 'height:25px;font-size:25px;text-align: center;',
                    handler: function(){
							
                                if(lastPanelId==0){
                                	mainPanel.dockedItems.items[0].setTitle('Sencha Places Mobile');
                                }
                                else if(lastPanelId==1){
                                    mainPanel.dockedItems.items[0].setTitle('Resultados de busqueda');
                                }
                                else if(lastPanelId==2){
                                    fetchFromDB();
                                    mainPanel.dockedItems.items[0].setTitle('Favoritos');                                
                                }
                                else if(lastPanelId==3){

                                    mainPanel.dockedItems.items[0].setTitle('Detalles');                                
                                }
                           
                    	    	mainPanel.setActiveItem(lastPanelId, {type: 'slide', direction: 'right'});
                    		}
                    
                	}	
                ]
            }]
        
        });
        
        //Main Panel with CardLayout
        var mainPanel = new Ext.Panel({
            layout: 'card',
            fullscreen: true,
            items: [searchPanel, tabResultPanel, favorites, resultDetailPanel],
            dockedItems: [{
                xtype: 'toolbar',
                title: 'Sencha Places Mobile',
                dock: 'top',
                items: [{
                
                    iconMask: true,
                    ui: 'round',
                    style: 'width:80px;height:30px;font-size:25px',
                    iconCls: 'home',
                    handler: function(){
                        lastPanelId = SEARCHPAGE;
						
                        mainPanel.dockedItems.items[0].setTitle('Sencha Places Mobile');
                        mainPanel.setActiveItem(lastPanelId, "slide");
                    }
                    
                }, {
                    xtype: 'spacer'
                }, {
                
                    iconMask: true,
                    ui: 'round',
                    style: 'width:80px;height:30px;font-size:25px',
                    iconCls: 'star',
                    handler: function(){
                    	fetchFromDB();
                    	lastPanelId = FAVPAGE;
                    	mainPanel.dockedItems.items[0].setTitle('Favoritos');
                        mainPanel.setActiveItem(lastPanelId, "slide");
                    }
                    
                }]
            }]
        });
		
		// These are all Google Maps APIs
        var addMarker = function(name,reference,position) {

				var marker = new google.maps.Marker({
					map: map.map,
					position: position,
					clickable: true,
					title: name,
					animation: google.maps.Animation.DROP,
				});
				google.maps.event.addListener(marker, 'click', function(){
					fetchDetails(reference);
					mainPanel.dockedItems.items[0].setTitle('Details');
           			mainPanel.setActiveItem(DETAILSPAGE, "slide");

				});
				
				markersAux.push(marker);
				
			

        };
        

        var fetchFromGoogle = function(keyword,range){

        	var rangeAux=range*1000;
        	
        	
        	//borrar markers antes de mostrar los nuevos
        	Ext.each(markersAux, function(marker) {
                    marker.setMap(null);
            });
            
        	
            navigator.geolocation.getCurrentPosition(
            	function(position){

            	    if(latitudAux==0){
            	        latitudAux = position.coords.latitude;
            	    }
            	    
            	    if(longitudAux==0){
            	        longitudAux = position.coords.longitude;
					
            	    }
            	    
					map.update({
								latitude: latitudAux,
								longitude: longitudAux
							});


					
            	    var googlePlaceUrl='https://maps.googleapis.com/maps/api/place/search/json?location='+latitudAux+','+longitudAux+'&radius='+rangeAux+'&name='+keyword+'&sensor=false&key=AIzaSyCami1I8wWyM5-VePBi7Dj6in7W-coqGkU';
		            Ext.Ajax.request({
        		        url: googlePlaceUrl,
                		success: function(response, opts){


                    		var obj = Ext.decode(response.responseText);
                    		
                    		result.update(obj.results);
							var data = obj.results;
							
							if(data.length==0){
							    new Ext.MessageBox().show({
		                            title: 'Search',
		                            defaults:{
		                                styleHtmlContent: true
		                            },
		                            msg: 'No hay resultados <br/><br/> Criterio B&uacute;squeda:'+keyword+'<br/><br/>Rango:'+range+' kms',
		                            multiline: true,
		                            style: 'width:80px;height:30px;font-size:30px;',
		                        })
							}
							else{
    
							    if(keyword==''){
			                        
			                        new Ext.MessageBox().show({
			                            title: 'Search',
			                            defaults:{
			                                styleHtmlContent: true
			                            },
			                            msg: 'Encontrados '+data.length+'  resultados <br/><br/> Rango:'+range+' kms',
			                            multiline: true,
			                            style: 'width:80px;height:30px;font-size:30px;',
			                        });
			                        
			                    }else{
			                        
			                        new Ext.MessageBox().show({
			                            title: 'Search',
			                            defaults:{
			                                styleHtmlContent: true
			                            },
			                            msg: 'Encontrados '+data.length+'  resultados <br/><br/> Criterio B&uacute;squeda::'+keyword+'<br/><br/>Rango:'+range+' kms',
			                            multiline: true,
			                            style: 'width:80px;height:30px;font-size:30px;',
			                        })
			                        
			                    }
							    
							}
							
					 		for (var i = 0, ln = data.length; i < ln; i++) { // Loop to add points to the map
          						var place = data[i];                           

          					if (place.geometry && place.geometry.location) {      
            					var position = new google.maps.LatLng( place.geometry.location.lat, place.geometry.location.lng);  
            					addMarker(place.name,place.reference,position);                  // Call addMarker function with new data
          					}
        				}

                	},
                	failure: function(response, opts){
                    	console.log('server-side failure with status code ' + response.status);
                               
                	}
                },
                function(err){
	                console.log('Failed to get geo location from phonegap '+err);
                }
            );
            })
        }
        
        var fetchFromDB = function(){

            var db = window.openDatabase("Favourites", "1.0", "Favourites", 200000);
            try {
                db.transaction(function(tx){
                	tx.executeSql('SELECT * FROM Favourite', [], 
                		function(tx, results){
                		    var arr = [];
                			for(var i=0;i<results.rows.length;i++){
                			    var data = results.rows.item(i)
								arr[i]=data;								
							}

                			if(results.rows.length==0){
                                
                                new Ext.MessageBox().show({
                                    title: 'Favoritos',
                                    msg: 'No hay favoritos registrados',
                                    multiline: true,
                                    style: 'width:80px;height:30px;font-size:30px;background:grey',
                                });
                                
                			}
                			    
                        	favorites.update(arr);
                        
                    	}, 
                    	function(error){
                        	console.log("Got error fetching favourites " + error.code + " " + error.message);
                    	}
                    );
                });
            } 
            catch (err) {
                console.log("Got error while reading favourites " + err);
            }

        }
        
        
        
        var fetchDetails = function (reference){
            placeDetailsPanel.update({name:"",formatted_address:"",formatted_phone_number:"",rating:"",url:""});
       		Ext.Ajax.request({
                url: 'https://maps.googleapis.com/maps/api/place/details/json?reference=' + reference + '&sensor=false&key=AIzaSyCami1I8wWyM5-VePBi7Dj6in7W-coqGkU',
                success: function(response, opts){
                    var obj = Ext.decode(response.responseText);
                    cachedDetails = obj.result; 
                    isFav(obj.result, function(result){
                       if(result){
							resultDetailPanel.items.items[1].setText("Eliminar de favoritos");
	                   }
	                   else{
							resultDetailPanel.items.items[1].setText("Añadir a favoritos");
	                   }
	                   placeDetailsPanel.update(obj.result);
                    });
                    
                   
                },
                failure: function(response, opts){
                    console.log('server-side failure with status code ' + response.status);
                }
            })
        }
    
    
    /**
     * Ensure we have the table before we use it
     * @param {Object} tx
     */
    var ensureTableExists=function(tx){
        tx.executeSql('CREATE TABLE IF NOT EXISTS Favourite (id unique, reference, name,address,phone,rating,icon,vicinity)');
    }
 
    var addCurrentToFav = function(){
      addToFavorite(cachedDetails);
    }
    
    var removeCurrentFromFav = function(){
        
        var msg = new Ext.MessageBox();
        msg.show({
        title: 'Eliminar favorito',
        defaults:{
            styleHtmlContent: true
        },
        width:400,
        multiline: true,
        style: 'width:100px;height:50px;font-size:30px;',
        msg: '¿Desea eliminar el lugar '+cachedDetails.name+' de favoritos?',
        buttons: [{text:'OK',itemId:'OK'},{text:'Close',itemId:'close'}],
        fn:function(response){
            
                if(response!=null && response=='OK'){
                    
                    removeFromFavorite(cachedDetails);
                    Ext.getCmp('button_favorito').setText("Añadir a favoritos");
                }

            }
        });
        msg.doComponentLayout();

      
    }

    /**
     * Add current business data to favourite
     * @param {Object} data
     */
    var addToFavorite = function(data){
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
            var insertStmt = 'INSERT INTO Favourite (id,reference, name,address,phone,rating,icon,vicinity) VALUES (' + id + ',' + reference+ ',' + name + ',' + address + ',' + phone + ',' + rating + ',' + icon + ',' + vicinity + ')';
            tx.executeSql(insertStmt);

        }, function(error){
            console.log("Data insert failed " + error.code + "   " + error.message);

        }, function(){
            console.log("Data insert successful");

        });
        
    }
    
    
    /**
     * Remove current business data from favourite
     * @param {Object} data
     */
    var removeFromFavorite = function(data){
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
            });
        } 
        catch (err) {
            console.log("Caught exception while deleting favourite " + data.name);
        }
        
    }
    
    /**
     *
     * @param {Object} reference
     * @return true if place is favourite else false
     */
    var isFav = function(data, callback){

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
        
    }
});


      
	
  