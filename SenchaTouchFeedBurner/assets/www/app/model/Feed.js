/**
 * @class RSS.model.Feed
 * @extends Ext.data.Model
 * Model which contains all the information related
 * to a single Feed managed by the application.
 */
Ext.define('RSS.model.Feed', {
    extend: 'Ext.data.Model',
    config: {
        fields: [
            { name: 'name',           type: 'string' },
            { name: 'url',            type: 'string' },
            { name: 'nameRewrite',    type: 'string' ,    convert: function(v, record){
        
                return url.prettify(record.get('name'));
        
            }}
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
            { type: 'format', field: 'url', matcher : /^(ftp|http|https?):\/\/+[a-z0-9\-\.]/, message : "Formato url incorrecto.La url debe empezar por http://,ftp:// o https://" },
            
            
        ],

        proxy: {
            type: 'localstorage',
            id: 'feeds'
        }
    }
});