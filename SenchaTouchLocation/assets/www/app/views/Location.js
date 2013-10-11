FlickrFindr.view.Location = Ext.extend(Ext.Map, {
  id: 'location',
  itemId:'location',
  layout: 'card',
  fullscreen: true,
  iconMask: true,
  iconCls: 'maps',
  title: 'Localización', // Name that appears on this tab
  defaults:{
      styleHtmlContent: true
  },
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
              position: location.center,
              title : 'My location',
              map: location
          });


          google.maps.event.addListener(marker, 'click', function() {

              new google.maps.InfoWindow({
              content: 'My location'
              }).open(location, marker);
          });

      }
  },

  initComponent: function() {
    Ext.apply(this, {
      dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        title: 'Location',
        items: [
{
    xtype: 'spacer'
  },
  {
      text: 'Check Connection',
      iconCls: 'settings',
      title: 'Check Connection',
      iconMask: true,
      style: 'height:25px;font-size:30px;text-align: center;',
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
               ]
      }],
    });

    
    navigator.geolocation.getCurrentPosition(
            function(positionAux){
                var latitudAux = positionAux.coords.latitude;
                var longitudAux = positionAux.coords.longitude;
                
                var location= Ext.getCmp('location');
                
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
                      
                        Ext.Msg.alert('Info', 'Localizacion<br/>'+results[0].formatted_address);
                        
                        var marker = new google.maps.Marker({
                            map: location.map,
                            position: results[0].geometry.location,
                            title: 'My Location',
                        });

                    } else {
                        Ext.Msg.alert('Error', 'No es posible obtener su localizacion.');
                    }
                    
                });
            }
            
    );
    
    
    FlickrFindr.view.Location.superclass.initComponent.apply(this, arguments);
  }
});

Ext.reg('location', FlickrFindr.view.Location);