define(['require', 'jquery', 'backbonejs', 'underscorejs', '../aggregator', 'moment','./page-collection-model'
       ],
    function (require, $, Backbone, _, aggregator, moment, BaseCollection) {
    aggregator.Notification = Backbone.Model.extend({

        initialize: function () {
            this.attributes.date = this.formatRelativeDate(this.attributes.timestamp);
            //this.idAttribute = "idAttributeName";
        },

        getDate: function () {
            if (this.attributes.timestamp) {
                return new Date(this.attributes.timestamp)
            }
            return null;
        },

        formatRelativeDate: function(date) {
            moment.lang('en', {
                calendar: {
                    lastDay: '[Yesterday at] LT',
                    sameDay: 'LT',
                    nextDay: '[Tomorrow at] LT',
                    lastWeek: '[last] dddd [at] LT',
                    nextWeek: 'dddd [at] LT',
                    sameElse: 'L'
                }
            });
            return moment(date).calendar();
        }
    });

    aggregator.Notifications = BaseCollection.extend({
        model: aggregator.Notification,
        url: aggregator.baseUrl() + '/notifications',
        initialize: function(models, options){
            this.subscribe(options || {});
        }
    });
});