/**
 * @class RSS.view.Feeds
 * @extends Ext.DataView
 * View which allow the user to handle all the feeds
 * handled by the application.
 */
Ext.define('RSS.view.Feeds', {
    extend: 'Ext.DataView',
    alias: 'widget.feedlist',
    requires: [
        'RSS.tux.dataview.FeedListItem'
    ],
    
    //Definition of the view configuration
    
    config: {
        cls: 'rss-feed-list',
        store: 'Feeds',
        useComponents: true,
        defaultType: 'feedlistitem',

        items: [
            {
                xtype: 'toolbar',
                docked: 'top',
                style: 'height:25px;font-size:25px;text-align: right;margin-right:0px',
                items: [
                    {
                        
                        // Button add a new feed
                        
                        text: 'Nuevo Feed',
                        action: 'addfeed',
                        style: 'height:25px;font-size:25px;text-align: center;',
                        
                    },
                    {
                        //Button add a new feed
                        
                        text: 'Sencha Touch FeedBurner',
                        action: 'info',
                        style: 'height:25px;font-size:25px;text-align: center;',
                    },
                    {
                        
                        // Button check connection
                        
                        text: 'Conexi&oacute;n',
                        action: 'conexion',
                        style: 'height:25px;font-size:25px;text-align: center;',
                        
                    }
                ]
            }
        ],
        listeners: {
            initialize: function() { 
                
                //inicializar store data
                var store = Ext.data.StoreManager.lookup('Feeds');

                var record = Ext.ModelMgr.create({name:"Java Hispano Podcasts",url:"http://feeds2.feedburner.com/JHPodcasts"}, 'RSS.model.Feed');
                
                var record2 = Ext.ModelMgr.create({name:"HtmlCssJavascript",url:"http://feeds2.feedburner.com/HtmlCssJavascript"}, 'RSS.model.Feed');
                
                var record3 = Ext.ModelMgr.create({name:"Web Professional Minute",url:"http://feeds2.feedburner.com/WebProfessionalMinute"}, 'RSS.model.Feed');
                
               //Getting the record associated to the feed to edit
                var record5 = Ext.getStore('Feeds').findRecord('name', record.get('name'));
                var record6 = Ext.getStore('Feeds').findRecord('name', record2.get('name'));
                var record7 = Ext.getStore('Feeds').findRecord('name', record3.get('name'));
                
                if(!record5){
                    store.add(record);
                }
                
                if(!record6){
                    store.add(record2);
                }
                
                if(!record7){
                    store.add(record3);
                }
                
                
                store.sync();
                
                
            }
        }
        
    },
    
});