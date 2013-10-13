(function() {
    
  var DetailsUniversityView, Controller, HomeView, ShowUniversityView, University, UniversityCollection, app;
  
  var __extends = function(child, parent) {
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    if (typeof parent.extended === "function") parent.extended(child);
    child.__super__ = parent.prototype;
  };
  
  app = {
    activePage: function() {
      return $(".ui-page-active");
    },
    reapplyStyles: function(el) {
      el.find('ul[data-role]').listview();
      el.find('div[data-role="fieldcontain"]').fieldcontain();
      el.find('button[data-role="button"]').button();
      el.find('input,textarea').textinput();
      return el.page();
    },
    redirectTo: function(page) {
      return $.mobile.changePage(page);
    },
    goBack: function() {
      return $.historyBack();
    }
  };
  
  
  //Modelo University
  University = (function() {
    function University() {
      return Backbone.Model.apply(this, arguments);
    }
    return University;
  })();
  
  __extends(University, Backbone.Model);
  University.prototype.getName = function() {
    return this.get('name');
  };
  University.prototype.getGrado = function() {
      return this.get('grado');
    };
  University.prototype.getAddress = function() {
      
    var position = new google.maps.LatLng(this.get('geolat'),this.get('geolong'));
    
    var addressAux=this.get('address');
    
    if(navigator.onLine){
        
    var geocoder = new google.maps.Geocoder();
    
    
    geocoder.geocode({'latLng': position}, function(results, status) {
      if (status == google.maps.GeocoderStatus.OK) {
         
         var geostr = "<h3>Localización</h3><p>"+results[0].formatted_address+"<br/>"+
         results[1].formatted_address+"</p>";

        if (results[1]) {
            
            navigator.notification.alert('Localizacion',null,results[1].formatted_address,'Aceptar');
            
            console.log(results[1].formatted_address);
            
            return addressAux=results[1].formatted_address;
            
        } else {

        }
      } else {

      }
    });
    
    }
    
    return addressAux;

  };
  University.prototype.getCity = function() {
      return  this.get('city');
  };
  University.prototype.getState = function() {
      return  this.get('state');
  };
  University.prototype.getImageUrl = function() {
    return this.get('photo_url');
  };
  University.prototype.getLatitude = function() {
    return this.get('geolat');
  };
  University.prototype.getLongitude = function() {
    return this.get('geolong');
  };
  University.prototype.getMapUrl = function(width, height) {
    width || (width = 300);
    height || (height = 220);
    if(navigator.onLine){
        return "http://maps.google.com/maps/api/staticmap?center=" + (this.getLatitude()) + "," + (this.getLongitude()) + "&zoom=14&size=" + width + "x" + height + "&maptype=terrain&markers=color:red|" + (this.getLatitude()) + "," + (this.getLongitude()) + "&sensor=false";
    }else{
        return "alert.png";
    }
  };
  
  //Listado Universidades JSON
  UniversityCollection = (function() {
    function UniversityCollection() {
      UniversityCollection.__super__.constructor.apply(this, arguments);
      this.refresh($UNIVERSITY_JSON);
      return this;
    }
    return UniversityCollection;
  })();
  __extends(UniversityCollection, Backbone.Collection);
  
  //Asociar el modelo a la coleccion
  UniversityCollection.prototype.model = University;
  this.Universitys = new UniversityCollection;
  
  
  //Detalles
  DetailsUniversityView = (function() {
    function DetailsUniversityView() {
      var _this;
      _this = this;
      this.render = function() { return DetailsUniversityView.prototype.render.apply(_this, arguments); };
      DetailsUniversityView.__super__.constructor.apply(this, arguments);
      this.el = app.activePage();
      this.template = _.template('<div class="ui-bar-c ui-corner-all ui-shadow" id="map_item" class="detalles"><form action="#University-<%= University.cid %>-update" method="post">\n\n  <div data-role="fieldcontain">\n  <p>\n    <img style="width: 50%;height: 30%;align:center" src="<%= University.get(\'imagen\') %>" />\n  </p>\n  <label>Universidad</label>\n    <input class="detalles" type="text" value="<%= University.getName() %>" name="name" />\n  </div>\n  \n  <div data-role="fieldcontain">\n    <label>Estudios</label>\n    <textarea rows="20" cols="80"  name="grado" class="detalles" ><%= University.getGrado() %> </textarea> <br/><br/>  <label>Localizacion</label>\n    <textarea rows="20" cols="80"  name="address" ><%= University.get(\'address\') %></textarea>\n  </div>\n  \n  <div data-role="fieldcontain">\n    <label>Ciudad</label>\n    <input type="text" value="<%= University.get(\'city\') %>" name="city" />\n  </div>\n  \n  <div data-role="fieldcontain">\n    <label>Provincia</label>\n    <input type="text" value="<%= University.get(\'state\') %>" name="state" />\n  </div>\n  \n  URL <input type="text" value="<%= University.get(\'url\') %>">\n</form></div>');
      this.model.bind('change', this.render);
      this.render();
      return this;
    }
    return DetailsUniversityView;
  })();
  __extends(DetailsUniversityView, Backbone.View);
  DetailsUniversityView.prototype.events = {
    "submit form": "onSubmit"
  };
  DetailsUniversityView.prototype.onSubmit = function(e) {
    this.model.set({
      name: this.$("input[name='name']").val(),
      address: this.$("input[name='address']").val(),
      city: this.$("input[name='city']").val(),
      imagen: this.$("input[name='imagen']").val(),
      url: this.$("input[name='url']").val(),
      state: this.$("input[name='state']").val()
    });
    this.model.trigger('change');
    app.goBack();
    e.preventDefault();
    return e.stopPropagation();
  };
  DetailsUniversityView.prototype.render = function() {
    this.el.find('h1').text("" + (this.model.getName()));
    this.el.find('.ui-content').html(this.template({
      University: this.model
    }));
    app.reapplyStyles(this.el);
    return this.delegateEvents();
  };
  
  
  //Mostrar
  ShowUniversityView = (function() {
    function ShowUniversityView() {
      var _this;
      _this = this;
      this.render = function() { return ShowUniversityView.prototype.render.apply(_this, arguments); };
      ShowUniversityView.__super__.constructor.apply(this, arguments);
      this.el = app.activePage();
      this.template = _.template('<div class="ui-bar-c ui-corner-all ui-shadow" id="map_item22"><div class="detalles">\n  \n  <p>\n   <img style="width: 100%" src="<%= University.getMapUrl() %>" />\n  </p>\n  \n   <b><address>\n    <%= University.getAddress() %>\n  </address>\n\n</b>  <ul data-role="listview" data-inset="true">\n    <li data-role="list-divider">Actions</li>\n <li><a href="#Universitys-<%= University.cid %>-Details">Detalles</a></li>\n  </ul>\n</div></div>');
      this.model.bind('change', this.render);
      this.render();
      return this;
    }
    return ShowUniversityView;
  })();
  __extends(ShowUniversityView, Backbone.View);
  ShowUniversityView.prototype.render = function() {
    this.el.find('h1').text(this.model.getName());
    this.el.find('.ui-content').html(this.template({
      University: this.model
    }));
    return app.reapplyStyles(this.el);
  };
  HomeView = (function() {
    function HomeView() {
      var _this;
      _this = this;
      this.render = function() { return HomeView.prototype.render.apply(_this, arguments); };
      HomeView.__super__.constructor.apply(this, arguments);
      this.el = app.activePage();
      this.template = _.template('<div class="ui-bar-c ui-corner-all ui-shadow" id="map_item"><div id="map_canvas_2"></div><div class="detalles">\n\n<ul data-role="listview" data-theme="c" data-filter="true">\n  <% Universitys.each(function(University){ %>\n    <li><img style="width:150%;height:150%" src="<%= University.get(\'imagen\') %>" /> <a href="#Universitys-<%= University.cid %>"><%= University.getName() %></a></li>\n  <% }); %>\n</ul>\n\n</div></div');
      this.render();
      return this;
    }
    return HomeView;
  })();
  __extends(HomeView, Backbone.View);
  HomeView.prototype.render = function() {
    this.el.find('.ui-content').html(this.template({
      Universitys: Universitys
    }));
    return app.reapplyStyles(this.el);
  };
  
  //Controlador
  Controller = (function() {
    function Controller() {
      Controller.__super__.constructor.apply(this, arguments);
      this._views = {};
      return this;
    }
    return Controller;
  })();
  
  __extends(Controller, Backbone.Controller);
  
  Controller.prototype.routes = {
    "Universitys-:cid-Details": "Details",
    "Universitys-:cid": "show",
    "home": "home"
  };
  Controller.prototype.home = function() {
    var _base;
    return (_base = this._views)['home'] || (_base['home'] = new HomeView);
  };
  
  Controller.prototype.show = function(cid) {
    var _base, _name;
    return (_base = this._views)[_name = "Universitys-" + cid] || (_base[_name] = new ShowUniversityView({
      model: Universitys.getByCid(cid)
    }));
  };
  
  Controller.prototype.Details = function(cid) {
    var _base, _name;
    return (_base = this._views)[_name = "Universitys-" + cid + "-Details"] || (_base[_name] = new DetailsUniversityView({
      model: Universitys.getByCid(cid)
    }));
  };
  
  app.Controller = new Controller();
  $(document).ready(function() {
      
   $('#exit_btn').click(function() {
          navigator.app.exitApp();
   });
      
   var networkstate = navigator.connection.type;
   if(networkstate == "none"){
       $(".offline").css("visibility","visible");
              
   }
   
   
   $('#botonConnection').click(function() {
      
       var networkState = navigator.connection.type;
       var states = {};
       states[Connection.UNKNOWN]  = 'Unknown connection';
       states[Connection.ETHERNET] = 'Ethernet connection';
       states[Connection.WIFI]     = 'WiFi connection';
       states[Connection.CELL_2G]  = 'Cell 2G connection';
       states[Connection.CELL_3G]  = 'Cell 3G connection';
       states[Connection.CELL_4G]  = 'Cell 4G connection';
       states[Connection.NONE]     = 'No network connection';

       navigator.notification.alert('',null,'Connection type: ' + states[networkState],'Aceptar');

   });
   
   
    Backbone.history.start();
    
    return app.Controller.home();
    
  });
  
  this.app = app;
  
}).call(this);
