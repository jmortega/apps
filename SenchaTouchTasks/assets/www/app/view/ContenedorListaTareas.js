Ext.define("TareasApp.view.ContenedorListaTareas", {
    extend: "Ext.Container",
    alias: "widget.ContenedorListaTareas",

    initialize: function () {

        this.callParent(arguments);

        var countTotal=Ext.getStore("Tareas").getCount();
		
		var listaTareas = Ext.getStore("Tareas");
		listaTareas.clearFilter();

		listaTareas.filter([{ property: 'completada', value: 'false'}]);
		var countPendientes = listaTareas.getCount();
	
		listaTareas.clearFilter();
		
		listaTareas.filter([{ property: 'completada', value: 'true'}]);
		var countCompletadas = listaTareas.getCount();
	
		listaTareas.clearFilter();
		
        var nuevaTarea = {
            xtype: "button",
            text: 'Nueva tarea',
            ui: 'action',
            handler: this.onNuevaTarea,
            scope: this,
            iconMask: true,
            style: 'width:140px;height:30px;font-size:25px;'
        };
		
		var tareasPendientes = {
			id:"tareasPendientes",
            xtype: "button",
            text: 'Tareas pendientes',
			badgeText: countPendientes,
			badgeCls  : 'x-badge-aux2',
            ui: 'action',
			iconCls: 'bookmarks',
			iconMask: true,
			iconAlign: 'top',
            handler: this.onTareasPendientes,
            scope: this,
            style: 'width:200px;height:60px;font-size:20px;'
        };
		
		var tareasCompletadas = {
			id:"tareasCompletadas",
            xtype: "button",
            text: 'Tareas completadas',
            badgeText: countCompletadas,
            badgeCls  : 'x-badge-aux',
            ui: 'action',
			iconCls: 'reply',
			iconMask: true,
			iconAlign: 'top',
            handler: this.onTareasCompletadas,
            scope: this,
            style: 'width:200px;height:60px;font-size:20px;'
        };
		
		var searchField = {
            xtype: "searchfield",
			id:"task_search",
			itemId:'task_search',  
            placeHolder: 'Busqueda de tareas',
            scope: this,
            style: 'width:60%;height:40px;font-size:25px;'
        };
		
		var info = {
            xtype: "button",
            text: 'Info',
            ui: 'forward',
            handler: this.onAppInfo,
            scope: this,
            style: 'width:100px;height:30px;font-size:25px;'
        };
        
        var totalTareas = {
            xtype: "button",
            id:"totalTareas",
            text: '',
            ui: 'forward',
            scope: this,
            style: 'width:100px;height:30px;font-size:25px;'
        };
		
        var topToolbar = {
            xtype: "toolbar",
            docked: "top",
            items: [
				
                nuevaTarea,
                searchField,
                {
                    ui: 'action',
                    iconCls: 'refresh',
                    iconMask: true,
                    handler: function(event, btn) {
                        Ext.getStore("Tareas").load();
                    }           
                },
            ]
        };
        
        var topToolbarTitulo = {
            xtype: "toolbar",
            title: 'Mis tareas',
            docked: "top",
            items: [info,
            { xtype: 'spacer' },
            totalTareas
            ]
        };
		
		var bottomToolbar = {
            xtype: "toolbar",
            docked: "bottom",
            items: [tareasPendientes,
            	{ xtype: 'spacer' },
                tareasCompletadas
            ]
        };
		
        var listaTareas = {
            xtype: "ListaTareas",
            store: Ext.getStore("Tareas"),
            listeners: {
                itemtap: { fn: this.onEditarTareaItemTap, scope: this },
                disclose: { fn: this.onEditarTareaItemDisclosure, scope: this }
            }
        };

        this.add([topToolbarTitulo,topToolbar,listaTareas,bottomToolbar]);
		
	
    },
    onNuevaTarea: function () {
        console.log("nueva tarea");
        this.fireEvent("nuevaTarea", this);
    },
    onEditarTareaItemTap: function (list, index, target, record, evt, options) {
        console.log("editar tarea");
        this.fireEvent('editarTareaAux', this, record);
    },
    onEditarTareaItemDisclosure: function (list, record, target, index, evt, options) {
        console.log("editar tarea");
        this.fireEvent('editarTareaAux', this, record);
    },
	onTareasPendientes: function () {
        console.log("tareas pendientes");
        this.fireEvent("tareasPendientes", this);
        Ext.getStore("Tareas").load();
    },
	onTareasCompletadas: function () {
        console.log("tareas completadas");
        this.fireEvent("tareasCompletadas", this);
        Ext.getStore("Tareas").load();
    },
	onAppInfo: function () {
        console.log("info");
        this.fireEvent("AppInfo", this);
    },
    config: {
        layout: {
            type: 'fit'
        }
    }
 
	 
	
});