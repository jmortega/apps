Ext.regStore('FlickrFindr.store.SavedPhotos', {
  model: 'FlickrFindr.model.SearchPhoto',
  autoLoad: true,
  proxy: {
    type: 'localstorage',
    id: 'flickr-savedPhotos'
  }
});