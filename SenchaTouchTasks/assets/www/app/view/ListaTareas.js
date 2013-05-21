Ext.define("TareasApp.view.ListaTareas", {
    extend: "Ext.dataview.List",
    alias: "widget.ListaTareas",
    config: {
        loadingText: "Cargando tareas...",
        emptyText: "<div class=\"list-empty-text\">No se han encontrado tareas.</div>",
        onItemDisclosure: true,
        grouped: true,
        itemTpl: "<div class=\"list-item-titulo\">{titulo}<tpl  if='descripcion'><font style='color:'><br/>{descripcion}</font></tpl><br/> <tpl  if='completada'><font style='color:green'>completada</font></tpl><tpl  if='!completada'><font style='color:red'>pendiente de resolver</font></tpl></div><tpl  if='prioridad == 1'><div class=\"list-item-prioridad\">Alta prioridad</div></tpl>"
    }
});