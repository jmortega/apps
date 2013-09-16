instagramSearch.models.InstagramModel = Ext.regModel("instagramSearch.models.InstagramModel", {
    fields: [
        {name: "filter", type: "string"},
        {name: "link", type: "string"},
        {name: "username", type: "string", mapping: "user.username"},
		{name: "full_name", type: "string", mapping: "user.full_name"},
        {name: "thumbnail_url", type: "string", mapping: "images.thumbnail.url"},
        {name: "low_res_url", type: "string", mapping: "images.low_resolution.url"},
        {name: "standard_res_url", type: "string", mapping: "images.standard_resolution.url"},
    ]
});
