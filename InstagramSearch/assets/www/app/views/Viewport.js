instagramSearch.views.Viewport = Ext.extend(Ext.TabPanel, {

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

        instagramSearch.views.instagramList = new instagramSearch.views.InstagramList();
        instagramSearch.views.instagramInfo = new instagramSearch.views.InstagramInfo();
        
        Ext.apply(this, {
            items: [
                {
                        xtype: 'container',
                        layout: 'fit',
                        html: '<div><img src="css/images/picture.png" align="left" /><img src="css/images/instagram.png" align="right" /></div>',
                        iconCls: 'home',
                        title: 'Home'
                },
                instagramSearch.views.instagramList,
                instagramSearch.views.instagramInfo
                
            ]
        });

        instagramSearch.views.Viewport.superclass.initComponent.apply(this, arguments);
    }

});