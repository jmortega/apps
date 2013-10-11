/**
 * This file sets application-wide settings and launches the application when everything has
 * been loaded onto the page. By default we just render the applications Viewport inside the
 * launch method (see app/views/Viewport.js).
 */
FlickrFindr = new Ext.Application({
    name: "FlickrFindr",
    launch: function() {
        this.viewport = new FlickrFindr.Viewport();
    }
});
Ext.namespace('FlickrFindr.view', 'FlickrFindr.model', 'FlickrFindr.store', 'FlickrFindr.controller');
