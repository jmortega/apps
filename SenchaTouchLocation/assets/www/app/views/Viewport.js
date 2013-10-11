FlickrFindr.Viewport = Ext.extend(Ext.TabPanel, {
  id: 'viewport',
  fullscreen: true,
  cardSwitchAnimation: 'slide',
  tabBar: {
    dock: 'bottom',
    layout: {
      pack: 'center'
    }
  },
  initComponent: function() {
    Ext.apply(this, {
      dockedItems: [],
      items: [
      {
        xtype: 'searchphotos',
        title: 'Buscar',
        iconCls: 'search'
      },
      {
        xtype: 'savedPhotos',
        title: 'Favoritos',
        iconCls: 'favorites'
      },
      {
          xtype: 'location',
          title: 'Location',
          iconCls: 'maps'
        },
      

                              ]
    });

    FlickrFindr.Viewport.superclass.initComponent.apply(this, arguments);
  }
});