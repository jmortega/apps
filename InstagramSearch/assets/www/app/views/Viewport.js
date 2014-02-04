SearchImageSenchaTouch.views.Viewport = Ext.extend(Ext.TabPanel, {

    fullscreen: true,
    layout: 'fit',
    requires: ['Ext.device.Connection'],
    



    cardSwitchAnimation: 'slide',
        tabBar:{
                dock: 'bottom',
                layout: {
                    pack: 'center'
                }
        },
    initComponent: function() {

        SearchImageSenchaTouch.views.searchImageList = new SearchImageSenchaTouch.views.SearchImageList();
        SearchImageSenchaTouch.views.searchImageInfo = new SearchImageSenchaTouch.views.SearchImageInfo();
        
        Ext.apply(this, {
            items: [
                {
                        xtype: 'container',
                        layout: 'fit',
                        html: '<div><img src="css/images/picture.png" align="left" /><img src="css/images/icon.png" align="right" /></div>',
                        iconCls: 'home',
                        title: 'Home'
                },
                SearchImageSenchaTouch.views.searchImageList,
                SearchImageSenchaTouch.views.searchImageInfo
                
            ]
        });

        SearchImageSenchaTouch.views.Viewport.superclass.initComponent.apply(this, arguments);
    }

});