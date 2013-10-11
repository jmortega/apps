/**
 * @class RSS.view.News
 * @extends Ext.Container
 * View which contains the real news content downloaded
 * by the selected feed.
 */
Ext.define('RSS.view.News', {
    extend: 'Ext.Container',
    alias: 'widget.news',
    config: {
        cls: 'rss-news',
        scrollable: 'vertical',
        tpl: [
            '<h1 class=\"list-item-titulo1\">{title}</h1>',
            '<h2 class=\"list-item-titulo2\">{content}</h2>'
        ]
    }
});