define(function(require, exports, module) {

    var appRouter = require('app/appRouter');
    var DayHeaderView = require('app/days/dayHeaderView');
    var CollectionDetailsView = require('app/components/collectionDetailsView');
    
    var DayCollectionHeadersView = CollectionDetailsView.extend({
        DetailsView: DayHeaderView,
        routeId: 'sessionCollection',
        className: 'topcoat-titles-wrap  js-day-headers',
        handleRouteIn: function(collectionId, viewId) {
            this.inView = true;
            this.navigateTo(collectionId);
        },
        navigateTo: function(itemId) {
            var item = this.collection.get(itemId);
            this.currentItem = item;
            if( !this.isRendered ) {
                this.render();
                this.isRendered = true;
            } else {
                this.setCurrentItem(item);
            }
            appRouter.setSubRoute(itemId);
        }
    });
    
    return DayCollectionHeadersView;
});
