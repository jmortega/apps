var overlay = new Ext.Panel({
    tpl: "<tpl for='.'><div class='gram-container'><img id='current-gram' /></div></tpl>",
    floating: true,
    modal: true,
    centered: true,
    width: 600,
    height: 400,
    styleHtmlContent: true,
    scroll: true,
    cls: 'htmlcontent',
    loadmask: true
});

var search = {
            xtype: "searchfield",
			id:"task_search",
			itemId:'task_search',
            placeHolder: 'Introduzca palabra clave',
			required: true,
			autoComplete: true,
			width: '40%',
			style: 'width:100px;height:30px;font-size:25px;'
};

var busqueda = {
            xtype: "button",
            text: 'Search',
            id:"search_button",
            style: 'width:80px;height:30px;font-size:20px;background:grey',
            handler:function(btn){

           
            /*Ext.create('Ext.Panel', {
                    html: 'This is a floating panel!',
                    left: 0,
                    padding: 10
            }).showBy(btn);*/

                
			if(Ext.getCmp('task_search').getValue()==''){
			
				Ext.Msg.alert('Introduzca criterio de b&uacute;squeda');
				
			}else{
			
			    if (!navigator.onLine) {
                    Ext.Msg.alert('Conexion Error');

			    }else{
			    
			        var task_search_value=Ext.getCmp('task_search').getValue();
			        
			        if(task_search_value !=null && task_search_value!=''){
			         
			            task_search_value=task_search_value.replace(' ','');
			        }
			        
			        var newProxy={
                          // JSON-P FTW
                          type: 'scripttag',
                          // If you're offline, uncomment the two lines below and comment the line above and the line with the Instagram URL.
                           //type: 'ajax',
                           //url: 'app/models/data_from_instagram_api.json',
                           url: 'https://api.instagram.com/v1/tags/'+task_search_value+'/media/recent?access_token=2260821.f59def8.7fe0d31e791e4e62a00fc8f44b831140'+'&limit=50',
                           params:{limit: '50',
                           },
                           reader: {
                               type: 'json',
                               // The root of the JSON response from which we will iterate over is called "data".  
                               // See an example of the JSON response in models/data_from_instagrams.json
                               root: 'data'
                           },
                           timeout: 20000,
                           listeners: {
                           exception:function () {
                           Ext.Msg.alert('NetWork ERROR', 'Network unreachable,Try in few minutes.');
                           }
                           }
                          

                       };
			            Ext.getStore('store_tp').setProxy(newProxy);
			            Ext.getStore('store_tp').load();
			    }
			
			}
			}

};

// This is the list item inside in the main list.  It is created for every record in the model, or simply
// each item in the array returned from the Instagram API.
instagramSearch.views.InstragramViewList = Ext.extend(Ext.List, {

    // Each item in the InnerList will be rendered with our imgTpl() declared in our Templates.js file.
    itemTpl: instagramSearch.views.innerListItemTpl(),
	requires: ['Ext.device.Connection'],

    // The class name associated with each InnerList item.  We can style using this as the root CSS class for
    // all styles inside the InnerList items.
    itemCls: 'instagram',
    loadingText: "Cargando imágenes...",
    onItemDisclosure: true,
    emptyText: "<div class=\"list-empty-text\">No se han encontrado imágenes para el criterio introducido</div>",
    // Here's where we add the pull to refresh plugin.  Yep, that's all you need to do. =)
    plugins: [{
        ptype: 'pullrefresh'
    }],

    // Bind our listeners to the each InnerList item.
    // On itemtap, we grab the current record so we can create the full size image overlay.
    // We added a loading animation while the image is downloaded so the user knows what's going on.
    listeners: {
        itemtap: function (list, index, element, event) {
            
            var msg = new Ext.MessageBox();
            msg.show({
            title: 'Image instagram',
            width:350,
            multiline: true,
            style: 'width:80px;height:30px;font-size:20px;background:grey',
            msg: '¿Desea visualizar la imagen o enlazar con la página de instagram?',
            buttons: [{text:'Image',itemId:'image'},{text:'Link',itemId:'link'},{text:'Close',itemId:'close'}],
            fn:function(response){
                
             // Grab a reference the record.
                var record = list.getRecord(element);

                    if(response!=null && response=='image'){
                        
                     // First, we update the the overlay with the proper record (data binding).
                        overlay.update(record.data);

                        // Next we show the overlay.
                        overlay.show({type: 'fade', duration: 400})
                        
                        // Show the loading indicator.
                        overlay.setLoading(true); 
                        
                        // Now, we grab a reference to the img element.
                        var img = document.getElementById('current-gram');
                        // Set the src value to the standard_res_url
                        img.src = record.data.standard_res_url;
                        
                        // After this has loaded, let's hide the loading animation.
                        img.onload = function()
                        {
                          overlay.setLoading(false);
                        }
                    
                    }
                    
                    if(response!=null && response=='link'){
                        
                        window.location = record.data.link;
                        
                    }
                }
            });
            msg.doComponentLayout();

        }
			
    }

});

instagramSearch.views.InstagramList = Ext.extend(Ext.Panel, {
    layout: 'fit',
	requires: ['Ext.device.Connection'],
    dockedItems: [{
			xtype: "toolbar",
			title: '<p style="text-align: right">Instagram Search Images</p>',
            items: [
	
                search,
				busqueda

            ]	
	  }],
    items: [

        new instagramSearch.views.InstragramViewList({
                   store: new Ext.data.Store({
				   
                       // We provide an id for the store so it's easy to debug.
                       // You can pull it up in the console with
                       // Ext.getStore('store_tp');
                       id: 'store_tp',
                       
                       model: 'instagramSearch.models.InstagramModel',

                       // Fire off a request when the page loads.  Here is why we don't *need* a controller for this simple view.
                       autoLoad: false,
                       proxy: {
                          // JSON-P FTW
                          type: 'scripttag',
                          // If you're offline, uncomment the two lines below and comment the line above and the line with the Instagram URL.
                           //type: 'ajax',
                           //url: 'app/models/data_from_instagram_api.json',
                           url: 'https://api.instagram.com/v1/tags/'+'java'+'/media/recent?access_token=2260821.f59def8.7fe0d31e791e4e62a00fc8f44b831140',
                           reader: {
                               type: 'json',
                               // The root of the JSON response from which we will iterate over is called "data".  
                               // See an example of the JSON response in models/data_from_instagrams.json
                               root: 'data'
                           }
                       }
                   })
               }
			   )
			   
    ], 
    iconCls: 'search',
    title: 'Search',

});
