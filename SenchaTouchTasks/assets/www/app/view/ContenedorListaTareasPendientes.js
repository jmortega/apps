Ext.define("TareasApp.view.ContenedorListaTareasPendientes", {
    extend: "Ext.Container",
    alias: "widget.ContenedorListaTareasPendientes",

    initialize: function () {

        this.callParent(arguments);
	
		var listaTareas = Ext.getStore("Tareas");
		listaTareas.clearFilter();
		
		listaTareas.filter([{ property: 'completada', value: 'true'}]);
		var countCompletadas= listaTareas.getCount();
	
		listaTareas.clearFilter();
		
        var nuevaTarea = {
            xtype: "button",
            text: 'Nueva tarea',
            ui: 'action',
            handler: this.onNuevaTarea,
            scope: this
        };
		
		var tareasCompletadas = {
			id:"tareasCompletadas2",
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
            title: 'Tareas pendientes',
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
				 tareasCompletadas
            ]
        };
		
        var listaTareas = {
            xtype: "ListaTareasPendientes",
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