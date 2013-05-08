Ext.define("TareasApp.controller.TareasControlador", {
    extend: "Ext.app.Controller",
    config: {
        refs: {
            ContenedorListaTareas: "ContenedorListaTareas",
			ContenedorListaTareasPendientes: "ContenedorListaTareasPendientes",
			ContenedorListaTareasCompletadas: "ContenedorListaTareasCompletadas",
			NuevaTarea:"NuevaTarea",
            EditorTareas: "EditorTareas",
			AppInfo:"AppInfo"
        },
        control: {
            ContenedorListaTareas: {
                nuevaTarea: "nuevaTarea",
                editarTareaAux: "editarTareaAux",
				tareasPendientes: "onTareasPendientes",
				tareasCompletadas: "onTareasCompletadas",
				AppInfo:"AppInfo"
            },
			ContenedorListaTareasPendientes: {
                nuevaTarea: "nuevaTarea",
                editarTareaAux: "editarTareaAux",
				tareasPendientes: "onTareasPendientes",
				tareasCompletadas: "onTareasCompletadas",
				tareasTodas: "tareasTodas"
            },
			ContenedorListaTareasCompletadas: {
                nuevaTarea: "nuevaTarea",
                editarTareaAux: "editarTareaAux",
				tareasPendientes: "onTareasPendientes",
				tareasCompletadas: "onTareasCompletadas",
				tareasTodas: "tareasTodas"
            },
			NuevaTarea: {
                guardarTarea: "guardarTarea",
                volver: "volver"
            },
            EditorTareas: {
                guardarTarea: "actualizarTarea",
                borrarTarea: "borrarTarea",
                volver: "volver"
            },
			AppInfo: {
                volver: "volver"
            }
        }
    },
    // Transiciones
    slideLeftTransition: { type: 'slide', direction: 'left' },
    slideRightTransition: { type: 'slide', direction: 'right' },
	
    // Genenerar numero aletarorio para el id de la tarea
    getRandomInt: function (min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    },
    // Funciones CallBacks
    editarTarea: function (record) {

        var editorTareas = this.getEditorTareas();
        editorTareas.setRecord(record);
        Ext.Viewport.animateActiveItem(editorTareas, this.slideLeftTransition);
    },
    tareasTodas: function () {
	
		var listaTareas = Ext.getStore("Tareas");
		
		listaTareas.clearFilter();

		listaTareas.filter([{ property: 'completada', value: 'false'}]);
		var countPendientes = listaTareas.getCount();

		Ext.getCmp("tareasPendientes").setBadgeText(countPendientes);
		Ext.getCmp("tareasPendientes2").setBadgeText(countPendientes);
		
		listaTareas.clearFilter();
		
		listaTareas.filter([{ property: 'completada', value: 'true'}]);
		var countCompletadas = listaTareas.getCount();

		Ext.getCmp("tareasCompletadas").setBadgeText(countCompletadas);
		Ext.getCmp("tareasCompletadas2").setBadgeText(countCompletadas);
		
		listaTareas.clearFilter();

		Ext.getCmp("totalTareas").setText(Ext.getStore("Tareas").getCount());
		 
		var value = Ext.getCmp("task_search").getValue();

		if(value){
			
				var thisRegEx = new RegExp(value, "i");
				
				listaTareas.filterBy(function(record) {

				if (thisRegEx.test(record.get('titulo'))) {
					return true;
				};
					return false;
				});
		}else{
			Ext.getCmp("task_search").setValue(" ");
			listaTareas.filterBy(function(record) {

			return true;
			
			});
			Ext.getCmp("task_search").setValue("");

		}	
		
  		
        Ext.Viewport.animateActiveItem(this.getContenedorListaTareas(), this.slideRightTransition);

    },
	tareasPendientes: function () {
	
		var listaTareas = Ext.getStore("Tareas");
		listaTareas.clearFilter();

		listaTareas.filter([{ property: 'completada', value: 'false'}]);
		var countPendientes = listaTareas.getCount();

		Ext.getCmp("tareasPendientes").setBadgeText(countPendientes);
		Ext.getCmp("tareasPendientes2").setBadgeText(countPendientes);
		
  		Ext.getCmp("totalTareas").setText(Ext.getStore("Tareas").getCount()); 
  		
        Ext.Viewport.animateActiveItem(this.getContenedorListaTareasPendientes(), this.slideLeftTransition);
    },
	tareasCompletadas: function () {
		
  		Ext.getCmp("totalTareas").setText(Ext.getStore("Tareas").getCount()); 
  			
        Ext.Viewport.animateActiveItem(this.getContenedorListaTareasCompletadas(), this.slideLeftTransition);
    },
	AppInfo: function () {
        Ext.Viewport.animateActiveItem(this.getAppInfo(), this.slideLeftTransition);
    },
    nuevaTarea: function () {

        console.log("nueva tarea");

        var fechaTarea = new Date();
        var IdTarea = (fechaTarea.getTime()).toString() + (this.getRandomInt(0, 100)).toString();

        var nuevaTarea = Ext.create("TareasApp.model.Tarea", {
            id: IdTarea,
            fecha: fechaTarea,
            titulo: "",
			descripcion: "",
			fechaLimite:null,
			prioridad:1,
			completada:false
        });

        this.nuevaTareaAux(nuevaTarea); 
     
    },
	nuevaTareaAux: function (record) {

        var nuevaTarea = this.getNuevaTarea();
        
        if(nuevaTarea.getValues().fechaLimite !=null){

        	record.set("fechaLimite", new Date());
        }
        
        nuevaTarea.setRecord(record);
        
        Ext.Viewport.animateActiveItem(nuevaTarea, this.slideLeftTransition);
    },
    editarTareaAux: function (list, record) {

        console.log("editarTarea");

        this.editarTarea(record);
    },
	guardarTarea: function () {

        console.log("guardar tarea");

        var nuevaTarea = this.getNuevaTarea();

        var tarea = nuevaTarea.getRecord();
        var valores = nuevaTarea.getValues();

        // Actualizar tarea con los nuevos valores
        tarea.set("titulo", valores.titulo);
		tarea.set("descripcion", valores.descripcion);
		tarea.set("fechaLimite", valores.fechaLimite);
		tarea.set("prioridad", valores.prioridad);
		
		//inicialmente se guarda como no completada
		tarea.set("completada", false);
		
        var errors = tarea.validate();

        if (!errors.isValid()) {
			
			if(valores.titulo==null || valores.titulo==''){
			
				Ext.Msg.alert("Compruebe campo t&iacute;tulo",errors.getByField("titulo")[0].getMessage(),Ext.emptyFn);
			}
			else{
				Ext.Msg.alert("Compruebe campo fecha L&iacute;mite",errors.getByField("fechaLimite")[0].getMessage(),Ext.emptyFn);
			}
            tarea.reject();
            return;
        }

        var listaTareas = Ext.getStore("Tareas");
		
        if (null == listaTareas.findRecord('id', tarea.data.id)) {
            listaTareas.add(tarea);
			Ext.Msg.alert('Se ha creado la tarea\n'+tarea.data.titulo);
        }

		var listaTareas = Ext.getStore("Tareas");
		
		listaTareas.filter([{ property: 'completada', value: 'false'}]);
		var countPendientes = listaTareas.getCount();
	
		Ext.getCmp("tareasPendientes").setBadgeText(countPendientes);
		Ext.getCmp("tareasPendientes2").setBadgeText(countPendientes);
		
		listaTareas.clearFilter();
		
		listaTareas.filter([{ property: 'completada', value: 'true'}]);
		var countCompletadas = listaTareas.getCount();
	
		Ext.getCmp("tareasCompletadas").setBadgeText(countCompletadas);
		Ext.getCmp("tareasCompletadas2").setBadgeText(countCompletadas);
		
		listaTareas.clearFilter();
		
        listaTareas.sort([{ property: 'prioridad', direction: 'ASC'}]);
      
  		Ext.getCmp("totalTareas").setText(Ext.getStore("Tareas").getCount()); 

  		this.tareasTodas();
    }, 
    actualizarTarea: function () {

        console.log("actualizar tarea");
		
		var actualizadaPendiente=false;
		var countPendientesAux;
		
        var editorTareas = this.getEditorTareas();

        var tarea = editorTareas.getRecord();
		
        var valores = editorTareas.getValues();

		if(tarea.data.completada==true && valores.completada==false){
			actualizadaPendiente=true;
		}
		
        // Actualizar tarea con los nuevos valores
        tarea.set("titulo", valores.titulo);
		tarea.set("descripcion", valores.descripcion);
		tarea.set("fechaLimite", valores.fechaLimite);
		tarea.set("prioridad", valores.prioridad);
		
		if(valores.completada==true){
			tarea.set("completada", true);
		}else{
			tarea.set("completada", false);
		}
		
        var errors = tarea.validate();

        if (!errors.isValid()) {
			
			if(valores.titulo==null || valores.titulo==''){
			
				Ext.Msg.alert("Compruebe campo t&iacute;tulo",errors.getByField("titulo")[0].getMessage(),Ext.emptyFn);
			}
			else{
				Ext.Msg.alert("Compruebe campo fecha L&iacute;mite",errors.getByField("fechaLimite")[0].getMessage(),Ext.emptyFn);
			}
            tarea.reject();
            return;
        }

        var listaTareas = Ext.getStore("Tareas");
		
		if(actualizadaPendiente){
		
			countPendientesAux = Ext.getCmp("tareasPendientes").getBadgeText()+1;
			
		}else{
		
			listaTareas.filter([{ property: 'completada', value: 'false'}]);
			countPendientesAux = listaTareas.getCount();
		}
		
		Ext.getCmp("tareasPendientes").setBadgeText(countPendientesAux);
		Ext.getCmp("tareasPendientes2").setBadgeText(countPendientesAux);
		
		Ext.Msg.alert('Se ha actualizado la tarea '+tarea.data.titulo);
		
		listaTareas.clearFilter();
		
		listaTareas.filter([{ property: 'completada', value: 'true'}]);
		var countCompletadas = listaTareas.getCount();
	
		Ext.getCmp("tareasCompletadas").setBadgeText(countCompletadas);
		Ext.getCmp("tareasCompletadas2").setBadgeText(countCompletadas);
		
		listaTareas.clearFilter();

		listaTareas.sort([{ property: 'prioridad', direction: 'ASC'}]);
		
		var value = Ext.getCmp("task_search").getValue();
			
		console.log(this,'Criterio busqueda: ' + value);
		
		if(value){
			
				var thisRegEx = new RegExp(value, "i");
				
				listaTareas.filterBy(function(record) {
				if (thisRegEx.test(record.get('titulo'))) {
					return true;
				};
					return false;
				});
		}else{
			
			Ext.getCmp("task_search").setValue(" ");
				listaTareas.filterBy(function(record) {
					return true;
			});
			Ext.getCmp("task_search").setValue("");

		}
		

  		Ext.getCmp("totalTareas").setText(Ext.getStore("Tareas").getCount());
  		
  		this.tareasTodas(); 			

    }, 
    borrarTarea: function () {

        console.log("borrar tarea");

        var editorTareas = this.getEditorTareas();
        var tarea = editorTareas.getRecord();
		
        var listaTareas = Ext.getStore("Tareas");
		
        Ext.Msg.confirm('Confirmacion','Confirma eliminar la tarea?',function(btn){  
            
           if(btn === 'yes'){  
                //si el usuario acepta  
                listaTareas.remove(tarea);
				Ext.Msg.alert('Se ha eliminado la tarea\n'+tarea.data.titulo);
            }
            
       		listaTareas.filter([{ property: 'completada', value: 'false'}]);
			var countPendientes = listaTareas.getCount();
	
			Ext.getCmp("tareasPendientes").setBadgeText(countPendientes);
			Ext.getCmp("tareasPendientes2").setBadgeText(countPendientes);
				
        	listaTareas.clearFilter();
        	
        	listaTareas.filter([{ property: 'completada', value: 'true'}]);
			var countCompletadas = listaTareas.getCount();
	
			Ext.getCmp("tareasCompletadas").setBadgeText(countCompletadas);
			Ext.getCmp("tareasCompletadas2").setBadgeText(countCompletadas);
		
			listaTareas.clearFilter();
				
  			Ext.getCmp("totalTareas").setText(listaTareas.getCount()); 

        }); 
        
       
        this.tareasTodas();  
        
    }, 
    volver: function () {

        console.log("volver");
		var listaTareas = Ext.getStore("Tareas");
		if(listaTareas.getFilters()!=null && listaTareas.getFilters()[0]!=null && listaTareas.getFilters()[0].getValue()=="true"){
			this.tareasCompletadas();
		}
		else if(listaTareas.getFilters()!=null && listaTareas.getFilters()[0]!=null && listaTareas.getFilters()[0].getValue()=="false"){
			this.tareasPendientes();
		}
		else{
			this.tareasTodas();
			
			var value = Ext.getCmp("task_search").getValue();
			
			console.log(this,'Criterio busqueda: ' + value);
		
			if(value){
			
				var thisRegEx = new RegExp(value, "i");
				
				listaTareas.filterBy(function(record) {
				if (thisRegEx.test(record.get('titulo'))) {
					return true;
				};
					return false;
				});
			}	
		}
		
    },
	onTareasPendientes: function () {
        console.log("onTareasPendientes");
		var listaTareas = Ext.getStore("Tareas");
		listaTareas.clearFilter();
        listaTareas.sort([{ property: 'prioridad', direction: 'ASC'}]);
		listaTareas.filter([{ property: 'completada', value: 'false'}]);
		
		var countPendientes = listaTareas.getCount();
		
		if(countPendientes==1){
			Ext.Msg.alert('Tiene '+countPendientes+' tarea pendiente');
		}else if(countPendientes>1){
			Ext.Msg.alert('Tiene '+countPendientes+' tareas pendientes');
		}else{
			Ext.Msg.alert('No tiene tareas pendientes');
		}
		
        this.tareasPendientes();
		
    },
	onTareasCompletadas: function () {

        console.log("onTareasCompletadas");
		var listaTareas = Ext.getStore("Tareas");
		listaTareas.clearFilter();

        listaTareas.sort([{ property: 'prioridad', direction: 'ASC'}]);
		listaTareas.filter([{ property: 'completada', value: 'true'}]);
		
		/*var countCompletadas = listaTareas.getCount();
		if(countCompletadas==1){
			Ext.Msg.alert('Tiene '+countCompletadas+' tarea completada');
		}
		else if(countCompletadas>1){
			Ext.Msg.alert('Tiene '+countCompletadas+' tareas completadas');
		}else{
			Ext.Msg.alert('No tiene tareas completadas');
		}*/
		
        this.tareasCompletadas();
		
    },
	onClearSearch: function() { 
        //call the clearFilter method on the store instance  
        var storeTareas = Ext.getStore("Tareas");
		storeTareas.clearFilter();
    },
	onSearchKeyUp: function(field) {  
        //get the store and the value of the field  
        var value = field.getValue();
		console.log(this,'Criterio busqueda: ' + value);
		
        var storeTareas = Ext.getStore("Tareas");
		storeTareas.clearFilter(); 
		
		if(value){
			var thisRegEx = new RegExp(value, "i");
			storeTareas.filterBy(function(record) {
			if (thisRegEx.test(record.get('titulo'))) {
				return true;
			};
				return false;
			});
		}		
    },
    // inicializacion y carga del store
    launch: function () {
        this.callParent(arguments);
        Ext.getStore("Tareas").load();
        console.log("launch");
  		Ext.getCmp("totalTareas").setText(Ext.getStore("Tareas").getCount());
    },
    init: function () {
        this.callParent(arguments);
        console.log("init");
		
		this.control({  
  
            '#task_search':{
                scope: this,  
                clearicontap: this.onClearSearch,  
                keyup: this.onSearchKeyUp  
            }
        }); 
        
    }
});