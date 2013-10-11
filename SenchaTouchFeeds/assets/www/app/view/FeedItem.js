Ext.define('MyApp.view.FeedItem', {
    extend: 'Ext.Panel',

    config: {
        layout: {
            type: 'fit'
        },
        scrollable: true,
        loadingText: "<div class=\"cargando\"><br/><br/><br/>Cargando datos....</div>",
        emptyText: "<div class=\"cargando\"><br/><br/><br/>Cargando noticias....</div>",
        loadmask: true,
        tpl: [
            '<article>',
            '            <header>',
            '                <h2>{title}</h2>',
            '                <tpl if="creator"><p class="creator">by {creator}</p></tpl>',
            '                <tpl if="pubDate"><div class="fecha"> <time datetime="{pubDate:date("c")}">{pubDate:date("M j, Y, g:i a")}</time></div></tpl>',
            '            </header>',
            '            <div class="content">',
            '              <tpl if="content.length &gt; 0">',
            '              <tpl for="content">',
            '                <tpl if="xindex == 2">{.}</tpl>',
            '              </tpl>',
            '              <tpl else>',
            '             <div class="descrip">   {description} </div>',
            '              </tpl>',
            '            </div>',
            '            <footer>',
            '                <a href="{link}">Leer Art&iacute;culo</a>',
            '            </footer>',
            '</article>'
        ]
    }

});