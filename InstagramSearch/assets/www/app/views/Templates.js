instagramSearch.views.innerListItemTpl = function () {
    return "<div> " +

	// using Src to resize the image in the cloud
    "<img width='120' height='120' src='http://src.sencha.io/286/{thumbnail_url}' />" +
	"<img src='css/images/instagram.png' align='center' />"+
    "<div class='copy-wrap'><h2><span class='username'>{full_name}</span></h2></div>" +
    "</div>";
};