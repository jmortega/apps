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
            scope: this
        };
		
		var tareasPendientes = {
			id:"tareasPendientes",
            xtype: "button",
            text: 'Tareas pendientes',
			badgeText: countPendientes,
            ui: 'action',
			iconCls: 'bookmarks',
			iconMask: true,
			iconAlign: 'top',
            handler: this.onTareasPendientes,
            scope: this
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
            scope: this
        };
		
		var searchField = {
            xtype: "searchfield",
			id:"task_search",
			itemId:'task_search',  
            placeHolder: 'Busqueda de tareas',
            scope: this
        };
		
		var info = {
            xtype: "button",
            text: 'Info',
            ui: 'forward',
            handler: this.onAppInfo,
            scope: this
        };
        
        var totalTareas = {
            xtype: "button",
            id:"totalTareas",
            text: '',
            ui: 'forward',
            scope: this
        };
		
        var topToolbar = {
            xtype: "toolbar",
            docked: "top",
            items: [
				
                nuevaTarea,
                searchField
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
                disclose: { fn: this.onEditarTarea, scope: this }
            }
        };

        this.add([topToolbarTitulo,topToolbar,listaTareas,bottomToolbar]);
		
	
    },
    onNuevaTarea: function () {
        console.log("nueva tarea");
        this.fireEvent("nuevaTarea", this);
    },
    onEditarTarea: function (list, record, target, index, evt, options) {
        console.log("editar tarea");
        this.fireEvent('editarTareaAux', this, record);
    },
	onTareasPendientes: function () {
        console.log("tareas pendientes");
        this.fireEvent("tareasPendientes", this);
    },
	onTareasCompletadas: function () {
        console.log("tareas completadas");
        this.fireEvent("tareasCompletadas", this);
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