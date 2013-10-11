Ext.define('MyApp.view.MyNavigationView', {
    extend: 'Ext.navigation.View',
    alias: 'widget.MainView',

    config: {
        items: [
            {
                xtype: 'list',
                title: 'Sencha Touch Feeds',
                id: 'FeedList',
                itemId: 'mylist',
                itemTpl: [
                    '<div class=\"list-item\">{name}</div>'
                ],
                store: 'FeedStore',
                defaults:{
                    styleHtmlContent: true
                },
                onItemDisclosure: true,
                preventSelectionOnDisclose: false,
                listeners: {
                    initialize: function() { 
                        
                        var store = Ext.data.StoreManager.lookup('FeedStore');

                        var record = Ext.ModelMgr.create({id:1,name:"Java Hispano Podcasts",url:"http://feeds2.feedburner.com/JHPodcasts"}, 'MyApp.model.Feed');
                        
                        var record2 = Ext.ModelMgr.create({id:2,name:"HtmlCssJavascript",url:"http://feeds2.feedburner.com/HtmlCssJavascript"}, 'MyApp.model.Feed');
                        
                        var record3 = Ext.ModelMgr.create({id:3,name:"Web Professional Minute",url:"http://feeds2.feedburner.com/WebProfessionalMinute"}, 'MyApp.model.Feed');
                        
                        store.add(record);
                        store.add(record2);
                        store.add(record3);

                        
                        store.sync();
                        
                        
                    }
                }
            }
        ],
        navigationBar: {
            items: [
                {
                    xtype: 'button',
                    align: 'right',
                    id: 'addButton',
                    itemId: 'mybutton',
                    text: 'Nuevo Feed',
                    style: 'height:25px;font-size:25px;text-align: center;'
                },
                {
                    xtype: 'button',
                    align: 'left',
                    id: 'infoButton',
                    itemId: 'infobutton',
                    text: 'Info',
                    style: 'height:25px;font-size:25px;text-align: center;'
                },
                {
                    xtype: 'button',
                    align: 'left',
                    id: 'checkConnectionButton',
                    itemId: 'checkConnectionButton',
                    text: 'Check Connection',
                    style: 'height:25px;font-size:25px;text-align: center;'
                },
                
            ]
        },
        listeners: [
            {
                fn: 'onFeedListItemSwipe',
                event: 'itemswipe',
                delegate: '#FeedList'
            },
            {
                fn: 'onAddButtonTap',
                event: 'tap',
                delegate: '#addButton'
            },
            {
                fn: 'onCheckConnectionTap',
                event: 'tap',
                delegate: '#checkConnectionButton'
            },
            {
                fn: 'onInfoButtonTap',
                event: 'tap',
                delegate: '#infoButton'
            },
        ]
    },

    onFeedListItemSwipe: function(dataview, index, target, record, e, options) {
        // this is where we should display a delete button
        // swiping left to right should show the button.
    },

    onAddButtonTap: function(button, e, options) {
        var sheet = Ext.getCmp('AddSheet');
        if(!Ext.isDefined(sheet)) {
            sheet = Ext.create('MyApp.view.AddSheet');
            Ext.Viewport.add(sheet);
        }
        sheet.show();
    },
    onInfoButtonTap: function(button, e, options) {
        var sheet = Ext.getCmp('InfoSheet');
        if(!Ext.isDefined(sheet)) {
            sheet = Ext.create('MyApp.view.InfoSheet');
            Ext.Viewport.add(sheet);
        }
        sheet.show();
    },
    
    onCheckConnectionTap: function(button, e, options) {
        
        var isOnline = navigator.onLine;
        
        if(!isOnline){
            
            new Ext.MessageBox().show({
                title: 'Connection',
                message: 'You are not connected any network',
             });
        }
        
        var networkState = navigator.connection.type;

        var states = {};
        states[Connection.UNKNOWN]  = 'Unknown connection';
        states[Connection.ETHERNET] = 'Ethernet connection';
        states[Connection.WIFI]     = 'WiFi connection';
        states[Connection.CELL_2G]  = 'Cell 2G connection';
        states[Connection.CELL_3G]  = 'Cell 3G connection';
        states[Connection.CELL_4G]  = 'Cell 4G connection';
        states[Connection.NONE]     = 'No network connection';
        
        new Ext.MessageBox().show({
            title: 'Connection',
            message: 'Connection type:'+states[networkState],
         });
        
    }

});