define(function(require, exports, module) {

    var SessionModel = require('app/sessions/sessionModel');
    var sessionDetailsTemplate = require('text!app/sessions/templates/sessionDetailsTemplate.html');
    var ItemDetailsView = require('app/components/itemDetailsView');
    var actionVerificationTemplate = require('text!app/templates/actionVerification.html');
    
    var Strings = {
        SESSION_STARRED: 'Session registrada en favoritos',
        SESSION_UNSTARRED: 'Session eliminada de favoritos'
    };
    
    var SessionDetailsView = ItemDetailsView.extend({
        model: SessionModel,
        template: _.template(sessionDetailsTemplate),
        
        parentRoute: 'sessionCollection',
        
        initialize: function() {
            this.parentView = this.options.parentView;
            this.model.on('change:starred', this.handleStarredChange, this);
        },
        
        showVerification: function(info, className) {
            if( this.$verificationEl ) {
                this.$verificationEl.remove();
                this.$verificationEl = null;
            }
            var $verificationEl = this.$verificationEl = $( _.template(actionVerificationTemplate, {
                info: info,
                className: className
            }) );
            this.$el.append( $verificationEl );
            $verificationEl.on('transitionEnd', function() {
                $verificationEl.remove();
            });

            setTimeout( function() {
                $verificationEl.addClass('js-overlay-transition-out');
            }, 1000);
        },
        
        handleStarredChange: function() {
            var newStatus = this.model.get('starred');
            var text = newStatus == true ? Strings.SESSION_STARRED : Strings.SESSION_UNSTARRED;
            var className = newStatus == true ? 'anyconf-overlay-star' : 'anyconf-overlay-unstar';
            this.showVerification(text, className);
        },
        
        serialize: function() {
            var modelData = this.model.toJSON();
            var speakerCollection = this.model.collection.speakerCollection;

            var subtitle = '';
            
            var startTime = {
                time: modelData.startTime.format('h:mm'),
                suffix: modelData.startTime.format('A')
            };

            var endTime = {
                time: modelData.endTime.format('h:mm'),
                suffix: modelData.endTime.format('A')
            };

            var sessionSpeakers = [];

            for( var i = 0; i < modelData.speaker_ids.length; i++ ) {
                var speakerId = modelData.speaker_ids[i];
                var speakerData = speakerCollection.get( speakerId ).toJSON();
                speakerData.route = 'speakerDetails/' + speakerData.id;
                sessionSpeakers.push( speakerData );
            }
            
            var templateValues = {
                title: modelData.title,
                subtitle: subtitle,
                startTime: startTime,
                endTime: endTime,
                details: modelData.details,
                speakers: sessionSpeakers
            };
            
            return templateValues;
        }
    });
    
    return SessionDetailsView;
});
