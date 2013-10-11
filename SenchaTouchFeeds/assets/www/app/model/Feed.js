Ext.define('MyApp.model.Feed', {
    extend: 'Ext.data.Model',

    config: {
        fields: [
            {
                name: 'id',
                type: 'int'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'url',
                type: 'string'
            }
        ],
        validations: [
                      {
                          type: 'presence', // name is required
                          field: 'name',
                          message : "Introduzca nombre"
                      },
                      {
                          type: 'presence', // url is required
                          field: 'url',
                          message : "Introduzca url"
                      },
                      {
                          type : 'format',
                          name : 'url',
                          matcher :  /^(ftp|http|https?):\/\/+[a-z0-9\-\.]/ ,
                          message : "Formato url incorrecto.La url debe empezar por http://,ftp:// o https://"
                      }
   
        ]
    }
});