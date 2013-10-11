FlickrFindr.view.PhotoDetails = Ext.extend(Ext.Panel, {
  id: 'photodetails',
  fullscreen: true,
  defaults:{
      styleHtmlContent: true
  },
  tpl: '<h1 class="descrip">{title}</h1><img src="http://src.sencha.io/x100/x100/http://farm{farm}.static.flickr.com/{server}/{id}_{secret}_b.jpg"></img>',
  dockedItems: [
    {
    xtype: 'toolbar',
    items: [
      {
      text: 'Back',
      ui: 'back',
      style: 'height:25px;font-size:25px;text-align: center;',
      handler: function() {
        Ext.dispatch({
          controller: 'searchphotos',
          action: 'showResults'
        });
      }
    }, {
      xtype: 'spacer'
    },
      {
      text: 'Guardar',
      ui: 'action',
      style: 'height:25px;font-size:25px;text-align: center;',
      handler: function() {
        Ext.dispatch({
          controller: 'savedPhotos',
          action: 'addSavedPhoto'
        });
      }
    }
    ]
  }
  ],
  initComponent: function() {
    FlickrFindr.view.PhotoDetails.superclass.initComponent.apply(this, arguments);
  }
});

Ext.reg('photodetails', FlickrFindr.view.PhotoDetails);