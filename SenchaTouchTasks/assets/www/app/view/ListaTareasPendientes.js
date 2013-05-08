Ext.define("TareasApp.view.ListaTareasPendientes", {
    extend: "Ext.dataview.List",
    alias: "widget.ListaTareasPendientes",
    config: {
        loadingText: "Cargando tareas pendientes...",
        emptyText: "<div class=\"list-empty-text\">No se han encontrado tareas pendientes.</div>",
        onItemDisclosure: true,
        grouped: true,
        itemTpl: "<div class=\"list-item-titulo\">{titulo}</div><p class=\"alert\">Pendiente de resolver</p>"
    }
});