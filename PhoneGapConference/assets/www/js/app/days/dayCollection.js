define(function(require, exports, module) {
    var config = require('app/config');
    var DayModel = require('app/days/dayModel');
    var SessionCollection = require('app/sessions/sessionCollection');
    
    var dayCollection = Backbone.Collection.extend({
        model: DayModel,

        initialize: function() {
        },
        
        setSpeakers: function(speakerCollection) {
            this.speakerCollection = speakerCollection;
        },
        
        selectionChangeHandler: function(model) {
            if (this.selected) {
               this.selected.set({
                   'selected': false
               });
            }
            this.selected = model;
        },

		makeDate: function(day, time) {
			var dateString = day + ' ' + time;
			var date = moment(dateString);
			if( !date.isValid() ) {
				throw Error('Invalid time: ' + dateString);
			}
			return date;
		}
    });

    return dayCollection;
});
