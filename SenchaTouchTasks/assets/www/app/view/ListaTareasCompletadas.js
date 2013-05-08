Ext.define("TareasApp.view.ListaTareasCompletadas", {
    extend: "Ext.dataview.List",
    alias: "widget.ListaTareasCompletadas",
    config: {
        loadingText: "Cargando tareas completadas...",
        emptyText: "<div class=\"list-empty-text\">No se han encontrado tareas completadas.</div>",
        onItemDisclosure: true,
        grouped: true,
        itemTpl: "<div class=\"list-item-titulo\">{titulo} <p class=\"completada\">Completada</p></div>"
    }
});