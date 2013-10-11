Ext.define('MyApp.model.FeedItem', {
    extend: 'Ext.data.Model',

    config: {
        fields: [
            { name: 'title',          type: 'string' },
            { name: 'link',           type: 'string' },
            {
                name: 'pubDate',
                type: 'date'
            },
            {
                name: 'content',
                mapping: 'encoded.content'
            },
            {
                name: 'creator',
                mapping: 'creator.content'
            },
            {
                name: 'description'
            },
            {
                name: 'thumbnail'
            },
            { name: 'author',         type: 'string' },
            { name: 'friendlyDate',   type: 'string' ,    convert: function(v, record){
                
                /* this field will contains the news published date
                 * converted in a more friendly format. */
                var date = record.get('pubDate'),
                    pf = date.getHours() > 12 ? 'PM' : 'AM';
        
                return Ext.Date.format(date, 'Y/m/d') + ', ' + 
                       Ext.Date.format(date, 'h:i') + pf;

            }}
        ]
    }
});