Ext.define('MyApp.view.FeedDetails', {
    extend: 'Ext.dataview.List',
    alias: 'widget.feeddetails',

    config: {
        layout: {
            type: 'fit'
        },
        store: 'FeedItemStore',
        scrollable: true,
        loadingText: "<div class=\"cargando\"><br/><br/><br/>Cargando datos....</div>",
        loadmask: true,
        emptyText: "<div class=\"alert\"><br/><br/><br/><br/><br/><br/>No se han encontrado feeds.<br/><br/>Es posible que no disponga de conexi&oacute;n <br/> o que la URL no sea correcta</div>",
        onItemDisclosure: true,
        onItemTap:true,
        itemTpl: "<div class=\"list-item-titulo\">{title}</div>"
    }

});