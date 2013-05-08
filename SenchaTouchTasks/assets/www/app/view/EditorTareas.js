Ext.define("TareasApp.view.EditorTareas", {
    extend: "Ext.form.Panel",
    requires: "Ext.form.FieldSet",
    alias: "widget.EditorTareas",
    config: {
        scrollable: 'vertical'
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

        var guardarTarea = {
            xtype: "button",
            ui: "confirm",
            text: "Actualizar",
			iconMask: true,
            handler: this.onGuardarTarea,
            scope: this
        };

        var topToolbar = {
            xtype: "toolbar",
            docked: "top",
            title: "Editar Tarea",
            items: [
                volver,
                { xtype: "spacer" }
            ]
        };

        var borrarTarea = {
            xtype: "button",
            iconCls: "trash",
			ui:"decline",
			text:"Borrar tarea",
            iconMask: true,
            handler: this.onBorrarTarea,
            scope: this
        };

        var bottomToolbar = {
            xtype: "toolbar",
            docked: "bottom",
            items: [
                borrarTarea,
				{ xtype: "spacer" },
                guardarTarea
            ]
        };

        var tituloTarea= {
            xtype: 'textfield',
            name: 'titulo',
            label: 'T&iacute;tulo',
			placeHolder: 'Titulo de la tarea',
            required: true
        };
		
		 var descripcionTarea = {
            xtype: 'textareafield',
            name: 'descripcion',
            label: 'Descripci&oacute;n',
			maxLength: 80,
			placeHolder: 'Descripcion de la tarea',
        };
		
		var fechaTarea = {
            xtype: 'textfield',
            name: 'fecha',
            label: 'Fecha Creaci&oacute;n',
			disabled:true
        };
		
		var fechaLimite = {
            xtype: 'datepickerfield',
            name: 'fechaLimite',
            label: 'Fecha L&iacute;mite',
			required: true,
			picker : {
                        yearFrom : 2013,
                        yearTo   : 2020
                    }
        };
		
		var prioridad = {
            xtype: 'spinnerfield',
            name: 'prioridad',
            label: 'Prioridad',
			minValue: 1,
			maxValue: 5,
			cycle: true,
        };
		
		var completada = {
            xtype: 'togglefield',
            name: 'completada',
            label: 'Completada?',
        };
		
        this.add([
            topToolbar,
            { xtype: "fieldset",
                items: [fechaTarea,tituloTarea,descripcionTarea,fechaLimite,prioridad,completada],
				html:'* Campos obligatorios'
            },
            bottomToolbar
        ]);
    },
    onGuardarTarea: function () {
        console.log("guardar Tarea");
        this.fireEvent("guardarTarea", this);
    },
    onBorrarTarea: function () {
        console.log("borrar tarea");
        this.fireEvent("borrarTarea", this);
    },
    onVolver: function () {
        console.log("volver");
        this.fireEvent("volver", this);
    }

});

