Ext.define("TareasApp.view.ListaTareas", {
    extend: "Ext.dataview.List",
    alias: "widget.ListaTareas",
    config: {
        loadingText: "Cargando tareas...",
        emptyText: "<div class=\"list-empty-text\">No se han encontrado tareas.</div>",
        onItemTap: true,
        onItemDisclosure: true,
        grouped: true,
        itemTpl: "<div class=\"list-item-titulo\">{titulo}<tpl  if='descripcion'><font style='color:'><br/>{descripcion}</font></tpl><br/> <tpl  if='completada'><div class=\"completada\"><font style='color:green'>completada</font></div></tpl><tpl  if='!completada'><font style='color:red'>pendiente de resolver</font></tpl></div><tpl  if='prioridad == 1'><tpl  if='!completada'><div class=\"list-item-prioridad\">* Alta prioridad</div></tpl></tpl>"
    }
});