/**
 * @class RSS.view.NewsList
 * @extends Ext.List
 * View which contains all the news related
 * to the user selected field.
 */
Ext.define('RSS.view.NewsList', {
    extend: 'Ext.List',
    alias: 'widget.newslist',
    config: {
        cls: 'rss-news-list',
        store: 'News',
        scrollable: true,
        loadingText: "Cargando datos....",
        emptyText: "<div class=\"cargando\"><br/><br/><br/>Cargando noticias....</div>",
        onItemDisclosure: true,
        itemTpl: [
            '<h1 class=\"list-item-titulo1\">{title}</h1>',
            '<p class=\"list-item-titulo\">{contentSnippet}</p>',
            '<span class=\"list-item-titulo2\">{author}</span>'
        ],
        items: [
            {
                //Definition of the top docked toolbar
                xtype: 'toolbar',
                docked: 'top',
                title: 'Noticias',
                style: 'height:25px;font-size:25px;text-align: center;',
                items: [
                    {
                        /* Definition of the button that allows
                         * the user to go back to the feeds view. */
                        text: 'Feeds',
                        action: 'showfeeds',
                        ui: 'back',
                        navigation: true,
                        style: 'height:25px;font-size:25px;text-align: center;'
                    },
                    {
                        
                        // Button check connection
                        
                        text: 'Conexi&oacute;n',
                        action: 'conexion',
                        style: 'height:25px;font-size:25px;text-align: center;',
                        
                    }
                ]
            }
        ]
    }    
});