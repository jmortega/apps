
Ext.Loader.setConfig({
    enabled: true
});

Ext.application({
    models: [
        'Feed',
        'FeedItem'
    ],
    stores: [
        'FeedStore',
        'FeedItemStore'
    ],
    views: [
        'MyNavigationView',
        'AddSheet',
        'InfoSheet',
        'FeedDetails',
        'FeedItem'
    ],
    name: 'MyApp',
    controllers: [
        'FeedController'
    ],
    
  //Setting the Viewport configuration
    viewport: {
        layout: {
            type: 'fit',
            animation: {
                type: 'slide'
            }
        }    
    },

    launch: function() {
        Ext.create('MyApp.view.MyNavigationView', {fullscreen: true});
    }

});
