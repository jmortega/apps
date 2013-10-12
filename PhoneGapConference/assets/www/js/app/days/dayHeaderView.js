define(function(require, exports, module) {

    var DayModel = require('app/days/dayModel');
    var utils = require('app/utils');
    var appRouter = require('app/appRouter');
    
    var DayHeaderView = Backbone.View.extend({
        manage: true,
        model: DayModel,
        tagName: 'h1',
        className: 'topcoat-navigation-bar__title',
        
        parentRoute: 'dayCollection',
        
        initialize: function() {
            console.log('header init');
        },
        
        afterRender: function() {
            console.log('set header content:' + this.model.get('dayOfWeek') );
            this.el.innerHTML = this.model.get('dayOfWeek');
            this.itemWidth = this.el.offsetWidth;
        },
        
        hide: function() {
            this.el.style.display = 'none';
        },
        
        setupAsCurrent: function() {
            this.el.style.display = 'block';
            this.el.style.left = '0';
            this.itemWidth = this.el.offsetWidth;
            utils.setTransform(this.el, 'none');
            var id = this.model.get('id');
            appRouter.goTo(null, 'sessionCollection/' + id, 'none');
            var selectedClass = 'anyconf-day-symbol--selected';
            $('.js-day-symbol').removeClass(selectedClass);
            $('.js-day-symbol[data-day-id=' + id + ']').addClass(selectedClass);
        },
        
        setupAsPrevious: function() {
            this.el.style.display = 'block';
            this.el.style.left = '-100%';
        },
        
        setupAsNext: function() {
            this.el.style.display = 'block';
            this.el.style.left = '100%';
        },
    });
    
    return DayHeaderView;
});
