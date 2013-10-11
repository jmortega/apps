Ext.define('MyApp.store.FeedItemStore', {
    extend: 'Ext.data.Store',
    alias: 'store.itemstore',

    requires: [
        'MyApp.model.FeedItem'
    ],

    config: {
        model: 'MyApp.model.FeedItem',
        storeId: 'FeedItemStore',
        reader: {
            type: 'json'
        }
    }
});