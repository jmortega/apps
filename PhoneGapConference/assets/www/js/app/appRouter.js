define(function(require, exports, module) {
    var effects = require('app/effects');

    var AppRouter = Backbone.Router.extend({
        
        history: [],
        
        routes: {
            'starredSessionCollection': 'starredSessionCollection',
            'speakerCollection': 'speakerCollection',

            'sessionCollection/:dayId': 'sessionCollection',
            'sessionDetails/:dayId/:sessionId': 'sessionDetails',
            'speakerDetails/:speakerId': 'speakerDetails'
        },

        lastItemId: null,
        backRoute: null,
        
        goTo: function( currentView, newRoute, transitionId ) {
            console.log('GOTO: ' + newRoute);
            var currentRoute = Backbone.history.fragment;
            if( newRoute == currentRoute ) {
                return;
            }
            var oldView = currentView || this.currentView;
            if( oldView ) {
                oldView.transitionOut(transitionId);
            }
            
            this.transitionId = transitionId || null;
            this.history.push({
                sourceRoute: currentRoute,
                destRoute: newRoute,
                transition: transitionId
            });
            this.navigate(newRoute, {trigger: true});
        },

        goBack: function() {
            var last = this.history.pop();
            this.transitionId = last.transition ? effects.getReverseTransition( last.transition ) : null;
            this.currentView.transitionOut(this.transitionId);
            this.navigate(last.sourceRoute, {trigger: true});
        },
        
        setCurrentView: function(view) {
            this.currentView = view;
        },

        setSubRoute: function(itemId) {
            this.lastItemId = itemId;
        },

        getSubRoute: function() {
            return this.lastItemId;
        },
        
        // These probably aren't necessary if we just use .on(route:*)
        sessionCollection: function() {
            
        },
            
        starredSessionCollection: function() {
            
        },
        
        speakerCollection: function() {
        
        },
        
        sessionDetails: function(dayId, sessionId) {

        },
        
        speakerDetails: function(speakerId) {
        
        }
    });
    
    var appRouter = new AppRouter();
    return appRouter;
});
