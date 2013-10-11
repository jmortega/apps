/**
 * @class RSS.view.New
 * @extends Ext.form.Panel
 * View which allow the user to add a new feed
 * that should be handled by the application.
 */
Ext.define('RSS.view.New', {
    extend: 'Ext.form.Panel',
    alias: 'widget.newfeed',
    config: {
        items: [
            {
                //Definition of the top docked toolbar
                xtype: 'toolbar',
                itemId: 'tbrFeedConfig',
                docked: 'top',
                enter: 'top',
                stretchX: true,
                stretchY: true,
                defaults:{
                    styleHtmlContent: true
                },
                items: [
                    {    
                        /* Definition of the button which allows
                         * the user to go back to the feeds view. */
                        text: 'Feeds',
                        action: 'showfeeds',
                        ui: 'back',
                        style: 'height:25px;font-size:25px;text-align: center;',
                    }
                ]
            },
            {
                
                items: [
                        {
                               xtype: 'container',
                               html: 'A&ntilde;ade un Feed Rss introduciendo nombre y URL. <br/>La URL debe estar en el formato http://feedURL.com.<br/>Por ejemplo http://feeds2.feedburner.com/JHPodcasts',
                               width: '100%',
                               style: 'width:100%;height:100px;font-size:20px;text-align: center;'
                        },
                    {
                        //Field which contains the feed name
                        xtype: 'textfield',
                        label: 'Nombre',
                        name: 'name',
                        style: 'width:100%;height:120px;font-size:30px;text-align: center;',
                        placeHolder:'Introduzca Nombre',
                    },
                    {
                        //Field which contains the feed url
                        xtype: 'textfield',
                        label: 'Url',
                        placeHolder: 'http://feeds.feedburner.com/',
                        name: 'url',
                        style: 'width:100%;height:120px;font-size:30px;text-align: center;',
                        vtype:'url',
                        
                        
                    }
                ]
            },
            {
                /* Definition of a button that allows the user
                 * to save the new specified field. */
                xtype: 'button',
                action: 'savefeed',
                ui: 'action',
                text: 'Guardar feed',
                style: 'width:100%;height:80px;font-size:25px;text-align: center;'
            },
            {
                xtype: 'container',
                height: 15
            },
            {
                /* Definition of a button that allows the user
                 * to delete the selected feed. */
                xtype: 'button',
                action: 'deletefeed',
                ui: 'decline',
                text: 'Borrar feed',
                hidden: true,
                style: 'width:100%;height:80px;font-size:25px;text-align: center;'
            }
        ]
    }    
});