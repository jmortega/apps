define(function(require, exports, module) {

    var SessionView = require('app/sessions/sessionView');
    var CollectionView = require('app/components/collectionView');
    var emptyPageTemplate = require('text!app/sessions/templates/emptyPageTemplate.html');
    var Strings = {
        NO_SESSIONS_TITLE: '',
        NO_SESSIONS_FOUND: 'You have no favourite sessions',
        NO_SESSIONS_INFO_BEFORE: 'You can add favourite via the',
        SESSIONS_LINK_TEXT: 'schedule',
        NO_SESSIONS_INFO_AFTER: '.'
    };

    var SessionCollectionView = CollectionView.extend({
        ItemView: SessionView,
        routeId: 'sessionCollection',
        showEmptyPage: function() {
            // Should only be for no Starred sessions
            var params = {
                title: Strings.NO_SESSIONS_TITLE,
                message: Strings.NO_SESSIONS_FOUND,
                helpInfoBefore: Strings.NO_SESSIONS_INFO_BEFORE,
                helpInfoAfter: Strings.NO_SESSIONS_INFO_AFTER,
                helpInfoLinkText: Strings.SESSIONS_LINK_TEXT,
                returnRouteId: this.options.returnRouteId
            };

            var page = _.template(emptyPageTemplate, params);
            this.$el.append(page);
        },
        initialize: function() {
            switch( this.options.type ) {
                case 'starred':
                    this.routeId = 'starredSessionCollection';
                    break;
                default:
                    this.routeId = 'sessionCollection';
            }
            
            // call super
            CollectionView.prototype.initialize.apply(this, arguments);
        },
        
        // TODO: Refactor this, shouldn't need to run for each day
        checkTime: function() {
            var _this = this;
            
            // check if we're in the right mode
            if( !Backbone.history.fragment || Backbone.history.fragment.indexOf('sessionCollection/') == -1 ) {
                // setTimeout(this.checkTime, 60 * 1000);
                return;
            }
        
            var now = moment();
            // TODO: For each track, if track is today...
            var timeOfNext = null;
            
            this.subCollection.each(function(session) {
                var start = session.get('startTime');
                var end = session.get('endTime');
                
                // check if session should be first "up next"
                if( !timeOfNext && now.isBefore( start ) ) {
                    session.setAsNextUp();
                    timeOfNext = start;
                // check if session is also "up next"
                } else if( timeOfNext && start.isSame(timeOfNext) ) {
                    session.setAsNextUp();
                // check if session is happening now
                } else if( now.isAfter( start ) && now.isBefore( end ) ) {
                    session.setAsCurrent();
                } else {
                    session.clearTimeFlag();
                }
            });
             
            setTimeout(function() {
                _this.checkTime.call(_this);
            }, 60 * 1000);
        }
    });
    
    return SessionCollectionView;
});
