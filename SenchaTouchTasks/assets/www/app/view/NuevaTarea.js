Ext.define("TareasApp.view.NuevaTarea", {
    extend: "Ext.form.Panel",
    requires: "Ext.form.FieldSet",
    alias: "widget.NuevaTarea",
    config: {
        scrollable: 'vertical',
		id: 'formulario'
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
            text: "Guardar",
			iconMask: true,
            handler: this.onGuardarTarea,
            scope: this
        };

        var topToolbar = {
            xtype: "toolbar",
            docked: "top",
            title: "Nueva Tarea",
            items: [
                volver,
                { xtype: "spacer" },
                
				
            ]
        };

		var limpiarCampos= {
            xtype: "button",
            iconCls: "trash",
			ui:"decline",
			text:"Limpiar campos",
            iconMask: true,
            handler: function(){
				Ext.getCmp('formulario').reset();

			},	
            scope: this
        };
		
        var bottomToolbar = {
            xtype: "toolbar",
            docked: "bottom",
            items: [
				{ xtype: "spacer" },
				guardarTarea
            ]
        };

        var tituloTarea= {
            xtype: 'textfield',
            name: 'titulo',
            label: 'T&iacute;tulo',
			placeHolder: 'Titulo de la tarea',
			autoCapitalize: false,
            required: true,
			useClearIcon: true
        };
		
		 var descripcionTarea = {
            xtype: 'textareafield',
            name: 'descripcion',
            label: 'Descripci&oacute;n',
			maxLength: 80,
			placeHolder: 'Descripcion de la tarea',
			useClearIcon: true
        };
		
		var fechaTarea = {
            xtype: 'textfield',
            name: 'fecha',
            label: 'Fecha Creaci&oacute;n',
			disabled:true,
			readOnly:true,
			useClearIcon: true
        };
		
		var fechaLimite = {
            xtype: 'datepickerfield',
            name: 'fechaLimite',
            label: 'Fecha L&iacute;mite',
			required: true,
			useClearIcon: true,
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
			useClearIcon: true
        };
		
        this.add([
            topToolbar,
            { xtype: "fieldset",
                items: [fechaTarea,tituloTarea,descripcionTarea,fechaLimite,prioridad],
				instructions:'* Campos obligatorios'
            },
            bottomToolbar
        ]);
    },
    onGuardarTarea: function () {
        console.log("guardar Tarea");
        this.fireEvent("guardarTarea", this);
    },
    onVolver: function () {
        console.log("volver");
        this.fireEvent("volver", this);
    }

});

