Ext.regApplication({
    name: 'instagramSearch',
	requires: ['Ext.device.Connection'],
    launch: function() {
        this.views.viewport = new this.views.Viewport();
    }
});