Ext.define("TareasApp.view.AppInfo", {
    extend: "Ext.form.Panel",
    requires: "Ext.form.FieldSet",
    alias: "widget.AppInfo",
    config: {
        scrollable: 'vertical',
		id: 'formularioInfo',
        modal: true
    },
    initialize: function () {

        this.callParent(arguments);

        var volver = {
            xtype: "button",
            ui: "back",
            text: "Home",
			iconMask: true,
            handler: this.onVolver,
            scope: this
        };

        var topToolbar = {
            xtype: "toolbar",
            docked: "top",
            title: "Info",
            items: [
                volver,
                { xtype: "spacer" },
            ]
        };

        var autor= {
            xtype: 'textareafield',
            name: 'autor',
            label: 'Autor',
			placeHolder: 'Jose Manuel Ortega Candel.Comentarios a jmoc25@gmail.com',
			readOnly:true,
        };
		
		 var proyecto = {
            xtype: 'textareafield',
            name: 'proyecto',
            label: 'Proyecto',
			maxLength: 80,
			placeHolder: 'SenchaTouch Tasks',
			readOnly:true,
        };
		
		var descripcion = {
            xtype: 'textareafield',
            name: 'descripcion',
            label: 'Descripcion',
			maxLength: 80,
			placeHolder: 'Aplicacion para gestionar una lista de tareas,asi como ver el estado de las mismas y asignarle una prioridad dentro de la lista',
			readOnly:true,
        };
		
        this.add([
            topToolbar,
            { xtype: "fieldset",
                items: [proyecto,descripcion,autor]
            }
        ]);
    },
    onVolver: function () {
        console.log("volver");
        this.fireEvent("volver", this);
    }

});
