Ext.define('MyApp.store.FeedStore', {
    extend: 'Ext.data.Store',

    requires: [
        'MyApp.model.Feed'
    ],

    config: {
        autoLoad: true,
        autoSync: true,
        model: 'MyApp.model.Feed',
        storeId: 'FeedStore',
        proxy: {
            type: 'localstorage'
        },
        reader: {
            type: 'json'
        }

    }
});