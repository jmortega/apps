define(function(require, exports, module) {

    var SpeakerDetailsView = require('app/speakers/speakerDetailsView');
    var CollectionDetailsView = require('app/components/collectionDetailsView');
    var appRouter = require('app/appRouter');
    
    var SpeakerCollectionDetailsView = CollectionDetailsView.extend({
        DetailsView: SpeakerDetailsView,
        routeId: 'speakerDetails',
        handleRouteIn: function(speakerId) {
            this.inView = true;
            appRouter.setCurrentView(this);
            this.navigateTo(speakerId);
        }
    });
    
    return SpeakerCollectionDetailsView;
});
