Ext.define("TareasApp.view.ContenedorListaTareasCompletadas", {
    extend: "Ext.Container",
    alias: "widget.ContenedorListaTareasCompletadas",

    initialize: function () {

        this.callParent(arguments);

		var listaTareas = Ext.getStore("Tareas");
		listaTareas.clearFilter();
		
		listaTareas.filter([{ property: 'completada', value: 'false'}]);
		var countPendientes = listaTareas.getCount();
	
		listaTareas.clearFilter();
		
        var nuevaTarea = {
            xtype: "button",
            text: 'Nueva tarea',
            ui: 'action',
            handler: this.onNuevaTarea,
            scope: this
        };
		
		var tareasPendientes = {
			id:"tareasPendientes2",
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
		
		var tareasTodas = {
            xtype: "button",
            text: 'Todas las tareas',
            ui: 'action',
			iconCls: 'reply',
			iconMask: true,
			iconAlign: 'top',
            handler: this.onTareasTodas,
            scope: this
        };

        var topToolbarTitulo = {
            xtype: "toolbar",
            title: 'Tareas completadas',
            docked: "top",
            items: [
            ]
        };
        
        var topToolbar = {
            xtype: "toolbar",
            docked: "top",
            items: [
                { xtype: 'spacer' },
                nuevaTarea
            ]
        };
		
		var bottomToolbar = {
            xtype: "toolbar",
            docked: "bottom",
            items: [tareasTodas,
				 { xtype: 'spacer' },
				 tareasPendientes
            ]
        };
		
        var listaTareas = {
            xtype: "ListaTareasCompletadas",
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
	onTareasTodas: function () {
        console.log("tareas completadas");
        this.fireEvent("tareasTodas", this);
    },
    config: {
        layout: {
            type: 'fit'
        }
    }
});