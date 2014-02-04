        var autor= {
            xtype: 'textareafield',
            name: 'autor',
            label: 'Autor',
            placeHolder: 'Jose Manuel Ortega Candel.Comentarios a jmoc25@gmail.com',
            readOnly:true,
            style: 'height:140px;font-size:25px;'
        };
        
         var proyecto = {
            xtype: 'textareafield',
            name: 'proyecto',
            label: 'Proyecto',
            maxLength: 80,
            placeHolder: 'SearchImageSenchaTouch',
            readOnly:true,
            style: 'height:100px;font-size:25px;'
        };
        
        var descripcion = {
            xtype: 'textareafield',
            name: 'descripcion',
            label: 'Descripcion',
            maxLength: 120,
            placeHolder: 'Aplicacion para buscar las últimas imágenes dadas de alta en instagram a partir del criterio de búsqueda del usuario.Permite visualizar las imágenes y los enlaces',
            readOnly:true,
            style: 'height:200px;font-size:25px;'
        };


SearchImageSenchaTouch.views.SearchImageInfo = Ext.extend(Ext.Panel, {
    layout: 'fit',
	requires: ['Ext.device.Connection'],
    dockedItems: [{
			xtype: "panel",
			title: '<p style="text-align: right">Search Images</p>',
            items: [
	
                    proyecto,descripcion,autor  

            ]
	  }],
    items: [


			   
    ],
    iconCls: 'info',
    title: 'Info'
});
