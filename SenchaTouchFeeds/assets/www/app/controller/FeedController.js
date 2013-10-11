Ext.define('MyApp.controller.FeedController', {
    extend: 'Ext.app.Controller',

    config: {
        refs: {
            mainView: 'MainView'
        },

        control: {
            "#FeedList": {
                itemtap: 'onListItemTap',
                disclouse: 'onListItemDisclouse'
            },
            "feeddetails": {
                itemtap: 'onDataviewItemTap'
            }
        }
    },

    onListItemTap: function(dataview, index, target, record, e, options) {
        this.createFeedDetailsView(record.get('name'), record.get('url'));
    },
    onListItemDisclouse: function(dataview, record, target, index, e, options) {
        this.createFeedDetailsView(record.get('name'), record.get('url'));
    },

    onDataviewItemTap: function(dataview, index, target, record, e, options) {

        var task = new Ext.util.DelayedTask(function(){
            if (target) {
                //dataview.getScrollable().getScroller().scrollTo(0,  target.dom.offsetTop, true);
            }
        }, dataview);
        task.delay(200);
        this.createFeedItemView(record);
    },

    createFeedDetailsView: function(name, url) {
        
        var overlay = new Ext.Panel({
            tpl: "<tpl for='.'><div>fsd</div></tpl>",
            floating: true,
            modal: true,
            centered: true,
            width: 600,
            height: 400,
            styleHtmlContent: true,
            scroll: true,
            cls: 'htmlcontent',
            loadmask: true
        });
        

        // Next we show the overlay.
        overlay.show({type: 'fade', duration: 4000})
        
        var newURL = 'http://query.yahooapis.com/v1/public/yql?',
            yql = {
                q: 'select * from rss where url="' + url + '"',
                format: 'json'
            };

        newURL += Ext.Object.toQueryString(yql);
        var details = Ext.create(
        'MyApp.view.FeedDetails', {
            title: name,
            store: Ext.create('MyApp.store.FeedItemStore', {
                proxy: {
                    type: 'jsonp',
                    url: newURL,
                    reader: {
                        type: 'json',
                        rootProperty: 'query.results.item',
                        totalProperty: 'query.count'
                    }
                }
            })
        });
        details.getStore().load();
        this.getMainView().push(details);
    },

    createFeedItemView: function(record) {
      
        var overlay = new Ext.Panel({
            tpl: "<tpl for='.'><div>fsd</div></tpl>",
            floating: true,
            modal: true,
            centered: true,
            width: 600,
            height: 400,
            styleHtmlContent: true,
            scroll: true,
            cls: 'htmlcontent',
            loadmask: true
        });
        

        // Next we show the overlay.
        overlay.show({type: 'fade', duration: 4000})
        

        var item = Ext.create(
        'MyApp.view.FeedItem', {
            title: 'Art&iacute;culo',
            record: record
        });
        this.getMainView().push(item);
        

    }

});