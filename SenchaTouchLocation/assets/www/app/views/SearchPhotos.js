FlickrFindr.view.SearchPhotosTpl = new Ext.XTemplate('<div class="searchresult">', '<img src="{[this.getPhotoURL("s", values)]}" height="75" width="75"/>', ' {title}</div>', {
  getPhotoURL: function(size, values) { /* Form a URL based on Flickr's URL specification: http://www.flickr.com/services/api/misc.urls.html */
    size = size || 's';
    var url = 'http://farm' + values.farm + '.static.flickr.com/' + values.server + '/' + values.id + '_' + values.secret + '_' + size + '.jpg';
    return url;
  }
});

FlickrFindr.view.SearchPhotos = Ext.extend(Ext.Panel, {
  id: 'searchphotos',
  layout: 'card',
  fullscreen: true,

  initComponent: function() {
    Ext.apply(this, {
      dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        title: 'Flickrr Mobile',
        items: [
{
    text: 'Update Location',
    iconCls: 'refresh',
    title: 'Update Location',
    iconMask: true,
    style: 'height:25px;font-size:25px;text-align: center;',
    ui: 'round',
    ui: 'confirm',
    handler: function(){
        
        var location= Ext.getCmp('location');
        
        var latitudAux=0;
        var longitudAux=0;
        
        //Prompt for user data:
        Ext.Msg.prompt('Location', 'Introduzca la nueva localizacion:', function(btn, text){
            if (btn == 'ok'){
                
                var geocoder = new google.maps.Geocoder();
                var mapAux = location.map;
                geocoder.geocode({'address': text}, function(results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
        
                        mapAux.setCenter(results[0].geometry.location);
                        latitudAux=results[0].geometry.location.lat();
                        longitudAux=results[0].geometry.location.lng();
                        
                        Ext.getCmp('field_latitud').setValue(latitudAux);
                        
                        Ext.getCmp('field_longitud').setValue(longitudAux);

                        var marker = new google.maps.Marker({
                            map: location.map,
                            position: results[0].geometry.location,
                            title: 'My Location',
                        });

                        //Ext.Msg.alert('Update Location', "Nueva Localizacion:<br/>"+results[0].formatted_address+"<br/>Latitud:<br/>"+latitudAux+"<br/>Longitud:<br/>"+longitudAux);
                        
                        Ext.Msg.alert('Info', 'Localizacion<br/>'+results[0].formatted_address);
                        
                    } else {

                        navigator.geolocation.getCurrentPosition(
                                function(positionAux){
                                   
                                    latitudAux = positionAux.coords.latitude;
                                    
                                    longitudAux = positionAux.coords.longitude;
                                    

                                    Ext.getCmp('field_latitud').setValue(latitudAux);
                                    Ext.getCmp('field_longitud').setValue(longitudAux);
                                    
                                    location.update({
                                                latitude: latitudAux,
                                                longitude: longitudAux
                                            });
                                    
                                    var position = new google.maps.LatLng(latitudAux, longitudAux);

                                    var geocoder = new google.maps.Geocoder();
                                    var mapAux = location.map;
                                    geocoder.geocode({'latLng': position}, function(results, status) {
                                        if (status == google.maps.GeocoderStatus.OK) {
   
                                            mapAux.setCenter(results[0].geometry.location);
                                            //alert(results[0].geometry.location);
                                            //alert(results[0].formatted_address);
                                            var marker = new google.maps.Marker({
                                                map: location.map,
                                                position: results[0].geometry.location,
                                                title: 'My Location',
                                            });
                                            
                                            Ext.Msg.alert('Info', 'Localizacion<br/>'+results[0].formatted_address);
                                            
                                        } else {
                                            Ext.Msg.alert('Error', 'No es posible obtener su localizacion.');
                                        }
                                        
                                    });
                                }
                                
                        );
                    }
                    
                });
                
                
                var dt = new Date().add(Date.YEAR, -1);
                
                var geo = new Ext.util.GeoLocation({
                    autoUpdate: true,
                    provider: navigator.geolocation,
                    timeout: 5000,
                    // 5 second timeout
                    listeners: {
                      locationupdate: function(geo) {
                        // Use our coordinates.
                        var easyparams = {
                          "min_upload_date": dt.format("Y-m-d H:i:s"),
                          "lat": Ext.getCmp('field_latitud').getValue(),
                          "lon": Ext.getCmp('field_longitud').getValue(),
                          "accuracy": 16,
                          "radius": 10,
                          "radius_units": "km"
                        };

                        var store = Ext.getCmp('searchphotos').down('list').getStore();
                        store.getProxy().extraParams = Ext.apply(store.getProxy().extraParams, easyparams);
                        
                        store.on('load', function () {
                            var count = store.getCount();
                            console.log(count);
                          });
                        
                        store.load();
                      },
                      locationerror: function(geo, bTimeout, bPermissionDenied, bLocationUnavailable, message) {
                        Ext.Msg.alert('Error','Error al obtener la localizacion');
                        var store = Ext.getCmp('searchphotos').down('list').getStore();
                        store.getProxy().extraParams = Ext.apply(store.getProxy().extraParams, easyparams);
                        store.load();

                      }
                    }
                  });
                  geo.provider=navigator.geolocation;
                  geo.updateLocation();
            }
        });
      
    }
},        
        {
          xtype: 'spacer'
        },
        {
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
        xtype: 'textfield',
        name: 'location',
        label: 'Latitud',
        id:"field_latitud",
        itemId:'field_latitud',
        useClearIcon: false,
        autoCapitalize: false,
        style:"visibility:hidden;display:none"
    },
    {
        xtype: 'textfield',
        name: 'location',
        label: 'Longitud',
        id:"field_longitud",
        itemId:'field_longitud',
        useClearIcon: false,
        autoCapitalize: false,
        style:"visibility:hidden;display:none"
    }
        
          ]
      }],
      items: [
        {
        xtype: 'list',
        store: 'FlickrFindr.store.SearchPhotos',
        itemTpl: FlickrFindr.view.SearchPhotosTpl,
        listeners: {
          render: function() {
            var dt = new Date().add(Date.YEAR, -1);

            var latitudAux=0;
            var longitudAux=0;
            
            if(Ext.getCmp('field_latitud').getValue()==''){
                
            navigator.geolocation.getCurrentPosition(
                    function(positionAux){
                       
                        latitudAux = positionAux.coords.latitude;
                        
                        longitudAux = positionAux.coords.longitude;
                        
                        var location= Ext.getCmp('location');

                        Ext.getCmp('field_latitud').setValue(latitudAux);
                        Ext.getCmp('field_longitud').setValue(longitudAux);
                        
                        var position = new google.maps.LatLng(latitudAux, longitudAux);

                        var geocoder = new google.maps.Geocoder();
                        var mapAux = location.map;
                        geocoder.geocode({'latLng': position}, function(results, status) {
                            if (status == google.maps.GeocoderStatus.OK) {

                                mapAux.setCenter(results[0].geometry.location);

                                var marker = new google.maps.Marker({
                                    map: location.map,
                                    position: results[0].geometry.location,
                                    title: 'My Location',
                                });
                                
                                Ext.Msg.alert('Info', 'Localizacion<br/>'+results[0].formatted_address);

                            } else {
                                Ext.Msg.alert('Error', 'No es posible obtener su localizacion.');
                            }
                            
                        });
                    }
                    
            );
            
            }

            var geo = new Ext.util.GeoLocation({
              autoUpdate: true,
              provider: navigator.geolocation,
              timeout: 10000,
              // 10 second timeout
              listeners: {
                locationupdate: function(geo) {
                  // Use our coordinates.
                  var easyparams = {
                    "min_upload_date": dt.format("Y-m-d H:i:s"),
                    "lat":  Ext.getCmp('field_latitud').getValue(),
                    "lon":  Ext.getCmp('field_longitud').getValue(),
                    "accuracy": 16,
                    "radius": 10,
                    "radius_units": "km"
                  };

                  var store = Ext.getCmp('searchphotos').down('list').getStore();
                  store.getProxy().extraParams = Ext.apply(store.getProxy().extraParams, easyparams);
                  
                  store.on('load', function () {
                      var count = store.getCount();
                      console.log(count);
                    });
                  
                  store.load();
                },
                locationerror: function(geo, bTimeout, bPermissionDenied, bLocationUnavailable, message) {
                  Ext.Msg.alert('Error','Error al obtener la localizacion');
                  var store = Ext.getCmp('searchphotos').down('list').getStore();
                  store.getProxy().extraParams = Ext.apply(store.getProxy().extraParams, easyparams);
                  store.load();

                }
              }
            });
            geo.provider=navigator.geolocation;
            geo.updateLocation();
           },
          itemtap: function(list, item) {
            //We're given just the item number, not the actual record. Have to get that first.
            var photo = list.getStore().getAt(item);

            Ext.dispatch({
              controller: 'searchphotos',
              action: 'showDetails',
              args: [photo]
            });
          }
        }
      },
        {
        xtype: 'photodetails'
      }]
    });

    FlickrFindr.view.SearchPhotos.superclass.initComponent.apply(this, arguments);
  }
});

Ext.reg('searchphotos', FlickrFindr.view.SearchPhotos);