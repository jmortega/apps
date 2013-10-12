define(function(require, exports, module) {

    var SpeakerCollection = require('app/speakers/speakerCollection');
    
    var DayModel = Backbone.Model.extend({

        selected: false,
        isToday: false,

        initialize: function() {
            var dateStr = this.get('date');
            this.id = dateStr;
            this.setDayOfWeek(dateStr);
        },
        
        setDayOfWeek: function(dayStr) {
            var dayOfWeek;
            var day = moment(dayStr);
            var curDate = moment().format("YYYY-MM-DD");
            var dayDate = day.format("YYYY-MM-DD");
            if( dayDate == curDate ) {
                this.isToday = true;
                dayOfWeek = 'TODAY';
            } else {
                dayOfWeek = day.format('dddd');
            }
            this.set('dayOfWeek', dayOfWeek);
        }
    });

    return DayModel;
});
