FlickrFindr.view.SavedPhotos = Ext.extend(Ext.Panel, {
  id: 'savedPhotos',
  layout: 'card',
  fullscreen: true,

  initComponent: function() {
    Ext.apply(this, {
      dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        title: 'Favoritos',
        items: [
               
                ]
      }],
      items: [
        {
        xtype: 'list',
        store: 'FlickrFindr.store.SavedPhotos',
        itemTpl: FlickrFindr.view.SearchPhotosTpl,
        loadingText: "Cargando favoritos...",
        emptyText: "<div class=\"list-empty-text\">No se han encontrado favoritos.</div>",
        listeners: {
          itemtap: function(list, item) {
            //We're given just the item number, not the actual record. Have to get that first.
            var photo = list.getStore().getAt(item);

            Ext.dispatch({
              controller: 'savedPhotos',
              action: 'showDetails',
              args: [photo]
            });
          }
        }
      },
        {
        xtype: 'savedPhotoDetails'
      },
      
      ]
    });

    FlickrFindr.view.SavedPhotos.superclass.initComponent.apply(this, arguments);
  }
});

Ext.reg('savedPhotos', FlickrFindr.view.SavedPhotos);