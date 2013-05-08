Ext.application({
    name: "TareasApp",
    models: ["Tarea"],
    stores: ["Tareas"],
    controllers: ["TareasControlador"],
    views: ["AppInfo","ListaTareas","ContenedorListaTareas","ContenedorListaTareasPendientes","ContenedorListaTareasCompletadas","ListaTareasPendientes","ListaTareasCompletadas","NuevaTarea","EditorTareas"],
	fullscreen: true,
	isIconPrecomposed: true,
    launch: function () {

        var ContenedorListaTareas = {
            xtype: "ContenedorListaTareas"
        };
		var ContenedorListaTareasPendientes = {
            xtype: "ContenedorListaTareasPendientes"
        };
		var ContenedorListaTareasCompletadas = {
            xtype: "ContenedorListaTareasCompletadas"
        };
        var NuevaTarea = {
            xtype: "NuevaTarea"
        };
		var EditorTareas = {
            xtype: "EditorTareas"
        };
		var AppInfo = {
            xtype: "AppInfo"
        };


        Ext.Viewport.add([ContenedorListaTareas,EditorTareas,NuevaTarea,ContenedorListaTareasPendientes,ContenedorListaTareasCompletadas,AppInfo]);

    }
});