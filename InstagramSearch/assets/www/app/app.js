Ext.regApplication({
    name: 'SearchImageSenchaTouch',
	requires: ['Ext.device.Connection'],
    launch: function() {
        this.views.viewport = new this.views.Viewport();
    }
});