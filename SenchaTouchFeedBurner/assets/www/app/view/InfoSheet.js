Ext.define('RSS.view.InfoSheet', {
    extend: 'Ext.Sheet',

    config: {
        id: 'InfoSheet',
        enter: 'top',
        stretchX: true,
        stretchY: true,
        defaults:{
            styleHtmlContent: true
        },
        items: [
            {
                xtype: 'formpanel',
                height: 500,
                items: [
                    {
                        xtype: 'textareafield',
                        id: 'projecto',
                        margin: '\'3 0 3 0\'',
                        label: 'Proyecto',
                        name: 'projecto',
                        placeHolder:'Sencha Touch FeedBurner- RSS Reader',
                        style: 'height:100px;font-size:25px;text-align: center;'
                    },
                    {
                        xtype: 'textareafield',
                        id: 'descripcion',
                        margin: '\'3 0 3 0\'',
                        label: 'Descripci&oacute;n',
                        name: 'descripcion',
                        placeHolder:'Lector RSS que permite leer las noticias de las fuentes RSS dadas de alta en la aplicacion',
                        style: 'height:100px;font-size:25px;text-align: center;'
                    },
                    {
                        xtype: 'textareafield',
                        id: 'autor',
                        margin: '\'3 0 3 0\'',
                        label: 'Autor',
                        name: 'autor',
                        placeHolder: 'Jose Manuel Ortega Candel.jmoc25@gmail.com',
                        style: 'height:100px;font-size:25px;text-align: center;'
                    },
                    {
                        xtype: 'textareafield',
                        id: 'perfil',
                        margin: '\'3 0 3 0\'',
                        label: 'Perfil Linkedin',
                        name: 'perfil',
                        placeHolder: 'https://www.linkedin.com/in/jmortega1',
                        style: 'height:100px;font-size:25px;text-align: center;'
                    },
                    {
                        xtype: 'button',
                        id: 'closeButton',
                        itemId: 'mybutton2',
                        margin: 10,
                        text: 'Cerrar',
                        style: 'width:100%;height:30px;font-size:20px;'
                    }
                ]
            }
        ],
        listeners: [
            {
                fn: 'onCloseButtonTap',
                event: 'tap',
                delegate: '#closeButton'
            }
        ]
    },

    onCloseButtonTap: function(button, e, options) {
        this.hide();
    }

});