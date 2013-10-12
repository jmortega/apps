define(function(require, exports, module) {

    var appRouter = require('app/appRouter');
    
    //Session
    var SessionCollection = require('app/sessions/sessionCollection'); // day of sessions or starred sessions
    
    var SessionCollectionView = require('app/sessions/sessionCollectionView');
    var SessionCollectionDetailsView = require('app/sessions/sessionCollectionDetailsView');

    var DayCollectionView = Backbone.View.extend({
        manage: true,
        className: 'anyconf-day-list-collection',
        initialize: function(args) {
            this.sessionCollection = args.sessionCollection;
        },
        setDefaultDayId: function(dayId) {
            this.defaultDayId = dayId;
        },
        afterRender: function() {
            var sessionCollection = this.sessionCollection;
            var starredFilter = function(model) {
                return model.get('starred');
            };

            var sessionCollectionStarredView = new SessionCollectionView({
                collection: sessionCollection,
                id: 'starred',
                type: 'starred',
                filter: starredFilter,
                returnRouteId: 'sessionCollection/' + this.defaultDayId,
                routeInHandler: function() {
                    appRouter.setCurrentView(this);
                    this.render();
                }
            });

            var sessionCollectionStarredDetailsView = new SessionCollectionDetailsView({
                collection: sessionCollection,
                filter: starredFilter,
                viewId: 'starred'
            });

            this.setView(sessionCollectionStarredView, true);
            this.setView(sessionCollectionStarredDetailsView, true);

            this.collection.each(function(dayModel) {
                var dateFilter = function(model) {
                    var sessionDate = model.get('instances')[0].date;
                    return (sessionDate == dayModel.get('date'));
                };
            
                var sessionCollectionView = new SessionCollectionView({
                    collection: sessionCollection,
                    filter: dateFilter,
                    id: dayModel.get('id')
                });
                
                var sessionCollectionDetailsView = new SessionCollectionDetailsView({
                    collection: sessionCollection,
                    filter: dateFilter,
                    viewId: dayModel.get('id')
                });

                this.setView(sessionCollectionView, true);
                this.setView(sessionCollectionDetailsView, true);

                sessionCollectionView.listenTo(sessionCollection, 'sync', function(evt) {
                    sessionCollectionView.id = dayModel.id;
                });
            }, this);
            
        }
    });
    
    return DayCollectionView;
});
