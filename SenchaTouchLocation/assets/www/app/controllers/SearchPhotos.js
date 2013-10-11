Ext.regController('searchphotos', {
  showResults: function() {
    var results = Ext.getCmp('searchphotos');
    var titlebar = results.getDockedItems()[0];
    var buttons = titlebar.query('button');
    Ext.each(buttons, function(button) {
      button.show();
    });

    results.doLayout();
    results.setActiveItem(0, {
      type: 'slide',
      direction: 'right'
    });
  },
  showDetails: function(interaction) {
    var photo = interaction.args[0];
    var results = Ext.getCmp('searchphotos');
    var titlebar = results.getDockedItems()[0];
    var buttons = titlebar.query('button');
    Ext.each(buttons, function(button) {
      button.hide();
    });

    results.doLayout();
    results.down('photodetails').update(photo.data);
    results.setActiveItem(1, 'slide');
  },
  nextPage: function() {
    var results = Ext.getCmp('searchphotos');
    results.down('list').getStore().nextPage();
  },
  previousPage: function() {
    var results = Ext.getCmp('searchphotos');
    results.down('list').getStore().previousPage();
  }
});