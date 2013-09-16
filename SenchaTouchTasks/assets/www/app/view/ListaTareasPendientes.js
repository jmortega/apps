Ext.define("TareasApp.view.ListaTareasPendientes", {
    extend: "Ext.dataview.List",
    alias: "widget.ListaTareasPendientes",
    config: {
        loadingText: "Cargando tareas pendientes...",
        emptyText: "<div class=\"list-empty-text\">No se han encontrado tareas pendientes.</div>",
        onItemTap: true,
        onItemDisclosure: true,
        grouped: true,
        itemTpl: "<div class=\"list-item-titulo\">{titulo}<tpl  if='descripcion'><font style='color:'><br/>{descripcion}</font></tpl><br/><tpl  if='prioridad == 1'><tpl  if='!completada'><div class=\"list-item-prioridad\">* Alta prioridad</div></tpl></tpl>"
                
    }
});