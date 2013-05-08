Ext.define("TareasApp.store.Tareas", {
    extend: "Ext.data.Store",
    requires: "Ext.data.proxy.LocalStorage",
    config: {
		autoLoad: true,
		autoSync: true,
        model: "TareasApp.model.Tarea",
        proxy: {
            type: 'localstorage',
            id: 'tareas-app-store'
        },
        sorters: [{ property: 'prioridad', direction: 'ASC'}],
        grouper: {
            sortProperty: "fecha",
            direction: "DESC",
            groupFn: function (record) {

                if (record && record.data.fecha) {
                    return record.data.fecha.toDateString();
                } else {
                    return '';
                }
            }
        }
    }
});
