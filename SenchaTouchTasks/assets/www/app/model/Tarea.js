Ext.define("TareasApp.model.Tarea", {
    extend: "Ext.data.Model",
    config: {
        fields: [
            { name: 'id', type: 'int' },
            { name: 'fecha', type: 'date', dateFormat: 'c' },
            { name: 'titulo', type: 'string',defaultValue: '' },
			{ name: 'descripcion', type: 'string',defaultValue: '' },
			{ name: 'fechaLimite',type: 'date',defaultValue:null,dateFormat: 'dd/mm/YYYY'},
			{ name: 'prioridad',type: 'int',defaultValue: 1},
			{ name: 'completada',type: 'boolean', defaultValue: false}
        ],
		idProperty: 'id',
        validations: [
            { type: 'presence', field: 'id' },
            { type: 'presence', field: 'fecha' },
            { type: 'presence', field: 'titulo', message: 'Introduzca una t&iacute;tulo para la nueva tarea.' },
			{ type: 'presence', field: 'fechaLimite', message: 'Introduzca una fecha l&iacute;mite para la nueva tarea.' }
        ]
    }
});