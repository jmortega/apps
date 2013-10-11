Ext.define("TareasApp.view.AppInfo", {
    extend: "Ext.form.Panel",
    requires: "Ext.form.FieldSet",
    alias: "widget.AppInfo",
    config: {
        scrollable: 'vertical',
		id: 'formularioInfo',
        modal: true,
        fullscreen: true,
        scrollable: 'vertical',
        defaults:{
            styleHtmlContent: true
        },
    },
    initialize: function () {

        this.callParent(arguments);

        var volver = {
            xtype: "button",
            ui: "back",
            text: "Home",
			iconMask: true,
            handler: this.onVolver,
            style: 'height:30px;font-size:25px;',
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
			placeHolder: 'Jose Manuel Ortega Candel.\n Comentarios a jmoc25@gmail.com',
			readOnly:true,
			clearIcon: false,
			maxRows: 4,
			styleHtmlContent: true,
			style: 'background: #eeeeee;height:50px;font-size:25px;'
        };
		
		 var proyecto = {
            xtype: 'textareafield',
            name: 'proyecto',
            label: 'Proyecto',
			maxLength: 80,
			placeHolder: 'SenchaTouch Tasks',
			readOnly:true,
			clearIcon: true,
			maxRows: 4,
			styleHtmlContent: true,
			style: 'background: #eeeeee;height:50px;font-size:25px;'
        };
		
		var descripcion = {
            xtype: 'textareafield',
            name: 'descripcion',
            label: 'Descripcion',
			maxLength: 120,
			maxRows: 4,
			placeHolder: 'Aplicacion para gestionar una lista de tareas,\n asi como ver el estado de las mismas y asignarle una prioridad dentro de la lista',
			readOnly:true,
			clearIcon: false,
			styleHtmlContent: true,
			style: 'width:100%;background: #eeeeee;height:50px;font-size:25px;'
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
        Ext.getStore("Tareas").load();
    }

});
