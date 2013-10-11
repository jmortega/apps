Ext.define('MyApp.view.AddSheet', {
    extend: 'Ext.Sheet',

    config: {
        id: 'AddSheet',
        enter: 'top',
        stretchX: true,
        stretchY: true,
        defaults:{
            styleHtmlContent: true
        },
        items: [
            {
                xtype: 'formpanel',
                height: 450,
                items: [
                    {
                        xtype: 'container',
                        height: 90,
                        html: 'A&ntilde;ade un Feed Rss introduciendo nombre y URL. <br/>La URL debe estar en el formato http://feedURL.com.<br/>Por ejemplo http://feeds2.feedburner.com/JHPodcasts',
                        margin: '\'0 0 5 0\'',
                        width: '100%',
                        style: 'height:120px;font-size:20px;text-align: center;'
                    },
                    {
                        xtype: 'textfield',
                        id: 'name',
                        margin: '\'3 0 3 0\'',
                        label: 'Nombre',
                        name: 'name',
                        placeHolder:'Introduzca Nombre',
                        style: 'height:70px;font-size:25px;text-align: center;'
                    },
                    {
                        xtype: 'urlfield',
                        id: 'url',
                        margin: '\'3 0 3 0\'',
                        label: 'URL',
                        name: 'url',
                        vtype:'url',
                        placeHolder: 'http://example.com',
                        value:'http://',
                        style: 'height:70px;font-size:25px;text-align: center;'
                    },
                    {
                        xtype: 'button',
                        id: 'SaveButton',
                        itemId: 'mybutton1',
                        ui: "confirm",
                        iconMask: true,
                        scope: this,
                        margin: 10,
                        text: 'Guardar',
                        style: 'width:150px;height:30px;font-size:20px;'
                    },
                    {
                        xtype: 'button',
                        id: 'CancelButton',
                        itemId: 'mybutton2',
                        margin: 10,
                        text: 'Cancelar',
                        style: 'width:150px;height:30px;font-size:20px;'
                    }
                ]
            }
        ],
        listeners: [
            {
                fn: 'onSaveButtonTap',
                event: 'tap',
                delegate: '#SaveButton'
            },
            {
                fn: 'onCancelButtonTap',
                event: 'tap',
                delegate: '#CancelButton'
            }
        ]
    },

    onSaveButtonTap: function(button, e, options) {
        var formPanel = this.down('formpanel');

        var values = formPanel.getValues();

        var store = Ext.data.StoreManager.lookup('FeedStore');
        
        var record = Ext.ModelMgr.create(values, 'MyApp.model.Feed');

        var errs = record.validate(); // Call the validations
        var msg = '';
 
        if (!errs.isValid()) // Checks whether the error is valid
        {
            errs.each(function (err) {
                msg += err.getField() + ' : ' + err.getMessage() + '<br/>';
            });
 
            Ext.Msg.alert('ERROR', msg);
 
        }
        else
        {
            Ext.Msg.alert('SUCCESS', 'Feed agregado correctamente');
            
            store.add(record);

            store.sync();
            
            
            this.hide();
        }
          
       
    },

    onCancelButtonTap: function(button, e, options) {
        this.down('formpanel').reset();
        this.hide();
    }

});