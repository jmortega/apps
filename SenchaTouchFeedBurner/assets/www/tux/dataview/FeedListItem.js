/**
 * @class RSS.tux.dataview.FeedListItem'
 * @extends Ext.dataview.component.DataItem
 * Custom DataView component used inside the Feeds DataView
 * to allow the user to Edit a Feed simply pressing a button
 * placed on every associated item.
 */
Ext.define('RSS.tux.dataview.FeedListItem', {
    
    extend: 'Ext.dataview.component.DataItem',

    requires: ['Ext.Button'],

    xtype: 'feedlistitem',

    defaults:{
        styleHtmlContent: true
    },
    
    config: {
        
    
        /**
         * Setting up the DataMap object.
         */
        dataMap: {
            getName: {
                setHtml: 'name'
            }
        },
    
        /**
         * @cfg {Ext.Component} name
         * The component used to show the Feed name.
         */
        name: {
            cls: 'x-name',
            flex: 1,
            style: 'font-size:40px;text-align: center;color:#0080FF;border: 3px solid #0080FF;-webkit-border-radius: 15px 55px 5px;margin-top:5px'
        },
    
        /**
         * @cfg {Ext.Button} editButton
         * The component that allows the user to edit the Feed.
         */
        editButton: {
            text: 'Edit',
            width: 100,
            style: 'height:25px;font-size:35px;text-align: center;color:#0080FF;border: 4px solid #0080FF;'
        },

        /**
         * The component use an 'hbox' stretched layout.
         * In this way the Edit Button will be placed at the
         * right edge of the device screen. */
        layout: {
            type: 'hbox',
            align: 'stretch'
        },
        
        
        defaults:{
            styleHtmlContent: true
        },
    },

    /**
     * Apply the DataView item Name component.
     * @param {Object} config The configuration Object.
     * @return {Ext.Component} The Name component.
     */
    applyName: function(config) {
    
        return Ext.factory(config, Ext.Component, this.getName());

    },

    /**
     * Update the Feed Name.
     * @param {Ext.Component} newName The new component.
     * @param {Ext.Component} oldName The old component.
     */
    updateName: function(newName, oldName) {
    
        //If is already defined a name component this is removed
        if (newName) {
            this.add(newName);
        }

        /* If is defined a new name component this is added
         * to this DataView component.
         */
        if (oldName) {
            this.remove(oldName);
        }
    },

    /**
     * Apply the DataView item Edit Feed button.
     * @param {Object} config The configuration Object.
     * @return {Ext.Button} The Edit button.
     */
    applyEditButton: function(config) {
        
        return Ext.widget('button', {
            width: 70,
            ui: 'plain',
            iconMask: true,
            iconCls: 'compose',
            navigation: true,
            style: 'height:25px;font-size:30px;text-align: center;color:#0000FF;border: 5px solid #0000FF;'
        });

    },

    /**
     * Update the Edit Feed Button.
     * @param {Ext.Button} newEditButton The new Edit Button.
     * @param {Ext.Button} oldEditButton The old Edit Button.
     */
    updateEditButton: function(newEditButton, oldEditButton) {
    
        //If is already defined an Edit button this is removed
        if (oldEditButton) {
            this.remove(oldEditButton);
        }
    
        /* If is defined a new Edit button this will be added
         * to this DataView component after setting an handler
         * for its tap event.
         */
        if (newEditButton) {
            newEditButton.on('tap', this.onItemEdit, this);
            this.add(newEditButton);
        }

    },

    /**
     * Tap function called when the user press the Edit Feed button.
     * @param {Ext.Button} btn The Edit Button.
     * @param {Ext.EventObject} e The Event object.
     */
    onItemEdit: function(btn, e){
    
        //Getting the dataview
        var dataview = this.config.dataview,
    
            //Getting the record associated to this dataview item
            record = this.getRecord();

        //Fires the "feededit" event
        dataview.fireEvent('feededit', dataview, this, record);

        //Prevent the dataview "itemtap" event
        e.stopPropagation();
    
    }

});
